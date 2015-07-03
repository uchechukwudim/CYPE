package cype.clubs.adapter;

import imageUploader.ImageUploader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.ClubDetails;
import com.cyper.www.ClubSearchInitializer;
import com.cyper.www.R;
import com.cyper.www.R.drawable;

import cype.database.Database;
import cype.date.CypeDate;


public class ClubsAdapter extends ArrayAdapter<club>{
	
	private static final String ADDRESS = "Address";
	private static final String CLUB_NAME = "Clubname";
	private static final int FILE_SELECT_CODE = 0;
    private ArrayList<club> clubs;
    private static LayoutInflater inflater=null;
    private Activity context;
    URL url;
	Bitmap bmp;
	ImageUploader upload;
	final String ImageName = "download.jpg";
	String grey = "grey";
	Database db;
	List<Item> clubs_res;
	
	private String EMAIL;
	private String CLUBNAME;
	private String CITY;
	final static String EMPTY = "";
	final String FALSE = "false";
	private ClubattendOnclickHandler attendingHandler;
	private ClubMoodOnclickHander cMoodeHandler;
	private ClubMusicRatingOnclickHandler musicRatingHandler;
	private CypeDate cypeDate;
	private  final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.click_sound);
 
	public ClubsAdapter(Activity context, int layoutResourceId, ArrayList<club> clubs) {
		super(context, layoutResourceId, clubs);
        /********** Take passed values **********/
      
         this.clubs= clubs;
         inflater = context.getLayoutInflater();
         this.context = context;
         db = new Database();
         cypeDate = new CypeDate();
         
         //http://www.freesfx.co.uk/ for sound effects
        
        
 }

	@Override
	public View getView(int rowIndex, View convertView, ViewGroup parent)
	{
		
		
		View row = convertView;
		if(convertView == null) 
		{
			final club Club = clubs.get(rowIndex);
		    row = inflater.inflate(R.layout.clubs_items_holder1, null);
		    final ViewHolder  holder = new ViewHolder();
		    attendingHandler = new ClubattendOnclickHandler(context,holder.attending, row, holder.SharePic, holder.clubMood, holder.rateMusic);
		    cMoodeHandler = new ClubMoodOnclickHander(context,row,holder.clubMood);
		    musicRatingHandler = new ClubMusicRatingOnclickHandler (context, row, holder.rateMusic);
		    
		    
		  //set Attending, checkin or disable both
			attendingHandler.runSetAttending(Club.getClubName(),Club.getEmail(),Club.getCity());
			//attendingHandler.updateClubFunctionResult(Club.getClubName(), Club.getCity());
			//cMoodeHandler.updateClubFuncResult(Club.getClubName(), Club.getCity());
			
			holder.logo = (TextView) row.findViewById(R.id.club_logo);
		    holder.logo.setText(Club.getClubName());
			
		    holder.club_logo = (ImageView) row.findViewById(R.id.club_picture);
		     new AsyncTask<String, String, Bitmap>(){
		    	 Bitmap bm;
				@Override
				protected Bitmap doInBackground(String... img) {
					// TODO Auto-generated method stub
					try {
						url = new URL(img[0]);
						bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
						
					} catch (MalformedURLException e) {
						 ClubsAdapter.this.context.runOnUiThread(new Runnable(){
							@Override
							public void run() {
								 Toast.makeText(context.getApplicationContext(), "Sorry could not get your "+Club.getClubName()+" Logo", Toast.LENGTH_SHORT).show();
							}});
					}catch(IOException ee){
						ClubsAdapter.this.context.runOnUiThread(new Runnable(){
							@Override
							public void run() {
								 Toast.makeText(context.getApplicationContext(), "Sorry could not get your "+Club.getClubName()+" Logo", Toast.LENGTH_SHORT).show();
							}});
					}
				
					return bm;
				}
				
				 @Override
			        protected void onPostExecute(Bitmap result) 
				 	{
					 	holder.club_logo.setImageBitmap(result);
				 	}
		    	 
		     }.execute(Club.getImage());
		    
			

			holder.attendingView = (TextView) row.findViewById(R.id.attend_view);
			holder.chechedInView = (TextView) row.findViewById(R.id.checkedIn_view);
			holder.musicRateView = (TextView) row.findViewById(R.id.rateMusic_view);
			
			holder.clubMoodView = (TextView) row.findViewById(R.id.club_mood);
			holder.clubMoodView.setText(Club.getMood());
			
			holder.checkedInViewCount = (TextView) row.findViewById(R.id.chckedIn_count);
			holder.checkedInViewCount.setText(Club.getCheckedInCount());
			/*
			if(Integer.parseInt(Club.getAttendingCount()) > 0 && Integer.parseInt(Club.getCheckedInCount()) == 0 )
			{
				//MarginLayoutParams paramsCIV = (MarginLayoutParams) holder.attendingView .getLayoutParams();
			    ///paramsCIV.rightMargin = 17; 
			    
			    //MarginLayoutParams paramsMRV = (MarginLayoutParams) holder.musicRateView.getLayoutParams();
			    //paramsMRV.leftMargin = 0; 
			}
			else if((Integer.parseInt(Club.getAttendingCount()) > 0 && Integer.parseInt(Club.getCheckedInCount()) > 0 ))
			{
				//MarginLayoutParams paramsCIV = (MarginLayoutParams) holder.chechedInView .getLayoutParams();
			   // paramsCIV.rightMargin = 15; 
			    
			    //MarginLayoutParams paramsMRV = (MarginLayoutParams) holder.musicRateView.getLayoutParams();
			    //paramsMRV.leftMargin = -14; 
			}
				
		*/
			
			
			holder.musicRateCount = (TextView) row.findViewById(R.id.rateMusic_count);
			holder.musicRateCount.setText(Club.getRateMusicCount());
			
			holder.ratingStar = (ImageView) row.findViewById(R.id.rate_star);
			if(Club.getRateMusicCount().equals("0"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.zero_star));
			}
			else if(Club.getRateMusicCount().equals("1"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.one_star));
			}
			else if(Club.getRateMusicCount().equals("2"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.two_star));
			}
			else if(Club.getRateMusicCount().equals("3"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.three_star));
			}
			else if(Club.getRateMusicCount().equals("4"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.four_star));
			}
			else if(Club.getRateMusicCount().equals("5"))
			{
				holder.ratingStar.setImageDrawable(context.getResources().getDrawable(R.drawable.five_star));
			}

			
			holder.attendingViewCount = (TextView) row.findViewById(R.id.attend_count);
			holder.attendingViewCount.setText(Club.getAttendingCount());
			
			
			
			holder.SharePic = (ImageView) row.findViewById(R.id.sharepic_click);
			holder.SharePic.setEnabled(false);
			holder.clubMood = (ImageView) row.findViewById(R.id.clubMood_click);
			holder.clubMood.setEnabled(false);
			holder.rateMusic = (ImageView) row.findViewById(R.id.ratemusic_click);
			holder.rateMusic.setEnabled(false);
			
			holder.attending = (ImageView) row.findViewById(R.id.attcheckin_click);
			holder.attending.setEnabled(false);
			
			;
			
			//set onclick listener
			attendingOnlick(Club, holder.attending, holder.SharePic, holder.clubMood, holder.rateMusic, holder.attendingViewCount, holder.checkedInViewCount);
			//OnClick listener methods
			CmoodOnlick(Club,holder.clubMood);
			rateMusicOnlick(Club, holder.rateMusic);
			ShareOnlick(Club,holder.SharePic);
			logoOnClick(Club,holder.logo);
			row.setTag(holder);
		}
		
		return row;
	}
	 public void attendingOnlick(final club Club, final ImageView attending, final ImageView sharePic, 
			 					final ImageView clubMood, final ImageView musicRating, final TextView attendingViewCount, final TextView checkedInViewCount )
	 {
		 attending.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mp.start();
				 String contentDesc = attending.getContentDescription().toString();
					
					if(contentDesc.equals("thumbUp"))
					{
						   EMAIL = Club.getEmail();
						   CLUBNAME = Club.getClubName();
						   CITY = Club.getCity();
						   	
						 
						//put into party_Wivme_count domain
						   
						
						//insert into club_function
						attendingHandler.runPutRequestForAttending(CLUBNAME, EMAIL, CITY);
					
							//get attendingViewCount and plus it
							   String attCount = attendingViewCount.getText().toString();
							   int  AttCount = Integer.parseInt(attCount)+1;
							   attendingViewCount.setText(""+AttCount);
							   
								//check if time is 6pm to make this available for clicking
								   if(cypeDate.isCheckInTime()){		
									   attending.setImageResource((R.drawable.checkedin));	
									   attending.setContentDescription("checkin");
							    	}
							    	else{
							    		attending.setImageResource((R.drawable.checkedin));
							    		attending.setEnabled(false);
							    	}
					
						//wait for 2 seconds before running update
						Handler h = new Handler();
						h.postDelayed(new Runnable() {
				            @Override
							public void run() {
				            	// select all attending from club_function count, update 
								//club_function_result domain with it.
								attendingHandler.updateClubFuncResultAttending(CLUBNAME, CITY);
				            }
				        }, 3000);
						
					}
					else if(contentDesc.equals("checkin"))
					{
						
						//update checkedin in club_function domain
						 String UserEmail = Club.getEmail();
						 final String ClubName = Club.getClubName();
						 final String City = Club.getCity();
						 
						 attendingHandler.runRequestForCheckedin(ClubName, UserEmail, City);
						 
						
							//get attendingViewCount and plus it
							   String checkCount = checkedInViewCount.getText().toString();
							   int  CheckCount = Integer.parseInt(checkCount)+1;
							   checkedInViewCount.setText(""+CheckCount);
							   attending.setImageResource((R.drawable.checkedin));
								
								//enable other Clickable's when CheckedIn
								attending.setEnabled(false);
								sharePic.setEnabled(true);
								
								clubMood.setEnabled(true);
								
								musicRating.setEnabled(true);
							
							    Handler h = new Handler();
								h.postDelayed(new Runnable() {
						            @Override
									public void run() {
						            	// select all checkedin from club_function count, update 
										//club_function_result domain with it.
									 attendingHandler.updateClubFuncResultCheckedin(ClubName, City);
						            }
						        }, 3000);
						
						 
						
						
					
					}
				}});//attend and check in ends here
	 }
	 
	 public void logoOnClick(final club Club, TextView clubName)
	 {
		 clubName.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mp.start();
					Intent goToClubDetails = new Intent(context, ClubDetails.class);
					goToClubDetails.putExtra(CLUB_NAME, Club.getClubName());
					goToClubDetails.putExtra(ADDRESS, Club.getClubAddress());
					context.startActivity(goToClubDetails);
				}});
	 }
	 
	 public void ShareOnlick(final club Club, ImageView SharePic)
	 {
		 SharePic.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mp.start();
						SharedPreferences share = context.getSharedPreferences("share", Context.MODE_PRIVATE);
					    //save in cache memory
					    share.edit().putString("club_name", Club.getClubName()).commit();
					    share.edit().putString("user_email", Club.getEmail()).commit();
					    
					    Intent target = new Intent(Intent.ACTION_GET_CONTENT); 
						target.setType("image/*"); 
						target.addCategory(Intent.CATEGORY_OPENABLE);
						context.setResult(Activity.RESULT_OK, target);
						context.startActivityForResult(Intent.createChooser(target, "Choose Image"), FILE_SELECT_CODE);
					
				}});
	 }
	 
	 
	 public void rateMusicOnlick(final club Club, ImageView rateMusic)
	 {
		 rateMusic.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

				//	mp.start();
					AlertDialog.Builder DAlert = new AlertDialog.Builder(v.getContext());
					DAlert.setTitle("Rate Music");
					DAlert.setItems(R.array.music_rate, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							 ListView lv = ((AlertDialog)dialog).getListView();
							 final String musicRating = (String)lv.getAdapter().getItem(which);
							 String UserEmail = Club.getEmail();
							 final String ClubName = Club.getClubName();
							 final String City = Club.getCity();
							 
							 musicRatingHandler.runPutRequestMusicRating(ClubName, UserEmail, musicRating, City);
							 
							 Handler h = new Handler();
								h.postDelayed(new Runnable() {
						            @Override
									public void run() {
						             musicRatingHandler.UpdateupdateClubFuncResultMusicRating(ClubName, City);
						             
						            }
						        }, 2000);
								
								
							
						}});
			
					DAlert.show();
				}});//rating music ends here
	 }
	 
	 public void CmoodOnlick(final club Club, ImageView clubMood)
	 {
		 clubMood.setOnClickListener(new OnClickListener(){		
				@Override
				public void onClick(View v) {
				//	mp.start();
					AlertDialog.Builder DAlert = new AlertDialog.Builder(v.getContext());
					DAlert.setTitle("Vote cMood");
					DAlert.setItems(R.array.club_moods, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*collect voted cmood, insert into club_function, 
							 * get all details from clubs_function domain for calculation,
							 * give result to club_function_result, refresh activity
							 */
							 ListView lv = ((AlertDialog)dialog).getListView();
							 final String cMood= (String)lv.getAdapter().getItem(which);
							
							 String UserEmail = Club.getEmail();
							 final String ClubName = Club.getClubName();
							 final String City = Club.getCity();
							 cMoodeHandler.runPutRequestForCmood(ClubName, UserEmail, City, cMood);
							 
							 Handler h = new Handler();
								h.postDelayed(new Runnable() {
						            @Override
									public void run() {
						            	 cMoodeHandler.updateClubFuncResultCmood(ClubName, City);
						            	 
						            }
						        }, 2000);
							
							 
							
							
						}});
			
					DAlert.show();
				
				}});//cmood dialog show ends here
	 }
	
	 
	
	 
	
	
	private static class ViewHolder{
		
		//views 

		TextView logo;
		TextView clubMoodView;
		TextView clubOpenTimeCountDown;
		
		TextView attendingView;
		TextView attendingViewCount;
		
		TextView chechedInView;
		TextView checkedInViewCount;
		
		TextView musicRateView;
		TextView musicRateCount;
		ImageView ratingStar;
		
		ImageView club_logo;
		
		//clickable
		ImageView SharePic;
		ImageView clubMood;
		ImageView attending;
		ImageView rateMusic;
	}
	
	
}
