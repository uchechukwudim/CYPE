package cype.cpeople.adapter;



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
import com.cyper.www.ClubSearchInitializer;
import com.cyper.www.R;

import cype.database.Database;
import cype.date.CypeDate;
import cype.history.adapter.ProcessingHistory;
import cype.history.adapter.ProcessingHistory.MyRunnable;

public class ProcessCpeople {
	
	private static String ATTENDING = "Attending";
	private static String CHECKEDIN = "Checkedin";
	
	 final static String EMPTY = "";
	 final static String TRUE = "true";
     private Database db;
     private CypeDate cypeDate;
     private List<cpeople> listDataHeader;
	 private ArrayList<Users> users;
	 private HashMap<cpeople, List<Users>> listDataChild;
	 private Activity ac;
	 private String city;
	 ProgressDialog progressDialog = null;
	    
	 private MyLocation.LocationResult locationResult;
	 private String latitude;
	 private String longitude;
	 private MyLocation myLocation = new MyLocation();

	// private int maleCountChk = 0;
	// private int femaleCountChk = 0;
	 
	 CpeopleExpandableAdapter listAdapter;
	 ExpandableListView expListView;
     
     public ProcessCpeople(Activity ac, View rootView)
     {
    	 cypeDate = new CypeDate();
    	 db = new Database();
    	 listDataHeader = new ArrayList<cpeople>();
    	 users = new ArrayList<Users>();
    	 listDataChild = new HashMap<cpeople, List<Users>>();
    	 this.ac = ac;
    	 expListView = ( ExpandableListView) rootView.findViewById(R.id.expandableListView1);
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
 	                Geocoder gcd = new Geocoder(ProcessCpeople.this.ac.getApplicationContext(), Locale.getDefault());
 					addresses = gcd.getFromLocation(lat, lng , 1);
 					if (addresses.size() > 0)
 					{
 						city = addresses.get(0).getLocality();
 					
 						processCpeople();
 						
 					}	
 				} 
 				catch (IOException e) 
 				{
 					
 					ProcessCpeople.this.ac.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							 progressDialog.dismiss();
							 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
							 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
						}
						
					});
 					Log.e("IO TOP", e.getMessage())
 					;e.printStackTrace();
 				}
 				catch(NullPointerException e){
 					ProcessCpeople.this.ac.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							 progressDialog.dismiss();
							 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
							 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
						}
						
					});
 				}
 			}
 		};
 		
 		 MyRunnable myRun = new MyRunnable();
 	        myRun.run();
 	       progressDialog = ProgressDialog.show(ac, "Getting cPeople in Clubs/Bars Around You",
 		             "Please Wait...", true);
      
     }
     
     private void processCpeople()
     {

    	 try
    	 {
    	  new processCpeople().execute();
    	 }catch(NoClassDefFoundError e){
    		 progressDialog.dismiss();
    		 Toast.makeText(ac.getApplicationContext(), "Unable to get Cpeople", Toast.LENGTH_SHORT).show();
    		 Toast.makeText(ac.getApplicationContext(), "Please check for Internet Connection", Toast.LENGTH_SHORT).show();
    		
    	 }
     }
     private class processCpeople extends AsyncTask<String,  String, List<cpeople>>
     {
    	
		@Override
		protected List<cpeople> doInBackground(String... listDataChild) {
			 
			try{
		
			String sql = "select club_name from club_function_results where date='"+cypeDate.CypeNewDate()+"' and city='"+city+"'";  
			
			List<Item> clubs = db.RunSQLStatement(sql);
			
			
			//check if there is any result before processing
			
				if(clubs.size() > 0)
				{
					
					getListHeader(clubs);
				}
				else
				{
					ProcessCpeople.this.ac.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							progressDialog.dismiss();
							 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Could not get CPeople at the moment", Toast.LENGTH_SHORT).show();
							 
						}
						
					});
				}
				
			}
			catch(Exception e)
			{
				ProcessCpeople.this.ac.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						progressDialog.dismiss();
						 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Could not get Clubs/Bars", Toast.LENGTH_SHORT).show();
						 Toast.makeText(ProcessCpeople.this.ac.getApplicationContext(), "Pleae check youe internet Connection", Toast.LENGTH_SHORT).show();
					}
					
				});
				
			}
			
			return listDataHeader;
		}
		
		 	@Override
	        protected void onPostExecute(List<cpeople> result) {
	            super.onPostExecute(result);
	            if(result==null)
	            {
	            	progressDialog.dismiss();
	            	Toast.makeText(ac.getApplicationContext(), "Please check your internet connection ", Toast.LENGTH_SHORT).show();
	            }
	            else{
		            listDataHeader = result;
		            listAdapter = new CpeopleExpandableAdapter(ac, listDataHeader, listDataChild);
		    		expListView.setAdapter(listAdapter);
		    		progressDialog.dismiss();
	            }
		 }

	private void getListHeader(List<Item> clubs)
	{
		if(clubs==null)
		{
			progressDialog.dismiss();
			Toast.makeText(ac.getApplicationContext(), "Please check your internet connection ", Toast.LENGTH_SHORT).show();
		}
		else
		{
			for(Item item : clubs)
			{
				ArrayList<Attribute> att = ( ArrayList<Attribute>) item.getAttributes();
				String club = att.get(0).getValue();
				
				cpeople cp = new cpeople();
				cp.setClubLogo(club);
				String sqlTotalcpeople = "select club_name from club_function where club_name='"+club+"' and  date ='"+cypeDate.CypeNewDate()+"'";
				int total = db.RunSQLStatement(sqlTotalcpeople).size();
				cp.setCpeopleTotal(total);
				
				String sqlAttending = "select user_email from club_function where club_name='"+club+"' and attending='"+TRUE+"' and checkedin='false' and date ='"+cypeDate.CypeNewDate()+"'";
				List<Item> attendingCp =  db.RunSQLStatement(sqlAttending);
				//for each here to get gender count for attending
				//use a function getAttendingGenders with list of items of input
				getAttendingGenders(attendingCp, cp);
				
				String sqlCheckedin = "select user_email from club_function where club_name='"+club+"'  and checkedin='"+TRUE+"' and attending='"+TRUE+"' and date ='"+cypeDate.CypeNewDate()+"'";
				List<Item> checkedinCp = db.RunSQLStatement(sqlCheckedin);
				//for each here to get gender count for checkedin
				//use a function getCheckedenders with list of items of input
				getCheckedinGenders(checkedinCp, cp);
				
				listDataHeader.add(cp);
				
			}
		 }
	 }

		private void getAttendingGenders(List<Item> attendingCp, cpeople cp) 
		{
			int maleCountAtt = 0;
			int femaleCountAtt = 0;
			
			if(attendingCp.size() > 0)
			{	
				ArrayList<Users> u = new ArrayList<Users>();
				for(Item item: attendingCp)
				{
					Users user = new Users(ac.getApplicationContext());
					ArrayList<Attribute> att = ( ArrayList<Attribute>) item.getAttributes();
					String email = att.get(0).getValue();
					
					String sql1 = "select count from party_Wiv_Me_count where email='"+email+"' and club_name='"+cp.getClubName()+"' and date='"+cypeDate.CypeNewDate()+"'";
					List<Item> items1 = db.RunSQLStatement(sql1);
					
					if(items1 == null)//this should never happen
						user.setPartyWivMeCount("0");
					if(items1.size() > 0)
					{
						Item PWM = items1.get(0);
						ArrayList<Attribute> attributes1 = ( ArrayList<Attribute>) PWM.getAttributes();
						
						user.setPartyWivMeCount(attributes1.get(0).getValue());
					}
					else
					{
						user.setPartyWivMeCount("0");
					}
					String sql = "select firstName, account_type, gender, picture from user_details where email='"+email+"'";
					
					//grabUserDetailsAttending(db.RunSQLStatement(sql), user, "Attending", cp, maleCountAtt,femaleCountAtt);
					List<Item> items = db.RunSQLStatement(sql);
				
					if(items.size()>0)
					{
						Item cpitem  = items.get(0);
					
						
						ArrayList<Attribute> attributes = ( ArrayList<Attribute>) cpitem.getAttributes();
						
					
						user.setUserName(attributes.get(2).getValue());
						user.setAccountType(attributes.get(3).getValue());
						user.setGender(attributes.get(1).getValue());
						String gender =attributes.get(1).getValue();
						if(gender.equals("male"))
						{
							maleCountAtt = maleCountAtt+1;
						}
						else if(gender.equals("female"))
							femaleCountAtt = femaleCountAtt+1;
						
						user.setUserImageSrc(attributes.get(0).getValue());
					    user.setStatus(ATTENDING);
					    
					}
					user.setEmail(email);
					user.setClubName(cp.getClubName());
					user.setImage(user.getUserImageSrc(), email);
					u.add(user);
					
				}
				
				cp.setBoysAttendingCount(maleCountAtt);
				cp.setGirlsAttendingCount(femaleCountAtt);
				listDataChild.put(cp, u);
			}
		}
		
		private void getCheckedinGenders(List<Item> checkedinCp, cpeople cp)
		{
			int maleCountChk = 0;
			int femaleCountChk = 0;
			if(checkedinCp.size() > 0)
			{	
				ArrayList<Users> u = new ArrayList<Users>();
				for(Item item: checkedinCp)
				{
					Users user = new Users(ac.getApplicationContext());
					ArrayList<Attribute> att = ( ArrayList<Attribute>) item.getAttributes();
					String email = att.get(0).getValue();
					
					 String sql = "select firstName, account_type, gender, picture from user_details where email='"+email+"'";
					 //grabUserDetailsCheckedin(db.RunSQLStatement(sql), user, "Checkedin", cp, maleCountChk, femaleCountChk);
					 List<Item> items = db.RunSQLStatement(sql);
					 if(!items.isEmpty())
						{
							Item chkitem  = items.get(0);
							ArrayList<Attribute> attributes = ( ArrayList<Attribute>) chkitem.getAttributes();
							
							user.setUserName(attributes.get(2).getValue());
							user.setAccountType(attributes.get(3).getValue());
							user.setGender(attributes.get(1).getValue());
							String gender =attributes.get(1).getValue();
							if(gender.equals("male"))
							{
								maleCountChk  = maleCountChk +1;
							}
						
							if(gender.equals("female")){
								femaleCountChk = femaleCountChk+1;
							}
							user.setUserImageSrc(attributes.get(0).getValue());
							user.setStatus(CHECKEDIN);
						  
						}
					 user.setEmail(email);
					 user.setImage(user.getUserImageSrc(), user.getEmail());
					 user.setClubName(cp.getClubName());
					 u.add(user);
					
				}
				cp.setBoysCheckedInCount(maleCountChk );
				cp.setGirlsCheckedInCount(femaleCountChk);
				
				if(listDataChild.containsKey(cp))
				{
					for(Users us: u)
					{
						listDataChild.get(cp).add(us);
					}
				}else
				{
					listDataChild.put(cp, u);
				}
				
			}
		}
     }//private class ends here
     
     private void grabUserDetailsAttending(List<Item> items, Users user, String status, cpeople cp, int maleCountAtt, int femaleCountAtt)
		{
    	 
			
			if(items.size()>0)
			{
				Item item  = items.get(0);
				
				
				ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
			
				user.setUserName(attributes.get(2).getValue());
				user.setAccountType(attributes.get(3).getValue());
				user.setGender(attributes.get(1).getValue());
				String gender =attributes.get(1).getValue();
				if(gender.equals("male"))
				{
					maleCountAtt = maleCountAtt+1;
				}
				else if(gender.equals("female"))
					femaleCountAtt = femaleCountAtt+1;
				
				user.setUserImageSrc(attributes.get(0).getValue());
			    user.setStatus(status);
			   
			    
			}
			
			cp.setBoysAttendingCount(maleCountAtt);
			cp.setGirlsAttendingCount(femaleCountAtt);
			
		}
     
     	private void grabUserDetailsCheckedin(List<Item> items, Users user, String status, cpeople cp, int maleCountChk, int femaleCountChk)
		{
    	
			if(!items.isEmpty())
			{
				Item item  = items.get(0);
				ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
				
				user.setUserName(attributes.get(2).getValue());
				user.setAccountType(attributes.get(3).getValue());
				user.setGender(attributes.get(1).getValue());
				String gender =attributes.get(1).getValue();
				if(gender.equals("male"))
				{
					maleCountChk  = maleCountChk +1;
				}
			
				if(gender.equals("female")){
					femaleCountChk = femaleCountChk+1;
				}
				
			  
			}
			//set cpeople header here for attending male and female
			cp.setBoysCheckedInCount(maleCountChk );
			cp.setGirlsCheckedInCount(femaleCountChk);
		}

		public List<cpeople> getListDataHeader() {
			return listDataHeader;
		}

		public void setListDataHeader(List<cpeople> listDataHeader) {
			this.listDataHeader = listDataHeader;
		}

		public ArrayList<Users> getUsers() {
			return users;
		}

		public void setUsers(ArrayList<Users> users) {
			this.users = users;
		}
     	
		public class MyRunnable implements Runnable 
		{
	        public MyRunnable() {
	        }

	        @Override
			public void run() 
	        {
		            myLocation.getLocation(ac.getApplicationContext(), locationResult);
		    }
	    }
     	
}
