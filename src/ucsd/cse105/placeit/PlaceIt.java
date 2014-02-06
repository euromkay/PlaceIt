package ucsd.cse105.placeit;

import android.widget.EditText;

public class PlaceIt {

	private int location;
	private String title, description;
	
	public PlaceIt(int location){
		this.location = location;
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
		return title;
	}
	public String getDescription(){
		return description;
	}
	public int getLocation(){
		return location;
	}
}
