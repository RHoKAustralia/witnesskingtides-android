package com.drkstr.kingtide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;



public class Upload extends SherlockActivity {
	
	StringUtils stringUtils;
	ServiceHandler serviceHandler;
	
	

	String pic;
	String date_time;
	Gson gson;
	OfflineDBCache db;
	
	
	String lat;
	String lng;
	String flat;
	
	public static final String PREFS_NAME = "UserDetails";
	public static final String NAME = "User_Name";
	public static final String EMAIL = "User_Email";
	
	
	// stuff for checking if we have network connection
	ConnectivityManager connectivityManager;
	boolean isConnected = false; // set to false; set to true once we check the network status
	
	
	public Upload (){
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		
		gson = new Gson();
		stringUtils = new StringUtils();
		serviceHandler = new ServiceHandler();
		db = new OfflineDBCache(this);
		db.open();// open the database
		
		autofillFeilds(); // check if we have the users name and email in the shared preff and fill them
		
		
		// getting all the data from the Intent extra
		Bundle extras = getIntent().getExtras();
		pic = extras.getString("pictureString");// pic is the image in base 64 string
		lat = extras.getString("latitude");
		lng = extras.getString("longitude");	
		date_time = extras.getString("date_pic");
		
		
		//EditText et = (EditText) findViewById(R.id.describe);
		//et.setText("Lat: "+lat+" Lng: "+lng+" Date and time: "+date_time);
		
		ActionBar actionBar = getSupportActionBar(); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	     int itemId = item.getItemId();
	     switch (itemId)
	     {
	     case android.R.id.home:
             Intent mainIntent = new Intent(getApplicationContext(), HomeScreen.class);
             mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(mainIntent);
	     }
	     
	     
	     return true;
	}
	
	
	public void autofillFeilds()
	{
		EditText name_field = (EditText) findViewById(R.id.name);
		EditText email_field = (EditText) findViewById(R.id.email);
		
		// get the user detals from the Shared preffrene file
				SharedPreferences user_details = getSharedPreferences(PREFS_NAME, 0);
				String name = user_details.getString(NAME, null);
				String email = user_details.getString(EMAIL, null);
				
				
				if(name != null )
					name_field.setText(name);
				if(email != null)
					email_field.setText(email);
	}

	
	/*
	 * called when the user hits the upload button
	 */
	
	public void onUpload(final View view) 
		{
		// check if all the fields have beed filled
			if(((EditText) findViewById(R.id.email)).getText().toString().trim() != null && ((EditText) findViewById(R.id.name)).getText().toString().trim() != null && ((EditText) findViewById(R.id.describe)).getText().toString().trim() != null)
			{
				
				setSharedPreffs(); // sets the users details in a Shared preff file; this will 
				   // auto complete the user details in the upload screen. 



				stringUtils.setEmail(((EditText) findViewById(R.id.email)).getText().toString().trim());
				stringUtils.setName(((EditText) findViewById(R.id.name)).getText().toString().trim());
				stringUtils.setDesc(((EditText) findViewById(R.id.describe)).getText().toString().trim());
				stringUtils.setlat(lat);
				stringUtils.setlng(lng);
				stringUtils.settime(date_time);
				stringUtils.setPhoto(pic);



				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				flat = gson.toJson(stringUtils);
				String jsonString= flat.toString();
				
				// check if the user is online
				if(!isOnline())
				{
					Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "You photo have been saved and will be uploaded when you connect to the internet.", Toast.LENGTH_LONG).show();
					
					long id = db.savePic(jsonString);
					db.close();//close the database
					
					//Toast.makeText(this, "No internet connection found. Please check your connection and try again", Toast.LENGTH_SHORT).show();
				}
				else // if the user is onlone upload the json string.
				{
				Toast.makeText(this, "Uploading you pic", Toast.LENGTH_SHORT).show();
				serviceHandler.upload(jsonString);
				}
				
				Intent i = new Intent(this, HomeScreen.class);
		 		startActivity(i);
			}
			else 
			{
				Toast.makeText(this, "Please enter your name,e-mail and a brief describtion of the image",Toast.LENGTH_LONG).show();
			}
		
		
			
		}
	
	
	
	// method to check if the user has network connectvity
	
		public boolean isOnline() 
			{
				try {
			        	connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

			        	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			        	isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
			        	return isConnected;

			        	} catch (Exception e) {
			            System.out.println("CheckConnectivity Exception: " + e.getMessage());
			           
			        	}
			        return isConnected;
			    }
	
		
		
	public void setSharedPreffs()
		{

			SharedPreferences user_details = getSharedPreferences(PREFS_NAME, 0);
	        SharedPreferences.Editor editor = user_details.edit();
	        
	        editor.putString(NAME, ((EditText) findViewById(R.id.name)).getText().toString().trim());
	        editor.putString(EMAIL, ((EditText) findViewById(R.id.email)).getText().toString().trim());
	        
	        // Commit the edits!
	        editor.commit();
		}
	
	
	
	}

	
	
	
	
	

