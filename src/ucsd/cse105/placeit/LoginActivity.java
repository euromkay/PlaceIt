package ucsd.cse105.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class LoginActivity extends Activity implements OnClickListener{

	protected void onCreate(Bundle b){
		super.onCreate(b);
		
		if(hasLoginCreds()){
			String username = getUsername();
			startMapActivity(username);
		}
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
	}
	
	private boolean hasLoginCreds(){
		return false;
	}
	
	private String getUsername(){
		return null;
	}
	
	public static final String USERNAME_KEY = "user name key";
	private void startMapActivity(String username){
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(USERNAME_KEY, username);
		startActivity(i);
		
		finish();
		
	}
	
}
