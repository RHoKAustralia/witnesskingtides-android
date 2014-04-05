package com.drkstr.kingtide;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;

	public ServiceHandler() {

	}

	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url) {
		return this.makeServiceCall(url, null);
	}

	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url,List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			// appending params to url
			if (params != null) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}
			HttpGet httpGet = new HttpGet(url);

			httpResponse = httpClient.execute(httpGet);
		
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			Log.e("Rakshak", "UnsupportedEncodingException "+e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e("Rakshak", "ClientProtocolException "+e.getMessage());
		} catch (IOException e) {
			Log.e("Rakshak", "IOException "+e.getMessage());
			
		}	
		return response;

	}
	

	// Handles the upload of the json string from the upload screen and the network change receiver 
	public void upload(final String json)
	{
		
		  Thread t = new Thread() 
		     {	
		    	 			
		    	 public void run() 
		    	 	{
		    		 	Looper.prepare(); //For Preparing Message Pool for the child Thread
		    		 	HttpClient client = new DefaultHttpClient();
		    		 	HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
		    		 	HttpResponse response;
		    		 	try {
		    		 			HttpPost post = new HttpPost("http://witnesskingtides.azurewebsites.net/api/photo/");
	            
		    		 			StringEntity se = new StringEntity(json);  
		    		 			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		    		 			post.setEntity(se);
		    		 			response = client.execute(post);
	             

		    		 		/*Checking response */
		    		 			if(response!=null){
		    		 				InputStream in = response.getEntity().getContent(); //Get the data in the entity
		    		 				Log.w("Rakshak", in.toString());
		    		 			}

		    		 		} catch(Exception e) 
		    		 		{
		    		 				e.printStackTrace();
		    		 				//createDialog("Error", "Cannot Estabilish Connection");
		    		 		}

		    		 	Looper.loop(); //Loop in the message queue
		    	 	}
		     	};
		     
		     	t.start();     
	
		
	}
	
	
	
}
