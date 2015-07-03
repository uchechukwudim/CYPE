package cype.cpeople.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.os.AsyncTask;


import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.ImageFlipper;
import com.cyper.www.R;
import com.cyper.www.UserImageView;
import com.cyper.www.gcmServer;

import cype.database.Database;
import cype.date.CypeDate;

public class CpeopleExpandableAdapter extends BaseExpandableListAdapter {

	private static final String CLUBNAME = "clubname";
	private static final String EMAILS = "emails";
	private static final String IMAGES = "images";
	private static final String NAMES = "names";
	private static final String PREF_NAME = "CYPE";
	public static final String KEY_EMAIL = "email";
	private final static  String EMPTY = "";
	private static final String TRUE = "true";
	private static String IMAGE = "image";
	
	private Context context;
	private List<cpeople> cpeopleHeader;
	private HashMap<cpeople, List<Users>> users;

	
	private  CypeDate cypeDate;
	private  Database db;
	private  ArrayList<String> emails;
	private  ArrayList<String> names;
	private  ArrayList<String> images;
	private String user_email;
	
	private gcmServer sender;
	private boolean isAttendingCheckedin;

	
	public CpeopleExpandableAdapter(Context context, List<cpeople> cpeopleHeader, HashMap<cpeople, List<Users>> users)
	{
		this.context = context;
		this.cpeopleHeader = cpeopleHeader;
		this.users = users;
		cypeDate = new CypeDate();
		db = new Database();
		emails = new ArrayList<String>();
		images = new ArrayList<String>();
		names = new ArrayList<String>();
		SharedPreferences shared =this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		user_email = (shared.getString(KEY_EMAIL, ""));
		sender = new gcmServer();
		 
	}
	
	

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
        return this.users.get(this.cpeopleHeader.get(groupPosition))
                .get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final Users user = (Users) getChild(groupPosition, childPosition);
		
