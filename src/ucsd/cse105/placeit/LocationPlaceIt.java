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


	
	//sets the due date
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

	//sets the schedule
	public void setSchedule(int sched) {
		schedule = sched;
	}


	
	//gets duedate
	public Date getDueDate() {
		return dueDate;
	}

	
	

	//gets location
	public LatLng getLocation() {
		return location;
	}

	//gets schedule
	public int getSchedule() {
		return schedule;
	}
	
	

	// Parcelable Members
	
	//saves to a parcel
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

	//sets locatoin
	public void setLocation(LatLng location) {
		this.location = location;
		
	}
}
