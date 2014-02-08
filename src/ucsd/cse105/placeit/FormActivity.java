package ucsd.cse105.placeit;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class FormActivity extends Activity implements OnClickListener{

	private EditText titleET, descriptionET;
	private int counter;
	
	
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_form);
		setupViews();
		
		if(b != null)
			loadPlaceIt(b);
	}
	
	protected void onResume(){
		super.onResume();
		counter = 1;
	}
	private void loadPlaceIt(Bundle b){
		
	}
	
	private void setupViews(){
		titleET = (EditText) findViewById(R.id.form_title);
		descriptionET = (EditText) findViewById(R.id.form_description);
		findViewById(R.id.formCancelButton).setOnClickListener(this);
		findViewById(R.id.formSaveButton).setOnClickListener(this);
	}

	private static final String warning = "You haven't saved your changes, press again if you really want to go back.";
	public static final String COMPLETED_PLACEIT = "completedPlaceit";
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.formCancelButton){
			boolean emptyForms = emptyForms();
			if(counter == 2 || emptyForms){
				setResult(RESULT_CANCELED, new Intent());
				finish();//exits and finishes the activity
			}
				
			else if(counter == 1 || emptyForms){
				counter++;
				makeToast(warning);
				return;
			}
		}
		else if(arg0.getId() == R.id.formSaveButton){
			if(emptyForms()){
				makeToast("Please put something in the title or description!");
			}
			PlaceIt placeIt = new PlaceIt(retrieveLocation());
			placeIt.setTitle(titleET);
			placeIt.setDescription(descriptionET);
			
			Intent i = new Intent();
			i.putExtra(COMPLETED_PLACEIT, placeIt);
			setResult(RESULT_OK, i);
			
			Database.save(placeIt);
			finish();
			
			return;
		}
	
	}
	private boolean emptyForms(){
		if(isEmpty(titleET) && isEmpty(descriptionET))
			return true;
		return false;
	}
	private boolean isEmpty(EditText et){
		return et.getText().toString().length() == 0;
	}
	private LatLng retrieveLocation(){
		Intent i = getIntent();
		Bundle b = i.getBundleExtra(MapActivity.PLACEIT_KEY);
		return new LatLng(b.getDouble(MapActivity.PLACEIT_LATITUDE), b.getDouble(MapActivity.PLACEIT_LONGITUDE));
	}

	private void makeToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
}
