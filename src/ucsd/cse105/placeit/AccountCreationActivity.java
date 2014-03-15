package ucsd.cse105.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AccountCreationActivity extends Activity implements OnClickListener{

	protected void onCreate(Bundle b){
		super.onCreate(b);
		Log.d("LoginActivity.onCreate", "going through onCreate");
		
		if(hasLoginCreds()){
			String username = getUsername();
			startMapActivity(username);
		}
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		findViewById(R.id.login_button).setOnClickListener(this);
	}
	
	private boolean hasLoginCreds(){
		return Database.checkLoginCredentials(this);
	}
	
	private String getUsername(){
		return Database.getUsername(this);
	}
	
	public static final String USERNAME_KEY = "user name key";
	private void startMapActivity(String username){
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(USERNAME_KEY, username);
		startActivity(i);
		
		finish();
		
	}

	@Override
	public void onClick(View v) {
		String username = ((TextView) findViewById(R.id.login_username)).getText().toString();
		EditText pwField = (EditText) findViewById(R.id.login_password);
		String password = pwField.getText().toString();
		
		if(validCreds(username, password)){
			startMapActivity(username);
		}
		else{
			pwField.setText("");
			Toast.makeText(this, "Incorrect password/username combination", Toast.LENGTH_LONG).show();
		}
			
			
	}
	
	private boolean validCreds(String username, String password){
		
		return Database.checkLogin(username, password, this);
	}
	
}
