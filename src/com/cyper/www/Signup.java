 package com.cyper.www;



import imageUploader.ImageUploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.R;

import constants.Constants;

import cype.SmsManager.SMSmanager;
import cype.database.Database;
import cype.email.sender.Mail;

public class Signup extends Activity {
	
	final static String EMPTY_FIELDS = "All Fields are required";
	final static String EMAIL_FORMAT_MESSAGE = "invalid email format";
	final static String PASSWORD_LENGTH_MESSAGE = "Password must be greater than six";
	final static String PASSWORD_MATCH_MESSAGE = "Password does not match";
	final static String EMAIL_EXIST = "Email already Exist";
	private final static String EMAIL = "email";
	 private static final String PREF_NAME = "CYPE";
	 
	 static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		static Random rnd;
		final static int CODE_LENGTH = 6;
	 
	final static int PASSWORD_MIN = 6;
	final static int PASSWORD_MAX = 21;
	final static String EMPTY ="";
	final static String APP_NAME = "CYPE";
	final static String CYPE_NUMBER = "325165";
	final static String WRONG_CODE = "The code wrong, please check your message and try again";
	private static String GENERATED_REG_CODE;
	private static String ENTER_CODE_HINT = "please enter code sent to you via sms";
    private static final String DEFAULTIMAGE = "default_imagee.png";
	List<Item> user;
	List<Item> user_details;
	
	
	private EditText firstName;
	private  EditText lastName;
	private EditText password;
	private EditText email;
	private EditText password_confirm;
	private EditText city;
	private RadioGroup gender;
	private RadioButton F_M;
	private Button submit_reg;
	private Button cancel;
	private Database db;
	ImageUploader uploader;

