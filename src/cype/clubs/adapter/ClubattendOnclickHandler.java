package cype.clubs.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;
import com.cyper.www.gcm;

import cype.database.Database;
import cype.date.CypeDate;

public class ClubattendOnclickHandler{
	

 private ImageView attending;
 private ImageView sharepic;
 private ImageView clubMood;
 private ImageView musicRating;
 private View view;
 private Database db;
 private Activity ac;
 private CypeDate cypeDate;
 

 final static String DEFAULTCOUNT = "0";
 final static String EMPTY = "";
 final String FALSE = "false";
 final String TRUE= "true";
 final String NONE = "None";
 List<Item> clubs_res;
 final String ZERO = "0";
 public static boolean ISCONNECTED = false;
 
 // User name (make variable public to access from outside)
 public static final String KEY_NAME = "name";
  
 // Email address (make variable public to access from outside)
 public static final String KEY_EMAIL = "email";
 private final static  String REG_ID = "registration_id";
 // Sharedpref file name
 private static final String PREF_NAME = "CYPE";
 int MODE_PRIVATE = 0;
  
 private gcm Gcm;
 
 public ClubattendOnclickHandler( Activity ac, ImageView attending, View view, ImageView sharepic, ImageView clubMood, ImageView musicRating)
 {
	 this.ac = ac;
	 db = new Database();
	 this.attending =  attending;
	 this.sharepic = sharepic;
	 this.clubMood = clubMood;
	 this.view = view;
	 this.musicRating = musicRating;
	 this.attending = (ImageView) view.findViewById(R.id.attcheckin_click);
	 this.clubMood =  (ImageView) view.findViewById(R.id.clubMood_click);
	 this.sharepic = (ImageView) view.findViewById(R.id.sharepic_click);
	 this.musicRating = (ImageView) view.findViewById(R.id.ratemusic_click);
	 cypeDate =  new CypeDate();
	 Gcm = new gcm(ac.getApplicationContext());
	 
	 //check for GCM registration
	 SharedPreferences shared = ac.getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
	 String email = shared.getString(KEY_EMAIL, EMPTY);
	 String reg_id = shared.getString(REG_ID, EMPTY);
	 Gcm.checkGcmReg(email, reg_id);
 }
 
 public ClubattendOnclickHandler( Activity ac){
	 this.ac = ac;
	 db = new Database();
	 cypeDate =  new CypeDate();
	 Gcm = new gcm(ac.getApplicationContext());
 }
 
	 public void runSetAttending(String ClubName, String UserEmail, String City)
	 {
		 
		
		 new setAttending().execute(ClubName, UserEmail, City);
	 }
	 private class setAttending extends AsyncTask<String, String, String[]>
	 {
		 
		@Override
		protected String[] doInBackground(String... club) 
		{
			int attendNum = 0;
			int checkedinNum = 1;
			String clubName = club[0];
			String city = club[2];
			String user_email = club[1];
			
			
			String [] res = new String[2];
			String sql = "select attending from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+user_email+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> AttendItem = db.RunSQLStatement(sql);
			if(AttendItem ==null)
			{
				return null;
			}
			else if(AttendItem.size() > 0)
			{
				Item item = AttendItem.get(0);
				List<Attribute> attend = item.getAttributes();
				res[attendNum] = attend.get(0).getValue();
	 	   }
		   else
		   {
				res[attendNum] = "";
		   }
			
			String sql1 = "select checkedin from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+user_email+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> checkedinItem = db.RunSQLStatement(sql1);
			if(checkedinItem ==null)
			{
				return null;
			}
			else if(checkedinItem.size() > 0)
			{
				Item item1 = checkedinItem.get(0);
				List<Attribute> checkedin = item1.getAttributes();
				res[checkedinNum] = checkedin.get(0).getValue();
			}
			else
			{
				res[checkedinNum] = "";
			}
			
			
			
			return res;
		}
		

		@Override
	    protected void onPostExecute(String [] result) {
			
			int attendNum = 0;
			int checkedinNum = 1;
				if(result ==null)
				{
					Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
				}
				else if(result[attendNum].equals(""))
			    {
			    	attending.setImageResource((R.drawable.thumbup));		
			    	attending.setEnabled(true);
			    	attending.setContentDescription("thumbUp");
			    	
			    }
			    else if(result[attendNum].equals("true")){
			    	if(cypeDate.isCheckInTime()){		
			    		    attending.setEnabled(true);
			    			attending.setImageResource((R.drawable.checkedin));
			    			attending.setContentDescription("checkin");
			    			
			    	}
			    	else{
			    		attending.setImageResource((R.drawable.checkedin));
			    		attending.setEnabled(false);
			    		attending.setContentDescription("checkin");
			    	}
					
				}
				
				if(result == null){
					
				}
				else if(result[checkedinNum].equals("true") && result[attendNum].equals(""))
			    {
			    	
			    }
			    else if(result[checkedinNum].equals("true"))
				{
					attending.setImageResource((R.drawable.checkedin));		
					attending.setEnabled(false);
					sharepic.setEnabled(true);
					clubMood.setEnabled(true);
					musicRating.setEnabled(true);
					
				}
		}
		 
	 }
 
