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

public class LoginActivity extends Activity implements OnClickListener{

	//setup method
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

		findViewById(R.id.createAccountButton).setOnClickListener(this);
	}
	
	//if the username has already logged in
	private boolean hasLoginCreds(){
		return Database.checkLoginCredentials(this);
	}
	
	//queries database for username
	private String getUsername(){
		return Database.getUsername(this);
	}
	
	//starts the map activity
	public static final String USERNAME_KEY = "user name key";
	private void startMapActivity(String username){
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(USERNAME_KEY, username);
		startActivity(i);
		
		finish();
		
	}

	//handles the button clicks, accoutn creation, or login
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.login_button){
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
		else if(v.getId() == R.id.createAccountButton){
			Intent i = new Intent(this, AccountCreationActivity.class);
			startActivity(i);
		}
			
	}
	
	//returns true if database recognizes database
	private boolean b;
	private boolean validCreds(final String username, final String password){
		Thread t = new Thread(new Runnable(){
			public void run(){
				b = Database.getAccount(username, password);
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(b)
			Database.saveUsername(username, this);
		return b;
	}
	
}
