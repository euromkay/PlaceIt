package ucsd.cse105.placeit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GoogleAppEngine {
	private static final String FILE_PLACEITS = "place_it_file";
	//returns an active list of all the placeits
	public static ArrayList<PlaceIt> getAllPlaceIts(Context a) {
		try {
			FileInputStream fis = a.openFileInput(FILE_PLACEITS);
			String[] content = reader(fis);
			
			int position = 0; //what part of the content you're in
			int count = Integer.parseInt(content[position++]);//how many placeits there are in the file

			ArrayList<PlaceIt> list = new ArrayList<PlaceIt>(count);
			for(int i = 0; i < count; i++){
				Log.d("Database.getAllPlaceIts", "# " + Integer.toString(i) + " placeit being loaded");
				String title = content[position++];
				
				Log.d("Database.getAllPlaceIts", "title of placeit being created: " + title);
				String description = content[position++];
				
				Log.d("Database.getAllPlaceIts", "description of placeit being created: " + description);
				String id_int = content[position++];
				
				Log.d("Database.getAllPlaceIts", "id of placeit being created: " + id_int);
				String lat_double = content[position++];
				
				Log.d("Database.getAllPlaceIts", "latitude of placeit being created: " + lat_double);
				String long_double = content[position++];
				
				Log.d("Database.getAllPlaceIts", "longitude of placeit being created: " + long_double);
				String dueDate_Date = content[position++];
				
				Log.d("Database.getAllPlaceIts", "dueDate of placeit being created: " + dueDate_Date);
				String schedule_String = content[position++];
				
				LatLng location = new LatLng(Double.parseDouble(lat_double), Double.parseDouble(long_double));
				
				PlaceIt p = new PlaceIt(location, Integer.parseInt(id_int));
				p.setTitle(title);
				p.setDescription(description);
				try {
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
					p.setDueDate(df.parse(dueDate_Date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				p.setSchedule(Integer.parseInt(schedule_String));
				list.add(p);
			}
			
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<PlaceIt>();
	}
}