	 public void runPutRequestForAttending(String ClubName, String UserEmail, String City)
	 {
		 new runSQLandPutRequest().execute(ClubName, UserEmail, City);
	 }
	 private class runSQLandPutRequest extends AsyncTask<String, String, List<Item>>
	 {
	
			@Override
			protected List<Item> doInBackground(String... club) 
			{
				clubs_res = checkForClubFuncId(club[0], club[1], club[2]);
				if(clubs_res == null)
				{
					ISCONNECTED = false;
					return clubs_res;
					
				}
				else if(clubs_res.size() > 0)
				{
					String itemName = grabUserAtrribute(clubs_res);
					String clubName = club[0];
					String UserEmail = club[1];
					String city = club[2];
					db.putIntoClubFunctionDomainAttending(itemName, clubName, UserEmail, TRUE, city);
					
					ISCONNECTED = true;
				}
				else
				{
					String newId = ""+getNewClubFuncId();
					String newPWCId = ""+getNewPartyWivCountId();
					String clubName = club[0];
					String UserEmail = club[1];
					String city = club[2];
					db.putNewIntoClubFunctionDomain(zero_padding(newId), zero_padding(newId), clubName, UserEmail, TRUE, FALSE, FALSE, ZERO, city, cypeDate.CypeNewDate());
					db.putIntoPartyWivMeCountDomain(zero_padding(newPWCId), UserEmail, clubName, DEFAULTCOUNT, cypeDate.CypeNewDate());
					ISCONNECTED =true;
				}
					
				return clubs_res;
		}
			@Override
		    protected void onPostExecute(List<Item> result) {
				if(result ==null)
				{
					ISCONNECTED = false;
					Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
				}
			}
		
		private List<Item> checkForClubFuncId(String clubName, String email, String city )
		{
			String sql = "select id from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+email+"' and date='"+cypeDate.CypeNewDate()+"'";
			
			return db.RunSQLStatement(sql);
		}
		
		private int getNewClubFuncId()
		{
			int id_user  = 00;
			// get user_details id here to increment
			String sql = "select id from `club_function` where id is not null order by id desc limit 1";
			List<Item> club =  db.RunSQLStatement(sql);
			String id = grabUserAtrribute(club);
			
			if(id.equals(EMPTY) || id.equals(null))
				return id_user;
			else
			 return Integer.parseInt(id)+1;
		}
		
		private int getNewPartyWivCountId()
		{
			int id_user  = 00;
			// get user_details id here to increment
			String sql = "select id from `party_Wiv_Me_count` where id is not null order by id desc limit 1";
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
		
	}
	 
	 public void updateClubFunctionResult(String clubName, String city)
	 {
		 new updateClubFuncResultAttending().execute(clubName, city);
		 new updateClubFuncResultCheckedin().execute(clubName, city);
	 }
	 
	 
	 public void updateClubFuncResultAttending(String clubName, String city){
		 new updateClubFuncResultAttending().execute(clubName, city);
	 }
	 
	 private class updateClubFuncResultAttending extends AsyncTask<String, String, List<Item> >
	 {

		@Override
		protected List<Item>  doInBackground(String... club) {
			List<Item> result = null;
			try{
				String clubName = club[0];
				String city = club[1];
				String sql = "select attending from club_function where attending ='"+TRUE+"' and club_name ='"+clubName+"' and city ='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> items = db.RunSQLStatement(sql);
				String currentAttendingCount = "";
				if(items != null)
					currentAttendingCount = ""+items.size();
				
				
				//get id
				String sql1 = "select id from club_function_results where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> cfrResult = db.RunSQLStatement(sql1);
				result = cfrResult;
				if(cfrResult==null)
				{
					return  cfrResult;
				}
				if(cfrResult.size() > 0)
				{
					//get id and update with result
					String itemName = grabUserAtrribute(cfrResult);
					db.putIntoClubFunctionResultsAttending(itemName, clubName, currentAttendingCount, city);
				}
				else{
					//insert with result
					String itemName = ""+getNewClubFuncResultId();
					String id = itemName;
					db.putIntoClubFunctionResults(zero_padding(itemName), zero_padding(id), ZERO, currentAttendingCount, NONE, ZERO, club[0], club[1], "", cypeDate.CypeNewDate());
				}
				
			}catch(Exception e){}
			return result;
		}
		@Override
	    protected void onPostExecute(List<Item> result) {
			if(result ==null)
			{
				Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
			}
		}
		
