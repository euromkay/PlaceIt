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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Now call PlaceRequest.java from ShowPlaceDetail.java to view Place detail
 */

public class ShowPlaceDetail extends Activity {
	 ProgressDialog progressDialog;
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
                 //set up dialog
                 Dialog dialog = new Dialog(ShowPlaceDetail.this);
                 dialog.setContentView(R.layout.maindialog);
                 dialog.setTitle(MyItemizedOverlay.title);
                 dialog.setCancelable(false);
  
                 //set up text
                 TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                 text.setText(ShowGoogleMap.detailText);
  
                 //set up image view
                 ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
                 img.setImageResource(R.drawable.user_marker);
  
                 //set up button
                 Button button = (Button) dialog.findViewById(R.id.cancel_button);
                 button.setOnClickListener(new OnClickListener() {
                     public void onClick(View v) {
                      ShowPlaceDetail.this.finish();
                     }
                 });
                 
                 Button deletButton = (Button) dialog.findViewById(R.id.delete);
                 deletButton.setOnClickListener(new OnClickListener() {
                     public void onClick(View v) {
                      DelSrv srv = new DelSrv();    
             //setProgressBarIndeterminateVisibility(true);
             srv.execute();
             progressDialog = ProgressDialog.show(ShowPlaceDetail.this, "", 
                         "Deleting! Please wait...", true);
                     }
                 });
                 
                 Button photoButton = (Button) dialog.findViewById(R.id.photo);
                 photoButton.setOnClickListener(new OnClickListener() {
                     public void onClick(View v) {
                      finish();
                     }
                 });
                 //now that the dialog is set up, it's time to show it    
                 dialog.show();
             }
     
      private class DelSrv extends AsyncTask<Void, Void, JSONObject>{
      @Override
      protected JSONObject doInBackground(Void... params) {
       JSONObject pl = null;
       try {
        // send place search request from here
         pl =   new PlaceRequest().deletePlace(MyItemizedOverlay.reference);  
       } catch (Exception e) {
        e.printStackTrace();
       }
       return pl;
      }
      
      @Override
      protected void onPostExecute(JSONObject result) {             
    Log.v(PlaceRequest.LOG_KEY, "Place Deleted is: " + result);
    
    if (result != null){
      Toast.makeText(getBaseContext(), "Your Place is Deleted", 
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
