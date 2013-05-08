package com.wingedstone.babylearn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpJsonClient {
	  private static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
	 
	    /* This is a test function which will connects to a given
	     * rest service and prints it's response to Android Log with
	     * labels "Praeda".
	     */
	    public static JSONObject request(String url)
	    {
	    	JSONObject json = null;
	        HttpClient httpclient = new DefaultHttpClient();
	        final HttpParams http_params = httpclient.getParams();
	        HttpConnectionParams.setConnectionTimeout(http_params, Configures.url_connect_timeout);
	        HttpConnectionParams.setSoTimeout(http_params, Configures.url_connect_read_timeout);
	 
	        // Prepare a request object
	        HttpGet httpget = new HttpGet(url); 
	 
	        // Execute the request
	        HttpResponse response;
	        try {
	            response = httpclient.execute(httpget);
	            // Examine the response status
	            Log.i("Praeda",response.getStatusLine().toString());
	 
	            // Get hold of the response entity
	            HttpEntity entity = response.getEntity();
	            // If the response does not enclose an entity, there is no need
	            // to worry about connection release
	 
	            if (entity != null) {
	 
	                // A Simple JSON Response Read
	                InputStream instream = entity.getContent();
	                String result= convertStreamToString(instream);
	                Log.i("Praeda",result);
	 
	                // A Simple JSONObject Creation
	                json=new JSONObject(result);
	 	 
	                // Closing the input stream will trigger connection release
	                instream.close();
	            }
	 
	 
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return json;
	    }
	    

}