		private int getNewClubFuncResultId()
		{
			int id_user  = 0;
			// get user_details id here to increment
			String sql = "select id from `club_function_results` where id is not null order by id desc limit 1";
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
			else
			{
				Item item  = items.get(0);
				
				ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
				
			
			   return attributes.get(0).getValue();
			}
		}
		
		
		 
	 }

	 public void runRequestForCheckedin(String ClubName, String UserEmail, String City)
	 {
		 new runSQLandPutRequestCheckedin().execute(ClubName, UserEmail, City);
	 }
	 private class runSQLandPutRequestCheckedin extends AsyncTask<String, String, List<Item>>
	 {
	
			@Override
			protected List<Item> doInBackground(String... club) 
			{
				clubs_res = checkForClubFuncId(club[0], club[1], club[2]);
				
				if(clubs_res==null)
				{
					ISCONNECTED = false;
					return clubs_res;
				}
				else if(clubs_res.size() > 0)
				{
					String itemName = grabUserAtrribute(clubs_res);
					String clubName = club[0];
					String UserEmail = club[1];
					String city = club[2];
					db.putIntoClubFunctionDomainCheckedin(itemName, clubName, UserEmail, TRUE, city);
					ISCONNECTED = false;
				}
				else
				{
					/*
					 * by logic this code will never run, because attending happens before checked in
					 * and if there isn't any matching data attending will insert one.
					 * i will leave it just in case my logic has flaw.
					 */
					
					String newId = ""+getNewClubFuncId();
					String clubName = club[0];
					String UserEmail = club[1];
					String city = club[2];
					db.putNewIntoClubFunctionDomain(zero_padding(newId), zero_padding(newId), clubName, UserEmail, TRUE, FALSE, FALSE, ZERO, city, cypeDate.CypeNewDate());
					ISCONNECTED = false;
				}
					
				return clubs_res;
		}
			
			@Override
		    protected void onPostExecute(List<Item> result) {
				if(result ==null)
				{
					ISCONNECTED = false;
					Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
				}
			}
		
		private List<Item> checkForClubFuncId(String clubName, String email, String city )
		{
			String sql = "select id from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+email+"' and date='"+cypeDate.CypeNewDate()+"'";
			
			return db.RunSQLStatement(sql);
		}
		
		private int getNewClubFuncId()
		{
			int id_user  = 00;
			// get user_details id here to increment
			String sql = "select id from `club_function` where id is not null order by id desc limit 1";
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
		
	 }
	 
	 public void updateClubFuncResultCheckedin(String clubName, String city)
	 {
		 new updateClubFuncResultCheckedin().execute(clubName, city);
	 }
	  
	 private class updateClubFuncResultCheckedin extends AsyncTask<String, String, List<Item>>
	 {

		@Override
		protected List<Item> doInBackground(String... club) {
			
			String clubName = club[0];
			String city = club[1];
			String sql = "select checkedin from club_function where checkedin ='"+TRUE+"' and club_name ='"+clubName+"' and city ='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> items = db.RunSQLStatement(sql);
			
			String currentCheckedinCount = ""+items.size();
			
			//get id
			String sql1 = "select id from club_function_results where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> cfrResult = db.RunSQLStatement(sql1);
			if(cfrResult==null)
			{
				return cfrResult;
			}
			if(cfrResult.size() > 0)
			{
				//get id and update with result
				String itemName = grabUserAtrribute(cfrResult);
				db.putIntoClubFunctionResultsCheckedin(itemName, clubName, currentCheckedinCount, city);
			}
			else{
				//insert with result
				/*
				 * Again this will never run, by logic
				 */
				String itemName = ""+getNewClubFuncResultId();
				String id = itemName;
				db.putIntoClubFunctionResults(zero_padding(itemName), zero_padding(id), currentCheckedinCount, ZERO, NONE , ZERO , club[0], club[1], "", cypeDate.CypeNewDate());
			}
			return cfrResult;
		}
		@Override
	    protected void onPostExecute(List<Item> result) {
			if(result ==null)
			{
				Toast.makeText(ac.getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
			}
		}
		
		private int getNewClubFuncResultId()
		{
			int id_user  = 0;
			// get user_details id here to increment
			String sql = "select id from `club_function_results` where id is not null order by id desc limit 1";
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
		
		
		 
	 }
 
	 private String zero_padding(String no_zeros_id) {
		    String zero_padded_id = "";
	
		    int id_length = no_zeros_id.length();
		    if (id_length == 1) {
		        zero_padded_id = "00000"+no_zeros_id; 
		    } else if (id_length == 2) {
		        zero_padded_id = "0000"+no_zeros_id;
		    } else if (id_length == 3) {
		        zero_padded_id = "000"+no_zeros_id;
		    } else if (id_length == 4) {
		        zero_padded_id = "00"+no_zeros_id;
		    } else if (id_length == 5) {
		        zero_padded_id = "0"+no_zeros_id;
		    } else if (id_length == 6) {
		        zero_padded_id = no_zeros_id;
		    }
	
		    return zero_padded_id;
		}
}

