package cype.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;


import com.cyper.www.ActionBarActivity;
import com.cyper.www.gcm;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import cype.session.SessionManager;
import cype.users.UserDetails;
import cype.users.UserLogin;

public class FacebookHandler{
	
  private Facebook facebook;
  private Activity activity;
  private Context context;
  private UserLogin user;
  private UserDetails user_details;
  private final String Fb_TAG = "Facebook";
  private final String isloggedin = "true";
  private SessionManager sessionManager;
  private gcm Gcm;
  private final static String EMAIL = "email";
  private static final String PREF_NAME = "CYPE";
   public FacebookHandler(Facebook facebook, Activity activity, Context context)
   {
	   this.facebook = facebook;
	
	   this.activity = activity;
	   this.context = context;
	   sessionManager = new SessionManager(this.facebook, this.context);
	   Gcm = new gcm(this.context);
   }
   
	@SuppressWarnings("deprecation")
	public void loginWithFacebook() {
		
	   sessionManager.setFbAccessTokenAndExpire();
		
	   if(facebook.isSessionValid())
	   {
		   
		   facebook.authorize(activity, new String[]{"email"}, new DialogListener(){

				@Override
				public void onComplete(Bundle values) {
			
					//get FB user details here
				     new facebookRequest().execute("me");
				 
					
					Intent goToActionBar = new Intent(context, ActionBarActivity.class );
					// Closing all the Activities
					goToActionBar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		             
		            // Add new Flag to start new Activity
					goToActionBar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		             
					context.startActivity(goToActionBar);
					activity.finish();
				}

				
				@Override
				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
					
				}

					@Override
				public void onError(DialogError e) {
					// TODO Auto-generated method stub
					
				}

				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}});
        /*
		   new facebookRequest().execute("me");
			Intent goToActionBar = new Intent(context, ActionBarActivity.class );
			
			goToActionBar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
			goToActionBar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
			context.startActivity(goToActionBar);
			activity.finish();
			*/
	   }
	   else
	   {
		  facebook.authorize(activity, new String[]{"email"}, new DialogListener(){

			@Override
			public void onComplete(Bundle values) {
		
				//get FB user details here
			     new facebookRequest().execute("me");
			 
				
				Intent goToActionBar = new Intent(context, ActionBarActivity.class );
				// Closing all the Activities
				goToActionBar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	             
	            // Add new Flag to start new Activity
				goToActionBar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	             
				context.startActivity(goToActionBar);
				activity.finish();
			}

			
			@Override
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}

				@Override
			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}

			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}});
	   }
	}
	

	public Facebook getFacebook() {
		return facebook;
	}
	
	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}

  private class facebookRequest extends AsyncTask<String, String, Boolean>
  {

	@SuppressWarnings("deprecation")
	@Override
	protected Boolean doInBackground(String... arg0) {
		//get facebook user details here
		JSONObject profile =null;
		URL img_url = null;
		String jsonUser;
		try 
		{
			
			jsonUser = facebook.request("me");
			profile  = Util.parseJson(jsonUser);
			
			Gcm.registerGcm();
			//grabbing user details from Facebook
			String id = profile .optString("id");
			img_url = new URL("https://graph.facebook.com/"+id+"/picture?type=normal");
			
			String image_url = "https://graph.facebook.com/"+id+"/picture?type=normal";
			//creating image here
			//Bitmap bmp = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
			
			
			//user = new UserLogin(profile.optString("email"), facebook.getAccessToken(),  Fb_TAG);
			user_details = new UserDetails(profile.optString("first_name"), profile.optString("last_name"),
			profile.optString("gender"), profile.optString("location.name"), image_url,  profile.optString("email"), Fb_TAG);
			
			//call for database method from userLogin and userDetails here
			//user.insertIntoDatabaseUserLoginFB();
			user_details.insertIntoDatabaseUserDetialsFB();
			sessionManager.createFacebookLoginSession(Fb_TAG,profile.optString("email"));
			
			Editor editor = activity.getApplication().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
	        editor.putString(EMAIL, profile.optString("email"));
	        editor.commit();
			activity.finish();
			
 		} catch (MalformedURLException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (FacebookError e) {e.printStackTrace();
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}
	  
  }//facebookrequest private class ends here
  
  private class callFacebookLogout extends AsyncTask<String, String, Boolean>
  {

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			facebook.logout(context);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	  
  }
  

}
