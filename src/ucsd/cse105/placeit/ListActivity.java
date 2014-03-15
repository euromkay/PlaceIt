package ucsd.cse105.placeit;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ListActivity extends Activity implements 
		OnClickListener {

	private ArrayList<Integer> list;

	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_list);

		if (list == null)
			setUpList();
	}

	protected void onResume() {
		super.onResume();
		if (list == null)
			setUpList();
	}

	private ArrayList<IPlaceIt> placeItList;
	private void setUpList() {
		findViewById(R.id.pullDownListButton).setOnClickListener(this);
		findViewById(R.id.backToMap).setOnClickListener(this);
		list = new ArrayList<Integer>();
		
		Thread t = new Thread(new Runnable(){
			public void run(){
				placeItList = Database.getAllPlaceIts();
			}
		});
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Log.d("ListActivity.setUpList", "Number of Placeits is :" + placeItList.size());
		
		
		
		for (int i = 0; i < placeItList.size(); i++){
			IPlaceIt p = placeItList.get(i);
			if(p.getUser().equals(Database.getUsername(this)) && !p.getIsCompleted())
				addPlaceItToList(p, i);
		}
	}

	protected void onPause() {


		ArrayList<Integer> listt = new ArrayList<Integer>();
		for (int i : list)
			listt.add(i);
		
		for (int i : listt)
			removeLayoutFromScreen(i);
		
		list = null;
		super.onPause();
	}

	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	private void addPlaceItToList(IPlaceIt p, int id) {

		TextView tv = new TextView(this);
		tv.setText(p.getTitle());
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(23);
		tv.setId(5 * id);
		tv.setOnClickListener(this);

		LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		tv.setLayoutParams(param);

		Button cb = new Button(this);
		cb.setBackgroundColor(Color.RED);
		cb.setScaleX(0.75f);// hopefully makes the checkbox smaller
		cb.setScaleY(0.75f);
		cb.setId((5 * id) + 1);
		cb.setText("C");
		cb.setOnClickListener(this);
		
		Button db = new Button(this);
		db.setBackgroundColor(Color.BLUE);
		db.setScaleX(0.75f);// hopefully makes the checkbox smaller
		db.setScaleY(0.75f);
		db.setId((5 * id) + 4);
		db.setText("D");
		db.setOnClickListener(this);

		Log.d("ListActivity.addPlaceItToList", Integer.toString((5 * id) + 1));
		list.add((5 * id) + 1);

		FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.RIGHT);
		cb.setLayoutParams(param2);

		Log.d("ListActivity.addingPlaceItToList",
				"id was " + Integer.toString(p.getID()));
		TextView longTV = new TextView(this);
		longTV.setVisibility(View.GONE);
		longTV.setId((5 * id) + 3);
		longTV.setText(Integer.toString(p.getID()));

		LinearLayout layout = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 0);
		layout.setLayoutParams(param);
		layout.setId((5 * id) + 2);
		layout.addView(tv);
		layout.addView(longTV);
		layout.addView(cb);
		layout.addView(db);
		
		if(p instanceof LocationPlaceIt)
			layout.setBackgroundColor(Color.LTGRAY);

		((LinearLayout) findViewById(R.id.listLayout)).addView(layout);
	}

	//delete is 4
	private IPlaceIt placeIt;
	private void deletePlaceIt(View arg0) {
		int id = arg0.getId();

		// removes it so theres not a double remove later on when the list is
		// cleared.

		// gets the id from the checkbox
		TextView cb = (TextView) findViewById(id - 1);

		if (cb == null)
			Log.d("ListActivity.onCheckedChange", "couldn't find the textview!");

		final int placeItID = Integer.parseInt(cb.getText().toString());

		Thread t = new Thread(new Runnable(){
			public void run(){
				placeIt = Database.getPlaceIt(placeItID);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (placeIt instanceof LocationPlaceIt){
			LocationPlaceIt p = (LocationPlaceIt) placeIt;

			// Check if PlaceIt is on a recurring schedule
			if (p.getSchedule() > 0) {
				// Update dueDate & save to DB
				p.setDueDate(p.getSchedule());
				Database.removePlaceIt(p);
			} 
		}
		Database.removePlaceIt(placeIt);

		// remove notification
		NotificationHelper helper = new NotificationHelper(this);
		helper.dismissNotificationByID(placeItID);
		list.remove((Object) (id - 3));
		removeLayoutFromScreen(id - 3);
	}

	private void completePlaceIt(View arg0) {
		int viewId = arg0.getId();


		// gets the id from the checkbox
		// c id = 1, hidden tv = 3
		TextView cb = (TextView) findViewById(viewId + 2);

		final int placeItID = Integer.parseInt(cb.getText().toString());

		Thread t = new Thread(new Runnable(){
			public void run(){
				placeIt = Database.getPlaceIt(placeItID);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		placeIt.setIsCompleted(true);
		
		if (placeIt instanceof LocationPlaceIt){
				LocationPlaceIt p = (LocationPlaceIt) placeIt;

			// Check if PlaceIt is on a recurring schedule
			if (p.getSchedule() > 0) {
				// Update dueDate & save to DB
				p.setDueDate(p.getSchedule());
			} else {
				// Remove Place-It from Database
				Database.save(p);

				// remove notification

			}
		}
		if(placeIt instanceof CategoryPlaceIt)
			Database.save((CategoryPlaceIt) placeIt);
		else if(placeIt instanceof LocationPlaceIt)
			Database.save((LocationPlaceIt) placeIt);
		else
			throw new NullPointerException();
		
		NotificationHelper helper = new NotificationHelper(this);
		helper.dismissNotificationByID(placeItID);
		
		list.remove((Object) (viewId));
		removeLayoutFromScreen(viewId);
	}
	
	private void removeLayoutFromScreen(int id) {
		// id is the id of the checkbox
		View tv = findViewById(id - 1);
		View cb = findViewById(id);
		View longTV = findViewById(id + 2);
		LinearLayout layout = (LinearLayout) findViewById(id + 1);
		layout.removeView(cb);
		layout.removeView(longTV);
		layout.removeView(tv);

		LinearLayout bigLayout = (LinearLayout) findViewById(R.id.listLayout);
		bigLayout.removeView(layout);
		list.remove((Object) id);
	}

	private IPlaceIt p;
	public void onClick(View v) {
		if(v.getId() == R.id.backToMap)
			onBackPressed();
		if(v.getId() == R.id.pullDownListButton){
			Intent i = new Intent(this, PullDownListActivity.class);
			startActivity(i);
			return;
		}
		
		if(v.getId() % 5 == 1){
			completePlaceIt(v);
			return;
		}
		if(v.getId() % 5 == 4){
			deletePlaceIt(v);
			return;
		}
			
		Intent i = new Intent(this, FormActivity.class);

		TextView tv = (TextView) findViewById(v.getId() + 3);
		final int id = Integer.parseInt(tv.getText().toString());

		Thread t = new Thread(new Runnable(){
			public void run(){
				p = Database.getPlaceIt(id);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle b = new Bundle();
		if(p instanceof LocationPlaceIt){
			LocationPlaceIt placeIt = (LocationPlaceIt) p;
			LatLng loc = placeIt.getLocation();
			b.putDouble(MapActivity.PLACEIT_LATITUDE, loc.latitude);
			b.putDouble(MapActivity.PLACEIT_LONGITUDE, loc.longitude);
			b.putBoolean(MapActivity.PLACEIT_HAS_POS, true);
		}
		b.putInt(ID_BUNDLE_KEY, id);

		i.putExtra(MapActivity.PLACEIT_KEY, b);

		ArrayList<Integer> newList = new ArrayList<Integer>();
		for (int j : list)
			newList.add(j);

		for (int j : newList) {
			Log.d("ListActivity.onClick", "Id being removed: " + Integer.toString(j));
			removeLayoutFromScreen(j);
		}

		startActivityForResult(i, 1);
	}

	public static final String ID_BUNDLE_KEY = "longIdKeyBundle";
}
