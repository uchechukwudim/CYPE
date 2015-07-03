package com.cyper.www;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;


import cype.database.Database;
import cype.login.updateLoginHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateDetails extends Activity {
	
	final static String EMPTY_FIELDS = "All Fields are required";
	final static String EMAIL_FORMAT_MESSAGE = "invalid email format";
	final static String PASSWORD_LENGTH_MESSAGE = "Password must be greater than six";
	final static String PASSWORD_MATCH_MESSAGE = "Password does not match";
	final static String EMAIL_EXIST = "Email already Exist";
	final static int PASSWORD_MIN = 6;
	final static int PASSWORD_MAX = 21;
	final static String EMPTY ="";
	final static String CYPE_NUMBER = "325165";
	final static String WRONG_CODE = "The code wrong, please check your message and try again";
	private EditText email;
	private EditText firstName;
	private EditText lastName;
	private EditText city;
	private EditText password;
	private EditText Confirm_password;
	private Button submit;
	private static final String APP_NAME = "CYPE";
	private String user_email;
    public static final String KEY_EMAIL = "email";
    private static final String PREF_NAME = "CYPE";
    private Database db;
    private String details_user_Id;
    private String password_user_Id;
    private String Store_Password;
    List<Item> details;
    List<Item> pwords;
    SharedPreferences shared;
    
    private static boolean isUpdated  = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_details);
		db = new Database();
		shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		user_email = shared.getString(KEY_EMAIL, "");
		
		email = (EditText) findViewById(R.id.update_email);
		firstName = (EditText) findViewById(R.id.update_firstName);
		lastName = (EditText) findViewById(R.id.update_lastName);
		city = (EditText) findViewById(R.id.update_city);
		password = (EditText) findViewById(R.id.update_password);
		Confirm_password = (EditText) findViewById(R.id.update_re_password);
		submit =(Button) findViewById(R.id.update_submit);
		LoginPrompt prompt = new LoginPrompt();
		prompt.show(getFragmentManager(), null);
		
		//grab user details and display them respectively 
		
		getUserDetails();
		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			
				ProcessAndSubmitDetails();
				
			}

			
			
		});
		
	
	}
	
	private void ProcessAndSubmitDetails() {
		final String Email = email.getText().toString();
		final String Password = password.getText().toString();
		final String FirstName = firstName.getText().toString();
		final String LastName = lastName.getText().toString();
		final String City = city.getText().toString();
		final String password_con = Confirm_password.getText().toString();
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... arg0) {
				if(FirstName.equals(EMPTY) || LastName.equals(EMPTY) || Email.equals(EMPTY) || Password.equals(EMPTY) || password_con.equals(EMPTY) ||
						City.equals(EMPTY))
				{
					dialogB box = new dialogB();
					box.setMessage(EMPTY_FIELDS);
					box.show(getFragmentManager(), EMPTY_FIELDS); 
				}
				else if(!isEmailValid(Email))
				{
					dialogB box = new dialogB();
					box.setMessage(EMAIL_FORMAT_MESSAGE);
					box.show(getFragmentManager(), EMAIL_FORMAT_MESSAGE);
				}
				else if(!user_email.equals(Email) && isAvailableEmail(Email))
				{
						dialogB box = new dialogB();
						box.setMessage(EMAIL_EXIST);
						box.show(getFragmentManager(), EMAIL_EXIST);
				}
				else if(!Store_Password.equals(Password) && (Password.length() < PASSWORD_MIN || Password.length() >= PASSWORD_MAX))
				{
						dialogB box = new dialogB();
							box.setMessage(PASSWORD_LENGTH_MESSAGE);
							box.show(getFragmentManager(), PASSWORD_LENGTH_MESSAGE);
						
				}
				else if(!Password.equals(password_con))
				{
				
					dialogB box = new dialogB();
					box.setMessage(PASSWORD_MATCH_MESSAGE);
					box.show(getFragmentManager(), PASSWORD_MATCH_MESSAGE);
				}
				else{
					if(!Store_Password.equals(Password))
					{
						Store_Password = MD5(Password);
					}
					
					if(!user_email.equals(Email))
					{
						Editor editor = shared.edit();
						editor.remove(KEY_EMAIL);
						editor.putString(KEY_EMAIL, Email);
						editor.commit();
						user_email = Email;
					}
					
					db.UpdateUserDetailsDomain(details_user_Id, user_email, FirstName, LastName, user_email, City, APP_NAME);
					db.putIntoUsersDomain(password_user_Id, user_email, Store_Password, APP_NAME);
					isUpdated = true;
					
				}
				
				return isUpdated;
			}
			
			@Override
			protected void onPostExecute(Boolean result)
			{
				if(result)
				{
					Toast.makeText(getApplicationContext(), "Your details has been updated", Toast.LENGTH_SHORT).show();
					shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
					isUpdated = false;
				}
			}
			private boolean isEmailValid(String email)
		    {
				return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		    }
			private String MD5(String md5) {
				return DigestUtils.md5Hex(md5 );
			}
			private boolean isAvailableEmail(String email)
			{
				String sql = "select email from users where email ='"+email+"'";
				List<Item> item = db.RunSQLStatement(sql);
				
				String Email = grabUserAtrribute(item);
				
				if(Email.equals(email))
					return true;
				else
					return false;
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
			
		}.execute();
		
	}

	private void getUserDetails() {
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... arg0) {
				String sql = "select id, email,firstName, lastName, city from user_details where email='"+user_email+"'";
				String sql2 = "select id, password from users where email='"+user_email+"'";
				
				details = db.RunSQLStatement(sql);
				
				
				pwords = db.RunSQLStatement(sql2);
				
				
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Boolean result)
			{
				try{
						grabDetailsAtrributes(details);
						grabPasswordAttribute(pwords);
				}
				catch(NullPointerException e){
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							
							 Toast.makeText(getApplicationContext(), "Could not get Details", Toast.LENGTH_SHORT).show();
							 Toast.makeText(getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
						}
						
					});
				}
			}
			
		}.execute();
		
	}
	
	private void grabDetailsAtrributes(List<Item> details)
	{
		if(details != null && details.size() >0)
		{
			Item detail = details.get(0);
			
			List<Attribute> Details = detail.getAttributes();
			email.setText(Details.get(1).getValue());
			email.setEnabled(false);
			firstName.setText(Details.get(2).getValue());
			lastName.setText(Details.get(0).getValue());
			city.setText(Details.get(3).getValue());
			details_user_Id = Details.get(4).getValue();
		}
		else{
			
		}
	}
	
	private void grabPasswordAttribute(List<Item> passwords)
	{
		Item password = passwords.get(0);
		List<Attribute> Pword = password.getAttributes();
		this.password.setText(Pword.get(0).getValue());
		Confirm_password.setText(Pword.get(0).getValue());
		password_user_Id = Pword.get(1).getValue();
		Store_Password = Pword.get(0).getValue();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_details, menu);
		return true;
	}
	

	@SuppressLint("ValidFragment")
	private class LoginPrompt extends DialogFragment
	{
		boolean isNotBackPressed = true;
		private String Email;
		private String Password;
		private String firstName;
		private String lastName;
		private String City;
		private String Gender;
		private String userId;
		private String UserDetailsId;
		private updateLoginHandler UpdateLoginHandler;
		 AlertDialog d;
		private Database db;
		 LayoutInflater li;
		 View promptsView;
		 public LoginPrompt() {
		      db = new Database();
			 this.Email = "";
			 this.Password = "";
			 this.firstName = "";
			 this.lastName = "";
			 this.City = "";
			 this.Gender = "";
			 this.userId = "";
			 this.UserDetailsId = "";
			
			 
			  li = LayoutInflater.from(getApplicationContext());
			  promptsView = li.inflate(R.layout.update_login, null);
		}

		@Override
		 public Dialog onCreateDialog(Bundle savedInstanceState) 
		 {
				
		
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setMessage(R.string.update_login_prompt);
		
				
				// set dialog message
				alertDialogBuilder.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int id) 
						{	
						  
						}//end of promptcode class onlcik method
				})
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
						@Override
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
							finish();
					    }
					  });
		
					// create alert dialog
					return alertDialogBuilder.create();
		 }
		
		@Override
		public void onStart()
		{
		    super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
		     d = (AlertDialog)getDialog();
		   
	
			final EditText email = (EditText) promptsView
					.findViewById(R.id.update_login_email);
			final EditText password = (EditText) promptsView
					.findViewById(R.id.update_login_password);
			
			
			 UpdateLoginHandler = new updateLoginHandler(UpdateDetails.this, getApplicationContext(),email, password);
			
		    if(d != null)
		    {
		        Button positiveButton = d.getButton(DialogInterface.BUTTON_POSITIVE);
		        positiveButton.setOnClickListener(new View.OnClickListener()
		                {
		                    @Override
		                    public void onClick(View v)
		                    {
		                    	UpdateLoginHandler.loginWithCype(d);
		                    	isNotBackPressed = false ;
		                    }
		                });
		        Button nagativeButton = d.getButton(DialogInterface.BUTTON_NEGATIVE);
		       nagativeButton.setOnClickListener(new View.OnClickListener()
		                {
		                    @Override
		                    public void onClick(View v)
		                    {
		                    	finish();
		                    }
		                });
		    }
		}
		
		@Override
		public void onStop()
		{
			super.onStop();
			if(isNotBackPressed)
				finish();
				
		}
		
		
			
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }
}