	Mail mail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);

	
		mail = new Mail();
		rnd = new Random();
		
		firstName = (EditText) findViewById(R.id.firstname);
		lastName = (EditText) findViewById(R.id.lastname);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password_reg);
		password_confirm = (EditText) findViewById(R.id.re_password);
		city = (EditText) findViewById(R.id.city);
		gender = (RadioGroup) findViewById(R.id.gender);
		
		
		submit_reg  = (Button) findViewById(R.id.btnRegister);
		cancel = (Button) findViewById(R.id.btnCancel);
		
	
		db = new Database();
		
		clickListenerSubmit();
		cancelClickListener();
		
	}
	
	private void clickListenerSubmit()
	{
		submit_reg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				String FirstName =  firstName.getText().toString();
				String LastName = lastName.getText().toString();
				String Email = email.getText().toString();
				String Password = password.getText().toString();
				String Password_con = password_confirm.getText().toString();
				String City = city.getText().toString();
				String Gender = "";
				
			
				 new RegProcess().execute(FirstName, LastName, Email, Password, Password_con, City, Gender);
				
			}});
	}
	
	
	
	private void cancelClickListener()
	{
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  firstName.setText(EMPTY);
				  lastName.setText(EMPTY);
				  email.setText(EMPTY);
				  password.setText(EMPTY);
				  password_confirm.setText(EMPTY);
				  city.setText(EMPTY);
			}});
	}

	
   

	
	private boolean isEmailValid(String email)
    {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}
	
	private String MD5(String md5) {
		return DigestUtils.md5Hex(md5 );
	}
	
	
	
	private class RegProcess extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... arg0) {
			int radio = gender.getCheckedRadioButtonId();
			String Gender = "";
			switch(radio)
			{
				case R.id.female_reg: 
					F_M = (RadioButton) findViewById(R.id.female_reg);
					Gender = F_M.getText().toString();
					break;
				case R.id.male_reg:
					F_M = (RadioButton) findViewById(R.id.male_reg);
					Gender = F_M.getText().toString();
					break;
			}
			registrationProcess(""+getUsersId(), ""+getUserDetailsId(),firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(), 
					password_confirm.getText().toString(), city.getText().toString(), Gender);
			return null;
		}
		
	}
	
	private void registrationProcess(String UserId, String UserDetailsId, String firstName, String lastName, String Email,
			String password, String password_con, String City, String Gender) {
		
		if(firstName.equals(EMPTY) || lastName.equals(EMPTY) || Email.equals(EMPTY) || password.equals(EMPTY) || password_con.equals(EMPTY) ||
				City.equals(EMPTY)|| Gender.equals(EMPTY))
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
		else if(isAvailableEmail(Email))
		{
			dialogB box = new dialogB();
			box.setMessage(EMAIL_EXIST);
			box.show(getFragmentManager(), EMAIL_EXIST);
		}
		else if(password.length() < PASSWORD_MIN || password.length() >= PASSWORD_MAX)
		{
			dialogB box = new dialogB();
			box.setMessage(PASSWORD_LENGTH_MESSAGE);
			box.show(getFragmentManager(), PASSWORD_LENGTH_MESSAGE);
		}
		else if(!password.equals(password_con))
		{
			
			dialogB box = new dialogB();
			box.setMessage(PASSWORD_MATCH_MESSAGE);
			box.show(getFragmentManager(), PASSWORD_MATCH_MESSAGE);
		}
		else{
			//send code to user here via SMS
			
	    	GENERATED_REG_CODE = randomString(CODE_LENGTH);
	    	try {
				mail.sendSignupCode(GENERATED_REG_CODE, Email);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			
			//open dialog box here to collect phone number to send text for signup confirmation
			 codePrompt CodePrompt = new  codePrompt(UserId, UserDetailsId, firstName, lastName, Email,
						 password, password_con, City,  Gender );
	    	 CodePrompt.show(getFragmentManager(), null);	 
		}
		
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
	
	private int getUsersId()
	{
		//get user id here to increment
		int id_user  = 00;
		String sql_user = "select id from `users` where id is not null order by id desc limit 1";
		user = db.RunSQLStatement(sql_user);
		String id = grabUserAtrribute(user);
		
		if(id.equals(EMPTY) || id.equals(null))
			return id_user;
		else
		 return Integer.parseInt(id)+1;
	}
	
	private int getUserDetailsId()
	{
		int id_user  = 00;
		// get user_details id here to increment
		String sql_user_details = "select id from `user_details` where id is not null order by id desc limit 1";
		user_details =  db.RunSQLStatement(sql_user_details);
		String id = grabUserAtrribute(user_details);
		
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

	
	
	
	@SuppressLint("ValidFragment")
	private class codePrompt extends DialogFragment
	{
		private String code;
		private String Email;
		private String password;
		private String firstName;
		private String lastName;
		private String City;
		private String Gender;
		private String userId;
		private String UserDetailsId;
		 LayoutInflater li;
		 View promptsView;
		 public codePrompt(String UserId, String UserDetailsId, String firstName,
				String lastName, String email, String password,
				String password_con, String city, String gender) {
		      
			 this.Email = email;
			 this.password = password;
			 this.firstName = firstName;
			 this.lastName = lastName;
			 this.City = city;
			 this.Gender = gender;
			 this.code = "";
			 this.userId = UserId;
			 this.UserDetailsId = UserDetailsId;
			  li = LayoutInflater.from(getApplicationContext());
			  promptsView = li.inflate(R.layout.code_prompt, null);
		}

		@Override
		 public Dialog onCreateDialog(Bundle savedInstanceState) 
		 {
				
		
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setMessage(R.string.prompt_name);
		
				
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
					    }
					  });
		
					// create alert dialog
					return alertDialogBuilder.create();
		 }
		
		@Override
		public void onStart()
		{
		    super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
		    AlertDialog d = (AlertDialog)getDialog();
		   
	
			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);
			userInput.setHint(ENTER_CODE_HINT);
		    if(d != null)
		    {
		        Button positiveButton = d.getButton(DialogInterface.BUTTON_POSITIVE);
		        positiveButton.setOnClickListener(new View.OnClickListener()
		                {
		                    @Override
		                    public void onClick(View v)
		                    {
		                    	//check if userInput is equal GENERATED_REG_CODE then input  	
						    	code = userInput.getText().toString();
						    
						    
						    	if(code.equals(GENERATED_REG_CODE))
						    	{
						    		//insert into database here
						    		new AsyncTask<String, String, Boolean>()
						    		{
										@Override
										protected Boolean doInBackground(String... params) {
											
											uploader = new ImageUploader();
											File file = getFileImage(uploader.getDefaultImage().getObjectContent());
											uploader.putImage(Email, file);
											
											/*/ register phone to gcm_reg domain here
											Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
									        editor.putString(EMAIL, Email);
									        editor.commit();
									        */
											
											db.putIntoUsersDomain(userId, Email, MD5(password), APP_NAME);
											db.putIntoUserDetailsDomain(UserDetailsId , firstName, lastName, Gender.toLowerCase(), City, DEFAULTIMAGE, Email, APP_NAME);
											return null;
										}
						    		}.execute();
						    		showCofirmationDialogBox();
						    		
						    	}else{
						    		Toast.makeText(getApplicationContext(), WRONG_CODE,  Toast.LENGTH_SHORT).show();
						    	}  
		                    }
		                });
		    }
		}
		 
			private void showCofirmationDialogBox()
			{
				final String CONFIRMATION_MESSAGE = "Thank you "+firstName+" for registering with CYPE";
				new DialogFragment(){
					@Override
				    public Dialog onCreateDialog(Bundle savedInstanceState) {
				        // Use the Builder class for convenient dialog construction
				        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				        builder.setMessage(CONFIRMATION_MESSAGE)
				               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				                   @Override
								public void onClick(DialogInterface dialog, int id) {
				                	   
				                	   Intent goToLogin = new Intent(getApplicationContext(), MainActivity.class );
					   					startActivity(goToLogin);
					   					finish();
				                   }
				               });
				               
				        // Create the AlertDialog object and return it
				        return builder.create();
				    }
				}.show(getFragmentManager(), null);
			}
	}
	
	private File getFileImage(InputStream in)
	{
		Bitmap mybitMap;

		mybitMap = BitmapFactory.decodeResource(getResources(),R.drawable.default_imagee);
	   
	    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	    OutputStream outStream = null;
	    File file = new File(extStorageDirectory, DEFAULTIMAGE);
	    try {
	     outStream = new FileOutputStream(file);
	     mybitMap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	     outStream.flush();
	     outStream.close();
	    }
	    catch(Exception e){}
	    return file;
	 }
	

	private String randomString( int len ) 
	{
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
}
