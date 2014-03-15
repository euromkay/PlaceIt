package ucsd.cse105.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiscardReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, final Intent intent) {
		final String username = Database.getUsername(context);
		Thread t = new Thread(new Runnable() {
			public void run() {
				int notificationID = intent.getIntExtra("notificationId", 0);
				IPlaceIt p = Database.getPlaceIt(notificationID, username);
				p.setIsCompleted(true);

				if (p instanceof LocationPlaceIt) {
					Database.save((LocationPlaceIt) p);
				} else {
					Database.save((CategoryPlaceIt) p);
				}
			}
		});
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int notificationID = intent.getIntExtra("notificationId", 0);
		// remove notification
		NotificationHelper helper = new NotificationHelper(context);
		helper.dismissNotificationByID(notificationID);
	}
}
