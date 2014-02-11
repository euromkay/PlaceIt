package ucsd.cse105.placeit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Database {

	private static final String FILE_ZOOM = "zoom_file";
	public static float getLastZoom(Activity a){
		Log.d("Database.getLastZoom", "entering getLastZoom");
		try {
			FileInputStream fis = a.openFileInput(FILE_ZOOM);
			String[] content = reader(fis);
			Log.d("Database.getLastZoom", "returning for zoom level " + content[0]);
			return Float.parseFloat(content[0]);
		} catch (FileNotFoundException e) {
			Log.d("Database.getLastZoom", "File not found");
			e.printStackTrace();
		}
		//default return value if file isn't found
		return 20;
		
	}
	public static void saveZoomLevel(float level, MapActivity a) {
		String data = Float.toString(level);
		Log.d("Database.saveZoomLevel", data + " is the saved zoom level");
		try {
			FileOutputStream fos = a.openFileOutput(FILE_ZOOM, Context.MODE_PRIVATE);
			writer(fos, new String[]{data});
		} catch (FileNotFoundException e) {
			Log.d("Database", "something went wrong?");
			e.printStackTrace();
		}
			
		
	}

	
	
	
	
	
	public static void savePosition(LatLng lat) {
		
	}
	public static LatLng getLastPosition(){
		return new LatLng(40.76793169992044,-73.98180484771729);
	}

	
	
	
	
	
	
	private static final String FILE_PLACEITS = "place_it_file";
	public static ArrayList<PlaceIt> getAllPlaceIts(Activity a) {
		try {
			FileInputStream fis = a.openFileInput(FILE_PLACEITS);
			String[] content = reader(fis);
			
			int position = 0; //what part of the content you're in
			int count = Integer.parseInt(content[position]);

			ArrayList<PlaceIt> list = new ArrayList<PlaceIt>(count);
			for(int i = 0; i < count; i++){
				String title = content[position++];
				String description = content[position++];
				String id_long = content[position++];
				String lat_double = content[position++];
				String long_double = content[position++];
				
				
				
				LatLng location = new LatLng(Double.parseDouble(lat_double), Double.parseDouble(long_double));
				
				PlaceIt p = new PlaceIt(location);
				p.setTitle(title);
				p.setDescription(description);
				p.setID(Long.parseLong(id_long));
				
				list.add(p);
			}
			
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<PlaceIt>();
	}


	public static void removePlaceIt(long placeItID, Activity a) {
		ArrayList<PlaceIt> list = getAllPlaceIts(a);
		for(PlaceIt p: getAllPlaceIts(a))
			if(p.getID() == (placeItID))
				list.remove(p);
		writePlaceIts(list, a);
	}
	public static PlaceIt getPlaceItByPos(LatLng pos, Activity a){
		for(PlaceIt p: getAllPlaceIts(a))
			if(p.getLocation().equals(pos))
				return p;

		throw new NullPointerException();
	}
	public static void save(PlaceIt p, Activity a) {
		ArrayList<PlaceIt> list = getAllPlaceIts(a);
		list.add(p);
		writePlaceIts(list, a);
	}
	private static void writePlaceIts(ArrayList<PlaceIt> list, Activity a){
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add(Integer.toString(list.size()));
		
		
		for(PlaceIt p: list){
			stringList.add(p.getTitle());
			stringList.add(p.getDescription());
			stringList.add(Long.toString(p.getID()));
			stringList.add(Double.toString(p.getLocation().latitude));
			stringList.add(Double.toString(p.getLocation().longitude));
		}
		
		String[] array = toStringArray(stringList.toArray());
		
		try {
			writer(a.openFileOutput(FILE_PLACEITS, Context.MODE_PRIVATE), array);
		} catch (FileNotFoundException e) {
			Log.d("Database", "Something went wrong in saving PlaceIt");
		}
	}
	private static String[] toStringArray(Object[] input){
		String[] array = new String[input.length];
		for(int i = 0; i < array.length; i++)
			array[i] = (String) input[i];
		return array;
	}
	
	
	
	
	private static void writer(FileOutputStream fos, String[] data){
		try{
			OutputStreamWriter out = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(out);
			
			for(String s: data)
				writer.write(s);
			writer.close();
			out.flush();
			out.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static String[] reader(FileInputStream fis){
		try {
			InputStreamReader input = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(input);
			
			ArrayList<String> list = new ArrayList<String>();
			while(true){
				String s = reader.readLine();
				Log.d("Database", " Read line: " + s);
				if(s == null)
					break;
				list.add(s);
			}
			
			if(list.size() == 0)
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
}
