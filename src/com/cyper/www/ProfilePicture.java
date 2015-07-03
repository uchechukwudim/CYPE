package com.cyper.www;

import imageUploader.ImageUploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.database.Database;
import cype.session.SessionManager;

public class ProfilePicture extends Activity {
	private static final int FILE_SELECT_CODE = 0;
	public static final String KEY_EMAIL = "email";
	private static final String PREF_NAME = "CYPE";
	final static String EMPTY = "";
	private ProgressDialog progressDialog = null;
	
	ImageView profilePic;
	ImageUploader uploader;
	TextView text;
	S3Object object;
	String s = "live";
	String email;
	SessionManager sessionManager;
	private Button browse;
	private Button upload;
	private Button finish;
	private TextView file_name; 
	private String path = "";
	Database db;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_picture_layout);
		db = new Database();
		profilePic = (ImageView) findViewById(R.id.profile_pic);
        browse = (Button) findViewById(R.id.browse);
        upload = (Button) findViewById(R.id.upload);
        finish = (Button) findViewById(R.id.finish_upload);
        file_name = (TextView) findViewById(R.id.file_name);
		//email = SessionManager.access_token;
		//object = new S3Object();
		//new runUpload().execute(email);
		
		browse.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent target = new Intent(Intent.ACTION_GET_CONTENT); 
				target.setType("image/*"); 
				target.addCategory(Intent.CATEGORY_OPENABLE);
				setResult(Activity.RESULT_OK, target);
				startActivityForResult(Intent.createChooser(target, "Choose Image"), FILE_SELECT_CODE);
				
			}});
		
		upload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String selectedImage = file_name.getText().toString();
				if(selectedImage.equals("Select Picture"))
				{
					Toast.makeText(getApplicationContext(), "Please select picture from Gallary", Toast.LENGTH_SHORT).show();
					
				}
				else{
					progressDialog = ProgressDialog.show(ProfilePicture.this, "Uploading Picture",
				             "Please Wait...", true);
			    new AsyncTask<String, String, byte[]>()
				{
							@Override
					protected byte[] doInBackground(String... arg0) {
								 
								 byte[] bytes = null;
								 
								SharedPreferences share = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
						         String Email = share.getString(KEY_EMAIL, "");
						         uploader = new ImageUploader(Email);
						       //get last image
						         String sql1 = "select picture from user_details where email='"+Email+"'";
						         List<Item> picName = db.RunSQLStatement(sql1);
						     if(picName == null)
						     {
						    	 return null;
						     }
						     else if(picName.size() > 0){
						         String pName = grabUserAtrribute(picName);
						         uploader.DelectImage(pName, Email);
						         uploader.putImage(Email, path);
						         
						         //put image name in database
						         File file = new File(path);
							     String imageName = file.getName();
							     //get id
						         String sql = "select id from user_details where email='"+Email+"'";
						         List<Item> id = db.RunSQLStatement(sql);
						         String Id = grabUserAtrribute(id);
						         
						         
			
						         //update database
						         db.putUserImageIntoUserDetailsDomain(Id, Email, imageName);
								 InputStream in =  uploader.getImageFromBucket(imageName, Email).getObjectContent();
								
								 try {
									  bytes = IOUtils.toByteArray(in);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						   
								 return bytes;
						    }
						    else{
						    	return null;
						    }
					}
							
							@Override
							protected void onPostExecute(byte[] result) {
								byte[] bytes;
								//check for byte heap length
								if(result == null)
								{
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(), "Could not Upload Picture", Toast.LENGTH_SHORT).show();
									Toast.makeText(getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
								}
								else{
										bytes = result;
										Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
										profilePic.setImageBitmap(bitmap);
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), "Profile picture uploaded", Toast.LENGTH_SHORT).show();
									
									}
								}
							
							}.execute();
				}
				
			}});//onclock listener for upload ends here
		
		finish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ActionBarActivity.class);
				startActivity(intent);
				
			}});
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_picture, menu);
		return true;
	}

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(data == null)
		{
			Intent intent = new Intent(getApplicationContext(), ProfilePicture.class);
			startActivity(intent);
		}
		else{
			Uri uri = data.getData();
			String imagePath;
			try {
					imagePath = getPath(getApplicationContext(), uri);
					path = imagePath;
					File file = new File(imagePath);
			        String imageName = file.getName();
			        file_name.setText(imageName);
			        file_name.setTextColor(getResources().getColorStateList(R.color.black));
			        
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
       
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null; 

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 
	
	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }

     
}
