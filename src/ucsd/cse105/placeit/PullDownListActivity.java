package ucsd.cse105.placeit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PullDownListActivity extends Activity implements OnCheckedChangeListener {

	private ArrayList<Integer> list;

	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_pulllist);

		if (list == null)
			setUpList();
	}

	protected void onResume() {
		super.onResume();
		if (list == null)
			setUpList();
	}

	private ArrayList<IPlaceIt> pList; 
	private void setUpList() {
		list = new ArrayList<Integer>();
		final String username = Database.getUsername(this);
		Thread t = new Thread(new Runnable(){
			public void run(){
				pList = Database.getAllPlaceIts(username);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Log.d("ListActivity.setUpList", "Number of Placeits is :" + list.size());
		for (int i = 0; i < pList.size(); i++){
			IPlaceIt p = pList.get(i);
			if(p.getIsCompleted())
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
		tv.setId(4 * id);

		LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		tv.setLayoutParams(param);

		CheckBox cb = new CheckBox(this);
		cb.setChecked(true);
		cb.setBackgroundColor(Color.RED);
		cb.setScaleX(0.75f);// hopefully makes the checkbox smaller
		cb.setScaleY(0.75f);
		cb.setId((4 * id) + 1);
		cb.setOnCheckedChangeListener(this);

		Log.d("ListActivity.addPlaceItToList", Integer.toString((4 * id) + 1));
		list.add((4 * id) + 1);

		FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.RIGHT);
		cb.setLayoutParams(param2);

		Log.d("ListActivity.addingPlaceItToList",
				"id was " + Integer.toString(p.getID()));
		TextView longTV = new TextView(this);
		longTV.setVisibility(View.GONE);
		longTV.setId((4 * id) + 3);
		longTV.setText(Integer.toString(p.getID()));

		LinearLayout layout = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 0);
		layout.setLayoutParams(param);
		layout.setId((4 * id) + 2);
		layout.addView(tv);
		layout.addView(longTV);
		layout.addView(cb);
		
		if(p instanceof LocationPlaceIt)
			layout.setBackgroundColor(Color.LTGRAY);

		((LinearLayout) findViewById(R.id.listLayout)).addView(layout);
	}

	private IPlaceIt p;
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		int id = arg0.getId();

		// removes it so theres not a double remove later on when the list is
		// cleared.

		// gets the id from the checkbox
		TextView cb = (TextView) findViewById(id + 2);

		if (cb == null)
			Log.d("ListActivity.onCheckedChange", "couldn't find the textview!");

		final int placeItID = Integer.parseInt(cb.getText().toString());

		final String username = Database.getUsername(this);
		Thread t = new Thread(new Runnable(){
			public void run(){
				p = Database.getPlaceIt(placeItID, username);
			}
		});
		
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Check if PlaceIt is on a recurring schedule
		if (p instanceof LocationPlaceIt && ((LocationPlaceIt) p).getSchedule() > 0) {
			// Update dueDate & save to DB
			((LocationPlaceIt) p).setDueDate(((LocationPlaceIt) p).getSchedule());
			Database.save((LocationPlaceIt) p);
		} else {
			// Remove Place-It from Database
			Database.removePlaceIt(p);

			// remove notification
			NotificationHelper helper = new NotificationHelper(this);
			helper.dismissNotificationByID(placeItID);

		}
		list.remove((Object) (id));
		removeLayoutFromScreen(id);
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
/*
	public void onClick(View v) {
		Intent i = new Intent(this, FormActivity.class);

		TextView tv = (TextView) findViewById(v.getId() + 3);
		int id = Integer.parseInt(tv.getText().toString());

		LatLng loc = Database.getPlaceIt(id, this).getLocation();

		Bundle b = new Bundle();
		b.putDouble(MapActivity.PLACEIT_LATITUDE, loc.latitude);
		b.putDouble(MapActivity.PLACEIT_LONGITUDE, loc.longitude);
		b.putInt(ID_BUNDLE_KEY, id);

		i.putExtra(MapActivity.PLACEIT_KEY, b);

		ArrayList<Integer> newList = new ArrayList<Integer>();
		for (int j : list)
			newList.add(j);

		for (int j : newList) {
			Log.d("ListActivity.onClick",
					"Id being removed: " + Integer.toString(j));
			removeLayoutFromScreen(j);
		}

		startActivityForResult(i, 1);
	}*/

	public static final String ID_BUNDLE_KEY = "longIdKeyBundle";

}
