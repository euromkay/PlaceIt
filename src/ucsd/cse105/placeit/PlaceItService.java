package ucsd.cse105.placeit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PlaceItService extends Service {
	private int initDelay = 0; // Initial delay for checkNotify()
	private int delay = 10000; // Subsequent delay for checkNotify()
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final float MILES_RANGE = 0.5f;

	private Timer timer = new Timer();

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

	// Returns a reference to they NotificationManager
	public NotificationManager getNotificationManager() {
		return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	private int discardRequestCode = 0; // Used for unique request code

	// Discarding Place-Its
	public PendingIntent getDiscardPendingIntent(int id) {
		// Create an Intent for the BroadcastReceiver
		Intent buttonIntent = new Intent(this, DiscardReceiver.class);
		buttonIntent.putExtra("notificationId", id);

		// Create the PendingIntent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				discardRequestCode++, buttonIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;
	}

	private int repostRequestCode = 0; // Used for unique request code

	// Reposting Place-Its
	public PendingIntent getRepostPendingIntent(int id) {
		// Create an Intent for the BroadcastReceiver
		Intent buttonIntent = new Intent(this, RepostReceiver.class);
		buttonIntent.putExtra("notificationId", id);

		// Create the PendingIntent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				repostRequestCode++, buttonIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;
	}

	private int mapRequestCode = 0;
	public static final String NOTIFICATION_MAP_FORM = "NOTIFICATION_MAP_FORM";

	// MapActivity
	private PendingIntent getMapActivityIntent(int id) {
		Intent intent = new Intent(this, MapActivity.class);

		// This will be used by MapActivity to auto load FormActivity
		intent.putExtra(NOTIFICATION_MAP_FORM, id);
		return PendingIntent.getActivity(this, mapRequestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private void setNotification(int id, String title, String description) {
		Builder builder = new Notification.Builder(this)
				.setContentIntent(getMapActivityIntent(id))
				.setContentTitle(title)
				.setContentText(description)
				.setSmallIcon(R.drawable.ic_launcher)
				.addAction(R.drawable.ic_menu_close_clear_cancel, "Discard",
						getDiscardPendingIntent(id))
				.addAction(R.drawable.ic_audio_alarm, "Repost",
						getRepostPendingIntent(id));

		Notification notification = new Notification.InboxStyle(builder)
				.addLine(description).build();
		// This flag prevents swiping reminder to clear
		notification.flags |= Notification.FLAG_NO_CLEAR;
		NotificationManager notificationManager = getNotificationManager();
		notificationManager.notify(id, notification);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startid) {
		setTimer();
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		timer.purge();
	}

	private void checkNotify() {
		ArrayList<PlaceIt> placeIts = Database.getAllPlaceIts(this);

		for (PlaceIt item : placeIts) {
			Date dueDate = item.getDueDate();
			Date now = new Date();
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			String s_dueDate = df.format(dueDate);
			String s_now = df.format(now);
			Log.d("PlaceItService - item.dueDate", s_dueDate);
			Log.d("PlaceItService - now", s_now);
			
			//First check to see if dueDate has elapsed
			if (now.after(dueDate)) {
				//Check to see if Place-It is within desired range
				if (Distance.isWithinRange(getApplicationContext(),
						item.getLocation(), MILES_RANGE)) {
					setNotification(item.getID(), item.getTitle(),
							item.getDescription());
				}
			}
		}
	}

}