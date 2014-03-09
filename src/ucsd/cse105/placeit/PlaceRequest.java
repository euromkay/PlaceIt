package ucsd.cse105.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import ucsd.cse105.placeit.Place.PlaceDetail;

import android.util.Log;

/*
 * This class have place search, search place details, add new place and delete place methods.
 * 
 * Note: Use Own valid Key in API_KEY = "YourMapKeyHere" and any where else needed.
*/

public class PlaceRequest {
	 // ArraList to store corresponding lat, lng
	 static List<Double> latList = new ArrayList<Double>();
	 static List<Double> lngList = new ArrayList<Double>();

	 // Create our transport.
	 private static final HttpTransport transport = new ApacheHttpTransport();
	 // The different Places API endpoints.
	 // order of data in requested URL doesn'n matter
	 private static final String PLACES_SEARCH_URL =  
	   "https://maps.googleapis.com/maps/api/place/search/json?";
	 private static final String PLACES_DETAILS_URL = 
	   "https://maps.googleapis.com/maps/api/place/details/json?";
	 private static final String PLACE_ADD_URL = 
	   "https://maps.googleapis.com/maps/api/place/add/json?" +
	   "key=AIzaSyAnOvJ-Woki5W5jUiZhv5bJ5YGB6ZY3yrs&sensor=false"; 
	 private static final String PLACES_DELETE_URL = 
	   "https://maps.googleapis.com/maps/api/place/delete/json?" +
	   "key=AIzaSyAnOvJ-Woki5W5jUiZhv5bJ5YGB6ZY3yrs&sensor=false";
	 
	 
	 // Fill in the API key you want to use.
	 private static final String API_KEY = "YourMapKeyHere";
	 static final String LOG_KEY = "GooglePlace";
	 
	 static ArrayList<String> placeReference =  new ArrayList<String>();
	   
	 private static String mySearchType = "hospital";  // this is just for testing
	 private static String searchName = "OffCourse Golf Hole";
	    
	 // converting back to -33.8670522, 151.1957362 format
	 double latitude = ShowGoogleMap.updated_lat / 1E6;
	 double longitude = ShowGoogleMap.updated_lng / 1E6;
	 
	 // Sydney, Australia
	 //double latitude = -33.8670522;
	 //double longitude = 151.1957362;
	 
	 // telenet
	 //double latitude = 51.034823;
	 //double longitude = 4.483774;
	 
	 public PlacesList performSearch() throws Exception {
	  try {
	   Log.v(LOG_KEY, "Start Search...");
	   GenericUrl reqUrl = new GenericUrl(PLACES_SEARCH_URL);
	   reqUrl.put("key", API_KEY);
	   reqUrl.put("location", latitude + "," + longitude);
	   reqUrl.put("radius", 5000); // radius of 5Km 
	   reqUrl.put("types", mySearchType);
	   reqUrl.put("name", searchName);
	   reqUrl.put("sensor", "false");
	   Log.v(LOG_KEY, "Requested URL= " + reqUrl);
	   HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
	   HttpRequest request = httpRequestFactory.buildGetRequest(reqUrl);
	    
	    Log.v(LOG_KEY, request.execute().parseAsString());       
	    PlacesList places = request.execute().parseAs(PlacesList.class);
	    Log.v(LOG_KEY, "STATUS = " + places.status);
	    // empty array lists
	    latList.clear();
	    lngList.clear();
	    for (Place place : places.results) {
	     Log.v(LOG_KEY, place.name); 
	     latList.add(place.geometry.location.lat);
	     lngList.add(place.geometry.location.lng); 
	     // assign last added place reference 
	     placeReference.add(place.reference);
	     
	    }          
	    return places;

	  } catch (HttpResponseException e) {
	   Log.v(LOG_KEY, e.getResponse().parseAsString());
	   throw e;
	  }
	  
	  catch (IOException e) {
	   // TODO: handle exception
	   throw e;
	  }
	 }
	 
