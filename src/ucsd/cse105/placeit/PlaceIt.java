package ucsd.cse105.placeit;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt implements Parcelable {

	// Fields
	private LatLng location;
	private String title = "";
	private String description = "";
	private int id;
	private Date dueDate;
	private int schedule;

	// Constructor
	public PlaceIt(LatLng location, int id) {
		this.location = location;
		this.id = id;
	}

	// Setters
	public void setTitle(EditText text) {
		title = getText(text);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(EditText text) {
		description = getText(text);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	//Sets the dueDate to the current system time plus
	//the number of seconds given.
	public void setDueDate(int secondsFromCurrentTime){
		Date dueDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dueDate);
		cal.add(Calendar.SECOND, secondsFromCurrentTime);
		setDueDate(cal.getTime());
	}

	public void setSchedule(int sched) {
		schedule = sched;
	}

	// Getters
	public int getID() {
		return id;
	}

	private String getText(EditText text) {
		return text.getText().toString();
	}

	public String getDescription() {
		return description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public String getTitle() {
		if (title.length() == 0) {
			int min = Math.min(description.length(), 20);
			return description.substring(0, min);
		}
		return title;
	}

	public LatLng getLocation() {
		return location;
	}

	public int getSchedule() {
		return schedule;
	}
	
	

	// Parcelable Members
	public int describeContents() {

		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeString(title);
		dest.writeDouble(location.latitude);
		dest.writeDouble(location.longitude);
		dest.writeInt(id);

		dest.writeInt(schedule);

		dest.writeLong(dueDate.getTime());
	}

	//Special constructor for Parcel
	public PlaceIt(Parcel in) {
		description = in.readString();
		title = in.readString();
		location = new LatLng(in.readDouble(), in.readDouble());
		id = in.readInt();

		schedule = in.readInt();

		long time = in.readLong();
		dueDate = new Date();
		dueDate.setTime(time);
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
