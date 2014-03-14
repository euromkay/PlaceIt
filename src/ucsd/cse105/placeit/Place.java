package ucsd.cse105.placeit;

import java.util.List;

/*
 *  All Bean classes for Search Place, Add Place and Place details look like:
 */

public class Place {
	 //@Key
	 public String id;
	 
	 //@Key
	 public String name;
	 
	 //@Key
	 public String reference;
	 
	 //@Key
	 public String types[];
	 
	 //@Key
	 public String international_phone_number;
	 
	 //@Key
	 public String vicinity;
	 
	 //@Key
	 public String formatted_address;
	 
	 //@Key
	 public String url;
	 
	 //@Key
	 public String rating;
	 
	 //@Key
	 public String website;
	 
	 //@Key
	 public List<Place> address_components;
	 
	 //@Key
	 public String long_name;
	 
	 //@Key
	 public String short_name;
	 
	   
	 //@Key
	 public PlaceGeometry geometry;
	 
	 public static class PlaceGeometry {
	  //@Key
	  public Location location;
	 }

	 public static class Location {
	  //@Key
	  public double lat;

	  //@Key
	  public double lng;
	 }
	 
	 @Override
	 public String toString() {
	  return name + " - " + id + " - " + reference;
	 }
}
