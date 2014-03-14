package ucsd.cse105.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.LocationListener;

public class CategoryPlaceItService extends Service implements LocationListener {
	static final String TAG = "CategoryPlaceItService";
	
	LocationManager manager;
	android.location.LocationListener listener;
	String bestProvider;
	int minUpdateTime = 2000;
	public static final int minDistance = 1; // Distance in meters

	
	@Override
	public void onCreate() {
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		bestProvider = LocationManager.NETWORK_PROVIDER; //manager.getBestProvider(crit, false);
	}

	// When location changes, check if there are any locations with categories
	// that match existing CategoryPlaceIts within configured radius.
	@Override
	public void onLocationChanged(Location pos) {
		// Call GAE requesting Category PlaceIts
		try {
			ArrayList<CategoryData> list = PlaceRequest.performSearch(pos, Database.getAllCategoryPlaceIts());
			list.size();
			NotificationHelper helper = new NotificationHelper(this);
			helper.sendNotification(1, "Category Works!", "Bedtime!");
			if (list.size() > 0){
				helper.sendNotification(1, "Category Results!", "$");	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Register for location changes
	@Override
	public void onStart(Intent intent, int startid) {
		manager.requestLocationUpdates(bestProvider, minUpdateTime,
				minDistance, listener);
	}

	// Unregister for location changes
	@Override
	public void onDestroy() {
		manager.removeUpdates(listener);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
