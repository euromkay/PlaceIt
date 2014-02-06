package ucsd.cse105.placeit;

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
	
	private int location;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_form);
		
		setupViews();
		location = extractLocation(getIntent());
	}
	private int extractLocation(Intent i){
		return 0;
	}
	
	protected void onResume(){
		counter = 1;
	}
	
	private void setupViews(){
		titleET = (EditText) findViewById(R.id.form_title);
		descriptionET = (EditText) findViewById(R.id.form_description);
		findViewById(R.id.formCancelButton).setOnClickListener(this);
		findViewById(R.id.formSaveButton).setOnClickListener(this);
	}

	private static final String warning = "You haven't saved your changes, press again if you really want to go back.";
	public void onClick(View arg0) {
		switch(arg0.getId()){
		
		case R.id.formCancelButton:
			if(counter == 2 || emptyForms())
				break;//exits and finishes the activity
			
			if(counter == 1 || emptyForms()){
				makeToast(warning);
				return;
			}
				
		
			
		case R.id.formSaveButton:
			if(emptyForms()){
				makeToast("Please put something in the title or description!");
				break;
			}
			PlaceIt p = new PlaceIt(location);
			p.setTitle(titleET);
			p.setDescription(descriptionET);
			
			Database.save(p);
			
			finish();
			
			return;
		}
		finish();
		
	}
	private boolean emptyForms(){
		if(isEmpty(titleET) && isEmpty(descriptionET))
			return true;
		return false;
	}
	private boolean isEmpty(EditText et){
		return et.getText().toString().length() == 0;
	}

	private void makeToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
}
