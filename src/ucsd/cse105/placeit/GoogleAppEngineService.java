package ucsd.cse105.placeit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.GoogleMap;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GoogleAppEngineService extends Service implements LocationListener {

	private int initDelay = 0; // Initial delay for checkNotify()
	private int delay = 10000; // Subsequent delay for checkNotify()
	//private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final float MILES_RANGE = 0.5f;
	private GoogleMap mMap;

	/*
	private Timer timer = new Timer();

	//Sets the timer to run a the TimerTask on the given schedule
	private void setTimer() {
		timer.scheduleAtFixedRate(task, initDelay, delay);
	}

	// Used to run checkNotify() continuously in background
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			checkNotify();
		}
	};
	*/

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startid) {
		//setTimer();
		mMap.setMyLocationEnabled(true);
	}

	@Override
	public void onDestroy() {
		//timer.cancel();
		//timer.purge();
	}

	//Checks to see if any Place-its have dueDate values that are less than
	//the current system time and if the current location is within the specified
	//threshold. If so, then have NotificationHelper create a notification.
	private void checkNotify() {
		ArrayList<PlaceIt> placeIts = GoogleAppEngine.getAllPlaceIts(this); // repalace database with google app engine

		for (PlaceIt item : placeIts) {
			/*
			Date dueDate = item.getDueDate();
			Date now = new Date();
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.US);
			String s_dueDate = df.format(dueDate);
			String s_now = df.format(now);
			Log.d("PlaceItService - item.dueDate", s_dueDate);
			Log.d("PlaceItService - now", s_now);
			*/
			
			//First check to see if dueDate has elapsed
			//if (now.after(dueDate)) {
				//Check to see if Place-It is within desired range
				if (Distance.isWithinRange(getApplicationContext(),
						item.getLocation(), MILES_RANGE)) {
					NotificationHelper notify = new NotificationHelper(this);
					//Create notification
					notify.sendNotification(item.getID(), item.getTitle(),
							item.getDescription());
				}
			//}
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		if( arg0  )
		checkNotify();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
