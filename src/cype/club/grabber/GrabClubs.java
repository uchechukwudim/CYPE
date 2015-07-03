package cype.club.grabber;

import googleClubSearch.GoogleClubSearch;
import googleClubSearch.GoogleClubSearchProcessor;
import googleClubSearch.MyLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import cype.clubs.adapter.ClubMoodOnclickHander;
import cype.clubs.adapter.ClubMusicRatingOnclickHandler;
import cype.clubs.adapter.ClubattendOnclickHandler;
import cype.clubs.adapter.club;
import cype.cpeople.adapter.ProcessCpeople.MyRunnable;
import cype.database.Database;

public class GrabClubs {
	
	private Database db;
	private  ArrayList<club> clubs = new ArrayList<club>();
	private  Activity activity;
	private  ListView listView;
	private View view;

	
	private String city;
	public static final String KEY_EMAIL = "email";
	int MODE_PRIVATE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "CYPE";
	 
	 public GrabClubs(Activity activity, ListView listView, View view)
	 {
		 this.activity = activity;
		 this.listView = listView;
		 this.view = view;
		 this.db = new Database();
		 
	 }
	 
	 public boolean isAvailableClubs(String city)
	 {
		 String sql = "select * from clubs_bars where city='"+city+"'";
		 
		 List<Item> clubs = this.db.RunSQLStatement(sql);
		 
		
		 if(clubs.size() > 0)
			 return true;
		 else
			 return false;
	 }
	 
	 public void GetClubsandBars(String city)
	 {
		 String sql = "select club_logo, club_name, club_address, city from clubs_bars where city='"+city+"'";
		 
		 List<Item> clubs = db.RunSQLStatement(sql);
		 
		 if(clubs ==null)
		 {
			 Toast.makeText(activity.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
		 }
		 else if (clubs.size() > 0)
		 {
			 for(Item Club : clubs)
			 {
				 club club = new club();
				 ArrayList<Attribute> attr = ( ArrayList<Attribute>) Club.getAttributes();
				 
				 club.setImage(attr.get(0).getValue());
				 club.setClubName(attr.get(1).getValue());
				 club.setClubAddress(attr.get(2).getValue());
				 club.setCity(attr.get(3).getValue());
				 
				 //used to diff users
				 SharedPreferences shared = activity.getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
				 String Email = (shared.getString(KEY_EMAIL, ""));
	             club.setEmail(Email);

				 this.clubs.add(club);
			 }
		 }
		 
		//Initialize googleClubProcessor here 
    	 new GoogleClubSearchProcessor(activity, view, listView, this.clubs);
	 }

	public ArrayList<club> getClubs() {
		return clubs;
	}

	public void setClubs(ArrayList<club> clubs) {
		this.clubs = clubs;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
	 
}
