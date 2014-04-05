package com.drkstr.kingtide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;


@SuppressLint("CutPasteId")
public class Get_Pic extends SherlockActivity  {
	
	private static int RESULT_LOAD_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	public Bitmap myBitmap;
	
	double lat;
	double lng;
	
	String lat_String_geo; // lat and lng that we get from the GPS provider 
	String lng_String_geo;
	
	String lat_String_nxw ; // lat and lng that we get from the netowrk provider
	String lng_String_nxw;
	
	String lat_String_pic; // lat and lng that we get from the meta-data(ExifInterface) of the picture 
	String lng_String_pic;
	
	String date; // the date and time the pic was taken
	String date_meta; // te date from the meta data of the pic selected form the gallery
	
	
	String encodedImage;
	
	
	int check = 0; // gets set to 1 if the user take a picture or selects a pic from the gallery
	//int gallery_pic = 0;// gets set to 1 if the user selects a pic from the gallery
	int using_LastLoc = 0;// get set to 1 if we use the last know location;
						// need this to know if we need to turn off location listners (GPS and network) in the onDestroy
	
	
	LocationManager locationManager;
	LocationListener locationListener_geo;
	LocationListener locationListener_network;
	
	StringUtils string_utils = new StringUtils();
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// setting the content view
		setContentView(R.layout.get_pic);
		
