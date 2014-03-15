package ucsd.cse105.placeit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PlaceService {

	private String API_KEY;

	//constructor for place sertice
	public PlaceService(String apikey) {
		this.API_KEY = apikey;
	}

	//sets the api key in case you need to
	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

	//finds places and returns a list of them
	public ArrayList<Place> findPlaces(double latitude, double longitude,
			String placeSpacification) {

		String urlString = makeUrl(latitude, longitude, placeSpacification);

		try {
			String json = getJSON(urlString);

			System.out.println(json);
			JSONObject object = new JSONObject(json);
			JSONArray array = object.getJSONArray("results");

			ArrayList<Place> arrayList = new ArrayList<Place>();
			for (int i = 0; i < array.length(); i++) {
				try {
					Place place = Place
							.jsonToPontoReferencia((JSONObject) array.get(i));
					
					JSONArray types = (JSONArray) ((JSONObject) array.get(i)).get("types");
					if (types != null) {
						place.types.add(types.getString(0)); 
					}

					Log.v("Places Services ", "" + place);
					arrayList.add(place);
				} catch (Exception e) {
					Log.v("Places Services ", "" + e.getMessage());
				}
			}
			return arrayList;
		} catch (JSONException ex) {
			Logger.getLogger(PlaceService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(double latitude, double longitude, String place) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/search/json?");

		String latVal = null;
		String lngVal = null;

		try {
			latVal = URLEncoder.encode(String.valueOf(latitude), "UTF-8");
			lngVal = URLEncoder.encode(String.valueOf(longitude), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (place.equals("")) {
			urlString.append("&location=");
			urlString.append(latVal);
			urlString.append(",");
			urlString.append(lngVal);
			urlString.append("&radius=1609");
			// urlString.append("&types="+place);
			urlString.append("&sensor=false&key=" + API_KEY);
			urlString.append("&userIP=128.54.46.159");
		} else {
			urlString.append("&location=");
			urlString.append(latVal);
			urlString.append(",");
			urlString.append(lngVal);
			urlString.append("&radius=1609");
			urlString.append("&types=" + place);
			urlString.append("&sensor=false&key=" + API_KEY);
			urlString.append("&userIP=128.54.46.159");
		}
		return urlString.toString();
	}

	//gets the json from the string
	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	//gets the contents
	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();
		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}