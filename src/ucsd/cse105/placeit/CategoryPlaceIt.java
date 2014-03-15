package ucsd.cse105.placeit;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryPlaceIt extends IPlaceIt{

	private static final int NUMBER_OF_CATEGORIES = 3;
	
	private String[] category;
	
	//constructor
	public CategoryPlaceIt(int id) {
		super(id);
		category = new String[NUMBER_OF_CATEGORIES];
	}
	
	//constructor from parcelable
	public CategoryPlaceIt(Parcel in){
		super(in);
		category = new String[NUMBER_OF_CATEGORIES];
		for(int i = 0; i < NUMBER_OF_CATEGORIES; i++)
			category[i] = in.readString();
	}

	//so you can pass around objects through activities
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		for(int i = 0; i < NUMBER_OF_CATEGORIES; i++)
			category[i] = dest.readString();
	}
	
	//gets category with index
	public String getCategory(int i){
		if(-1 < i  && i < NUMBER_OF_CATEGORIES)
			return category[i];
		else 
			throw new IllegalArgumentException();
	}
	
	//sets category with index
	public void setCategory(String ... category){
		for(int i = 0; i < NUMBER_OF_CATEGORIES; i++)
			this.category[i] = category[i];
	}
	
	public static final Parcelable.Creator<CategoryPlaceIt> CREATOR = new Parcelable.Creator<CategoryPlaceIt>() {
		public CategoryPlaceIt createFromParcel(Parcel in) {
			return new CategoryPlaceIt(in);
		}

		public CategoryPlaceIt[] newArray(int size) {
			return new CategoryPlaceIt[size];
		}
	};
}