		// set the button color to transparent blue.
		Button newButton = (Button) findViewById(R.id.cont);
		newButton.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));
		
		Button newButton1 = (Button) findViewById(R.id.upload_pic);
		newButton1.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));	
		
		Button newButton2 = (Button) findViewById(R.id.take_pic);
		newButton2.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// check if the GPS is enabled
		if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	       buildAlertMessageNoGps(); // call the method for alert dialog to get the user to enable GPS 	
		}

		
		Location last_Loc = locationManager.getLastKnownLocation(getProvider());
		
		if(System.currentTimeMillis()-last_Loc.getTime() > 180000 || last_Loc.getAccuracy() > 10)
		{
			// if the last know location in too old (>180000 mill secs = 3 min) or to inaccurate (>10 meters)
			// we start up a location listner(GPS)
			// and the network location listner in case we dont get a gps lock before we upload the pic
			startGPSListner();
			startNetworkListner();
			
		}
		else // if the last know location is not more that 3 mins old or is less that 10 meters accurate  
		{
			using_LastLoc = 1;
			
			lat = last_Loc.getLatitude();
			lat_String_geo= String.valueOf(lat);
			
			lng = last_Loc.getLongitude();
			lng_String_geo = String.valueOf(lng);
		}
		
		
	}
	
	

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
			
		if(using_LastLoc == 0)
		{
			locationManager.removeUpdates(locationListener_geo);
			locationManager.removeUpdates(locationListener_network);
		}
		
	}
	
	
	//Method to get best available provider for the last know location
	
	private String getProvider()
	{
		Criteria cr = new Criteria();
		cr.setAccuracy(Criteria.ACCURACY_MEDIUM);
		cr.setPowerRequirement(Criteria.POWER_HIGH);
		
		return  locationManager.getBestProvider(cr, true);
	}
	
	
	// Methord that starts the GPS location listner 
	private void startGPSListner()
		{
			locationListener_geo = new LocationListener() {
			    public void onLocationChanged(Location location) {
			    	
			    	lat = location.getLatitude();
			    	lat_String_geo = String.valueOf(lat);
			    	
			    	lng = location.getLongitude();
			    	lng_String_geo = String.valueOf(lng);
			    	
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {}

			    public void onProviderEnabled(String provider) {}

			    public void onProviderDisabled(String provider) {}
			  };
			  
			  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 1, locationListener_geo);
		}
		
	// Methord that starts the network based location listner 
	private void startNetworkListner()
		{
					
			locationListener_network = new LocationListener() {
				public void onLocationChanged(Location location) {
					    	
					  lat = location.getLatitude();
					  lat_String_nxw = String.valueOf(lat);
					    	
					  lng = location.getLongitude();
					  lng_String_nxw = String.valueOf(lng);
					    	
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {}

				public void onProviderEnabled(String provider) {}

				public void onProviderDisabled(String provider) {}
				};
					  
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener_network);
			}
	
	
	
	// alert dialog for asking the user to enable GPS 
	private void buildAlertMessageNoGps()
		{
			
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage(R.string.no_GPS)
		    .setTitle(R.string.no_GPS_title)
		           .setCancelable(false)
		           .setPositiveButton(R.string.on_GPS, new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		               }
		           })
		           .setNegativeButton(R.string.no_Gps_cont, new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                    dialog.cancel();
		               }
		           });
		    AlertDialog alert = builder.create();
		    alert.show();
				}
	
	
	
	

	// called when the user selects to take a new picture 
	public void take_Pic(final View view) {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}
	
	// called when the user selects to select a pic from the gallery
	public void choose_Existing(final View view) {

		Intent i = new Intent(
		Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);	 
		startActivityForResult(i, RESULT_LOAD_IMAGE);				

	}
	
	// called when the user hits the continue button
	
	public void cont(final View view) {
		
		if(check == 1 )// we check if the user has uploaded or selected an image 
		{	
			 
	/*
	 * This bit of code converts the pic into a base64 string
	 */
			 
			 
		    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		 		myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); //myBitmap is the bitmap object   
		 		byte[] b = baos.toByteArray(); 	// b is the byte array 
		 		encodedImage = Base64.encodeToString(b, Base64.DEFAULT); // encodedImage is where we are storing the Base64 string
		    	 
		 		
		 		
	/*
	 * This bit of code gets us the time the pic was taken and converts it into the right format
	 */
				Date now = new Date();	 
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");	 
				date = format.format(now);
				
				
				
	/*
	 * we collect all the date, time the pic was taken; the location of the picture and 
	 * the base64 encode string and send it on its way to get uploaded
	 */
		if(lat_String_pic == null || lng_String_pic == null) // check if the 
		{
			if(lat_String_geo == null || lng_String_geo == null) // if the GPS locatin provider did not get a lock
																	// we will use the location from the network provider
				{
					Intent i = new Intent(this, Upload.class);
					i.putExtra("pictureString", encodedImage);
					i.putExtra("latitude", lat_String_nxw);
					i.putExtra("longitude", lng_String_nxw);
					i.putExtra("date_pic", date);
					startActivity(i);
				}
				else
				{								
					Intent i = new Intent(this, Upload.class);
					i.putExtra("pictureString", encodedImage);
					i.putExtra("latitude", lat_String_geo);
					i.putExtra("longitude", lng_String_geo);
					i.putExtra("date_pic", date);
					startActivity(i);
			}
		}
	
		else
			{

			Intent i = new Intent(this, Upload.class);
			i.putExtra("pictureString", encodedImage);
			i.putExtra("latitude", lat_String_pic);
			i.putExtra("longitude", lng_String_pic);
			i.putExtra("date_pic", date_meta);
			startActivity(i);
			}
		}
		else // if the user has not snaped a new pic or selected one from their gallery we ask them to do so
		{	
				Toast.makeText(getApplicationContext(), "Please upload or snap a new photo to continue" ,Toast.LENGTH_LONG).show();
		}

	}

	/*
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * the result for snapping a new picture or selecting one from the gallery 
	 */
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	     
	     check = 1;//set to 1 to confirm that the user has snapped a pic or selected one from the gallery
	     
	    
	    	 if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	    		 
	    		 //gallery_pic = 1;// set to 1 if the user selects a pic from the gallery
	    		 
	             Uri selectedImage = data.getData(); // this bit is used to get a new pic from the camera and put it on  display
	             String[] filePathColumn = { MediaStore.Images.Media.DATA };
	     
	             Cursor cursor = getContentResolver().query(selectedImage,
	                     filePathColumn, null, null, null);
	             cursor.moveToFirst();
	     
	             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	             String picturePath = cursor.getString(columnIndex);
	             cursor.close();
	                          
	             File imgFile = new  File(picturePath);
	             if(imgFile.exists())
	             {
	            	 myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	            	 
	            	 // set the image in the sample area
	            	 ImageView myImage = (ImageView) findViewById(R.id.pic);
	            	 myImage.setImageBitmap(myBitmap);
	            	 
	            	 getMetaData(imgFile.getAbsolutePath());
	            	
	             }
	         }
	    	 
	    	 if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) 
	    	 	{

	    		 myBitmap = data.getExtras().getParcelable("data");
			     ImageView photo = (ImageView) findViewById(R.id.pic);
			     photo.setImageBitmap(myBitmap);
			     
			    }  	 
	     }

	
	public void getMetaData(String filePath)
	{
		
		  
		try {
	    		   
			ExifInterface exif = new ExifInterface(filePath);
			
			
			// getting the location of the pic from the meta data and setting it into the right class varable
			String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String lat_reff = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String lng_reff = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			
			// we check if the pic has location data attached to it
			// if the user has disabled geo taging in their default camera the pic will not have location data attached to it
			// some phones have goe tagging for turned off by default for their built in camera app
			
			if(lat == null || lng == null)
			{
				lat_String_pic = null;
				lng_String_pic = null;
			}
			else
			{
				String location = string_utils.getInDegrees(lat, lat_reff, lng, lng_reff);
				String[] parts = location.split(",");
				lat_String_pic = parts[0];
				lng_String_pic = parts[1];
			}
			
			
			// getting the date from the meta data and setting it in teh right format
			String date_temp = exif.getAttribute(ExifInterface.TAG_DATETIME);
			date_meta = string_utils.getDateFormated(date_temp);
				
						
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
		
	}
}
	
	
	

