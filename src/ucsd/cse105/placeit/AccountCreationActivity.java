package ucsd.cse105.placeit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class AccountCreationActivity extends Activity implements OnClickListener{

	//setup
	protected void onCreate(Bundle b){
		super.onCreate(b);
		Log.d("LoginActivity.onCreate", "going through onCreate");
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_account_creation);
		
		findViewById(R.id.creation_button).setOnClickListener(this);
	}
	
	public static final String USERNAME_KEY = "user name key";

	//pushes changes to cloud
	@Override
	public void onClick(View v) {
		String username = ((TextView) findViewById(R.id.creation_username)).getText().toString();
		EditText pwField = (EditText) findViewById(R.id.creation_password);
		String password = pwField.getText().toString();
		
		Database.saveAccount(username, password );
			
		finish();
	}
	
	
}
