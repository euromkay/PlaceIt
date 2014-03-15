package ucsd.cse105.placeit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Database {
	final static String TAG_LOCATION_PLACEIT = "GAE Location PlaceIt";
	final static String TAG_CATEGORY_PLACEIT = "GAE Category PlaceIt";
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	private static final String FILE_ZOOM = "zoom_file";

	public static float getLastZoom(Activity a) {
		Log.d("Database.getLastZoom", "entering getLastZoom");
		try {
			FileInputStream fis = a.openFileInput(FILE_ZOOM);
			String[] content = reader(fis);
			Log.d("Database.getLastZoom", "returning for zoom level "
					+ content[0]);
			return Float.parseFloat(content[0]);
		} catch (FileNotFoundException e) {
			Log.d("Database.getLastZoom", "File not found");
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException f) {
			Log.d("Database.getLastZoom", "No file found");

		}
		// default return value if file isn't found
		return 20;
	}

	public static void saveZoomLevel(float level, MapActivity a) {
		String data = Float.toString(level);
		Log.d("Database.saveZoomLevel", data + " is the saved zoom level");
		try {
			FileOutputStream fos = a.openFileOutput(FILE_ZOOM,
					Context.MODE_PRIVATE);
			writer(fos, new String[] { data });
		} catch (FileNotFoundException e) {
			Log.d("Database", "something went wrong?");
			e.printStackTrace();
		}

	}

	
	
	public static void saveUsername(String username, Activity a) {

		try {
			FileOutputStream fos = a.openFileOutput(FILE_CREDENTIALS,
					Context.MODE_PRIVATE);
			writer(fos, new String[] { username });
		} catch (FileNotFoundException e) {
			Log.d("Database", "something went wrong?");
			e.printStackTrace();
		}

	}

	public static String getUsername(Activity a) {
		try {
			FileInputStream fis = a.openFileInput(FILE_CREDENTIALS);
			return reader(fis)[0];
		} catch (FileNotFoundException e) {
			Log.d("Database", "something went wrong?");
			e.printStackTrace();
		}
		return null;
	}

	// checks to see if the user has already logged in
	private static final String FILE_CREDENTIALS = "logincredentials file";

	public static boolean checkLoginCredentials(Activity a) {
		try {
			String content = getUsername(a);
			Log.d("Database.checkLoginCredentials", "content is " + content);
			return content.length() >= 1;//equals("account1") || content.equals("account2");
		} catch (Exception e) {
			return false;
		}
	}

	public static void clearCredentials(Activity a) {
		Log.d("Database.clearCredentials", "clearing credentials");
		a.deleteFile(FILE_CREDENTIALS);
		// FileOutputStream fos = a.openFileOutput(FILE_CREDENTIALS,
		// Context.MODE_PRIVATE);
		// write the one line to the file
		// writer(fos, new String[]{"SHOULD FAIL"});
	}

	// checks to see if the login was correct
	public static boolean checkLogin(String username, String password,
			Activity a) {
		if (!password.equals("password"))
			return false;

		if (username.equals("account1") || username.equals("account2")) {
			saveUsername(username, a);
			return true;
		}

		return false;

	}

	
	
	
	
	
	private static final String FILE_POSITION = "last_known_location file";

	public static void savePosition(LatLng pos, Activity a) {
		String[] data = new String[] { Double.toString(pos.latitude),
				Double.toString(pos.longitude) };

		try {
			FileOutputStream fos = a.openFileOutput(FILE_POSITION,
					Context.MODE_PRIVATE);
			writer(fos, data);
		} catch (FileNotFoundException e) {
			Log.d("Database", "something went wrong?");
			e.printStackTrace();
		}
	}

	public static LatLng getLastPosition(Activity a) {
		Log.d("Database.getLastZoom", "entering getLastZoom");
		try {
			FileInputStream fis = a.openFileInput(FILE_ZOOM);
			String[] content = reader(fis);
			double lat = Double.parseDouble(content[0]);
			double lng = Double.parseDouble(content[1]);
			return new LatLng(lat, lng);
		} catch (FileNotFoundException e) {
			Log.d("Database.getLastZoom", "File not found");
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException f) {
			Log.d("Database.getLastZoom", "No file found");

		}
		// default return value if file isn't found
		return new LatLng(0, 0);
	}

	
	
	
	private static void writer(FileOutputStream fos, String[] data) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(out);

			for (String s : data)
				writer.write(s + "\n");
			writer.close();
			out.flush();
			// out.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static String[] reader(FileInputStream fis) {
		try {
			InputStreamReader input = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(input);

			ArrayList<String> list = new ArrayList<String>();
			while (true) {
				String s = reader.readLine();
				Log.d("Database", " Read line: " + s);
				if (s == null)
					break;
				list.add(s);
			}

			if (list.size() == 0)
				Log.d("Database", "Came up with empty file");

			reader.close();
			input.close();
			fis.close();

			return toStringArray(list.toArray());

		} catch (FileNotFoundException e) {
			Log.d("Database", "unable to get last zoom level");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("Database", "unable to get last zoom level");
			e.printStackTrace();
		}
		return null;
	}
	private static String[] toStringArray(Object[] input) {
		String[] array = new String[input.length];
		for (int i = 0; i < array.length; i++)
			array[i] = (String) input[i];
		return array;
	}
	
	private static ArrayList<LocationPlaceIt> lPlaceIts = null;
	
	// returns an active list of all the placeits
	// uses network calls so must be called on a separate thread if 
	// being called from main ui thread.
	public static ArrayList<LocationPlaceIt> getAllLocationPlaceIts() {
		String tag = "Database.getAllPlaceIts()";

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(MapActivity.PLACEIT_LOC_URI);
		ArrayList<LocationPlaceIt> list = new ArrayList<LocationPlaceIt>();
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			Log.d(tag, data);
			JSONObject myjson;

			try {
				myjson = new JSONObject(data);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					LocationPlaceIt placeIt = new LocationPlaceIt(
							obj.getInt("name"));
					placeIt.setTitle(obj.getString("title"));
					placeIt.setDescription(obj.getString("description"));
					placeIt.setSchedule(obj.getInt("schedule"));

					Log.d(tag, obj.getString("dueDate"));
					Date dueDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).parse(obj
							.getString("dueDate"));
					placeIt.setDueDate(dueDate);

					LatLng location = new LatLng(obj.getDouble("latitude"),
							obj.getDouble("longitude"));
					placeIt.setLocation(location);
					
					placeIt.setUser(obj.getString("user"));
					placeIt.setIsCompleted(obj.getBoolean("isCompleted"));

					list.add(placeIt);
				}

			} catch (JSONException e) {

				Log.d(tag, "Error in parsing JSON");
			}
			catch (ParseException e){
				Log.d(tag, e.getMessage());
				e.printStackTrace();
			}

		} catch (ClientProtocolException e) {

			Log.d(tag, "ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {

			Log.d(tag, "IOException while trying to connect to GAE");
		}
		return list;
	}
	
	public static ArrayList<CategoryPlaceIt> getAllCategoryPlaceIts() {
		String tag = "Database.getAllCategoryPlaceIts()";

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(MapActivity.PLACEIT_CAT_URI);
		ArrayList<CategoryPlaceIt> list = new ArrayList<CategoryPlaceIt>();
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			Log.d(tag, data);
			JSONObject myjson;

			try {
				myjson = new JSONObject(data);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					CategoryPlaceIt placeIt = new CategoryPlaceIt(
							obj.getInt("name"));
					placeIt.setTitle(obj.getString("title"));
					placeIt.setDescription(obj.getString("description"));
					placeIt.setCategory(obj.getString("cat1"), obj.getString("cat2"),
							obj.getString("cat3"));
					placeIt.setUser(obj.getString("user"));
					placeIt.setIsCompleted(obj.getBoolean("isCompleted"));
					list.add(placeIt);
				}

			} catch (JSONException e) {

				Log.d(tag, "Error in parsing JSON");
			}

		} catch (ClientProtocolException e) {

			Log.d(tag, "ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {

			Log.d(tag, "IOException while trying to connect to GAE");
		}
		return list;
	}
	
	public static ArrayList<IPlaceIt> getAllPlaceIts(){
		ArrayList<IPlaceIt> list = new ArrayList<IPlaceIt>();
		list.addAll(getAllCategoryPlaceIts());
		list.addAll(getAllLocationPlaceIts());
		return list;
	}
	
	
	public static LocationPlaceIt getLocationPlaceIt(LatLng position) {
		Log.d("Database.getPlaceIt", "trying to find placeIt with id #: "
				+ position.toString());

		Thread t = new Thread(new Runnable() {
			public void run() {
				lPlaceIts = getAllLocationPlaceIts();
			}
		});
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (LocationPlaceIt p : lPlaceIts)
			if (p.getLocation().equals(position))
				return p;

		return null;
	}
	public static LocationPlaceIt getLocationPlaceIt(int id) {
		Log.d("Database.getPlaceIt", "trying to find placeIt with id #: "
				+ Integer.toString(id));

		ArrayList<LocationPlaceIt> lPlaceIts = getAllLocationPlaceIts();
		

		for (LocationPlaceIt p : lPlaceIts)
			if (p.getID() == id)
				return p;

		return null;
	}
	public static IPlaceIt getPlaceIt(int id) {
		Log.d("Database.getPlaceIt", "trying to find placeIt with id #: "
				+ Integer.toString(id));

		ArrayList<IPlaceIt> iPlaceIts = getAllPlaceIts();
		

		for (IPlaceIt p : iPlaceIts)
			if (p.getID() == id)
				return p;

		return null;
	}

	public static void removePlaceIt(final IPlaceIt placeIt) {
		Log.d("Database.removePlaceIt", "called here");
		
		
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				String url;
				
				if(placeIt instanceof CategoryPlaceIt){
					url = MapActivity.PLACEIT_CAT_URI;
				}
				else{
					url = MapActivity.PLACEIT_LOC_URI;
				}
				
				HttpPost post = new HttpPost(url);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					
					nameValuePairs.add(
							new BasicNameValuePair("id", Integer.toString(placeIt.getID())));
					
					nameValuePairs.add(new BasicNameValuePair("action", "delete"));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d(TAG_LOCATION_PLACEIT, line);
					}

				} catch (IOException e) {
					Log.d(TAG_LOCATION_PLACEIT,
							"IOException while trying to conect to GAE");
				}
			}

		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Database.remove", "Unable to find the placeit to remove");
	}

	public static void save(final LocationPlaceIt placeIt) {
		// final ProgressDialog dialog = ProgressDialog.show(a,
		// "Posting Data...", "Please wait...", false);

		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(MapActivity.PLACEIT_LOC_URI);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							8);
					nameValuePairs.add(new BasicNameValuePair("id", Integer
							.toString(placeIt.getID())));

					nameValuePairs.add(new BasicNameValuePair("title", placeIt
							.getTitle()));

					nameValuePairs.add(new BasicNameValuePair("description",
							placeIt.getDescription()));

					LatLng location = placeIt.getLocation();
					nameValuePairs.add(new BasicNameValuePair("longitude",
							Double.toString(location.longitude)));
					nameValuePairs.add(new BasicNameValuePair("latitude",
							Double.toString(location.latitude)));

					String dueDate = new SimpleDateFormat(DATE_FORMAT)
							.format(placeIt.getDueDate());
					nameValuePairs.add(new BasicNameValuePair("dueDate",
							dueDate));

					nameValuePairs.add(new BasicNameValuePair("schedule",
							Integer.toString(placeIt.getSchedule())));

					nameValuePairs.add(new BasicNameValuePair("isCompleted",
							Boolean.toString(placeIt.getIsCompleted())));
					
					nameValuePairs.add(new BasicNameValuePair("user",
							placeIt.getUser()));
					
					nameValuePairs.add(new BasicNameValuePair("action", "put"));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d(TAG_LOCATION_PLACEIT, line);
					}

				} catch (IOException e) {
					Log.d(TAG_LOCATION_PLACEIT,
							"IOException while trying to conect to GAE");
				}
				// dialog.dismiss();
			}

		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// dialog.show();
	}

	public static void save(final CategoryPlaceIt placeIt) {
		// final ProgressDialog dialog = ProgressDialog.show(a,
		// "Posting Data...", "Please wait...", false);

		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(MapActivity.PLACEIT_CAT_URI);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							7);

					nameValuePairs.add(new BasicNameValuePair("id", Integer
							.toString(placeIt.getID())));

					nameValuePairs.add(new BasicNameValuePair("title", placeIt
							.getTitle()));

					nameValuePairs.add(new BasicNameValuePair("description",
							placeIt.getDescription()));

					nameValuePairs.add(new BasicNameValuePair("cat1", placeIt
							.getCategory(0)));
					nameValuePairs.add(new BasicNameValuePair("cat2", placeIt
							.getCategory(1)));
					nameValuePairs.add(new BasicNameValuePair("cat3", placeIt
							.getCategory(2)));
					
					nameValuePairs.add(new BasicNameValuePair("isCompleted",
							Boolean.toString(placeIt.getIsCompleted())));
					
					nameValuePairs.add(new BasicNameValuePair("user",
							placeIt.getUser()));

					nameValuePairs.add(new BasicNameValuePair("action", "put"));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d(TAG_CATEGORY_PLACEIT, line);
					}

				} catch (IOException e) {
					Log.d(TAG_CATEGORY_PLACEIT,
							"IOException while trying to conect to GAE");
				}
				// dialog.dismiss();
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveAccount(final String name, final String password) {
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(MapActivity.PLACEIT_ACCOUNT_URI);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("name", name));
					nameValuePairs.add(new BasicNameValuePair("password", password));
					nameValuePairs.add(new BasicNameValuePair("action", "put"));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d("ACCOUNT", line);
					}

				} catch (IOException e) {
					Log.d("ACCOUNT",
							"IOException while trying to conect to GAE");
				}
				// dialog.dismiss();
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean getAccount(final String name, final String password) {
		String tag = "Database.getAccount()";

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(MapActivity.PLACEIT_ACCOUNT_URI);
		ArrayList<Account> list = new ArrayList<Account>();
		
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			Log.d(tag, data);
			JSONObject myjson;

			try {
				myjson = new JSONObject(data);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Account account = new Account(obj.getString("name"), obj.getString("password"));
					list.add(account);
				}

			} catch (JSONException e) {
				Log.d(tag, "Error in parsing JSON");
			}

		} catch (ClientProtocolException e) {

			Log.d(tag, "ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {

			Log.d(tag, "IOException while trying to connect to GAE");
		}
		
		for (Account a : list){
			Log.d("Database.getAccoutn", a.toString());
			if (a.getName().equals(name) && a.getPassword().equals(password))
				return true;
		}

		return false;
	}
	

	public static ArrayList<LocationPlaceIt> getCompletedPlaceIts(
			PullDownListActivity pullDownListActivity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
