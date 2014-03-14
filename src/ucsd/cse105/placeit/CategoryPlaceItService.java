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
	int minUpdateTime = 5000;
	int minDistance = 100; // Distance in meters

	
	@Override
	public void onCreate() {
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		bestProvider = LocationManager.NETWORK_PROVIDER; //manager.getBestProvider(crit, false);
	}

	// When location changes, check if there are any locations with categories
	// that match existing CategoryPlaceIts within configured radius.
	@Override
	public void onLocationChanged(Location arg0) {
		// Call GAE requesting Category PlaceIts
		new CloudControllerGet().execute(MapActivity.PLACEIT_CAT_URI);
	}

	// Register for location changes
	@Override
	public void onStart(Intent intent, int startid) {
		new CloudControllerGet().execute(MapActivity.PLACEIT_CAT_URI);
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

	private class CloudControllerGet extends
			AsyncTask<String, Void, List<String>> {
		@Override
		protected List<String> doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			List<String> list = new ArrayList<String>();
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				Log.d(TAG, data);
				JSONObject myjson;

				try {
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						list.add(obj.get("name").toString());
					}

				} catch (JSONException e) {

					Log.d(TAG, "Error in parsing JSON");
				}

			} catch (ClientProtocolException e) {

				Log.d(TAG,
						"ClientProtocolException while trying to connect to GAE");
			} catch (IOException e) {

				Log.d(TAG, "IOException while trying to connect to GAE");
			}
			return list;
		}

		//Process any acquired Category PlaceIts
		protected void onPostExecute(List<String> list) {
			for(String s : list){
				String test = s;
			}
		}

	}
}
