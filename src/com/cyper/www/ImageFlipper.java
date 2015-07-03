package com.cyper.www;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import imageUploader.ImageUploader;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;
import cype.database.Database;

public class ImageFlipper extends Activity {
	private static final String CLUBNAME = "clubname";
	private static final String EMAILS = "emails";
	private static final String IMAGES = "images";
	private static final String NAMES = "names";
	 private static final String PREF_NAME = "CYPE";
	    public static final String KEY_EMAIL = "email";

	ImageUploader uploader;
	ImageView im;
	Button next;
	Button prev;
	Button delete;
	ViewFlipper viewFlipper;
	private Bitmap bm;
	private int loop = 0;
	private int changeCount = 0;
	private String user_email;
	private TextView name;
	Database db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		db = new Database();
		SharedPreferences shared = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		user_email = (shared.getString(KEY_EMAIL, ""));
		uploader = new ImageUploader();
		Bundle bundle = getIntent().getExtras();
		
		final String clubName = bundle.getString(CLUBNAME);
		final ArrayList<String> images = bundle.getStringArrayList(IMAGES);
		final ArrayList<String> emails = bundle.getStringArrayList(EMAILS);
		final ArrayList<String> names = bundle.getStringArrayList(NAMES);

	
		name = (TextView) findViewById(R.id.im_name);
		next = (Button) findViewById(R.id.next_image);
		prev = (Button) findViewById(R.id.prev_image);
		delete = (Button) findViewById(R.id.del_image);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		name.setText("Uploaded by "+names.get(0));
		
		if(user_email.equals(emails.get(0)))
		{
			delete.setVisibility(View.VISIBLE);
		}
		else
		{
			delete.setVisibility(View.GONE);
		}
	

		for(final String image : images)
		{
			//getClubImages(clubName,emails.get(loop), image);
			if(loop == images.size()){
				loop--;
			}
			final String Email = emails.get(loop);
			
			new AsyncTask<String, String, byte[]>()
			{
				
				
				@Override
				protected byte[] doInBackground(String... arg) {
			
					
					 byte[] bytes = null;
					 try {
						 	
						 	InputStream in =  uploader.getImageFromBucket(clubName, Email, image).getObjectContent();
						  	bytes = IOUtils.toByteArray(in);
					 	 } catch (Exception e) {}
					 
					 return bytes;
				}
				
				@Override
				protected void onPostExecute(byte[]  result) {
					byte[] bytes;
					//check for byte heap length
					try{
							bytes = result;
							BitmapFactory.Options options = new BitmapFactory.Options();
							  options.inJustDecodeBounds = true;
							  BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
							
							  // Calculate inSampleSize
							    options.inSampleSize = calculateInSampleSize(options, 300, 300);

							 // Decode bitmap with inSampleSize set
							  options.inJustDecodeBounds = false;
							  options.inPreferredConfig = Config.RGB_565;
							  Bitmap myBitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
								
							ImageView img = new ImageView(getApplicationContext());
							img.setImageBitmap(myBitmap);
							viewFlipper.addView(img);	
					}catch(Exception e){
						e.getStackTrace();
						Toast.makeText(getApplicationContext(), "Please check your internet Connection", Toast.LENGTH_SHORT).show();
						return;
					}
			     }
				
				
				
			}.execute();

			loop++;
		}
		
		
	
		
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				viewFlipper.showNext();
			      if(changeCount == images.size()-1)
			    	  changeCount = 0;
			      else
			    	  changeCount++;
			      
			      name.setText("Uploaded by "+names.get(changeCount));
			      if(user_email.equals(emails.get(changeCount)))
					{
						delete.setVisibility(View.VISIBLE);
					}
			      else
					{
						delete.setVisibility(View.GONE);
					}
			      
				  
			}});
		
		prev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				viewFlipper.showPrevious();
				if(changeCount == 0)
					changeCount = images.size()-1;
				else
					changeCount--;
				
				name.setText("Uploaded by "+names.get(changeCount));
				if(user_email.equals(emails.get(changeCount)))
				{
					delete.setVisibility(View.VISIBLE);
				}
				else
				{
					delete.setVisibility(View.GONE);
				}
			}});
		
		delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String deletedImage = images.get(changeCount);
				uploader.DelectImage(clubName, images.get(changeCount), emails.get(changeCount));
				deletImageFromDomain(images.get(changeCount),clubName,emails.get(changeCount));
				images.remove(changeCount); emails.remove(changeCount); names.remove(changeCount);
				
				viewFlipper.removeViewAt(changeCount);
		
			      name.setText("Uploaded by "+names.get(viewFlipper.getDisplayedChild()));
			      if(user_email.equals(emails.get(viewFlipper.getDisplayedChild())))
					{
						delete.setVisibility(View.VISIBLE);
					}
			      else
					{
						delete.setVisibility(View.GONE);
					}
		      
				
				Toast.makeText(getApplicationContext(), ""+deletedImage+" had been deleted", Toast.LENGTH_SHORT).show();

			}});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view, menu);
		return true;
	}
	
	public void getClubImages(final String clubName, final String email, final String src) 
	{
	
		
	}
	
	private void deletImageFromDomain(final String imageName, final String clubName, final String email)
	{
		
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... arg0) {

				String sql = "select id from clubs_pictures where image_name='"+imageName+"' and club_name='"+clubName+"' and user_email='"+email+"'";
				List<Item> ids = db.RunSQLStatement(sql);
				
				if(ids== null)
				{
					
				}
				else if(ids.size() > 0)
				{
					for(Item id : ids)
					{
						List<Attribute> att = id.getAttributes();
						String ID = att.get(0).getValue();
						db.DeletImageRecord(ID);
					}
				}
				
				return null;
			}
			
		}.execute();
	}
	
	private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }

}
