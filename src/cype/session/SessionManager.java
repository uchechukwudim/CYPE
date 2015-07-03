package cype.session;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.ActionBarActivity;
import com.cyper.www.MainActivity;
import com.cyper.www.gcm;
import com.facebook.android.Facebook;

import cype.database.Database;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    private Facebook facebook;
    public static String access_token;
    private final static  String EMPTY = "";
    Database db;
    
	private final static  String REG_ID = "registration_id";
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Sharedpref file name
    private static final String PREF_NAME = "CYPE";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
     
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
     
    // Email address (make variable public to access from out
	 public static final String KEY_EMAIL = "email";
	gcm Gcm ;
     
    // Constructor
    @SuppressLint("CommitPrefEdits")
	public SessionManager(Facebook facebook, Context context){
        this._context = context;
        this.facebook = facebook;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        Gcm = new gcm(_context.getApplicationContext());
    }
    @SuppressLint("CommitPrefEdits")
	public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        Gcm = new gcm(_context.getApplicationContext());
    }
     
   @SuppressWarnings("deprecation")
public void setFbAccessTokenAndExpire()
   {
	   access_token = pref.getString("access_token", null);
		
		
		
		long expires = pref.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		
   }
   
    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email){
        // Storing login value as TRUE
    	Gcm.registerGcm();
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing name in pref
        editor.putString(KEY_NAME, name);
         
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
         
        access_token = email;
        
        // commit changes
        editor.commit();
    }   
    
    public void createFacebookLoginSession(String name, String email){
    	Gcm.registerGcm();
    	editor.putBoolean(IS_LOGIN, true);
    	editor.putString("access_token", facebook.getAccessToken());
    	editor.putString(KEY_EMAIL, email);
    	editor.putString(KEY_NAME, name);
    	access_token = facebook.getAccessToken();
		editor.putLong("access_expires", facebook.getAccessExpires());
		editor.commit();
    }
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, ActionBarActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);
           return true;
        }
         
        return false;
    }
    
    
    public boolean checkLoginFb(){
        // Check login status
        if(facebook.isSessionValid()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, ActionBarActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);
           return true;
        }
       
         
        return false;
    }
 
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
         
        // user email 
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
         
        // return user
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(Activity ac){
        // Clearing all data from Shared Preferences
    	db = new Database();
    	String email = pref.getString(KEY_EMAIL, null);
    	String reg_id = pref.getString(REG_ID, null);
    	deleteGcmRegId(email, reg_id); 
    
	
        editor.clear();
        editor.commit();
        ac.finish();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
    private void deleteGcmRegId(final String email, final String reg_id)
    {
    	db = new Database();
    	new AsyncTask<String, String, Boolean>()
    	{

			@Override
			protected Boolean doInBackground(String... params) {
				    String sql = "select id from gcm_reg where user_email='"+email+"' and reg_id='"+reg_id+"'";
				    
				    if(db.RunSQLStatement(sql) != null)
				    {
						    List<Item> ID = db.RunSQLStatement(sql);
						    String id =  grabUserAtrribute(ID);
						    db.DeletGcmRegRecord(id);
							Gcm.unRegisterGcm();
				    }
				    else
				    {
				    	deleteGcmRegId(email, reg_id);
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
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

	public Facebook getFacebook() {
		return facebook;
	}

	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		SessionManager.access_token = access_token;
	}
    
	
    
	  public class callFacebookLogout extends AsyncTask<String, String, Boolean>
	  {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				facebook.logout(_context);
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