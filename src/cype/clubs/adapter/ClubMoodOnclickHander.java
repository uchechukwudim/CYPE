package cype.clubs.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.database.Database;
import cype.date.CypeDate;

public class ClubMoodOnclickHander 
{
	private String club_mood = "";
    private final String EMPTY = "";
	private ImageView cMood;
	private View view;
	private Database db;
	private List<Item> club_res;
	private Activity activity;
	private String ClubName;
	private CypeDate cypeDate;
	public  ClubMoodOnclickHander(Activity activity, View view, ImageView cMood)
	{
		this.activity = activity;
		this.cMood = cMood;
		this.view = view;	
		db = new Database();
		cMood = (ImageView) view.findViewById(R.id.clubMood_click);
		cypeDate = new CypeDate();
	}
	
	public ClubMoodOnclickHander(Activity activity){
		this.activity = activity;
		db = new Database();
		cypeDate = new CypeDate();
	}
	
	public void runPutRequestForCmood(String clubName, String userEmail, String city, String Cmood)
	{
		new runPutRequestForCmood().execute(clubName, userEmail, city, Cmood);
	}
	private class runPutRequestForCmood extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... club) {
			      
			club_res = checkForClubFuncId(club[0], club[1], club[2]);
			
			if(club_res == null)
			{
				return false;
			}
			else if(club_res.size() > 0)
			{
				String itemName = grabUserAtrribute(club_res);
				String clubName = club[0];
				String UserEmail = club[1];
				String city = club[2];
				String Cmood = club[3];
				db.putIntoClubFunctionDomainCmood(itemName, clubName, UserEmail, Cmood, city);
				return true;
			}
			else
				return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			
			if(result == false)
			{

				Toast.makeText(activity.getApplicationContext(), "Could not Vote Cmood", Toast.LENGTH_SHORT).show();
		        Toast.makeText(activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			
		}
		
		private List<Item> checkForClubFuncId(String clubName, String email, String city )
		{
			String sql = "select id from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+email+"' and date='"+cypeDate.CypeNewDate()+"'";
			
			return db.RunSQLStatement(sql);
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
	
	public void updateClubFuncResult(String clubName, String city){
		new updateClubFuncResultCmood().execute(clubName, city);
	}
	
	public void updateClubFuncResultCmood(String ClubName, String City)
	{
		new updateClubFuncResultCmood().execute(ClubName, City);
	}
	private class updateClubFuncResultCmood extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... club) {
			String clubName = club[0];
			ClubName = club[0];
			String city = club[1];
			String sql = "select user_cmood_vote from club_function where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> cmoods = db.RunSQLStatement(sql);
			
			if(cmoods == null)
			{
				return false;
			}
			else if(cmoods.size() > 0)
			{
				String VotedMood = calculateCmoodVote(cmoods);
				club_mood = VotedMood;
				String sql1 = "select id from club_function_results where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> cmResult = db.RunSQLStatement(sql1);
				
				String itemName = grabUserAtrribute(cmResult);
				db.putIntoClubFunctionResultsCmood (itemName, clubName, VotedMood, city);
				
				return true;
			}
			else 
				return false;
			
		
		}
		

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result == false)
			{

				Toast.makeText(activity.getApplicationContext(), "Could not vote Cmood", Toast.LENGTH_SHORT).show();
		         Toast.makeText(activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(activity.getApplicationContext(), "You voted  "+club_mood+" for "+ClubName+" Mood", Toast.LENGTH_SHORT).show();
			}
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
		private String calculateCmoodVote(List<Item> cmoods)
		{
		
			int max = 0;
			String votedMood = "";
			int funCount = 0;
			int mentalCount = 0;
			int crazyCount = 0;
			int calmCount = 0;
			
			for(Item item : cmoods)
			{
				String cm = item.getAttributes().get(0).getValue();
				
				if(cm.equals("Fun"))
					funCount++;
				else if(cm.equals("Mental"))
					mentalCount++;
				else if(cm.equals("Crazy"))
					crazyCount++;
				else if(cm.equals("calm"))
					calmCount++;
				
			}
			
			max = funCount;
			votedMood = totalCmoodVoteString(max, mentalCount, crazyCount,  calmCount);
			 
			return votedMood;
		}
		
		private String totalCmoodVoteString(int max, int mentalCount, int crazyCount, int calmCount)
		{
			 
			  String votedMood = "Fun";
			  if(mentalCount > max)
			  {
				  max = mentalCount;
			    votedMood = "Mental";
			  }
			   
			   if(crazyCount > max)
			   {
				   max = crazyCount;
				   votedMood = "Crazy";
			   }
			   
			   if(calmCount > max)
			   {
				   max = calmCount;
				   votedMood = "calm";
			   }
				  
			   if(votedMood== "Fun" && max ==0)
				   votedMood = "None";
			   
			   return votedMood;
		}
	}
}
