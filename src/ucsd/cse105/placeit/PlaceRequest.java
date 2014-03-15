package ucsd.cse105.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;

public class PlaceRequest {

	// ArraList to store corresponding lat, lng
	static List<Double> latList = new ArrayList<Double>();
	static List<Double> lngList = new ArrayList<Double>();

	// Create our transport.
	private static final HttpTransport transport = new ApacheHttpTransport();
	// The different Places API endpoints.
	// order of data in requested URL doesn'n matter
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
	private static final String PLACE_ADD_URL = "https://maps.googleapis.com/maps/api/place/add/json?key=YOUR KEY HERE&sensor=false";
	private static final String PLACES_DELETE_URL = "https://maps.googleapis.com/maps/api/place/delete/json?key=YOUR KEY HERE&sensor=false";

	// Fill in the API key you want to use.
	private static final String API_KEY = "AIzaSyDE5PzvRwZ0XCJ--9lkv9dd8dBWagVJJaM";
	private static final String LOG_KEY = "GooglePlace";

	static ArrayList<String> placeReference = new ArrayList<String>();

	private static String mySearchType = "hospital"; // this is just for testing
	private static String searchName = "OffCourse Golf Hole";

	// converting back to -33.8670522, 151.1957362 format
	// double latitude = ShowGoogleMap.updated_lat / 1E6;
	// double longitude = ShowGoogleMap.updated_lng / 1E6;

	// Sydney, Australia
	// double latitude = -33.8670522;
	// double longitude = 151.1957362;

	// telenet
	// double latitude = 51.034823;
	// double longitude = 4.483774;

	public static ArrayList<CategoryData> performSearch(Location pos, ArrayList<CategoryPlaceIt> passedInList) throws Exception{

		ArrayList<CategoryPlaceIt> list = new ArrayList<CategoryPlaceIt>();
		for(CategoryPlaceIt p: passedInList)
			list.add(p);
		
		TreeMap<String, Place> map = new TreeMap<String, Place>();
		
		ArrayList<CategoryData> cList = new ArrayList<CategoryData>();
		
		while(list.size() != 0){
			CategoryPlaceIt p = list.get(0);
			int result = mapHasCategory(map, p);
			
			Place place;
			
			if(result != -1)
				place = map.get(p.getCategory(result));
			else
				place = getResult(pos);
			
			for(String s: place.types)
				map.put(s, place);
			
			if(place != null)
				cList.add(new CategoryData(p.getTitle(), place.name, place.formatted_address, p.getID()));
			
			list.remove(p);
		}
		/*
		for (Place place : places.results) {
				
			Log.v(LOG_KEY, place.name);	
			latList.add(place.geometry.location.lat);
			lngList.add(place.geometry.location.lng);	
			// assign last added place reference 
			placeReference.add(place.reference);
			
				
			String title;
			String name = place.name;
			String address = place.formatted_address;
			cList.add(new CategoryData(title, name, address));
				
		}	     */  
		return cList;

		
	}

	
	private static int mapHasCategory(TreeMap<String, Place> map, CategoryPlaceIt p){
		for(int i = 0; i < 3; i++)
			if(map.containsKey(p.getCategory(i)))
				return i;
		return -1;
	}

	private static Place getResult(Location pos) {
		try {
			// Log.v(LOG_KEY, "Start Search...");
			GenericUrl reqUrl = new GenericUrl(PLACES_SEARCH_URL);
			reqUrl.put("key", API_KEY);
			reqUrl.put("location", pos.getLatitude() + "," + pos.getLongitude());
			reqUrl.put("radius", CategoryPlaceItService.MIN_DISTANCE); // radius
																		// of
																		// 5Km
			reqUrl.put("types", mySearchType);
			reqUrl.put("name", searchName);
			reqUrl.put("sensor", "false");
			// Log.v(LOG_KEY, "Requested URL= " + reqUrl);
			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory.buildGetRequest(reqUrl);

			Log.v(LOG_KEY, request.execute().parseAsString());
			List<Place> list = request.execute().parseAs(PlacesList.class).results;
			if(list.size() != 0)
				return list.get(0);
			else
				return null;

			//Log.v(LOG_KEY, "STATUS = " + places.status);
			// empty array lists
			// latList.clear();
			// lngList.clear();
		} catch (HttpResponseException e) {
			//Log.v(LOG_KEY, e.getResponse().parseAsString());
			e.printStackTrace();
			//throw e;
		}

		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			//throw e;
		}
		throw new NullPointerException();
	}

	class PlacesList {

		@Key
		public String status;

		@Key
		public List<Place> results;

	}
	public static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {

		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("Google-Places-DemoApp");
				request.setHeaders(headers);
				JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
				// JsonHttpParser.builder(new JacksonFactory());
				// parser.jsonFactory = new JacksonFactory();
				request.addParser(parser);
			}
		});
	}
}