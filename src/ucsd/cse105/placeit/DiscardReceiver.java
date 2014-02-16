package ucsd.cse105.placeit;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiscardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("notificationId", 0);
        
        //Remove Place-It from Database
        Database.removePlaceIt(notificationId, context);

        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
