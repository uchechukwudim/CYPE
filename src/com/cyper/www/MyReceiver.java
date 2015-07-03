package com.cyper.www;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.cyper.www.R;

public class MyReceiver extends BroadcastReceiver {
	private final static String APP_NAME = "Cype";
	private final static String GCM_REGISTER = "com.google.android.c2dm.intent.REGISTER";
	private final static String GCM_UNREGISTER = "com.google.android.c2dm.intent.UNREGISTER";
	private final static String GCM_RECIEVE = "com.google.android.c2dm.intent.RECEIVE";
	private final static  String REG_ID = "registration_id";
	private final static  String EMPTY = "";
	 public static final String KEY_EMAIL = "email";
	private final static String MESSAGE = "data";
	private final static String USER = "data1";
	private final static String EMAIL = "Email";
	
	static final String AB = "0123456789";
	static Random rnd;
	final static int CODE_LENGTH = 6;


	private final static String REQUEST = "data2";
	private final static String DANCEREQUEST = "Dance Request";
	private final static String DRINKREQUEST = "Drink Request";
	private final static String ACCEPTEDQUEST = "Accepted Request";
	private final static String REQUEST1 = "request";
	
	private Database db;
	private static final String PREF_NAME = "CYPE";
	private Context context;

	private int iD;
	private int PRIVATE_MODE = 0;
	private  MediaPlayer mp;
	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		 mp = MediaPlayer.create(context.getApplicationContext(), R.raw.notification_sound);
		rnd = new Random();
		db = new Database();
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    }  else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context.getApplicationContext(), intent);
	       
	    }
	}

	private void handleMessage(Context applicationContext, Intent intent) {
		String receive = intent.getAction();
		
		if(receive.equals("com.google.android.c2dm.intent.RECEIVE"))
		{
			String message  = intent.getStringExtra(MESSAGE);
			String request = intent.getStringExtra(REQUEST);
			String useremail = intent.getStringExtra(USER);
			
			if(request.equals(ACCEPTEDQUEST))
			{
		
				
					Toast.makeText(applicationContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
					createNotification(context,request,useremail, message);
					mp.start();
	
			}
			else if(request.equals(DANCEREQUEST))
			{
				Toast.makeText(applicationContext.getApplicationContext(), request, Toast.LENGTH_SHORT).show();
				//handle with notification here
				createNotification(context,request,useremail, message);
				mp.start();
			}
			else if(request.equals(DRINKREQUEST))
			{
				Toast.makeText(applicationContext.getApplicationContext(), request, Toast.LENGTH_SHORT).show();
				//handle with notification here
				createNotification(context,request,useremail, message);
				mp.start();
			}
			
		}
		
		
	}

	private void createNotification(Context context, String TitleRequest, String user, String message) {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(context.getApplicationContext(), NotificationDialog.class);
	    intent.putExtra(MESSAGE, message);
	    intent.putExtra(USER, user);
	    intent.putExtra(REQUEST1, TitleRequest);
	    String random = randomString(); 
	    PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), Integer.parseInt(random), intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(context.getApplicationContext())
	    	.setTicker(APP_NAME+" "+TitleRequest)
	        .setContentTitle(TitleRequest)
	        .setContentText(message)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent).getNotification();
	      
	  
	    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(Integer.parseInt(random), noti);

	  }
	
	
	

	private void handleRegistration(Context context, Intent intent) {
		
		
		String registration = intent.getStringExtra(REG_ID);
		
		String error =intent.getStringExtra("error");
		String unReg = intent.getStringExtra("unregistered");
		SharedPreferences shared = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		String Email = shared.getString(KEY_EMAIL, "");
		
		if(error != null)
		{
			Log.d("c2dm", "registration failed"+error);
		    if(error == "SERVICE_NOT_AVAILABLE"){
		    	Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
		    }
		    else if(error == "ACCOUNT_MISSING")
		    {
		    	Log.d("c2dm", "ACCOUNT_MISSING");
		    }
		    else if(error == "AUTHENTICATION_FAILED")
		    {
		    	Log.d("c2dm", "AUTHENTICATION_FAILED");
		    }
		    else if(error == "TOO_MANY_REGISTRATIONS")
		    {
		    	Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
		    }
		    else if(error == "INVALID_SENDER")
		    {
		    	Log.d("c2dm", "INVALID_SENDER");
		    }
		    else if(error == "PHONE_REGISTRATION_ERROR")
		    {
		    	Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
		    }
		}
		else if(unReg != null)
		{
			
	    	Log.d("UNREGISTER","C2DM unregistered sucessfully");
		}
		else if (registration != null) 
		{
			RegisterGcmId(Email, registration, context);	
			 Log.d("REG", ""+registration);
		}
		
	}
	
	private void RegisterGcmId(final String Email, final String registration, final Context context)
	{
		final int id_user  = 00;
		// get user_details id here to incremen
				
        new AsyncTask<String, String, String>(){

			@Override
			protected String doInBackground(String... arg0) {
				String sql = "select id from gcm_reg where user_email='"+Email+"'";
				List<Item> IDS = db.RunSQLStatement(sql);
				
				if(IDS.size() == 0)
				{
					String RId;
					String sql1 = "select id from `gcm_reg` where id is not null order by id desc limit 1";
					
					
					if(db.RunSQLStatement(sql1) == null || db.RunSQLStatement(sql1).size() == 0)
					{
						RId = ""+id_user;
						
						iD = Integer.parseInt(RId)+1;
						db.putIntoDomaingcm_reg(""+iD, Email, registration);
						
						SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
					    Editor  editor = pref.edit();
					    editor.putString(REG_ID, registration);
					    editor.commit();
					}
					else
					{
						
						List<Item> Id  =  db.RunSQLStatement(sql1);
						String Rid = grabUserAtrribute(Id);
							iD = Integer.parseInt(Rid)+1;
							db.putIntoDomaingcm_reg(""+iD, Email, registration);
							
							SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
						    Editor  editor = pref.edit();
						    editor.putString(REG_ID, registration);
						    editor.commit();
					}
			}
			else if(IDS.size() > 0)
			{
			
				List<Item> ids = db.RunSQLStatement(sql);
				Item id = ids.get(0);
				
				ArrayList<Attribute> attributes = (ArrayList<Attribute>) id.getAttributes();
				
				String ID = attributes.get(0).getValue();
				db.putIntoDomaingcm_reg(ID, Email, registration);
				
				SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
			    Editor  editor = pref.edit();
			    editor.putString(REG_ID, registration);
			    editor.commit();
			
				
			}
        
				return null;
			}
        	
        }.execute();
				
	
		
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
	
	private String randomString() 
	{
	   StringBuilder sb = new StringBuilder(CODE_LENGTH);
	   for( int i = 0; i <  CODE_LENGTH ; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	
	private int getNewClubFuncId()
	{
		int id_user  = 00;
		// get user_details id here to increment
		String sql = "select id from `gcm_reg` where id is not null order by id desc limit 1";
		List<Item> club =  db.RunSQLStatement(sql);
		String id = grabUserAtrribute(club);
		
		if(id.equals(EMPTY) || id.equals(null))
			return id_user;
		else
		 return Integer.parseInt(id)+1;
	}
	
}
