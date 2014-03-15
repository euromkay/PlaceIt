package ucsd.cse105.placeit;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class CategoryPlaceItService extends Service implements
		LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	static final String TAG = "CategoryPlaceItService";
	private static final int MIN_UPDATE_TIME = 1000;
	public static final int MIN_DISTANCE = 5; // Distance in meters
	private static Date LAST_CHECK = new GregorianCalendar(2014,1,1).getTime();
	
	private int delay = 10000; // Subsequent delay for checkNotify()
	private Timer timer = new Timer();
	
	public static synchronized void resetDelay(){
		LAST_CHECK = new Date();
	}

	//Sets the timer to run a the TimerTask on the given schedule
	private void setTimer() {
		timer.scheduleAtFixedRate(task, delay, delay);
	}

	// Used to run checkNotify() continuously in background
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			timedCheck();
		}
	};

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(0).setFastestInterval(MIN_UPDATE_TIME)
			.setSmallestDisplacement(MIN_DISTANCE)
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

	LocationClient mLocationClient;

	ArrayList<CategoryPlaceIt> placeIts;

	@Override
	public void onCreate() {
		mLocationClient = new LocationClient(this, this, this);
	}

	// When location changes, check if there are any locations with categories
	// that match existing CategoryPlaceIts within configured radius.
	@Override
	public synchronized void onLocationChanged(Location pos) {
			checkForChanges();
	}
	
	private synchronized void timedCheck(){
		checkForChanges();
	}

	private void checkForChanges() {
		long timeSinceLastCheck = new Date().getTime() - LAST_CHECK.getTime();
		if (timeSinceLastCheck < delay) {
			return;
		}
		
		// Call GAE requesting Category PlaceIts
		try {
			Thread t = new Thread(new Runnable() {
				public void run() {
					placeIts = Database.getAllCategoryPlaceIts();
				}
			});
			t.start();

			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (placeIts.size() == 0) {
				return;
			}

			// ArrayList<CategoryData> list = PlaceRequest.performSearch(pos,
			// placeIts);

			// if (list.size() > 0){
			NotificationHelper helper = new NotificationHelper(this);
			helper.sendNotification(1, "Category PlaceIt!", "Vons",
					"2354 N Sucks Rd");
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LAST_CHECK = new Date();
	}

	// Register for location changes
	@Override
	public void onStart(Intent intent, int startid) {
		checkForChanges(); //Perform initial check
		mLocationClient.connect(); //Register for location changes
		setTimer();
	}

	// Unregister for location changes
	@Override
	public void onDestroy() {
		mLocationClient.disconnect();
		timer.cancel();
		timer.purge();
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
