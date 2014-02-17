package ucsd.cse105.placeit;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class FormActivity extends Activity implements OnClickListener{

	private EditText titleET, descriptionET;
	private int counter;
	
	
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_form);
		setupViews();
		
		if(hasId()){
			Log.d("FormActivity.onCreate", "Found an id,therefore going to modify it");
			PlaceIt p = Database.getPlaceIt(getId(), this);
			loadPlaceIt(p);
		
		}else
			Log.d("FormActivity.onCreate", "No id found");
		
	}
	private boolean hasId(){
		int id = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(ListActivity.ID_BUNDLE_KEY);;
		Log.d("FormActivity.onCreate", "id pulled from the bundle was : " + Integer.toString(id));
		return id != 0;
	}
	private int getId(){
		return getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(ListActivity.ID_BUNDLE_KEY);
	}
	
	private void loadPlaceIt(PlaceIt p){
		titleET.setText(p.getTitle());
		descriptionET.setText(p.getDescription());
		Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
		
		int value;
		Log.d("FormActivity.loadPlaceIt", "the schedule is " + Integer.toString(p.getSchedule()));
		switch(p.getSchedule()){
		case -1:
			value = 0;
			break;
			
		case 10:
			value = 1;
			break;
			
		case 60:
			value = 2;
			break;
			
		case 60*60:
			value = 3;
			break;
			
		case 60*60*24:
			value = 4;
			break;
		
		case 60*60*24*7:
			value = 5;
			break;
			
		default:
			throw new NullPointerException("Bad value in placeit");
		}
		
		spinner.setSelection(value);
	}
	protected void onResume(){
		super.onResume();
		setupViews();
		counter = 1;
	}
	private void setupViews(){
		if(titleET == null){
			titleET = (EditText) findViewById(R.id.form_title);
			descriptionET = (EditText) findViewById(R.id.form_description);
			findViewById(R.id.formCancelButton).setOnClickListener(this);
			findViewById(R.id.formSaveButton).setOnClickListener(this);
			
			Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.rules_formValues, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Apply the adapter to the spinner
			
			spinner.setAdapter(adapter);
			//spinner.setSelection(prefs.getInt(Constants.RULES_SK_ALIGNMENT, 3));
			
			
			//spinner.setOnItemSelectedListener(this);
		}
	}

	private static final String warning = "You haven't saved your changes, press again if you really want to go back.";
	public static final String COMPLETED_PLACEIT = "completedPlaceit";
	
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.formCancelButton){
			onBackPressed();
		}
		else if(arg0.getId() == R.id.formSaveButton){
			if(emptyForms()){
				makeToast("Please put something in the title or description!");
				return;
			}
			int id = nextID();
			Log.d("FormActivity.onClick", "the id is " + Integer.toString(id));
			PlaceIt placeIt = new PlaceIt(retrieveLocation(), id);
			placeIt.setTitle(titleET);
			placeIt.setDescription(descriptionET);
			
			Date dueDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dueDate);
			cal.add(Calendar.SECOND, 10);
			dueDate = cal.getTime();
			placeIt.setDueDate(dueDate);
			
			
			Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
			String s = (String) spinner.getSelectedItem();
			Log.d("FormActivity.onCLick", s + " is the spinner selection");
			
			placeIt.setSchedule(stringToSched(s));
			Log.d("FormActivity.loadOnClick", "the schedule is " + Integer.toString(placeIt.getSchedule()));
			
			
			Intent i = new Intent();
			i.putExtra(COMPLETED_PLACEIT, placeIt);
			setResult(RESULT_OK, i);
			
			Database.save(placeIt, this);
			
			clearForms();
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
	private int stringToSched(String s){
		if(s.equals("None"))
			return -1;
		
		if(s.equals("10 Seconds"))
			return 10;
		
		if(s.equals("1 Minue"))
			return 60;
		
		if(s.equals("1 Hour"))
			return 60 * 60;
		
		if(s.equals("1 Day"))
			return 60 * 60 * 24;
		
		if(s.equals("1 Week"))
			return 60 * 60 * 24 * 7;
		
		else
			throw new NullPointerException("Couldn't find the value in stringToSched");
	}
	
	
	private int nextID() {
		int id = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(ListActivity.ID_BUNDLE_KEY);;
		
		if(id == 0)
			return new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE);
		else
			return id;
	}
	
	
	private void clearForms(){
		titleET.setText("");
		descriptionET.setText("");
	}
	
	private void makeToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

	public void onBackPressed(){
		Log.d("FormActivity.onBackPressed", "back was pressed");
		boolean emptyForms = emptyForms();
		if(counter == 2 || emptyForms || hasId()){
			setResult(RESULT_CANCELED, new Intent());
			clearForms();
			finish();//exits and finishes the activity
		}
			
		else if(counter == 1 || emptyForms){
			counter++;
			makeToast(warning);
			return;
		}
	}

}
