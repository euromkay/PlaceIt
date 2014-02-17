package ucsd.cse105.placeit;

import java.util.Calendar;
import java.util.Date;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiscardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("notificationId", 0);
        
        PlaceIt p = Database.getPlaceIt(notificationId, context);
        
        //Check if PlaceIt is on a recurring schedule
        if (p.getSchedule() > 0){
        	Date dueDate = new Date();
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(dueDate);
    		cal.add(Calendar.SECOND, p.getSchedule());
    		p.setDueDate(cal.getTime());
    		
    		Database.save(p, context);
        }
        else{
        	//Remove Place-It from Database
            Database.removePlaceIt(notificationId, context);
        }
        
        // cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
