package cype.history.adapter;

import googleClubSearch.GoogleClubSearch;
import googleClubSearch.MyLocation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.cpeople.adapter.ProcessCpeople;
import cype.database.Database;
import cype.date.CypeDate;

public class ProcessingHistory {

	final static String EMPTY = "";
	 final static String TRUE = "true";
	 final static int DAYS = 5;
    private Database db;
    private CypeDate cypeDate;
    private List<history> listDataHeader;
    private HashMap<history, List<HistoryChildren>> listDataChild;
    private MyLocation.LocationResult locationResult;
    private String city;
	private Activity ac;
	ProgressDialog progressDialog = null;
	
	private String latitude;
    private String longitude;
    MyLocation myLocation = new MyLocation();
    
	private HistoryExpandableAdapter listAdapter;
	private ExpandableListView expListView;
	
	public ProcessingHistory(Activity ac, View rootView)
	{
		db = new Database();
		cypeDate = new CypeDate();
		this.ac = ac;
		listDataHeader = new ArrayList<history>();
   	    listDataChild = new HashMap<history, List<HistoryChildren>>();
   	    expListView = ( ExpandableListView) rootView.findViewById(R.id.expandableList_history);
   	    
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
		             Geocoder gcd = new Geocoder(ProcessingHistory.this.ac.getApplicationContext(), Locale.getDefault());
					
		             addresses = gcd.getFromLocation(lat, lng , 1);
					if (addresses.size() > 0)
					{
						city = addresses.get(0).getLocality();
						
					}	
				} 
				catch (IOException e) 
				{
					
					 ProcessingHistory.this.ac.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								 progressDialog.dismiss();
								 Toast.makeText(ProcessingHistory.this.ac.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
								 Toast.makeText(ProcessingHistory.this.ac.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
							}
							
						});
					Log.e("IO TOP", e.getMessage())
					;e.printStackTrace();
				}
				catch(NullPointerException e){
					ProcessingHistory.this.ac.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							 progressDialog.dismiss();
							 Toast.makeText(ProcessingHistory.this.ac.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
							 Toast.makeText(ProcessingHistory.this.ac.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
						}
						
					});
				}
			}
		};
		
		 MyRunnable myRun = new MyRunnable();
	        myRun.run();
	        progressDialog = ProgressDialog.show(ac, "Getting Clubs/Bars History",
		            "Please Wait...", true);
	}
	
	public void processHistory()
	{
		
		try{
			new processHistory().execute();
		}catch(NoClassDefFoundError e){
		 progressDialog.dismiss();
		 Toast.makeText(ac.getApplicationContext(), "Unable to get Cpeople", Toast.LENGTH_SHORT).show();
		 Toast.makeText(ac.getApplicationContext(), "Please check for Internet Connection", Toast.LENGTH_SHORT).show();
		
	 }
	}
	private class processHistory extends AsyncTask<String, String, List<history>>
	{

		@Override
		protected List<history> doInBackground(String... arg0) {
			//get current date. minus five time(because your looking for the past 5days)
			//while minus check for end of start of day, month so it doesn't go into minus
			//for each date minus too, get all clubs activities for that date.. and then display
		try{
			String date = cypeDate.CypeNewDate();
			for(int loop=0; loop < DAYS-1; loop++)
			{
			
				String nDate = minusCypeDate(date);
				 //select details for displa from club_function result
				history History = new history(nDate);
				String sql = "select club_name, attending_result , checkedin_result, music_rating_result, cmood_vote_result from club_function_results where date='"+nDate+"' and city='"+city+"'";
				List<Item> clubs = db.RunSQLStatement(sql);
				List<HistoryChildren> clubH = new ArrayList<HistoryChildren>();
				
				if(clubs == null)
				{
					return null;
				}
				if(clubs.size() > 0)
				{
					for(Item club: clubs)
					{
						HistoryChildren HC = new HistoryChildren();
						
						ArrayList<Attribute> att = (ArrayList<Attribute>) club.getAttributes();
						HC.setClubLogo(att.get(0).getValue());
						HC.setCheckedInCount(att.get(1).getValue());
						HC.setAttendedCount(att.get(2).getValue());
						HC.setMusicRatingCount(att.get(3).getValue());
						HC.setClubMood(att.get(4).getValue());
						HC.setDate(nDate);
						
						clubH.add(HC);
					}	
				}
				listDataChild.put(History, clubH);
				listDataHeader.add(History);
				date = nDate;
			}
			 }catch(Exception e){
				 ProcessingHistory.this.ac.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						progressDialog.dismiss();
						 Toast.makeText(ac.getApplicationContext(), "Unable to get Cpeople", Toast.LENGTH_SHORT).show();
						 Toast.makeText(ac.getApplicationContext(), "Please check for Internet Connection", Toast.LENGTH_SHORT).show();
						
					}
					 
				 });
			 }
			return listDataHeader;
			
			
		}
		
		@Override
		protected void onPostExecute(List<history> result) 
		{
			//progressDialog.dismiss();
			if(result == null)
			{
				progressDialog.dismiss();
				Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
			}else{
			
	            listDataHeader = result;
	            listAdapter = new HistoryExpandableAdapter(ac, listDataHeader, listDataChild);
	    		expListView.setAdapter(listAdapter);
	    		progressDialog.dismiss();
			}
	    }
		
		private String minusCypeDate(String cypeDate)
		{
			int day = getDayFromDate(cypeDate);
			int month = getMonthFromDate(cypeDate);
			int year = getYearFromDate(cypeDate);
			String date ="";
			if(day == 01)
			{
				if(month == 01) //check if its january
				{
					month = 12; //if it is jan give month dec
					
					//give day last day of december
					year--;
					day  = getmonthNumberofDays(month, year);
					String Month = ""+month;
					if(Month.length() ==1)
						Month = "0"+Month;
					String Day = ""+day;
					if(Day.length()==1)
						Day = "0"+Day;
					
				 	date = year+"-"+Month+"-"+Day;
				}
				else
				{
					month = month-1;
					day =  getmonthNumberofDays(month,year);
					
					String Month = ""+month;
					if(Month.length() ==1)
						Month = "0"+Month;
					
					String Day = ""+day;
					if(Day.length()==1)
						Day = "0"+Day;
					
					date = year+"-"+Month+"-"+Day;
				}
			}
			else
			{
				//just minus day and return 
				day = day-1;
				String Month = ""+month;
				if(Month.length() ==1)
					Month = "0"+Month;
				
				String Day = ""+day;
				if(Day.length()==1)
					Day = "0"+Day;
				
				date = year+"-"+Month+"-"+Day;
			}
			
			return date;
		}

		private int getYearFromDate(String cypeDate) {
			String year = ""+cypeDate.charAt(0)+cypeDate.charAt(1)+cypeDate.charAt(2)+cypeDate.charAt(3);
			return Integer.parseInt(year);
		}

		private int getMonthFromDate(String cypeDate) {
			String month = ""+cypeDate.charAt(5)+cypeDate.charAt(6);
			return Integer.parseInt(month);
		}

		private int getDayFromDate(String cypeDate) {
			String day = ""+cypeDate.charAt(8)+cypeDate.charAt(9);
			return Integer.parseInt(day);
		}
		private int getmonthNumberofDays(int month, int year)
		{
			boolean leapYear = (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
			if (month == 4 || month == 6 || month == 9 || month == 11)
				return 30;
			else if (month == 2) 
				return (leapYear) ? 29 : 28;
			else 
				return 31;
		}
	}
	public class MyRunnable implements Runnable 
		{
	        public MyRunnable() {
	        }

	        @Override
		public void run() {
	            myLocation.getLocation(ac.getApplicationContext(), locationResult);
	        }
	    }
}
