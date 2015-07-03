package googleClubSearch;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import constants.Constants;


import cype.clubs.adapter.club;


public class GoogleClubSearch {
	
	ArrayList<club> clubs = new ArrayList<club>();
	Activity activity;
	
    private String city = "";
    private final String type = "night+clubs+and+bar+";
    private StringBuilder query = new StringBuilder();
    MyLocation myLocation = new MyLocation();
    MyLocation.LocationResult locationResult;
    ProgressDialog progressDialog = null;
    View view;
    private ListView listview;
    // Shared pref mode
    int MODE_PRIVATE = 0;
     
    public static boolean INTERNET = true;
    // Sharedpref file name
    private static final String PREF_NAME = "CYPE";
    public static final String KEY_EMAIL = "email";
    
    public GoogleClubSearch(Activity activity, View view, ListView list, String city)
    {
    	this.activity = activity;
    	this.view = view;
    	this.listview = list;
    	this.city = replaceSpaceWithPlus(city);
    	
	         
	     try{
				//progressDialog.dismiss();
				new GetCurrentLocation().execute(this.city);
				
			   }catch(ExceptionInInitializerError e){
				   GoogleClubSearch.this.activity.runOnUiThread(new Runnable() {
					   public void run() {
					      //put your code here
						   Toast.makeText(GoogleClubSearch.this.activity.getApplicationContext(), "Unable to get Clubs/Bars", Toast.LENGTH_SHORT).show();
						   Toast.makeText(GoogleClubSearch.this.activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
					   }
					});
			
			   }catch(NoClassDefFoundError e){
				   GoogleClubSearch.this.activity.runOnUiThread(new Runnable() {
					   public void run() {
					      //put your code here
						   Toast.makeText(GoogleClubSearch.this.activity.getApplicationContext(), "Unable to get Clubs/Bars", Toast.LENGTH_SHORT).show();
						   Toast.makeText(GoogleClubSearch.this.activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
					   }
					});
				}
	       
	     
	    		
    }
    
    private String replaceSpaceWithPlus(String city)
    {
    	String val = "";

    	for(int i = 0; i< city.length(); i++)
    	{
    		if(city.charAt(i) == ' ')
    		{
    			val = val+'+';
    		}
    		else
    			val = val+city.charAt(i);
    	}
    	return val;
    }
    
    private String replacePlusWithSpace(String city)
    {
    	String val = "";

    	for(int i = 0; i< city.length(); i++)
    	{
    		if(city.charAt(i) == '+')
    		{
    			val = val+' ';
    		}
    		else
    			val = val+city.charAt(i);
    	}
    	return val;
    }
    
    private class GetCurrentLocation extends AsyncTask<Object, String, Boolean> {

        @Override
        protected Boolean doInBackground(Object... myLocation) {
        	  if(null != city ) {
                  return true;
              } else {
                  return false;
              }
        } 

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            assert result;

            query.append("https://maps.googleapis.com/maps/api/place/textsearch/xml?");
            query.append("query=" + type+city + "&");
            query.append("sensor=true&"); //Must be true if queried from a device with GPS
            query.append("key=" + Constants.GOOGLEP_API_KEY);
    
            new QueryGooglePlaces().execute(query.toString());
        }
    }
    
    /**
     * Based on: http://stackoverflow.com/questions/3505930
     */
    //Query googlePlaces class
    private class QueryGooglePlaces extends AsyncTask<String, String, String> 
	{

        @Override
        protected String doInBackground(String... query) {
        	
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            
            try {
            		response = httpclient.execute(new HttpGet(query[0]));
            		StatusLine statusLine = response.getStatusLine();
            		
            		if(statusLine.getStatusCode() == HttpStatus.SC_OK)
					{
            		
	                    ByteArrayOutputStream out = new ByteArrayOutputStream();
	                    response.getEntity().writeTo(out);
	                    out.close();
	                    responseString = out.toString();  
					}
					else 
					{
						//Closes the connection.
					
						response.getEntity().getContent().close();
						
					}
				} catch (ClientProtocolException e) 
				{
					Log.e("CPE", e.getMessage());
				} catch (IOException e) 
					{
						Log.e("IO", e.getMessage());
					}
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
            
            	if(result.contains("ZERO_RESULT"))
            	{
            	
            		Toast.makeText(GoogleClubSearch.this.activity.getApplicationContext(), "Sorry Could not get clubs/Bar in your Location", Toast.LENGTH_SHORT).show();
            	}
            	
	                Document xmlResult = loadXMLFromString(result);
	                NodeList nodeList =  xmlResult.getElementsByTagName("result");
	
	            	 for(int i = 0, length = nodeList.getLength(); i < length; i++) {
	            	     Node node = nodeList.item(i);
	
	            	      if(node.getNodeType() == Node.ELEMENT_NODE) 
	            	      {
	            	        Element nodeElement = (Element) node;
	                        club Club = new club();
	                        Node name = nodeElement.getElementsByTagName("name").item(0);
	                        Node address = nodeElement.getElementsByTagName("formatted_address").item(0);
	                        Node id = nodeElement.getElementsByTagName("id").item(0);
	                        
	    	                Club.setClubAddress(address.getTextContent());
	    	                Club.setClubId(id.getTextContent());
	    	                Club.setClubName(name.getTextContent());
	    	                Club.setCity(replacePlusWithSpace(city));
	    	                
	    	                //used to diff users
	    	                SharedPreferences shared = activity.getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
							String Email = (shared.getString(KEY_EMAIL, ""));
	    	                Club.setEmail(Email);
	 
	    	               clubs.add(Club);
	            	    } 
	            	 }
                
                //Initialize googleClubProcessor here 
            	 new GoogleClubSearchProcessor(activity, view, listview, clubs);
            	 
            } catch (Exception e) {
            	Toast.makeText(GoogleClubSearch.this.activity.getBaseContext(), "Unable to get Clubs and Bars", Toast.LENGTH_SHORT).show();
            	Toast.makeText(activity.getBaseContext(), "Please check for internet connection", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "stuff happened");
            }
        }
    }//googl query private class ends herer
    
    public static Document loadXMLFromString(String xml) throws Exception 
   	{
           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();

           InputSource is = new InputSource(new StringReader(xml));

           return builder.parse(is);
    }
    

 
}
