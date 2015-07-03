 package com.cyper.www;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;


import android.widget.Button;
import android.widget.EditText;


import org.apache.commons.codec.digest.DigestUtils;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;


import cype.database.Database;
import cype.email.sender.Mail;

public class ForgotPassword extends Activity {
	private static final String APP_NAME = "CYPE";
	private EditText email;
	private Button submit;
	private Database db;
	private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Random rnd;
	private final static int CODE_LENGTH = 6;
	private final static String EMPTY ="";
	private final static String EMPTYFILDMESSAGE = "Email address field is Empty";
	private final static String INVALIDEMAILMESSAGE = "Email address format is invalide";
	private final static String UNREGUSERMESSAGE = " is not a CYPE user";
	
	
	Mail mail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foregot_password);
		db = new Database();
		mail = new Mail();
		email = (EditText) findViewById(R.id.fp_user_email);
		submit = (Button) findViewById(R.id.fp_submit);
		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				runprocess(email.getText().toString());
			}});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.foregot_password, menu);
		return true;
	}
	

	private void runprocess(final String email)
	{
			new AsyncTask<String, String, Boolean>()
			{
				@Override				protected Boolean doInBackground(String... arg0) {
					ProcessForgetPassword(email);
					return null;
				}
			
			}.execute();
	}
	
	private void ProcessForgetPassword(String email)
	{
		if(email.replace("/'\'", "").equals(""))
		{
			//empty field
			dialogB box = new dialogB();
			box.setMessage(EMPTYFILDMESSAGE);
			box.show(getFragmentManager(), EMPTYFILDMESSAGE); 
		}
		else if(!isEmailValid(email.replace("/'\'", "")))
		{
			//invalid email format
			dialogB box = new dialogB();
			box.setMessage(INVALIDEMAILMESSAGE);
			box.show(getFragmentManager(), INVALIDEMAILMESSAGE); 
		}
		else if(!loginValidation(email.replace("/'\'", "")))
		{
			//email is not registered with Cype message
			dialogB box = new dialogB();
			box.setMessage(email+UNREGUSERMESSAGE);
			box.show(getFragmentManager(), UNREGUSERMESSAGE); 
		}
		else
		{
			updateUserDomain(email.replace("/'\'", ""));
		}
	}
	
	private void updateUserDomain(String email)
	{
		String sql = "select id from users where email='"+email+"'";
		List<Item> items = db.RunSQLStatement(sql);
		String id = grabUserId(items);
		String newPassword = getRandomPassword();
		db.putIntoUsersDomain(id, email, MD5(newPassword), APP_NAME);

		try {
			mail.send(email, newPassword, getFragmentManager(), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String grabUserId(List<Item> items)
	{
		if(items.isEmpty())return EMPTY;
		else
		{
			Item item  = items.get(0);
			ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
			return attributes.get(0).getValue();
		}
	}

	
	private boolean loginValidation(String email)
	{
		boolean isUser =false;
		String sql = "select email from users where email = '"+email+"' and accountType='"+APP_NAME+"'";
		//select with email from database to see if user exist
		
	  List<Item>	user = db.RunSQLStatement(sql);

		if(user.size() == 0)
			isUser = false;
		else
			isUser =true;
		
		return isUser;
	}
	
	private boolean isEmailValid(String email)
    {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
	
	private String MD5(String md5) {
		return DigestUtils.md5Hex(md5);
	}
	
	
	private String getRandomPassword()
	{
		return randomString(CODE_LENGTH);
	}
	private String randomString( int len ) 
	{
		rnd = new Random();
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}

	
	
	@SuppressLint("ValidFragment")
	private class dialogB extends DialogFragment {
			
		 private String message;
	     
		
		    @Override
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the Builder class for convenient dialog construction
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage(message)
		               .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		                   @Override
						public void onClick(DialogInterface dialog, int id) {
		                       dialog.cancel();
		                   }
		               });
		               
		        // Create the AlertDialog object and return it
		        return builder.create();
		    }
		 
		 public void setMessage(String message)
		 {
			 this.message = message;
		 }
	}//dialog box class ends here

	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }
}

