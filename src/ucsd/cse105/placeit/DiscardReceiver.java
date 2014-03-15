package ucsd.cse105.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiscardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationID = intent.getIntExtra("notificationId", 0);
        
        String placeItType = intent
				.getStringExtra(NotificationHelper.NOTIFICATION_PLACEIT_TYPE);
        
        IPlaceIt p;
        
        if (placeItType == "Location") {
        	p = Database.getLocationPlaceIt(notificationID);
        	p.setIsCompleted(true);
			Database.save((LocationPlaceIt)p);
		}
		else
		{
			p = Database.getLocationPlaceIt(notificationID);
			p.setIsCompleted(true);
			Database.save((CategoryPlaceIt)p);
		}
        
        // remove notification
        NotificationHelper helper = new NotificationHelper(context);
        helper.dismissNotificationByID(notificationID);
    }
}