	 public PlaceDetail performDetails(String reference) throws Exception { 
	  try {
	   Log.v(LOG_KEY, "Perform Place Detail....");
	   GenericUrl reqUrl = new GenericUrl(PLACES_DETAILS_URL);
	   reqUrl.put("key", API_KEY);
	   reqUrl.put("reference", reference);
	   reqUrl.put("sensor", "false");
	   Log.v(LOG_KEY, "Requested URL= " + reqUrl);
	   HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
	   HttpRequest request = httpRequestFactory.buildGetRequest(reqUrl);

	   Log.v(LOG_KEY, request.execute().parseAsString()); 
	   PlaceDetail placeDetail = request.execute().parseAs(PlaceDetail.class);
	   
	   return placeDetail;
	       
	  } catch (HttpResponseException e) {
	   Log.v(LOG_KEY, e.getResponse().parseAsString());
	   throw e;
	  }
	  catch (IOException e) { 
	   // TODO: handle exception
	   throw e;
	  }
	   
	 }
	 
	 public JSONObject addPlace(double lat, double lng, String type, String name) throws Exception {
	  try {
	   Log.v(LOG_KEY, "Adding Place...");
	    String vic = "5/48 Pirrama Road, Pyrmont";
	    String formtd_address = "5/48 Pirrama Road, Pyrmont NSW, Australia";
	    String formtd_phone_number = "(02) 9374 4000";
	    String myUrl = "http://maps.google.com/maps/place?cid=10281119596374313554";
	    String myWebsite = "http://www.google.com.au/";
	    
	   HttpPost post = new HttpPost(PLACE_ADD_URL);
	   String postBody = 
	      "{"+
	               "\"location\": {" +
	                 "\"lat\": " + lat + "," +
	                 "\"lng\": " + lng +
	                "}," + 
	                "\"accuracy\":50.0," +
	                "\"name\": \"" + name + "\"," +
	                "\"types\": [\"" + type + "\"]," +
	                "\"vicinity\":\""+ PlaceAdd.vic +"\","+
	                "\"formatted_address\":\""+ PlaceAdd.formtd_address +"\","+
	                "\"formatted_phone_number\":\""+ PlaceAdd.formtd_phone_number +"\","+
	                "\"url\":\""+ PlaceAdd.myUrl +"\","+
	                "\"website\":\""+ PlaceAdd.myWebsite +"\","+  
	                "\"language\": \"en\" " +
	                
	           "}"; 
	   
	   StringEntity se = new StringEntity(postBody,HTTP.UTF_8);
	   post.setEntity(se);
	   ResponseHandler<String> responseHandler=new BasicResponseHandler();
	   String responseBody = new DefaultHttpClient().execute(post, responseHandler);
	   JSONObject response = new JSONObject(responseBody);
	   Log.v(LOG_KEY, "Requested URL= " + PLACE_ADD_URL); 
	        
	   return response;

	  } catch (HttpResponseException e) {
	   Log.v(LOG_KEY, e.getResponse().parseAsString());
	   throw e;
	  }
	  
	  catch (IOException e) {
	   // TODO: handle exception
	   throw e;
	  }
	 }  
	 
	 public JSONObject deletePlace(String reference) throws Exception { 
	  try {
	   Log.v(LOG_KEY, "Deleting Place...");
	   HttpPost post = new HttpPost(PLACES_DELETE_URL);   
	   String postBody = "{\"reference\":\""+ reference +"\"}";
	   StringEntity se = new StringEntity(postBody,HTTP.UTF_8);
	   post.setEntity(se);
	   ResponseHandler<String> responseHandler = new BasicResponseHandler();
	   String responseBody = new DefaultHttpClient().execute(post, responseHandler);
	   JSONObject response = new JSONObject(responseBody);
	   Log.v(LOG_KEY, "Requested URL= " + PLACES_DELETE_URL); 
	        
	   return response;
	       
	  } catch (HttpResponseException e) {
	   Log.v(LOG_KEY, e.getResponse().parseAsString());
	   throw e;
	  }
	  catch (IOException e) { 
	   // TODO: handle exception
	   throw e;
	  }
	   
	 }
	 
	 public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
	      
	    return transport.createRequestFactory(new HttpRequestInitializer() {
	     public void initialize(HttpRequest request) {
	      GoogleHeaders headers = new GoogleHeaders();
	      headers.setApplicationName("Google-Places-DemoApp");
	      request.setHeaders(headers);
	      JsonHttpParser parser = new JsonHttpParser(new JacksonFactory()) ;
	      //JsonHttpParser.builder(new JacksonFactory());
	      //parser.jsonFactory = new JacksonFactory();
	      request.addParser(parser);
	     }
	  });
	 }
}
