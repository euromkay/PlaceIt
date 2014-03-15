package ucsd.cse105.placeit;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements LocationListener, OnMapClickListener, OnClickListener, OnMarkerClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	// this is declare for all separate thread tasks
	private ArrayList<LocationPlaceIt> placeIts = null;
	ProgressDialog dialog;
	
	public static final String PLACEIT_LOC_URI = "http://cs110team29.appspot.com/location";
	public static final String PLACEIT_CAT_URI = "http://cs110team29.appspot.com/category";
	public static final String PLACEIT_ACCOUNT_URI = "http://cs110team29.appspot.com/account";
	
	private GoogleMap mMap;
	private LocationClient locationManager;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_map);
		
		manageCategoryPlaceItService();
		manageLocationPlaceItService();

		setUpMapIfNeeded();
		int launchPlaceItId = getIntent().getIntExtra(NotificationHelper.NOTIFICATION_MAP_FORM, 0);
		
		if(launchPlaceItId != 0){
			Log.d("MapActivity.onCreate", "Calling startPlaceIt()");
			startPlaceIt(launchPlaceItId);
		}
		
		
	}
	
	private void manageCategoryPlaceItService() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (CategoryPlaceItService.class.getName().equals(service.service.getClassName())) {
	            return;
	        }
	    }
	    startService(new Intent(this, CategoryPlaceItService.class));
	}

	private void manageLocationPlaceItService() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (PlaceItService.class.getName().equals(service.service.getClassName())) {
	            return;
	        }
	    }
	    startService(new Intent(this, PlaceItService.class));
	}
	private LatLng loc; 
	private void startPlaceIt(final int id){
		Intent i = new Intent(this, FormActivity.class);
		i.putExtra(NotificationHelper.NOTIFICATION_MAP_FORM, id);
		final String username = Database.getUsername(this);
		Thread t = new Thread(new Runnable(){
			public void run(){
				loc = Database.getLocationPlaceIt(id, username).getLocation();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		Bundle b = new Bundle();
		b.putDouble(PLACEIT_LATITUDE, loc.latitude);
		b.putDouble(PLACEIT_LONGITUDE, loc.longitude);
		b.putInt(ListActivity.ID_BUNDLE_KEY, id);
		b.putBoolean(PLACEIT_HAS_POS, true);
		
		i.putExtra(PLACEIT_KEY, b);
		
		Log.d("MapActivity.startPlaceIt", "Started Placeit");
		
		startActivity(i);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				//placeit was saved to database
				IPlaceIt p = getPlaceIt(data);
				if(p instanceof LocationPlaceIt)
					addPlaceItToMap((LocationPlaceIt) p);
			}
			if(resultCode == RESULT_CANCELED){
				
			}	
		}
		if(requestCode == 3){
			if(resultCode == RESULT_OK){
				redoMarkers();
			}
			if(resultCode == RESULT_CANCELED){
				
			}
		}
	}
	
	private void redoMarkers(){
		mMap.clear();
		populatePlaceIts();
	}
	
	private IPlaceIt getPlaceIt(Intent data){
		return data.getParcelableExtra(FormActivity.COMPLETED_PLACEIT);
	}
	
	protected void onStart(){
		super.onStart();
		locationManager.connect();
		
	}
	
	protected void onStop(){
		locationManager.disconnect();
		super.onStop();
	}
	
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}
	
	protected void onPause(){
		if(mMap == null){
			super.onPause();
			return;
		}
		
		float level = mMap.getCameraPosition().zoom;
		Database.saveZoomLevel(level, this);
		
		LatLng lat = mMap.getCameraPosition().target;
		Database.savePosition(lat, this);
		
		super.onPause();
	}
	
	private void setUpMapIfNeeded() {
		
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				
			    locationManager = new LocationClient(this,this,this);
			    if(locationManager == null)
			    	makeToast("SOMETHINGS WRONG");
			    
				setPreviousZoom();
				setPreviousLocation();
				
				mMap.setOnMapClickListener(this);
				mMap.setOnMarkerClickListener(this);
				mMap.setMyLocationEnabled(true);
				
				findViewById(R.id.mapListButton).setOnClickListener(this);
				findViewById(R.id.mapPlaceItButton).setOnClickListener(this);
				findViewById(R.id.mapLogoutButton).setOnClickListener(this);
				populatePlaceIts();
			}
		}
	}
	
	private void setPreviousZoom(){
		float level = Database.getLastZoom(this);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);
		mMap.moveCamera(zoom);
	}
	
	private void setPreviousLocation(){
		CameraUpdate center = CameraUpdateFactory.newLatLng(Database.getLastPosition(this));
		
		mMap.moveCamera(center);
	}
	
	private void addPlaceItToMap(LocationPlaceIt p){
		LatLng pos = p.getLocation();
		mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
	}
	
	private void populatePlaceIts(){
		placeIts = null;
		final String username = Database.getUsername(this);
		Thread t = new Thread(new Runnable() {
			public void run() {
				placeIts = Database.getAllLocationPlaceIts(username);
			}
		});
		t.start();
		dialog = ProgressDialog.show(this, "Loading data...", "Please wait...", false);
		try {
			t.join();
			dialog.dismiss();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(IPlaceIt p: placeIts)
			if(p instanceof LocationPlaceIt){
				LocationPlaceIt placeIt = (LocationPlaceIt) p;
				if(!p.getIsCompleted())
					addPlaceItToMap(placeIt);
			}
	}
	
	public void onMapClick(LatLng pos) {
		Log.d("MapActivity.onMapClick", "Map clicked to make a new place-it");
		makeNewPlaceIt(pos);
	}
	public static final String PLACEIT_LATITUDE = "latitude";
	public static final String PLACEIT_LONGITUDE = "longitude";
	public static final String PLACEIT_HAS_POS = "position";
	public static final String PLACEIT_KEY = "placeitkey";
	private void makeNewPlaceIt(LatLng pos){
		Intent i = new Intent(this, FormActivity.class);
		Bundle b = new Bundle();
		
		if(pos != null){
			double lat = pos.latitude;
			double longitude = pos.longitude;
			b.putBoolean(PLACEIT_HAS_POS, true);
			b.putDouble(PLACEIT_LATITUDE, lat);
			b.putDouble(PLACEIT_LONGITUDE, longitude);
		}else
			b.putBoolean(PLACEIT_HAS_POS, false);
		
		i.putExtra(PLACEIT_KEY, b);
		
		startActivityForResult(i, 1);
	}
	
	public void onClick(View arg0) {
		int buttonID = arg0.getId();
		if(buttonID == R.id.mapListButton)
			startListActivity();
		else if(buttonID == R.id.mapLogoutButton)
			logout();
		else if(buttonID == R.id.mapPlaceItButton)
			makeNewPlaceIt(null);
	}
	
	private void startListActivity(){
		Intent i = new Intent(this, ListActivity.class);
		mMap.clear();//removes placeits from the map
		startActivityForResult(i, 3);
	}
	
	private void logout(){
		Log.d("MapActivity", "Logout button pressed");
		
		//Stop services
		stopService(new Intent(this, PlaceItService.class));
		stopService(new Intent(this, CategoryPlaceItService.class));
		
		//Dismiss all notifications
		NotificationHelper nHelper = new NotificationHelper(this);
		nHelper.dismissAll();
		
		Intent i = new Intent(this, LoginActivity.class);
		mMap.clear();
		Database.clearCredentials(this);
		startActivity(i);
		finish();
	}
	
	public void onLocationChanged(Location arg0) {
		LatLng coordinate = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
	}

	public boolean onMarkerClick(final Marker marker) {
		final String username = Database.getUsername(this);
		
		IPlaceIt p = Database.getLocationPlaceIt(marker.getPosition(), username);
		Log.d("MapActivity.onMarkerClick", "Id was: " + Integer.toString(p.getID()));
		startPlaceIt(p.getID());
		
		return false;
	}

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
	}

	public void onConnected(Bundle arg0) {
		setCurrentLocation();
	}

	private void setCurrentLocation(){
	    Location location = locationManager.getLastLocation();
	    double lat =  location.getLatitude();
	    double lng = location.getLongitude();
	    LatLng coordinate = new LatLng(lat, lng);
	    CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
	    mMap.moveCamera(center);
	}

	public void onDisconnected() {
		makeToast("Disconnected. Please re-connect.");
		
	}

	private void makeToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

}
