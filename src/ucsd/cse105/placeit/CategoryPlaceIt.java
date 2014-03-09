package ucsd.cse105.placeit;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryPlaceIt extends IPlaceIt{

	private String category1, category2, category3;
	
	public CategoryPlaceIt(int id) {
		super(id);
	}
	
	public CategoryPlaceIt(Parcel in){
		super(in);
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(category1);
		dest.writeString(category2);
		dest.writeString(category3);
	}
	
	public void setCategory(String category1, String category2, String category3){
		if(!category1.equals("Empty"))
			this.category1 = category1;
		if(!category2.equals("Empty"))
			this.category2 = category2; 
		if(!category2.equals("Empty"))
			this.category3 = category3;
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
