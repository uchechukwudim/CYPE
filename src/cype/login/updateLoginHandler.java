package cype.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import com.cyper.www.R;


import cype.database.Database;

import cype.session.SessionManager;

public class updateLoginHandler {
	final static String EMPTY_MESSAGE = "Email or password is Empty";
	final static String EMAIL_FORMAT_MESSAGE = "invalid email format";
	final static String LOGIN_MESSAGE = "Invalid email or password";
	private static final String EMPTY_STRING = "";
	private static final String APP_NAME = "CYPE";
    public static final String KEY_EMAIL = "email";
    private static final String PREF_NAME = "CYPE";
    private static final String DIFFERENTUSER = "Different user email";
	private EditText email;
	private EditText password;
	private Activity activity;
	private Context context;
	private SessionManager sessionManager;
	private Database db;
	List<Item> user;
	String user_email;
	
	
	
	public updateLoginHandler(Activity ac, Context context, EditText email, EditText password)
	{
		activity = ac;
		this.email = email;
		this.password = password;
		db = new Database();
		user = new ArrayList<Item>();
		this.context = context;
		SharedPreferences shared = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		user_email = shared.getString(KEY_EMAIL, "");
	
	}
	
	public void loginWithCype(AlertDialog dialog)
	{
	
		new runSQL().execute(dialog);
	}
	
	//connect to database here to check 
	private boolean loginValidation(String email, String password)
	{
		boolean isLoggedin =false;
		String sql = "select email, password from users where email = '"+email+"' and accountType='"+APP_NAME+"'";
		
		final int PASSWORD = 1;
		//select with email from database to see if user exist
		
		user = db.RunSQLStatement(sql);
		
		if(user == null)
		{
			activity.runOnUiThread(new Runnable(){

				@Override
				public void run() {
				  Toast.makeText(activity, "Unable to Connect You", Toast.LENGTH_SHORT).show();
				  Toast.makeText(activity, "Please check you internet Connection", Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		else if(user.size() == 0)
			isLoggedin = false;
		else if(user.size() == 1 && !grabUserLogin(user,PASSWORD).equals(MD5(password)))
			isLoggedin = false;
		else if(user.size() ==1 && grabUserLogin(user,PASSWORD).equals(MD5(password)))
			isLoggedin = true;
		
		return isLoggedin;
	}
	
	private boolean isEmailValid(String email)
    {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
	

	private String MD5(String md5) {
		return DigestUtils.md5Hex(md5);
	}
	
	private String grabUserLogin(List<Item> items, int index)
	{
		Item item  = items.get(0);
		
		ArrayList<Attribute> attributes = ( ArrayList<Attribute>) item.getAttributes();
		
	
		return attributes.get(index).getValue();
	}


	  private class runSQL extends AsyncTask<AlertDialog, String, Boolean>{

		  
			@Override
			protected Boolean doInBackground(AlertDialog... dialog) {
				
				if(email.getText().toString().equals("") || password.getText().toString().equals(""))
				{
					//display message with dialog box or toast
					dialogB box = new dialogB();
					box.setMessage(EMPTY_MESSAGE);
					box.show(activity.getFragmentManager(),EMPTY_MESSAGE );
					
				}
				else if(!isEmailValid(email.getText().toString()))
				{
					//display message with dialog box or toast
					
					dialogB box = new dialogB();
					box.setMessage(EMAIL_FORMAT_MESSAGE);
					box.show(activity.getFragmentManager(), EMAIL_FORMAT_MESSAGE);
					//email.setText("");
				}
				else if (!user_email.equals(email.getText().toString()))
				{
					
					dialogB box = new dialogB();
					box.setMessage(DIFFERENTUSER);
					box.show(activity.getFragmentManager(), DIFFERENTUSER);
				}
				else if(!loginValidation(email.getText().toString(), password.getText().toString()))
				{
					//display message with dialog box or Toast
					
					dialogB box = new dialogB();
					box.setMessage(LOGIN_MESSAGE);
					box.show(activity.getFragmentManager(), LOGIN_MESSAGE);
					
				}
				else if(loginValidation(email.getText().toString(), password.getText().toString()))
				{
			      dialog[0].cancel();
				 
				}
				return null;
			}
		  
		
	  }
	
	public EditText getEmail() {
		return email;
	}

	public void setEmail(EditText email) {
		this.email = email;
	}

	public EditText getPassword() {
		return password;
	}

	public void setPassword(EditText password) {
		this.password = password;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}



	@SuppressLint("ValidFragment")
		private class dialogB extends DialogFragment {
				
			 private String message;
		
			
			    @Override
			    public Dialog onCreateDialog(Bundle savedInstanceState) {
			        // Use the Builder class for convenient dialog construction
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage(message)
			               .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			                   @Override
							public void onClick(DialogInterface dialog, int id) {
			                       dialog.cancel();
			                   }
			               })
			               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			                   @Override
							public void onClick(DialogInterface dialog, int id) {
			                     //  MainActivity.this.finish();
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
}
