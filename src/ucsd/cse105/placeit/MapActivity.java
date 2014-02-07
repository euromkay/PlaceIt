package ucsd.cse105.placeit;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements LocationListener, OnMapClickListener, OnClickListener, OnMarkerClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnCameraChangeListener{

	private GoogleMap mMap;
	private LocationClient locationManager;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		setContentView(R.layout.activity_map);

		setUpMapIfNeeded();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				PlaceIt p = getPlaceIt(data);
				addPlaceItToMap(p);
			}
			if(resultCode == RESULT_CANCELED){
				
			}
				
		}
	}
	private PlaceIt getPlaceIt(Intent data){
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
		Database.saveZoomLevel(level);
		
		LatLng lat = mMap.getCameraPosition().target;
		Database.savePosition(lat);
		
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
				mMap.setOnCameraChangeListener(this);
				
				findViewById(R.id.mapHomeButton).setOnClickListener(this);
				findViewById(R.id.mapListButton).setOnClickListener(this);
			}
		}
	}
	private void setPreviousZoom(){
		float level = Database.getLastZoom();
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);
		mMap.moveCamera(zoom);
	}
	private void setPreviousLocation(){
		CameraUpdate center = CameraUpdateFactory.newLatLng(Database.getLastPosition());
		
		mMap.moveCamera(center);
	}
	private void addPlaceItToMap(PlaceIt p){
		LatLng pos = p.getLocation();
		mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
	}
	

	
	public void onMapClick(LatLng pos) {
		makeNewPlaceIt(pos);
	}
	public static final String PLACEIT_LATITUDE = "latitude";
	public static final String PLACEIT_LONGITUDE = "longitude";
	public static final String PLACEIT_KEY = "placeitkey";
	private void makeNewPlaceIt(LatLng pos){
		double lat = pos.latitude;
		double longitude = pos.longitude;
		
		Intent i = new Intent(this, FormActivity.class);
		Bundle b = new Bundle();
		b.putDouble(PLACEIT_LATITUDE, lat);
		b.putDouble(PLACEIT_LONGITUDE, longitude);
		
		i.putExtra(PLACEIT_KEY, b);
		startActivityForResult(i, 1);
	}
	
	public void onClick(View arg0) {
		int viewID = arg0.getId();
			Toast.makeText(this, "BUTTON", Toast.LENGTH_LONG).show();
			
		if(viewID == R.id.mapHomeButton)
			centerMapOnUser();
		else if(viewID == R.id.mapListButton)
			startListActivity();
	}
	private void centerMapOnUser(){
		setCurrentLocation();
	}

	private void startListActivity(){
		
	}

	
	
	
	public void onLocationChanged(Location arg0) {
		LatLng coordinate = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
	}

	public boolean onMarkerClick(Marker arg0) {
		
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

	private boolean internetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		makeToast("CAMERA MOVED");
		
	}
}
