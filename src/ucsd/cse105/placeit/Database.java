package ucsd.cse105.placeit;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Database {

	public static void save(PlaceIt p) {
		
	}
	
	public static float getLastZoom(){
		return 25;
	}

	public static void saveZoomLevel(float level) {
		
	}

	public static void savePosition(LatLng lat) {
		
	}
	
	public static LatLng getLastPosition(){
		return new LatLng(40.76793169992044,-73.98180484771729);
	}

	public static ArrayList<PlaceIt> getAllPlaceIts() {
		// TODO Auto-generated method stub
		return new ArrayList<PlaceIt>();
	}


	public static void removePlaceIt(long placeItID) {
		// TODO Auto-generated method stub
		
	}
}
