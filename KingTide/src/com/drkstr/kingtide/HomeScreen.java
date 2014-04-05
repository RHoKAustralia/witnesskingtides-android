package com.drkstr.kingtide;
/*
 * This is the home screen. Here we set the background and 
 * get the buttons to work the way we want them
 */

import com.actionbarsherlock.app.SherlockActivity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.view.View;
import android.widget.Button;



// constants for Location manager




// we use sherlock activity to make the action bar work on older devices
public class HomeScreen extends SherlockActivity  {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setting the front end UI 
		setContentView(R.layout.home_screen);
		// setting the Button Color
		Button newButton = (Button) findViewById(R.id.map);
		newButton.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));
		
		Button newButton1 = (Button) findViewById(R.id.upload);
		newButton1.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));
		
		Button newButton2 = (Button) findViewById(R.id.king_time);
		newButton2.getBackground().setColorFilter(new LightingColorFilter(0x5292B3, 0x5292B3));
			
	}
	
	
	// called when the user hits the king tide timings button
	public void onKing_time(final View view) 
	{

		Intent i = new Intent(this, KingTideList.class);
 		startActivity(i);

	}
	
	
	// Called when the user hits the Upload an Image button
	public void onUpload(final View view) 
	{

		Intent i = new Intent(this, Get_Pic.class);
 		startActivity(i);

	}
	
	
	// called when the user hits the about king tides button
	public void onKingtideClick(final View view)
	{

		Uri webpage = Uri.parse("http://www.witnesskingtides.org/what-are-king-tides.aspx");
	    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);	
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(intent);
	    } 

	}
	
}
	


