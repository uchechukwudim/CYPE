package com.cyper.www;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;
import cype.database.Database;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class NotificationDialog extends Activity {
	private gcmServer Gserver;
	private final static String DANCEREQUEST = "Dance Request";
	private final static String DRINKREQUEST = "Drink Request";
	private final static String ACCEPTEDQUEST = "Accepted Request";
	private static final String DANCE = "Dance";
	private static final String DRINK = "Drink";
	private final static String REQUEST = "request";
	
	public static final String KEY_EMAIL = "email";
	private final static String MESSAGE = "data";
	private final static  String EMPTY = "";
	private final static String USER = "data1";
	private String UserName;
	private static final String PREF_NAME = "CYPE";
	private Database db;
	private String email;
	String Request;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_dialog);
		Gserver = new gcmServer();
		db = new Database();
		
		SharedPreferences shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		 email = (shared.getString(KEY_EMAIL, ""));
		
		Bundle bundle = getIntent().getExtras();
		String Email = bundle.getString(USER);
		String message = bundle.getString(MESSAGE);
		String request = bundle.getString(REQUEST);
		if(request.equals(DANCEREQUEST)){
			Request = DANCE;
			showAcceptDialogBox(message,  Email, Request);
		}
		else if(request.equals(DRINKREQUEST)){
			Request = DRINK;
		showAcceptDialogBox(message,  Email, Request);
		}
		else if(request.equals(ACCEPTEDQUEST))
		{
			showAcceptDialogBoxAcceptedRequest(message,  Email, Request);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_dialog, menu);
		return true;
	}
	
	private void showAcceptDialogBox(String message, final String email, final String request)
	{
		final String CONFIRMATION_MESSAGE = message;
		new DialogFragment(){
			@Override
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the Builder class for convenient dialog construction
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage(CONFIRMATION_MESSAGE)
		        
		               .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
		                   @Override
						public void onClick(DialogInterface dialog, int id) {
		                	   //send notification to the accepted user
		                	   new AsyncTask<String, String, String>()
		                	   {

								@Override
								protected String doInBackground(
										String... params) {
										String userName = getUserName(NotificationDialog.this.email);
				                	   Gserver.AcceptedRequest(userName,getUsersRegId(email), request);
					   				   finish();
								 return null;
								}
		                		   
		                	   }.execute();
		                	  
		                   }

						
		               })
		          
		          .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
	   					finish();
					}});  
		        // Create the AlertDialog object and return it
		        return builder.create();
		    }
		}.show(getFragmentManager(), null);
	}
	
	private void showAcceptDialogBoxAcceptedRequest(String message, final String email, final String request)
	{
		final String CONFIRMATION_MESSAGE = message;
		new DialogFragment(){
			@Override
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the Builder class for convenient dialog construction
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage(CONFIRMATION_MESSAGE)
		        
		               .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		                   @Override
						public void onClick(DialogInterface dialog, int id) {
		                	   //send notification to the accepted user
		                	   finish();
		                	  
		                   }

						
		               });
		          
		            
		        // Create the AlertDialog object and return it
		        return builder.create();
		    }
		}.show(getFragmentManager(), null);
	}

	private String getUserName(final String email) 
	{
		
		
				String sql = "select firstName from user_details where email='"+email+"'";
				List<Item> username = db.RunSQLStatement(sql);
				
				String Un = grabUserAtrribute(username);
				return Un;

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
	
	private String getUsersRegId(final String Email)
	{
		
		
				String sql = "select reg_id from gcm_reg where user_email='"+Email+"'";
				List<Item> regid = db.RunSQLStatement(sql);
				
				String RegId = grabUserAtrribute(regid);
				 Log.d("IDD", RegId);
				return RegId;
			
		
	}
	
	
}
