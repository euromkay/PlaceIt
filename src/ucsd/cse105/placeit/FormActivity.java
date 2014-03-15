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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class FormActivity extends Activity implements OnClickListener {

	private EditText titleET, descriptionET;
	private int counter;

	private Boolean hasLocation;

	//first method run in activity
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_form);

		hasLocation = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getBoolean(MapActivity.PLACEIT_HAS_POS);
		Log.d("FormActivity.onCreate", "has location" + hasLocation.toString());
		setupViews(hasLocation);

		if (hasId()) {
			Log.d("FormActivity.onCreate",
					"Found an id,therefore going to modify it");
			IPlaceIt p = getPlaceIt(getId());
			loadPlaceIt(p);
		} else
			Log.d("FormActivity.onCreate", "No id found");
	}


	private IPlaceIt p;
	private IPlaceIt getPlaceIt(int id){
		final String username = Database.getUsername(this);
		Thread t = new Thread(new Runnable(){
			public void run(){
				p = Database.getPlaceIt(getId(), username);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
	//returns whether a placeit was passed in or not
	private boolean hasId() {
		int id = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(
				ListActivity.ID_BUNDLE_KEY);
		;
		Log.d("FormActivity.onCreate", "id pulled from the bundle was : "
				+ Integer.toString(id));
		return id != 0;
	}

	//gets the id of the placeit
	private int getId() {
		return getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(
				ListActivity.ID_BUNDLE_KEY);
	}

	//loads the details of the placeit into the form
	private void loadPlaceIt(IPlaceIt placeIt) {
		titleET.setText(placeIt.getTitle());
		descriptionET.setText(placeIt.getDescription());
		if (placeIt instanceof LocationPlaceIt) {
			LocationPlaceIt p = (LocationPlaceIt) placeIt;
			Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
			int value;
			Log.d("FormActivity.loadPlaceIt",
					"the schedule is " + Integer.toString(p.getSchedule()));
			switch (p.getSchedule()) {
			case -1:
				value = 0;
				break;

			case 10:
				value = 1;
				break;

			case 60:
				value = 2;
				break;

			case 60 * 60:
				value = 3;
				break;

			case 60 * 60 * 24:
				value = 4;
				break;

			case 60 * 60 * 24 * 7:
				value = 5;
				break;

			default:
				throw new NullPointerException("Bad value in placeit");
			}
			spinner.setSelection(value);
		} else {
			CategoryPlaceIt p = (CategoryPlaceIt) placeIt;

			Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
			int position = categoryToId(p.getCategory(0));
			spinner.setSelection(position);

			spinner = (Spinner) findViewById(R.id.from_spinner2);
			position = categoryToId(p.getCategory(1));
			spinner.setSelection(position);

			spinner = (Spinner) findViewById(R.id.from_spinner3);
			position = categoryToId(p.getCategory(2));
			spinner.setSelection(position);
		}
	}

	//transforms the string category to the spinner position
	private int categoryToId(String s) {
		Log.d("Form Activity.categoryToId", "category is " + s);
		int i = 0;
		for (String t : categoryList) {
			if (t.equals(s))
				return i;
			i++;
		}
		throw new NullPointerException("Incorrect String");
	}

	protected void onResume() {
		super.onResume();
		if (hasLocation == null) {
			hasLocation = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getBoolean(MapActivity.PLACEIT_HAS_POS);
			setupViews(hasLocation);
		}
		counter = 1;
	}

	//sets up the views
	private void setupViews(boolean isLocation) {
		if (titleET == null) {
			titleET = (EditText) findViewById(R.id.form_title);
			descriptionET = (EditText) findViewById(R.id.form_description);
			findViewById(R.id.formCancelButton).setOnClickListener(this);
			findViewById(R.id.formSaveButton).setOnClickListener(this);

			Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			int array;
			if (isLocation)
				array = R.array.formValues;
			else {
				((TextView) findViewById(R.id.form_spinner_titleTV))
						.setText("Category");
				array = R.array.categoryValues;
				setUpRestOfSpinners();
			}
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, array,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// Apply the adapter to the spinner

			spinner.setAdapter(adapter);
			// spinner.setSelection(prefs.getInt(Constants.RULES_SK_ALIGNMENT,
			// 3));

			// spinner.setOnItemSelectedListener(this);
		}
	}

	//sets up spinners for category
	private void setUpRestOfSpinners() {
		Spinner spinner = (Spinner) findViewById(R.id.from_spinner2);
		spinner.setVisibility(View.VISIBLE);
		int array = R.array.categoryValues;
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner = (Spinner) findViewById(R.id.from_spinner3);
		spinner.setVisibility(View.VISIBLE);
		adapter = ArrayAdapter.createFromResource(this, array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private static final String warning = "You haven't saved your changes, press again if you really want to go back.";
	public static final String COMPLETED_PLACEIT = "completedPlaceit";

	//handles the button clickers save and cancel
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.formCancelButton) {
			onBackPressed();
		} else if (arg0.getId() == R.id.formSaveButton) {
			if (emptyForms()) {
				makeToast("Please put something in the title or description!");
				return;
			}
			int id = nextID();
			Log.d("FormActivity.onClick", "the id is " + Integer.toString(id));

			IPlaceIt placeIt;
			if ( ((Spinner) findViewById(R.id.from_spinner2)).getVisibility() != View.VISIBLE ) {
				LocationPlaceIt locPlaceIt = new LocationPlaceIt(id);
				placeIt = locPlaceIt;

				locPlaceIt.setTitle(titleET.getText().toString());
				locPlaceIt.setDescription(descriptionET.getText().toString());
				locPlaceIt.setUser(Database.getUsername(this));

				locPlaceIt.setLocation(retrieveLocation());
				Date dueDate = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(dueDate);
				cal.add(Calendar.SECOND, 10);
				dueDate = cal.getTime();
				locPlaceIt.setDueDate(dueDate);

				Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
				String s = (String) spinner.getSelectedItem();
				Log.d("FormActivity.onCLick", s + " is the spinner selection");

				locPlaceIt.setSchedule(stringToSched(s));
				Log.d("FormActivity.loadOnClick", "the schedule is "
								+ Integer.toString(locPlaceIt.getSchedule()));
				Database.save(locPlaceIt);
			} else {
				CategoryPlaceIt catPlaceIt = new CategoryPlaceIt(id);
				placeIt = catPlaceIt;

				catPlaceIt.setTitle(titleET.getText().toString());
				catPlaceIt.setDescription(descriptionET.getText().toString());
				catPlaceIt.setUser(Database.getUsername(this));

				Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
				String s1 = (String) spinner.getSelectedItem();

				spinner = (Spinner) findViewById(R.id.from_spinner2);
				String s2 = (String) spinner.getSelectedItem();

				spinner = (Spinner) findViewById(R.id.from_spinner3);
				String s3 = (String) spinner.getSelectedItem();

				catPlaceIt.setCategory(s1, s2, s3);
				Database.save(catPlaceIt);
			}

			Intent i = new Intent();
			i.putExtra(COMPLETED_PLACEIT, placeIt);
			setResult(RESULT_OK, i);

			clearForms();
			finish();
			return;
		}

	}

	//returns true if all forms are empty
	private boolean emptyForms() {
		if (isEmpty(titleET) && isEmpty(descriptionET))
			return true;
		return false;
	}

	//returns true if et is empty
	private boolean isEmpty(EditText et) {
		return et.getText().toString().length() == 0;
	}

	//gets the location from the intent
	private LatLng retrieveLocation() {
		Intent i = getIntent();
		Bundle b = i.getBundleExtra(MapActivity.PLACEIT_KEY);
		if (b.getBoolean(MapActivity.PLACEIT_HAS_POS))
			return new LatLng(b.getDouble(MapActivity.PLACEIT_LATITUDE),
					b.getDouble(MapActivity.PLACEIT_LONGITUDE));
		else
			return null;
	}

	//transforms spinner to string value
	private int stringToSched(String s) {
		if (s.equals("None"))
			return -1;

		if (s.equals("10 Seconds"))
			return 10;

		if (s.equals("1 Minute"))
			return 60;

		if (s.equals("1 Hour"))
			return 60 * 60;

		if (s.equals("1 Day"))
			return 60 * 60 * 24;

		if (s.equals("1 Week"))
			return 60 * 60 * 24 * 7;

		else
			throw new NullPointerException(
					"Couldn't find the value in stringToSched");
	}

	//gets a random id for the placeit
	private int nextID() {
		int id = getIntent().getBundleExtra(MapActivity.PLACEIT_KEY).getInt(
				ListActivity.ID_BUNDLE_KEY);
		;

		if (id == 0)
			return new Random(System.currentTimeMillis())
					.nextInt(Integer.MAX_VALUE);
		else
			return id;
	}

	//clears the title and description
	private void clearForms() {
		titleET.setText("");
		descriptionET.setText("");
	}

	//toast maker
	private void makeToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

	//handles on back pressed
	public void onBackPressed() {
		Log.d("FormActivity.onBackPressed", "back was pressed");
		boolean emptyForms = emptyForms();
		if (counter == 2 || emptyForms || hasId()) {
			setResult(RESULT_CANCELED, new Intent());
			clearForms();
			finish();// exits and finishes the activity
		}

		else if (counter == 1 || emptyForms) {
			counter++;
			makeToast(warning);
			return;
		}
	}

	private static final String[] categoryList = { "Empty", "accounting", "airport",
			"amusement_park", "aquarium", "art_gallery", "atm", "bakery",
			"bank", "bar", "beauty_salon", "bicycle_store", "book_store",
			"bowling_alley", "bus_station", "cafe", "campground", "car_dealer",
			"car_rental", "car_repair", "car_wash", "casino", "cemetery",
			"church", "city_hall", "clothing_store", "convenience_store",
			"courthouse", "dentist", "department_store", "doctor",
			"electrician", "electronics_store", "embassy", "establishment",
			"finance", "fire_station", "florist", "food", "funeral_home",
			"furniture_store", "gas_station", "general_contractor",
			"grocery_or_supermarket", "gym", "hair_care", "hardware_store",
			"health", "hindu_temple", "home_goods_store", "hospital",
			"insurance_agency", "jewelry_store", "laundry", "lawyer",
			"library", "liquor_store", "local_government_office", "locksmith",
			"lodging", "meal_delivery", "meal_takeaway", "mosque",
			"movie_rental", "movie_theater", "moving_company", "museum",
			"night_club", "painter", "park", "parking", "pet_store",
			"pharmacy", "physiotherapist", "place_of_worship", "plumber",
			"police", "post_office", "real_estate_agency", "restaurant",
			"roofing_contractor", "rv_park", "school", "shoe_store",
			"shopping_mall", "spa", "stadium", "storage", "store",
			"subway_station", "synagogue", "taxi_stand", "train_station",
			"travel_agency", "university", "veterinary_care", "zoo", };

}
