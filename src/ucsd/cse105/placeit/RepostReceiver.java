package ucsd.cse105.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RepostReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int notificationId = intent.getIntExtra("notificationId", 0);

		String placeItType = intent
				.getStringExtra(NotificationHelper.NOTIFICATION_PLACEIT_TYPE);

		final String username = Database.getUsername(context);
		if (placeItType == "Location") {
			LocationPlaceIt placeIt = Database
					.getLocationPlaceIt(notificationId, username);
			placeIt.setDueDate(10);

			Database.save(placeIt);
		}
		else
		{
			CategoryPlaceItService.resetDelay();
		}

		// remove notification
		NotificationHelper helper = new NotificationHelper(context);
		helper.dismissNotificationByID(notificationId);
	}
}
