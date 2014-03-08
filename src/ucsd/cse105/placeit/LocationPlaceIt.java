package ucsd.cse105.placeit;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class LocationPlaceIt extends IPlaceIt {

	// Fields
	private LatLng location;
	private Date dueDate;
	private int schedule;

	// Constructor
	public LocationPlaceIt(int id) {
		super(id);
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


	

	public Date getDueDate() {
		return dueDate;
	}

	
	

	
	public LatLng getLocation() {
		return location;
	}

	public int getSchedule() {
		return schedule;
	}
	
	

	// Parcelable Members
	

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeDouble(location.latitude);
		dest.writeDouble(location.longitude);
		dest.writeInt(schedule);
		dest.writeLong(dueDate.getTime());
	}

	//Special constructor for Parcel
	public LocationPlaceIt(Parcel in) {
		super(in);
		location = new LatLng(in.readDouble(), in.readDouble());

		schedule = in.readInt();

		long time = in.readLong();
		dueDate = new Date();
		dueDate.setTime(time);
	}

	public static final Parcelable.Creator<LocationPlaceIt> CREATOR = new Parcelable.Creator<LocationPlaceIt>() {
		public LocationPlaceIt createFromParcel(Parcel in) {
			return new LocationPlaceIt(in);
		}

		public LocationPlaceIt[] newArray(int size) {
			return new LocationPlaceIt[size];
		}
	};

	public void setLocation(LatLng location) {
		this.location = location;
		
	}
}
