package ucsd.cse105.placeit;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
	private Context _context;
	private static int mapRequestCode = 0;
	private static int repostRequestCode = 0; // Used for unique request code
	private static int discardRequestCode = 0; // Used for unique request code
	
	public static final String NOTIFICATION_MAP_FORM = "NOTIFICATION_MAP_FORM";

	// Constructor
	public NotificationHelper(Context context) {
		_context = context;
	}

	// Discarding Pending Intent
	private PendingIntent getDiscardPendingIntent(int id) {
		// Create an Intent for the BroadcastReceiver
		Intent buttonIntent = new Intent(_context, DiscardReceiver.class);
		buttonIntent.putExtra("notificationId", id);

		// Create the PendingIntent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(_context,
				discardRequestCode++, buttonIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;
	}

	// Reposting Pending Intents
	private PendingIntent getRepostPendingIntent(int id) {
		// Create an Intent for the BroadcastReceiver
		Intent buttonIntent = new Intent(_context, RepostReceiver.class);
		buttonIntent.putExtra("notificationId", id);

		// Create the PendingIntent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(_context,
				repostRequestCode++, buttonIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;
	}

	// MapActivity
	private PendingIntent getMapActivityIntent(int id) {
		Intent intent = new Intent(_context, MapActivity.class);

		// This will be used by MapActivity to auto load FormActivity
		intent.putExtra(NOTIFICATION_MAP_FORM, id);
		return PendingIntent.getActivity(_context, mapRequestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private NotificationManager getNotificationManager() {
		return (NotificationManager) _context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void sendNotification(int id, String title, String description) {
		Builder builder = new Notification.Builder(_context)
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
	
	//Removes a notification with the given notificationID, if it exists.
	public void dismissNotificationByID(int notificationID){
		getNotificationManager().cancel(notificationID);
	}
}