package cype.club.grabber;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.clubs.adapter.ClubMoodOnclickHander;
import cype.clubs.adapter.ClubMusicRatingOnclickHandler;
import cype.clubs.adapter.ClubattendOnclickHandler;
import cype.clubs.adapter.ClubsAdapter;
import cype.clubs.adapter.club;
import cype.database.Database;
import cype.date.CypeDate;



public class GoogleClubSearchProcessor {
	ArrayList<club> clubs = new ArrayList<club>();
	Activity activity;
	private View view;
	private ListView listview;
	List<Item> club_res;
	Database db;
	CypeDate cypeDate;
	private final static String EMPTY_STRING="";
	private final static String NONE = "None";
	private final static int ZERO = 0;
	private final static int ONE = 1;
	private final static int TWO = 2;
	private final static int THREE = 3;
	
	private ClubattendOnclickHandler attendingHandler;
	private ClubMoodOnclickHander cMoodeHandler;
	private ClubMusicRatingOnclickHandler musicRatingHandler;
	
	@SuppressWarnings("unchecked")
	public GoogleClubSearchProcessor(Activity activity, View view, ListView list, ArrayList<club> clubs)
	{
		this.activity = activity;
		this.view = view;
		this.listview = list;
		this.clubs = clubs;
		db = new Database();
		cypeDate = new CypeDate();
		attendingHandler = new ClubattendOnclickHandler(activity);
		cMoodeHandler = new ClubMoodOnclickHander(activity);
		musicRatingHandler = new ClubMusicRatingOnclickHandler(activity); 
		 new processClubs().execute(this.clubs);
	}
	
	private class processClubs extends AsyncTask<ArrayList<club>, String , ArrayList<club>>
	{

		@Override
		protected ArrayList<club> doInBackground(ArrayList<club>... Clubs) {
			
			return UpdateClubFromGoogleWithCypeValues(Clubs[ZERO]);	
		}
		
		 @Override
	        protected void onPostExecute(ArrayList<club> result) 
		 	{
			  
			  
			 	ClubsAdapter cAdapter = new ClubsAdapter(activity,R.layout.clubs_layout , result );
			  	listview = (ListView)view.findViewById(R.id.clubs_list);
            	listview.setAdapter(cAdapter);
		 	}
		
	}
	
	/*
	 * 
	 */
	private ArrayList<club> UpdateClubFromGoogleWithCypeValues(ArrayList<club> clubs)
	{
		
		
		ArrayList<club> Clubs = new ArrayList<club>();
	
		for(club Club : clubs)
		{
			attendingHandler.updateClubFunctionResult(Club.getClubName(), Club.getCity());
			cMoodeHandler.updateClubFuncResult(Club.getClubName(), Club.getCity());
			musicRatingHandler.updateClubFunctionResult(Club.getClubName(), Club.getCity());
			
			String sql = "select  checkedin_result, attending_result, music_rating_result, " +
						"cmood_vote_result from `club_function_results` " +
						"where club_name = '"+Club.getClubName()+"' and city='"+Club.getCity()+"' and date='"+cypeDate.CypeNewDate()+"'";
			
			     club_res = db.RunSQLStatement(sql);
			     if(club_res==null){
			    	 
			    }
			    else if(club_res.size() > ZERO) 
					{
						setOtherClubAttributeValues(club_res, Club);
						Clubs.add(Club);
					}
					else{
						Club.setAttendingCount(EMPTY_STRING+ZERO);
						Club.setCheckedInCount(EMPTY_STRING+ZERO);
						Club.setRateMusicCount(EMPTY_STRING+ZERO);
						Club.setMood(NONE);
						Clubs.add(Club);
					}
			
		}
		
		return Clubs;
	}
	
	private void setOtherClubAttributeValues(List<Item> club_res, club Club)
	{
			Item res = club_res.get(ZERO);
			ArrayList<Attribute> attributes = (ArrayList<Attribute>) res.getAttributes();
			
			String checkedIn = attributes.get(ZERO).getValue();
			String attending = attributes.get(ONE).getValue();
			String music_rating = attributes.get(TWO).getValue();
			String cmood_vote = attributes.get(THREE).getValue();
			
			//set other club values
			Club.setCheckedInCount(checkedIn);
			Club.setAttendingCount(attending);
			Club.setRateMusicCount(music_rating);
			Club.setMood(cmood_vote);
	}

	public ArrayList<club> getClubs() {
		return clubs;
	}

	public void setClubs(ArrayList<club> clubs) {
		this.clubs = clubs;
	}
	
	
}
