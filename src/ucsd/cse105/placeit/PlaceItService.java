package ucsd.cse105.placeit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PlaceItService extends Service {
	private int delay = 5000;
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			checkNotify();
		}
	};
	
	public NotificationManager getNotificationManager() {
		  return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 }

	 public PendingIntent getPendingIntent() {
		  return PendingIntent.getActivity(this, 0, new Intent(this,
		    HandleNotificationActivity.class), 0);
		 }
	
	private void setNotification(int id, String title, String description)
	{
		PendingIntent pi = getPendingIntent();
		  Builder builder = new Notification.Builder(this)
		    .setContentTitle(title)
		    .setContentText(description)
		    .setSmallIcon(R.drawable.ic_launcher)
		    .addAction(R.drawable.ic_menu_close_clear_cancel, "Discard", pi)
		    .addAction(R.drawable.ic_audio_alarm, "Repost", pi);

		  Notification notification = new Notification.InboxStyle(builder)
		    .addLine(description).build();
		  // Put the auto cancel notification flag
		  notification.flags |= Notification.FLAG_AUTO_CANCEL;
		  NotificationManager notificationManager = getNotificationManager();
		  notificationManager.notify(id, notification);
		
	}
	
/*	private void setNotification(int id, String title, String description){
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setContentText(description);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MapActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MapActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}*/


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

	final Timer timer = new Timer();

	private void setTimer(){
		timer.scheduleAtFixedRate(task, delay, delay);
	}
	
	@Override
	public void onDestroy() {
		timer.purge();
	}
	
	private void checkNotify(){
		//TODO: DB checking
		//ArrayList<PlaceIt> placeIts = Database.getAllPlaceIts(activityContext);
		ArrayList<PlaceIt> placeIts = Database.getAllPlaceIts(this);
		
		for(PlaceIt item : placeIts){
			Date dueDate = item.getDueDate();
			Date now = new Date();
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			String s_dueDate = df.format(dueDate);
			String s_now = df.format(now);
			Log.d("PlaceItService - item.dueDate", s_dueDate);
			Log.d("PlaceItService - now", s_now);
			if(now.after(dueDate))
			{
				setNotification((int)item.getID(), item.getTitle(), item.getDescription());
			}
		}
	}

}