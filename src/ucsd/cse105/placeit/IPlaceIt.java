package ucsd.cse105.placeit;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class IPlaceIt implements Parcelable{
	private String title = "";
	private String description = "";
	private int id;
	private int sched;

	public IPlaceIt(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	// Getters
	public int getID() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTitle() {
		if (title.length() == 0) {
			int min = Math.min(description.length(), 20);
			return description.substring(0, min);
		}
		return title;
	}

	public IPlaceIt(Parcel in){
		description = in.readString();
		title = in.readString();
		id = in.readInt();
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeString(title);
		dest.writeInt(id);
	}

	
	public void setSchedule(int sched) {
		this.sched = sched;
	}
}