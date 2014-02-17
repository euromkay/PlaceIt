package ucsd.cse105.placeit;

import java.util.Calendar;
import java.util.Date;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RepostReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("notificationId", 0);
        
        PlaceIt placeIt = Database.getPlaceIt(notificationId, context);
        
        Date dueDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dueDate);
		cal.add(Calendar.SECOND, 10);
		placeIt.setDueDate(cal.getTime());
		
		Database.save(placeIt, context);

        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
