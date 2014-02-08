package ucsd.cse105.placeit;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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


public class ListActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

	
	protected void onCreate(Bundle b){
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		setContentView(R.layout.activity_map);

		setUpList();
	}
	protected void onResume(){
		super.onResume();
		setUpList();
	}
	private void setUpList(){
		ArrayList<PlaceIt> list = Database.getAllPlaceIts();
		for(int i = 0; i < list.size(); i++)
			addPlaceItToList(list.get(i), i);
	}
	
	private void addPlaceItToList(PlaceIt p, int id){
		
		TextView tv = new TextView(this);
		tv.setText(p.getTitle());
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(23);
		tv.setId(3 * id);
		tv.setOnClickListener(this);
		
		LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		tv.setLayoutParams(param);
		
		
		
		
		
		CheckBox cb = new CheckBox(this);
		cb.setChecked(true);
		cb.setBackgroundColor(Color.RED);
		cb.setScaleX(0.75f);//hopefully makes the checkbox smaller
		cb.setScaleY(0.75f);
		cb.setId((3 * id) + 1);
		cb.setText(Long.toString(p.getID()));
		cb.setOnCheckedChangeListener(this);
		
		
		FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
		cb.setLayoutParams(param2);
		
		
		
		LinearLayout layout = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0);
		layout.setLayoutParams(param);
		layout.setId((3 * id) + 2);
		layout.addView(tv);
		layout.addView(cb);
		
		((LinearLayout) findViewById(R.id.listLayout)).addView(layout);
	}

	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		int id = arg0.getId();
		id -= 1;
		id /= 3;
		
		//gets the id from the checkbox
		CheckBox cb = (CheckBox) findViewById((3 * id) + 1);
		long placeItID = Long.parseLong(cb.getText().toString());
		Database.removePlaceIt(placeItID);
		
		View tv = findViewById(3 * id);
		LinearLayout layout = (LinearLayout) findViewById((3 * id) + 2);
		layout.removeView(cb);
		layout.removeView(tv);
	
	}
	public void onClick(View v) {
		
	}
}
