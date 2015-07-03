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

public class ClubMusicRatingOnclickHandler {

	private String mus_rating = "";
	private Database db;
	private ImageView musicRating;
	private View view;
	private List<Item> club_res;
	private Activity activity;
	final String EMPTY = "";
	private String ClubName;
	 private CypeDate cypeDate;
	
	public ClubMusicRatingOnclickHandler(Activity activity, View view, ImageView musicRating)
	{
		this.activity = activity;
		this.musicRating = (ImageView) view.findViewById(R.id.ratemusic_click);
		this.view = view;
		db = new Database();
		cypeDate = new CypeDate();
	}
	
	public ClubMusicRatingOnclickHandler(Activity activity){
		this.activity = activity;
		db = new Database();
		cypeDate = new CypeDate();
	}
	
	public void runPutRequestMusicRating(String clubName, String UserEmail, String musicRating, String city)
	{
		new runPutRequestMusicRating().execute(clubName, UserEmail, musicRating, city);
	}
	
	//puts in the new voted music rating in club_function domain.
	//first get the id of the user from club_funtion so insertion can carry up accordingly
	private class runPutRequestMusicRating extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... club) {
			String clubName = club[0];
			String UserEmail = club[1];
			String mRating = club[2];
			String city = club[3];
			
			club_res = checkForClubFuncId(clubName,  UserEmail , city);
			
			if(club_res == null)
			{
				return false;
			}
			if(club_res.size() > 0)
			{
				String itemName = grabUserAtrribute(club_res);
				
				db.putIntoClubFunctionMusicRating(itemName, clubName, UserEmail, mRating, city);
				return true;
			}
			
				return false;
		}
		
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result == false)
			{

				Toast.makeText(activity.getApplicationContext(), "Could not rate music at this time", Toast.LENGTH_LONG).show();
		         Toast.makeText(activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			
		}
		
		
	}
	
	private List<Item> checkForClubFuncId(String clubName, String email, String city )
	{
		String sql = "select id from club_function where club_name = '"+clubName+"' and city ='"+city+"' and  user_email ='"+email+"' and date='"+cypeDate.CypeNewDate()+"'";
		
		return db.RunSQLStatement(sql);
	}
	
	public void updateClubFunctionResult(String clubName, String city){
		new UpdateupdateClubFuncResultMusicRating().execute(clubName, city);
	}
	
	
	public void  UpdateupdateClubFuncResultMusicRating(String clubName, String city)
	{
		new UpdateupdateClubFuncResultMusicRating().execute(clubName, city);
	}
	private class UpdateupdateClubFuncResultMusicRating extends AsyncTask<String, String, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... club) {
			boolean isRate = false;
			String clubName = club[0];
			ClubName = club[0];
			String city = club[1];
			
			String sql = "select user_music_rating from club_function where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
			List<Item> musicRating = db.RunSQLStatement(sql);
			
			if(musicRating == null)
			{
				return false;
			}
			else if(musicRating.size() > 0)
			{
				String musicRatingVote = ""+MusicRatingVoteCalculator(musicRating);
				mus_rating = musicRatingVote;
				String sql1 = "select id from club_function_results where club_name ='"+clubName+"' and city='"+city+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> mrResult = db.RunSQLStatement(sql1);
				
				String itemName = grabUserAtrribute(mrResult);
				db.putIntoClubFunctionResultsMusicRating(itemName, clubName, musicRatingVote, city);
				return true;
			}
				return null;
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result == null){
				
			}
			
			if(result == false)
			{

				Toast.makeText(activity.getApplicationContext(), "Could not vote Music Rating", Toast.LENGTH_SHORT).show();
		         Toast.makeText(activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(activity.getApplicationContext(), "You voted  "+mus_rating+" Star for "+ClubName+" Music", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		 private int MusicRatingVoteCalculator(List<Item> musicRating)
		 {

			 	int one = 0;
			 	int two = 0;
			 	int three  = 0;
			 	int four = 0;
			 	int five = 0;
			  int votedMusicRating = 0;
			  
			 	for(Item item : musicRating)
				{
					String mr = item.getAttributes().get(0).getValue();
				
					 if(mr.equals("1"))
						one++;
					
					 if(mr.equals("2")) 
						 two++;
						
					 if(mr.equals("3")) 
						 three++;
					
					 if(mr.equals("4"))
						 four++;
					 if(mr.equals("5")) 
						 five++;
					
				}
			 	
			 	votedMusicRating = totalMusicVote(one, two, three, four, five);
			 	
			 	return votedMusicRating;
		 }
		 
		 private int totalMusicVote(int oneCount, int twoCount, int threeCount, int fourCount, int fiveCount)
		 {
			 int max = oneCount ;
			  int Mvote = 1;
				 if(twoCount > max)
				 {
					 max = twoCount;
				     Mvote = 2;
				 }
				 if(threeCount > max)
				 {
					 max = threeCount;
				 	Mvote = 3;
				 }
				 
				 if(fourCount > max)
				 {
				  max = fourCount;
				  Mvote = 4;
				 }
					 
				 
				 if(fiveCount > max){
					  max = fiveCount;
				 	  Mvote = 5;
				 }
				 
			if(Mvote == 1 && max == 0)
				return 0;
			else
				return Mvote;
		 }
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
