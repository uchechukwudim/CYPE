package com.cyper.www;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.database.Database;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver{
	
	private final static String APP_NAME = "Cype";
	private final static String GCM_REGISTER = "com.google.android.c2dm.intent.REGISTER";
	private final static String GCM_UNREGISTER = "com.google.android.c2dm.intent.UNREGISTER";
	private final static String GCM_RECIEVE = "com.google.android.c2dm.intent.RECEIVE";
	private final static  String REG_ID = "registration_id";
	private final static  String EMPTY = "";
	private final static String EMAIL = "Email";
	private final static String MESSAGE = "message";
	private final static String USER = "email";
	private final static String DANCEREQUEST = "Dance Request";
	private final static String DRINKREQUEST = "Drink Request";
	private final static String REQUEST = "request";
	Database db;
	private static final String PREF_NAME = "CYPE";
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		 
		this.context = context;
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			Log.d("RCHECK", "working");
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context.getApplicationContext(), intent);
	       
	    }
	
	}


	private void handleMessage(Context applicationContext, Intent intent) {
		String receive = intent.getAction();
		
		if(receive.equals(GCM_RECIEVE))
		{
			String message  = intent.getStringExtra(MESSAGE);
			String request = intent.getStringExtra(REQUEST);
			String user = intent.getStringExtra(REQUEST);
			//handle with notification here
		}
		
	}
	 public void createNotification(Context context, String TitleRequest, String user, String message) {
		    // Prepare intent which is triggered if the
		    // notification is selected
		    Intent intent = new Intent(context.getApplicationContext(), NotificationDialog.class);
		    intent.putExtra(MESSAGE, message);
		    PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

		    // Build notification
		    // Actions are just fake
		    Notification noti = new Notification.Builder(context.getApplicationContext())
		    	.setTicker(APP_NAME)
		        .setContentTitle(TitleRequest)
		        .setContentText(user+message)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent).getNotification();
		      
		       
		    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    // hide the notification after its selected
		    noti.flags |= Notification.FLAG_AUTO_CANCEL;

		    notificationManager.notify(0, noti);

		  }

	private void handleRegistration(Context context, Intent intent) {

		String registration = intent.getStringExtra(REG_ID);
		
		String error =intent.getStringExtra("error");
		String unReg = intent.getStringExtra("unregistered");
		SharedPreferences shared = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		String Email = (shared.getString(EMAIL, ""));
		
		if(error != null)
		{
			Log.d("c2dm", "registration failed");
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
			
	    	Toast.makeText(context,"C2DM unregistered sucessfully",Toast.LENGTH_SHORT).show();
		}
		else if (registration != null) 
		{
			Toast.makeText(context, ""+registration, Toast.LENGTH_SHORT).show();
			db.putIntoDomaingcm_reg(""+getNewClubFuncId(), Email, registration);
			shared.edit().remove(EMAIL);
		}
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
