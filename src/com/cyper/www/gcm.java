package com.cyper.www;


import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.AsyncTask;

public class gcm {

	private final static String GCM_REGISTER = "com.google.android.c2dm.intent.REGISTER";
	private final static String GCM_UNREGISTER = "com.google.android.c2dm.intent.UNREGISTER";
	private final static String APP = "app";
	private final static String SENDER = "sender";
	private Context context;
	private Database db;
	private static final String PREF_NAME = "CYPE";
	private int iD;
	int PRIVATE_MODE = 0;
	private final static  String EMPTY = "";
	private final static  String REG_ID = "registration_id";
	

	
	public gcm(Context context)
	{
		 this.context = context;
		 db = new Database();
		
	}
	
	public void registerGcm()
	{
	
		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registrationIntent.putExtra("sender","199247383137");
		context.startService(registrationIntent);
		
	}
	
	public void unRegisterGcm()
	{
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		context.startService(unregIntent);
	}
	
	public void checkGcmReg(final String email, final String reg_id)
	{

		new AsyncTask<String, String, String>()
		{

			@Override
			protected String doInBackground(String... params) {
				String sql = "select id, reg_id from gcm_reg where user_email='"+email+"' and reg_id='"+reg_id+"'";
				if(db.RunSQLStatement(sql) == null && reg_id.equals(null) || reg_id.equals(EMPTY))
				{
					unRegisterGcm();
					registerGcm();
				}
				else if(db.RunSQLStatement(sql) == null && !reg_id.equals(null))
				{
					RegisterGcmId(email, reg_id, context);
				}
			
				return null;
			}
			
		}.execute();
	}
	
	private void RegisterGcmId(String Email,  String registration,  Context context)
	{
		final int id_user  = 00;
		// get user_details id here to increment

				String RId;
				String sql = "select id from `gcm_reg` where id is not null order by id desc limit 1";
				
				
				if(db.RunSQLStatement(sql) == null)
					RId = ""+id_user;
				else
				{
					List<Item> Id  =  db.RunSQLStatement(sql);
					 RId = grabUserAtrribute(Id);
				}
				iD = Integer.parseInt(RId)+1;
				db.putIntoDomaingcm_reg(""+iD, Email, registration);
				
	
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
