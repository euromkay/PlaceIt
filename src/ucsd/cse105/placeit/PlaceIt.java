package ucsd.cse105.placeit;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt implements Parcelable {

	private LatLng location;
	private String title = "";
	private String description = "";
	private Long id;
	
	public PlaceIt(LatLng location){
		this.location = location;
		Time now = new Time();
		now.setToNow();
		id = now.toMillis(true);
	}
	
	public void setTitle(EditText text){
		title = getText(text);
	}
	public void setDescription(EditText text){
		description = getText(text);
	}
	private String getText(EditText text){
		return text.getText().toString();
	}
	
	
	public String getTitle(){
		if(title.length() == 0)
			return description.substring(0, 20);
		return title;
	}
	public String getDescription(){
		return description;
	}
	public LatLng getLocation(){
		return location;
	}


	public int describeContents() {

		return 0;
	}


	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeString(title);
		dest.writeDouble(location.latitude);
		dest.writeDouble(location.longitude);
		dest.writeLong(id);
	}
	
	public PlaceIt(Parcel in){
		description = in.readString();
		title = in.readString();
		location = new LatLng(in.readDouble(), in.readDouble());
		id = in.readLong();
	}
	
	public long getID(){
		return id;
	}
	
	public static final Parcelable.Creator<PlaceIt> CREATOR = new Parcelable.Creator<PlaceIt>() {
		  public PlaceIt createFromParcel(Parcel in) {
			  return new PlaceIt(in); 
		  }
		   
		  public PlaceIt[] newArray(int size) {
		     return new PlaceIt[size];
		  	}
		  };
}
