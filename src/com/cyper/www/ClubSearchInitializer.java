package com.cyper.www;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import googleClubSearch.GoogleClubSearch;

import cype.club.grabber.GrabClubs;
import cype.club.grabber.MyLocation;
import cype.clubs.adapter.ClubMoodOnclickHander;
import cype.clubs.adapter.ClubMusicRatingOnclickHandler;
import cype.clubs.adapter.ClubattendOnclickHandler;

import cype.database.Database;
import android.app.Activity;
import android.app.ProgressDialog;

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

public class ClubSearchInitializer {
	 final static String DEFAULTISUSED = "No";
	 final static String EMPTY = "";
	 
	Activity activity;
	ListView listView;
	View view;
	GrabClubs grabClub;
	private String latitude;
    private String longitude;
	MyLocation myLocation = new MyLocation();
	String city;
	String country;
	Database db;
	MyLocation.LocationResult locationResult;
	ProgressDialog progressDialog = null;

	
	public ClubSearchInitializer(Activity activity, View view, ListView listView )
	{
		this.activity = activity;
		this.listView = listView;
		this.view = view;
		this.db = new Database();

		 
		grabClub = new GrabClubs(activity,listView, view);
		 locationResult = new MyLocation.LocationResult() {

				@Override
				public void gotLocation(Location location) 
				{
					
	          
	                List<Address> addresses;
					try 
					{
						latitude = String.valueOf(location.getLatitude());
						longitude = String.valueOf(location.getLongitude());  
		                double lat = Double.parseDouble(latitude);
		                double lng = Double.parseDouble(longitude);
		                Geocoder gcd = new Geocoder(ClubSearchInitializer.this.activity.getBaseContext(), Locale.getDefault());
						
						addresses = gcd.getFromLocation(lat, lng , 1);
						if (addresses.size() > 0)
						{
							city = addresses.get(0).getLocality();
							country = addresses.get(0).getCountryName();
							if(city==null || city=="")
							{
								ClubSearchInitializer.this.activity.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										 progressDialog.dismiss();
										 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Sorry Could not get your location", Toast.LENGTH_SHORT).show();
										 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();
									}
									
								});
							}
							else{
								init(grabClub, city, country);
							}
						}	
					} 
					catch (IOException e) 
					{
					
						ClubSearchInitializer.this.activity.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								 progressDialog.dismiss();
								 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
								 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
							}
							
						});
						
						Log.e("IO TOP", e.getMessage());
						e.printStackTrace();
					}
					catch(NullPointerException e){
						ClubSearchInitializer.this.activity.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								 progressDialog.dismiss();
								 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
								 Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
							}
							
						});
					}
				
				}
	        	
	        };
	        progressDialog = ProgressDialog.show(activity, "Finding Clubs/Bars Around You",
	  	             "Please Wait...", true);
	        MyRunnable myRun = new MyRunnable();
	        myRun.run();
	       
	}
	
	private void init(final GrabClubs grabClub, final String city, final String country)
	{
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params) {
			       if(grabClub.isAvailableClubs(city))
			       {
			    	   grabClub.GetClubsandBars(city);
			    	   
			    	   return true;
			       }
			       else
			       {
			    	   //register the city
			    	   //if city exist before insertinh
			    	   if(!checkForCity(city))
			    	   {
			    		   db.PutIntoPlacesDomain(""+getNewPlacesFuncId(), DEFAULTISUSED, city, country, latitude, longitude);
			    		   return false;
			    	   }
			    	   
			    	  // new GoogleClubSearch(ClubSearchInitializer.this.activity, ClubSearchInitializer.this.view, ClubSearchInitializer.this.listView, city);
			       }
				return false;
			}
			protected void onPostExecute(Boolean result) {
	            super.onPostExecute(result);
	            
	            progressDialog.dismiss();
	            if(result == false){
	            	ClubSearchInitializer.this.activity.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "Sorry could not get Clubs/Bars for your city", Toast.LENGTH_LONG).show();
							Toast.makeText(ClubSearchInitializer.this.activity.getApplicationContext(), "We are working on it! ", Toast.LENGTH_LONG).show();
	            		}	
					});
	            }
			}
			
		}.execute();
	}
	
		private int getNewPlacesFuncId()
		{
			int id_user  = 00;
			// get user_details id here to increment
			String sql = "select id from `places` where id is not null order by id desc limit 1";
			List<Item> club =  db.RunSQLStatement(sql);
			String id = grabUserAtrribute(club);
			
			if(id.equals(EMPTY) || id.equals(null))
				return id_user;
			else
			 return Integer.parseInt(id)+1;
		}
		private String grabUserAtrribute(List<Item> items)
		{
			if(items.isEmpty())
				return EMPTY;
			else{
			Item item  = items.get(0);
			
			ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
			
		
			return attributes.get(0).getValue();
			}
		}
	  private boolean checkForCity(String city)
	  {
		  String sql = "select * from places where city='"+city+"'";
		  
		  List<Item> cities = db.RunSQLStatement(sql);
		  
		  if(cities.size() > 0)
			  return true;
		  else
			  return false;
	  }
	  

	  private class MyLocationListener implements LocationListener 
	 	{

	         @Override
	         public void onLocationChanged(Location location) {
	             latitude = String.valueOf(location.getLatitude());
	             longitude = String.valueOf(location.getLongitude());
	         }

	         @Override
	         public void onStatusChanged(String s, int i, Bundle bundle) {
	         }

	         @Override
	         public void onProviderEnabled(String s) {
	         }

	         @Override
	         public void onProviderDisabled(String s) {
	         }
	     }//location listener ends here
	  public class MyRunnable implements Runnable 
		{
	        public MyRunnable() {
	        }

	        @Override
			public void run() 
	        {
		            myLocation.getLocation(ClubSearchInitializer.this.activity.getApplicationContext(), locationResult);
		    }
	    }
	  
}
