package ucsd.cse105.placeit;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class IPlaceIt implements Parcelable{
	private String title = "";
	private String description = "";
	private int id;
	private boolean isCompleted = false;;
	private String user = "";

	//constructor
	public IPlaceIt(int id) {
		this.id = id;
	}
	
	//sets username
	public void setUser(String user){
		this.user = user;
	}
	
	//gets username
	public String getUser(){
		return this.user;
	}
	
	//sets whether placeit was completed or nto
	public void setIsCompleted(boolean isCompleted){
		this.isCompleted = isCompleted;
	}
	
	//gets whether placeit was completed or not
	public boolean getIsCompleted(){
		return this.isCompleted;
	}
	
	//sets title of placeit
	public void setTitle(String title) {
		this.title = title;
	}
	
	//sets description of placeit
	public void setDescription(String description) {
		this.description = description;
	}

	// Getters
	public int getID() {
		return id;
	}
	
	//gets descriptoin of place it
	public String getDescription() {
		return description;
	}
	
	//gets title of place it
	public String getTitle() {
		if (title.length() == 0) {
			int min = Math.min(description.length(), 20);
			return description.substring(0, min);
		}
		return title;
	}

	//constructor for parcelable
	public IPlaceIt(Parcel in){
		description = in.readString();
		title = in.readString();
		id = in.readInt();
	}
	
	//writes contents to parcel
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeString(title);
		dest.writeInt(id);
	}

	//nothing done here really
	public int describeContents() {

		return 0;
	}
	
}