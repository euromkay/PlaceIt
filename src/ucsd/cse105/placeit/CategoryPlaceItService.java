package ucsd.cse105.placeit;

import java.util.ArrayList;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class CategoryPlaceItService extends Service implements LocationListener,
	ConnectionCallbacks, OnConnectionFailedListener {
	
	static final String TAG = "CategoryPlaceItService";
	private static final int MIN_UPDATE_TIME = 2000;
	public static final int MIN_DISTANCE = 5; // Distance in meters
	
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(0)
			.setFastestInterval(MIN_UPDATE_TIME)
			.setSmallestDisplacement(MIN_DISTANCE)
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	
	LocationClient mLocationClient;
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient(this, this, this);
	}

	// When location changes, check if there are any locations with categories
	// that match existing CategoryPlaceIts within configured radius.
	@Override
	public void onLocationChanged(Location pos) {
		// Call GAE requesting Category PlaceIts
		try {
			ArrayList<CategoryData> list = PlaceRequest.performSearch(pos, Database.getAllCategoryPlaceIts());
			list.size();
			NotificationHelper helper = new NotificationHelper(this);
			helper.sendNotification(1, "Category Works!", "Bedtime!");
			if (list.size() > 0){
				helper.sendNotification(1, "Category Results!", "$");	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Register for location changes
	@Override
	public void onStart(Intent intent, int startid) {
		mLocationClient.connect();
	}

	// Unregister for location changes
	@Override
	public void onDestroy() {
		mLocationClient.disconnect();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
