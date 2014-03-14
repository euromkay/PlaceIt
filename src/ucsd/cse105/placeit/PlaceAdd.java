package ucsd.cse105.placeit;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * Now call PlaceRequest.java from PlaceAdd.java to add you actual location into Google Map.
 */
/*
public class PlaceAdd extends Activity {
	 static String placeName;
	 static String vic;
	 static String formtd_address;
	 static String formtd_phone_number;
	 static String myUrl;
	 static String myWebsite;
	 
	 ProgressDialog progressDialog;
	 
	 public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	      //set up dialog
	      Dialog dialog = new Dialog(this);
	      dialog.setContentView(R.layout.addplace);
	      dialog.setTitle("Enter Place Details");
	      dialog.setCancelable(false);
	      
	      final EditText name = (EditText) dialog.findViewById(R.id.place_title);
	      final EditText vicinity = (EditText) dialog.findViewById(R.id.editText1);
	      final EditText formatted_address = (EditText) dialog.findViewById(R.id.editText2);
	      final EditText formatted_phone_number = (EditText) dialog.findViewById(R.id.editText3);
	      final EditText url = (EditText) dialog.findViewById(R.id.editText4);
	      final EditText website = (EditText) dialog.findViewById(R.id.editText5);
	     
	    
	     
	      Button ok = (Button) dialog.findViewById(R.id.btn_ok);
	      ok.setOnClickListener(new OnClickListener() {
	          public void onClick(View v) {
	           placeName = name.getText().toString();
	           vic = vicinity.getText().toString();
	              formtd_address = formatted_address.getText().toString();
	              formtd_phone_number = formatted_phone_number.getText().toString();
	              myUrl =  url.getText().toString();
	              myWebsite = website.getText().toString();
	              
	           Log.v(PlaceRequest.LOG_KEY, "Description: " + vic);
	           Log.v(PlaceRequest.LOG_KEY, "Description: " + formtd_address);
	           Log.v(PlaceRequest.LOG_KEY, "Description: " + formtd_phone_number);
	           Log.v(PlaceRequest.LOG_KEY, "Description: " + myUrl);
	           Log.v(PlaceRequest.LOG_KEY, "Description: " + myWebsite);
	           
	           AddSrv srv = new AddSrv();   
	    //setProgressBarIndeterminateVisibility(true);
	    srv.execute();
	    //YouTube.this.getApplicationContext()
	    progressDialog = ProgressDialog.show(PlaceAdd.this, "",  "Adding Place! Please wait...", true);
	            
	          }
	      });
	     
	      Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
	      cancel.setOnClickListener(new OnClickListener() {
	          public void onClick(View v) {
	          Toast.makeText(getBaseContext(), "Adding Place is cancelled!", 
	            Toast.LENGTH_SHORT).show();
	           finish();
	          }
	      });
	       //now that the dialog is set up, it's time to show it    
	       dialog.show();
	     
	 }
	 
	 class AddSrv extends AsyncTask<Void, Void, JSONObject>{
	     @Override
	     protected JSONObject doInBackground(Void... params) {
	      JSONObject pl = null;
	      try {
	       // send place search request from here
	        pl =   new PlaceRequest().addPlace(ShowGoogleMap.addLat, 
	          ShowGoogleMap.addLng, ShowGoogleMap.locType, placeName);  
	      } catch (Exception e) {
	       e.printStackTrace();
	      }
	      return pl;
	     }
	     
	     @Override
	     protected void onPostExecute(JSONObject result) {            
	   Log.v(PlaceRequest.LOG_KEY, "Place Added is: " + result);
	   
	   
	   if (result != null){
	     Toast.makeText(getBaseContext(), "Your Place is added", 
	       Toast.LENGTH_SHORT).show();
	   }
	   else{
	    Toast.makeText(getBaseContext(), "Please Try Again", 
	      Toast.LENGTH_SHORT).show();
	   }
	   //setProgressBarIndeterminateVisibility(false);
	   progressDialog.dismiss();
	   finish();
	     }
	    } // End of class SearchSrv here 
}
*/