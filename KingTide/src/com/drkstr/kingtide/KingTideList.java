package com.drkstr.kingtide;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.actionbarsherlock.app.SherlockListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class KingTideList extends SherlockListActivity {

	
	private ProgressDialog pDialog;
	
	String jsonStr;// we store the json string in this before it gets parsed
	
	String state_select;// the state the user selects from the drop bown box.
	
	
	// All static variables
	static final String URL = "http://witnesskingtides.azurewebsites.net/api/kingtides";
	
	String[] states_array;
	
	StringUtils string_utils = new StringUtils();
	
	
	// All the nodes
	static final String KEY_DATE = "DateRange";
	static final String KEY_TIME = "HighTideOccurs";
	static final String KEY_LAT = "Latitude";
	static final String KEY_LNG = "Longitude";
	static final String KEY_LOC = "Location";
	static final String KEY_STATE = "State";
	
	// the Shared preff string name we toss the JSON string into 
	//and the time we last downloaded the JSON string 
	static final String PESFFS_NAME = "json_details";
	static final String KEY_JSON = "json_string";
	static final String KEY_JSON_TIME = "time";
	
	
	// stuff for checking if we have network connection
	ConnectivityManager connectivityManager;
    boolean isConnected = false; // set to false; set to true once we check the network status
	
	// Kingtide JSONArray
	JSONArray kingtide = null;
	
	
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> kingTideList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kingtide_details_list);
		setTitle("Kingtide Locations");
		
		Toast.makeText(this, R.string.onClickHint,Toast.LENGTH_LONG).show();
		
		
		pDialog = new ProgressDialog(KingTideList.this);
		if (pDialog.isShowing())
		{
		pDialog.dismiss();
		}
		
		
		kingTideList = new ArrayList<HashMap<String, String>>();

		

		// selecting single ListView item
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String lat_temp = ((TextView) view.findViewById(R.id.lat)).getText().toString();
				String lng_temp = ((TextView) view.findViewById(R.id.lng)).getText().toString();
				String loc_temp = ((TextView) view.findViewById(R.id.location)).getText().toString();
				
				
				
				String[] lat_parts = lat_temp.split(":");
				String[] lng_parts = lng_temp.split(":");
				String[] loc_parts = loc_temp.split(",");

				
				String format = "geo:0,0?q=" + Double.valueOf(lat_parts[1].trim()) + "," + Double.valueOf(lng_parts[1].trim()) + "(" + loc_parts[0] + ")";
				
				Uri uri = Uri.parse(format); 
				
				
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
			

			}
		});
		
		
		
		
		
		//stuff for the spinner to work
  		Spinner spin = (Spinner) findViewById(R.id.spinner_state);
  		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.states,android.R.layout.simple_spinner_item);
  		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  		spin.setAdapter(adapter);
  		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

  		        @Override
  		        public void onItemSelected(AdapterView<?> arg0, View arg1,
  		                int position, long arg3) {

  		        	state_select = string_utils.getState(position);
  		        	
  		        	kingTideList.clear();
  		        	
  		        	new GetKingTides().execute();
  		        	
  		        }

  		        @Override
  		        public void onNothingSelected(AdapterView<?> arg0) {
  				
  		        
  				}
	
  		});
	}
	
	
	
	
	/*
	 * The Async task for downloading the json string, parsing it and filling up the list view
	 * We also do the state wise filtering here in the do in background methiod
	 */
	
	
	
	
	private class GetKingTides extends AsyncTask<Void, Void,Void>
	{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			
			
			
			pDialog = new ProgressDialog(KingTideList.this);
			pDialog.setMessage("Downloading Kingtide timings. Please wait...");
			pDialog.setCancelable(true);
			pDialog.show();

		}
		
		// method to check if the user has network connectvity
		
		 public boolean isOnline() {
		        try {
		        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		        isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
		        return isConnected;


		        } catch (Exception e) {
		           
		          
		        }
		        return isConnected;
		    }
		
		
		@Override
		protected Void doInBackground(Void... args0) 
		{
			
			
		
			
			long time_json = 0; // set initial value 0 
			SharedPreferences json_details = getSharedPreferences(PESFFS_NAME, 0);
			String json_string = json_details.getString(KEY_JSON, null);
			time_json = json_details.getLong(KEY_JSON_TIME, 0);
			
			if(!isOnline() && json_string != null ) // if user is not online and we have the json string in the shared preffs
			{
				
				jsonStr = json_string;		
			}
			else if ((System.currentTimeMillis()-time_json < 432000000) && json_string != null) // if it has been less than 12 hrs since we last downloaded the json string 
				// 86400000 millseconds = 24 hrs
			{
			
				jsonStr = json_string;
			}
			else if (System.currentTimeMillis()-time_json > 43200000 && isOnline()) // we dont have a copy of the json string in the shared preffs; we have to download it from the URL
			{	
			
				// Creating service handler class instance
				ServiceHandler sh = new ServiceHandler();

				// Making a request to url and getting response
				jsonStr = sh.makeServiceCall(URL);	
			}
			
		
			
			
			if (jsonStr != null)
			{
				// once we are sure we have the json string from the URL; we save ti locally
				
				SharedPreferences json = getSharedPreferences(PESFFS_NAME, 0);
		        SharedPreferences.Editor editor = json.edit();
		        
		        editor.putString(KEY_JSON, jsonStr);
		        editor.putLong(KEY_JSON_TIME, System.currentTimeMillis());
		        
		        // Commit the edits!
		        editor.commit();
				
				
				try {
						
						// Getting JSON Array node
						 kingtide = new JSONArray(jsonStr);
						
						// looping through All Kingtide events
						for (int i = 0; i < kingtide.length(); i++) 
						{
							JSONObject k = kingtide.getJSONObject(i);
							
							String date = "Date Range: "+k.getString(KEY_DATE);
							String lat = "Latitude: "+k.getString(KEY_LAT);
							String lng = "Longitude: "+k.getString(KEY_LNG);
							String loc = k.getString(KEY_LOC)+", "+k.getString(KEY_STATE);
							String state_temp = k.getString(KEY_STATE);
							String temp_Time = k.getString(KEY_TIME);
							String[] time_parts = temp_Time.split("T");
							String time = "Highttide at: "+time_parts[1].substring(0,5)+";"+string_utils.getDate(time_parts[0]);
						
							// tmp hashmap for single kingtide event
							HashMap<String, String> kt = new HashMap<String, String>();
							
							// adding each child node to HashMap key => value
							kt.put(KEY_DATE, date);
							kt.put(KEY_LAT, lat);
							kt.put(KEY_LNG, lng);
							kt.put(KEY_LOC, loc);
							kt.put(KEY_TIME, time);
							
							
							if(state_select.equalsIgnoreCase(state_temp) || state_select.equalsIgnoreCase("all"))
							{
							
								kingTideList.add(kt);
							}
														
						}	
					} catch (JSONException e) {
						System.out.print(e.getMessage());
						//Log.e("Rakshak", "JSONException "+e.getMessage());
					}
			}
			else 
				
			
			
			return null;
			return null;
		}
		
		
		
		
		@Override
		protected void onPostExecute(Void result) 
		{
		
			// Dismiss the progress dialog
						if (pDialog.isShowing())
							pDialog.dismiss();
						

						// Adding menuItems to ListView
						ListAdapter adapter = new SimpleAdapter(KingTideList.this, kingTideList,
								R.layout.list_item,
								new String[] { KEY_LOC,KEY_TIME,KEY_DATE,KEY_LAT,KEY_LNG}, new int[] {R.id.location,R.id.time,R.id.date,R.id.lat,R.id.lng});

						setListAdapter(adapter);	
						
						pDialog = new ProgressDialog(KingTideList.this);
						if (pDialog.isShowing())
						{
						pDialog.dismiss();
						}
						
						
	}
	}




	@Override
	protected void onStart() {
		
		super.onStart();
		
		state_select = "all";
		
		pDialog = new ProgressDialog(KingTideList.this);
		if (pDialog.isShowing())
		{
		pDialog.dismiss();
		}
		
		
		
	}
	
}