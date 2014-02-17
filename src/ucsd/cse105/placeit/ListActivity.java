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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class ListActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

	private ArrayList<Integer> list;
	protected void onCreate(Bundle b){
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_list);

		if(list == null)
			setUpList();
	}
	protected void onResume(){
		super.onResume();
		if(list == null)
			setUpList();
	}
	private void setUpList(){
		list = new ArrayList<Integer>();
		ArrayList<PlaceIt> list = Database.getAllPlaceIts(this);
		Log.d("ListActivity.setUpList", "Number of Placeits is :" + list.size());
		for(int i = 0; i < list.size(); i++)
			addPlaceItToList(list.get(i), i);
	}
	protected void onPause(){
		for(int i: list)
			removeLayoutFromScreen(i);
		list = null;
		super.onPause();
	}
	public void onBackPressed(){
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);     
		finish();
	}
	
	private void addPlaceItToList(PlaceIt p, int id){
		
		TextView tv = new TextView(this);
		tv.setText(p.getTitle());
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(23);
		tv.setId(4 * id);
		tv.setOnClickListener(this);
		
		
		
		LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		tv.setLayoutParams(param);
		
		
		
		
		
		CheckBox cb = new CheckBox(this);
		cb.setChecked(true);
		cb.setBackgroundColor(Color.RED);
		cb.setScaleX(0.75f);//hopefully makes the checkbox smaller
		cb.setScaleY(0.75f);
		cb.setId((4 * id) + 1);
		cb.setOnCheckedChangeListener(this);
		
		Log.d("ListActivity.addPlaceItToList", Integer.toString((4*id)+1));
		list.add((4 * id) + 1);
		
		
		FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
		cb.setLayoutParams(param2);
		
		Log.d("ListActivity.addingPlaceItToList", "id was " + Integer.toString(p.getID()));
		TextView longTV = new TextView(this);
		longTV.setVisibility(View.GONE);
		longTV.setId((4 * id) + 3);
		longTV.setText(Integer.toString(p.getID()));
		
		
		LinearLayout layout = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0);
		layout.setLayoutParams(param);
		layout.setId((4 * id) + 2);
		layout.addView(tv);
		layout.addView(longTV);
		layout.addView(cb);
		
		((LinearLayout) findViewById(R.id.listLayout)).addView(layout);
	}

	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		int id = arg0.getId();
		
		//removes it so theres not a double remove later on when the list is cleared.
		
		//gets the id from the checkbox
		TextView cb = (TextView) findViewById(id + 2);
		
		if(cb == null)
			Log.d("ListActivity.onCheckedChange", "couldn't find the textview!");
		
		int placeItID = Integer.parseInt(cb.getText().toString());

		list.remove((Object) (id));
		Database.removePlaceIt(placeItID, this);
		
		removeLayoutFromScreen(id);
	}
	private void removeLayoutFromScreen(int id){
		//id is the id of the checkbox
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
		for(int j: list)
			newList.add(j);
		
		for(int j: newList){
			Log.d("ListActivity.onClick", "Id being removed: " + Integer.toString(j));
			removeLayoutFromScreen(j);
		}
		
		startActivityForResult(i, 1);
	}
	public static final String ID_BUNDLE_KEY = "longIdKeyBundle";
}
