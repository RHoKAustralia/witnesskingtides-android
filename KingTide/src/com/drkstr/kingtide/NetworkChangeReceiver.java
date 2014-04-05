package com.drkstr.kingtide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;


public class NetworkChangeReceiver extends BroadcastReceiver {
	
	
	OfflineDBCache db; 
	Upload upload;
	ServiceHandler serviceHandler;
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		
		final ConnectivityManager connMgr = (ConnectivityManager) context
                							.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        db = new OfflineDBCache(context);
        upload = new Upload();
        serviceHandler = new ServiceHandler();
        
        if (wifi.isAvailable() || mobile.isAvailable()) // we are GO for upload :D 
        {                   
        	
        	db.open();// open the database
        	Cursor cursor = db.fetchAllStrings();
        	int numRows = cursor.getCount(); // numeber of JSON stirngs in the database
        	
        	cursor.moveToFirst();// move the cursor to the first entry       
        	
        	if(cursor != null)
        	{
        		for(int i=0;i<numRows;i++)
        		{
        			if(upload.isConnected)// we chech that the user is still online
        			{
        				// get the json string
        				String json = cursor.getString(cursor.getColumnIndex(OfflineDBCache.KEY_JSON_STRING));
        				// upload it
        				serviceHandler.upload(json);
        		
        				// get the row_id
        				long id = cursor.getLong(cursor.getColumnIndex(OfflineDBCache.KEY_ROWID));
        				// delete it
        				db.deleteEntry(id);
        		
        				cursor.moveToNext(); // moves the cursor to the next entry.Will be null if we are at the last entry
        		
        			}

        		}	// yay brackets :/
        	}
        }
    }

}
