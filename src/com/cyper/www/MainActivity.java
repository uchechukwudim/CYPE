package com.cyper.www; 


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.R;
import com.facebook.android.Facebook;
import com.facebook.widget.LoginButton;

import constants.Constants;

import cype.facebook.FacebookHandler;
import cype.login.CypeLoginHandler;
import cype.session.SessionManager;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity{
	
	//variables
	private static final String APP_NAME = "CYPE";
	private TextView signup;
	private EditText username;
	private EditText password;
	private Button submit;
	private Button cancel;
    private Facebook facebook;
	private LoginButton loginFB;
	private FacebookHandler fbHandle;
	private static final String EMPTY_STRING = "";
    private CypeLoginHandler loginHandle;
    private SessionManager sessionManager;
    private SessionManager sessionManagerFb;
	private static final String FACEBOOK_APP_ID = "221584528026756";

	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, Constants.BUGSENSEAPIKEY );
	
		
		setContentView(R.layout.login);
		//gcm G =new gcm(getApplicationContext());
		//G.unRegisterGcm();
		
		//initialize  MainActivity variables here
       signup = (TextView) findViewById(R.id.sign_up);
	   username = (EditText) findViewById(R.id.login_username);
	   password = (EditText) findViewById(R.id.login_password);
	   submit = (Button) findViewById(R.id.submit);
	   cancel = (Button) findViewById(R.id.cancel);
	   loginFB = (LoginButton) findViewById(R.id.authButton);
		
		//initialize other package variables here
	   facebook = new Facebook(FACEBOOK_APP_ID);
	   fbHandle = new FacebookHandler(facebook, this, getApplicationContext());
	    
	   sessionManager = new SessionManager(getApplicationContext());
	   sessionManagerFb = new SessionManager(facebook, getApplicationContext());
	   if(sessionManager.checkLogin()){
		   finish();
	   }
	   else if(sessionManagerFb.checkLoginFb())
	   {
		   finish();
		   
	   }
	   
	   loginHandle = new CypeLoginHandler(this, getApplicationContext(), username, password);
	  
		//call MainActivty private methods here
	   cypeLoinSumitButtonHandler();
	   cypeLoinSumitButtonHandler();
	   facebookLoginSumitButtonHandler();
	   cypeLoginCancelButtonHandler();
	   cypeSignupLinkHandler();
		
	
	}
	
	/*
	 * Do username and password check here first
	 * get username and password, check correctness,
	 * proceed or not depending on the value
	 */
	private void cypeLoinSumitButtonHandler()
	{
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) { 
				
				  String email = username.getText().toString(); 
				  String pword = password.getText().toString();
				  
				  loginHandle.setEmail(username);
				 loginHandle.setPassword(password);
	
				  //validate email and password here
				  loginHandle.loginWithCype();
				  
	    }});//submit onclick listener ends here
	}
	
	private void facebookLoginSumitButtonHandler()
	{
		loginFB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {				
				fbHandle.loginWithFacebook();	
			}});
	}
	
	private void cypeLoginCancelButtonHandler()
	{
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
		      username.setText(EMPTY_STRING);
		      password.setText(EMPTY_STRING);
				
		}}); //cancel onclick listener ends here
	}
	
	private void cypeSignupLinkHandler()
	{
		signup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent goToSignup = new Intent(getApplicationContext(), Signup.class);
				startActivity(goToSignup);
			}});//signup onclick listerner end here
		
	}
	
	private void logout()
	{
		sessionManager.logoutUser(this);
	}
	
	
	
	
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		fbHandle.getFacebook().authorizeCallback(requestCode, resultCode, data); 
		   facebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.forgot_pword:
				forgotPassword();
				return true;
			default:
				return false;
		}
		
	
	}

	private void forgotPassword() {
	
		Intent goToUploader = new Intent(getApplicationContext(),ForgotPassword.class );
		
		goToUploader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
            // Add new Flag to start new Activity
		goToUploader.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//give any info needed for the next page here
		startActivity(goToUploader);
	}
	

}
