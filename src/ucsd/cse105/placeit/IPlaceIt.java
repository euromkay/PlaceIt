package ucsd.cse105.placeit;

import com.google.android.gms.maps.model.LatLng;

public interface IPlaceIt {

	public abstract void setTitle(String title);

	public abstract void setDescription(String description);

	// Getters
	public abstract int getID();

	public abstract String getDescription();

	public abstract String getTitle();

	public abstract LatLng getLocation();

}