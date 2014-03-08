package ucsd.cse105.placeit;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class Distance {
	
	private static float CONVERSION_CONSTANT = 1609.34f;

	// Checks to see if given location is within given range, in miles.
	public static boolean isWithinRange(Context context, LatLng position,
			float milesRange) {
		// Convert LatLng to Location
		Location location = new Location("Test");
		location.setLatitude(position.latitude);
		location.setLongitude(position.longitude);

		float miles = milesTo(context, location);
		return miles >= 0 && miles <= milesRange;
	}

	private static float milesTo(Context context, Location location) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to
		// go to the settings
		if (enabled) {

			//Criteria criteria = new Criteria();
			//String provider = locationManager.getBestProvider(criteria, false);
			Location currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			float distanceTo = currentLocation.distanceTo(location);
			return distanceTo / CONVERSION_CONSTANT;
		}
		else
		{
			return -1; //Location services not enabled
		}
	}

}