		if(convertView == null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.cpeople_users, null);
		}
		TextView userName = (TextView) convertView.findViewById(R.id.user_name);
		TextView userGender = (TextView) convertView.findViewById(R.id.user_gender);
		TextView userAccountType = (TextView) convertView.findViewById(R.id.user_account_type);
		TextView userStatus = (TextView) convertView.findViewById(R.id.user_status);
		TextView userPartyWivCount = (TextView) convertView.findViewById(R.id.partyWiv_count);
		TextView askDance = (TextView) convertView.findViewById(R.id.ask_dance);
		TextView askDrink = (TextView) convertView.findViewById(R.id.ask_drink);
		TextView partyWivMeClick = (TextView) convertView.findViewById(R.id.party_with_me);
		
		
		
		if(user.getEmail().equals(user_email))
		{
			askDance.setEnabled(false);
			askDrink.setEnabled(false);
			partyWivMeClick.setEnabled(false);
			askDance.setTextColor(context.getResources().getColorStateList(R.color.gryish));
			askDrink.setTextColor(context.getResources().getColorStateList(R.color.gryish));
			partyWivMeClick.setTextColor(context.getResources().getColorStateList(R.color.gryish));
		}
		else
		{
			if(checkAttendCheckinClub(user_email, user.getClubName()))
			{
				askDance.setEnabled(true);
				askDrink.setEnabled(true);
				partyWivMeClick.setEnabled(true);
				askDance.setTextColor(context.getResources().getColorStateList(R.color.white));
				askDrink.setTextColor(context.getResources().getColorStateList(R.color.white));
				partyWivMeClick.setTextColor(context.getResources().getColorStateList(R.color.white));
				
			}
			else
			{
				askDance.setEnabled(false);
				askDrink.setEnabled(false);
				partyWivMeClick.setEnabled(false);
				askDance.setTextColor(context.getResources().getColorStateList(R.color.gryish));
				askDrink.setTextColor(context.getResources().getColorStateList(R.color.gryish));
				partyWivMeClick.setTextColor(context.getResources().getColorStateList(R.color.gryish));
			}
		}
		
		
		ImageView userImage = (ImageView) convertView.findViewById(R.id.user_image);
		
		userName.setText(user.getUserName());
		userGender.setText(user.getGender());
		userAccountType.setText(user.getAccountType());
		userStatus.setText(user.getStatus());
		
	
	
		  if(user.getPartyWivMeCount() != null && (Integer.parseInt(user.getPartyWivMeCount()) == 1  || Integer.parseInt(user.getPartyWivMeCount()) == 0))
				userPartyWivCount.setText(user.getPartyWivMeCount()+" Person #PartyingWivMe");
			
		  if(user.getPartyWivMeCount() != null  && Integer.parseInt(user.getPartyWivMeCount()) > 1)
				userPartyWivCount.setText(user.getPartyWivMeCount()+" People #PartyingWivMe");
		  
			
		  getPWMCount(user.getEmail(), user.getClubName(), userPartyWivCount);
			
		  userImage.setImageBitmap(user.getImage());
		
		  AskDance(askDance, user_email, user.getEmail());
			
			
		  AskDrink(askDrink, user_email, user.getEmail());
			
		  PartyWivMe(partyWivMeClick, userPartyWivCount, user.getEmail(), user.getClubName(), cypeDate.CypeNewDate(), user.getUserName());
		  
		  viewUserImage(userImage, user.getImage());
			
	   return convertView;
	}
	
	
	
	private void viewUserImage(ImageView userImage, final Bitmap bitmap) {
		userImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			   Intent image = new Intent(context.getApplicationContext(), UserImageView.class);
			   image.putExtra(IMAGE, bitmap);
			   // Closing all the Activities
	            image.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	             
	            // Add new Flag to start new Activity
	            image.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	             
			   context.startActivity(image);
				
			}});
		
	}



	private void PartyWivMe(final TextView partyWivMeClick, final TextView userPartyWivCount, final String email,
			final String clubName2, String cypeNewDate, final String userName) {

		partyWivMeClick.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				isClickPartyWivMeOnce(userPartyWivCount, clubName2, user_email, email, userName);
				
			}

			});
		
	}

	private void isClickPartyWivMeOnce(final TextView userPartyWivCount,final String clubName, final String click_sender_email, 
										final String click_reciever_email, final String userName)
	{
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params) {
				String sql = "select * from check_partyWivMe_click where pwm_sender_email='"+click_sender_email+"'" +
						"and pwm_reciever_email='"+click_reciever_email+"' and club_name='"+clubName+"' and date='"+cypeDate.CypeNewDate()+"'";
				if(db.RunSQLStatement(sql).size() > 0)
					return true;
				else
					return false;
			}
			
			@Override
			protected void onPostExecute(Boolean result)
			{
		
				if(result)
				{
					Toast.makeText(context, "Your already partyingWiv "+userName, Toast.LENGTH_SHORT).show();
				}
				else
				{
					
					String PWM = userPartyWivCount.getText().toString();
					int pwmCount = getPWMNumber(PWM)+1;
				
					if(pwmCount == 1)
					{
						userPartyWivCount.setText(pwmCount+" Person #PartyingWivU");
					}
					else if(pwmCount >1)
					{
						userPartyWivCount.setText(pwmCount+" People #PartyingWivU");
					}
					
					updatePartyWivMe(click_reciever_email, clubName, pwmCount);
				
			  }
			}
			
			
		}.execute();
	}
	
     private int getPWMNumber(String pWM) {
		String count = "";
		for(int loop = 0; loop < pWM.length(); loop++)
		{
			if(pWM.charAt(loop) >= '0' && pWM.charAt(loop) <= '9')
				count = count+pWM.charAt(loop);
	    }
		if(count.equals(""))
			count = "0";
		
		return Integer.parseInt(count);
	}
	
	private void updatePartyWivMe(final String email, final String clubName2, final int pwmCount) {
		 
		 new AsyncTask<String, String, Boolean>()
		 {

			@Override
			protected Boolean doInBackground(String... arg0) {
			   String sql = "select id from party_Wiv_Me_count where email='"+email+"' and club_name='"+clubName2+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> items = db.RunSQLStatement(sql);
			    if(items.size() > 0)
			    {
			    	String ID = grabUserAtrribute(items);
			    	db.UpdatePartyWivMeDomain(ID, email, clubName2, pwmCount);
			    	String Id = ""+getNewPartyWivClickId();
			    	db.putIntoCheckPartyWivMeClick(""+zero_padding(Id), user_email, email, clubName2, cypeDate.CypeNewDate());
			    }
			   return null;
			}
			
		 }.execute();
	
	}
	
	private int getNewPartyWivClickId()
	{
		int id_user  = 00;
		// get user_details id here to increment
		String sql = "select id from `check_partyWivMe_click` where id is not null order by id desc limit 1";
		List<Item> club =  db.RunSQLStatement(sql);
		String id = grabUserAtrribute(club);
		
		if(id.equals(EMPTY) || id.equals(null))
			return id_user;
		else
		 return Integer.parseInt(id)+1;
	}
	
	private void getPWMCount(final String email, final String clubName, final TextView PWMCountView)
	{

		new AsyncTask<String, String, String>(){

			@Override
			protected String doInBackground(String... arg0) {
				String pwmCount = "";
				String sql1 = "select count from party_Wiv_Me_count where email='"+email+"' and club_name='"+clubName+"' and date='"+cypeDate.CypeNewDate()+"'";
				List<Item> items1 = db.RunSQLStatement(sql1);
				if(items1.size() > 0)
				{
					Item PWM = items1.get(0);
					ArrayList<Attribute> attributes1 = ( ArrayList<Attribute>) PWM.getAttributes();
					
					pwmCount = attributes1.get(0).getValue();
				}
				return pwmCount;
			}
			
			@Override
			protected void onPostExecute(String result)
			{
				int PWM = getPWMNumber(result);
				if(PWM == 1)
				{
					PWMCountView.setText(PWM+" Person #PartyingWivU");
				}
				else 
				{
					PWMCountView.setText(PWM+" People #PartyingWivU");
				}
				
			}
			
		}.execute();
	}

	private void AskDance(TextView askDance, final String senderEmail, final String recieverEmail)
	{
		askDance.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				new AsyncTask<String, String, Boolean>()
				{

					@Override
					protected Boolean doInBackground(String... arg0)
					{
						String sql = "select reg_id from gcm_reg where user_email='"+recieverEmail+"'";
						String sql1 = "select firstName from user_details where email='"+senderEmail+"'";
						
						 List<Item> items = db.RunSQLStatement(sql);
						 List<Item> Uname = db.RunSQLStatement(sql1);
						 String userName = "";
						 if(Uname.size() > 0)
						 {
							 userName = grabUserAtrribute(Uname);
						 }
						 if(items.size() == 1){
							String reg_id = grabUserAtrribute(items);
								sender.AskForADance(senderEmail, recieverEmail, reg_id, userName);
						 }
						 else if(items.size() > 1)
						 {
							 for(Item item : items)
							 {
								 ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
								 String reg_id = attributes.get(0).getValue();
								 sender.AskForADance(senderEmail, recieverEmail, reg_id, userName);
									
							 }
						 }//
						 return null;
					}
					
				}.execute();	
				
				
			}});
	}
	private void AskDrink(TextView askDrink, final String senderEmail, final String recieverEmail)
	{
		askDrink.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				new AsyncTask<String, String, Boolean>()
				{

					@Override
					protected Boolean doInBackground(String... arg0)
					{
						String sql = "select reg_id from gcm_reg where user_email='"+recieverEmail+"'";
						String sql1 = "select firstName from user_details where email='"+senderEmail+"'";
						List<Item> Uname = db.RunSQLStatement(sql1);
						 String userName = "";
						 if(Uname.size() > 0)
						 {
							 userName = grabUserAtrribute(Uname);
						 }
						 List<Item> items = db.RunSQLStatement(sql);
						 if(items.size() == 1){
							String reg_id = grabUserAtrribute(items);
								sender.AskForADrink(senderEmail, recieverEmail, reg_id, userName);
						 }
						 else if(items.size() > 1)
						 {
							 for(Item item : items)
							 {
								 ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
								 String reg_id = attributes.get(0).getValue();
								 sender.AskForADrink(senderEmail, recieverEmail, reg_id, userName);
							 }
						 }
								return null;		
					}
					
				}.execute();
				
			}});
	}
	private String grabUserAtrribute(List<Item> items)
	{
		if(items ==null || items.isEmpty())
			return EMPTY;
		else{
		Item item  = items.get(0);
		
		ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
		
	
		return attributes.get(0).getValue();
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		try
		{
		  count = this.users.get(this.cpeopleHeader.get(groupPosition)).size();
		}catch(NullPointerException e){
			String clubName = this.cpeopleHeader.get(groupPosition).getClubName();
			Toast.makeText(context, "No Cpeople for "+clubName+" yet", Toast.LENGTH_SHORT ).show();
		}
		
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return this.cpeopleHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.cpeopleHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		//
		final cpeople cp = (cpeople) getGroup(groupPosition);
		
		if(convertView ==null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.cpeople_header_holder, null);
		}
		//link varables here
		TextView clubName= (TextView) convertView.findViewById(R.id.club_name);
		clubName.setText(cp.getClubName());
		TextView cpeople_count = (TextView) convertView.findViewById(R.id.c_people);
		if(cp.getCpeopleTotal() == 1)	
			cpeople_count.setText(""+cp.getCpeopleTotal()+" Cperson");
		else
		{
			cpeople_count.setText(""+cp.getCpeopleTotal()+" Cpeople");
		}
		TextView b_attnd_count = (TextView) convertView.findViewById(R.id.boys_attend_count);
		b_attnd_count.setText(""+cp.getBoysAttendingCount());
		TextView b_checkedin_count = (TextView) convertView.findViewById(R.id.boys_chekedIn_count);
		b_checkedin_count.setText(""+cp.getBoysCheckedInCount());
		TextView g_attnd_count = (TextView) convertView.findViewById(R.id.att_girl_count);
		g_attnd_count.setText(""+cp.getGirlsAttendingCount());
		TextView g_checkedin_count = (TextView) convertView.findViewById(R.id.chk_girl_count);
		g_checkedin_count.setText(""+cp.getGirlsCheckedInCount());
		
		TextView view_photos = (TextView) convertView.findViewById(R.id.view_photos);
		
		//if(cypeDate.isCheckInTime())
			//view_photos.setEnabled(true);
		//else
			//view_photos.setEnabled(false);
		
		view_photos.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				//select image from picture domain
				//for each club for the particular date
				
				new AsyncTask<String, String, List<Item>>()
				{

					@Override
					protected List<Item> doInBackground(String... arg0) {
						String sql = "select image_name, user_email from clubs_pictures where club_name='"+cp.getClubName()+"' and date='"+cypeDate.CypeNewDate()+"'";
						List<Item> images = db.RunSQLStatement(sql);
						
						return images;
					}
					
					@Override
					protected void onPostExecute(List<Item>  result) {
						
						if(result == null)
						{
							Toast.makeText(context, "Unable to load", Toast.LENGTH_SHORT).show();
							Toast.makeText(context, "please check your internet connection ", Toast.LENGTH_SHORT).show();
						}
						else if(result.size()> 0)
						{
							getImageAndUserEmail(cp.getClubName(),result);	
						}
						else
						{
							Toast.makeText(context, "There are no uploaded images yet for "+cp.getClubName(), Toast.LENGTH_SHORT).show();
						}
						
				     }
					
				}.execute();
				
				
				
				
			}});

		return convertView;
	}
	
	private void getImageAndUserEmail(final String clubName, List<Item> result)
	{
		for(Item item: result)
		{
			
			List<Attribute> att = item.getAttributes();
			
			images.add(att.get(0).getValue());
			emails.add(att.get(1).getValue()); 
			final String Email = att.get(1).getValue();
			
			//get user names
			new AsyncTask<String, String, List<Item>>()
			{

				@Override
				protected List<Item> doInBackground(String... arg0) {
					
					String sql = "select firstName from user_details where email='"+Email+"'";
					List<Item> Names = db.RunSQLStatement(sql);
					if(Names == null)
					{
						return Names;
					}
					else if(Names.size() > 0)
					{
						Item name = Names.get(0);
						
							List<Attribute> atten = name.getAttributes();
					
							names.add(atten.get(0).getValue()); 
			
					}
					return null;
				}
				@Override
				protected void onPostExecute(List<Item>  result) {
					
					Intent intent = new Intent(context, ImageFlipper.class);
					
					intent.putExtra(CLUBNAME, clubName);
					intent.putStringArrayListExtra(IMAGES, images);
					intent.putStringArrayListExtra(EMAILS, emails);
					intent.putStringArrayListExtra(NAMES, names);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		             
		            // Add new Flag to start new Activity
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
					
				
			}.execute();
		
		}
	}
	
	private boolean checkAttendCheckinClub(final String email, final String clubName)
	{
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... arg0) {
				String sql = "select * from `club_function` where user_email ='"+email+"'  and club_name ='"+clubName+"' and date='"+ cypeDate.CypeNewDate()+"' and (checkedin ='"+TRUE+"' or attending='"+TRUE+"')";
				if(db.RunSQLStatement(sql).size() > 0)
				isAttendingCheckedin = true;
				else
					isAttendingCheckedin =  false;
				
				return isAttendingCheckedin;
			}
			
			@Override
			protected void onPostExecute(Boolean result)
			{
				isAttendingCheckedin = result;
			}
			
		}.execute();
		
		return isAttendingCheckedin;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
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
