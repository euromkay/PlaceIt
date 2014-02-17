package ucsd.cse105.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiscardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationID = intent.getIntExtra("notificationId", 0);
        
        PlaceIt p = Database.getPlaceIt(notificationID, context);
        
        //Check if PlaceIt is on a recurring schedule
        if (p.getSchedule() > 0){
        	//Update dueDate & save to DB
        	p.setDueDate(p.getSchedule());
    		Database.save(p, context);
        }
        else{
        	//Remove Place-It from Database
            Database.removePlaceIt(notificationID, context);
        }
        
        // remove notification
        NotificationHelper helper = new NotificationHelper(context);
        helper.dismissNotificationByID(notificationID);
    }
}
