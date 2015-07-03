 package cype.clubs.adapter;

import imageUploader.ImageUploader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;

public class ClubSharePicHandler {


	private ImageUploader imageUpload;
	private Database db;
	private List<Item> clubs_pics;
	
	private String EMPTY = "";
	private Activity activity;
	private String ImageName;
	
	public ClubSharePicHandler(Activity activity)
	{
		this.activity = activity;
		db = new Database();
		imageUpload = new ImageUploader();
	}
	
	public void uploadImage(String clubName, String userEmail, String imagePath)
	{
		new UploadImage().execute(clubName, userEmail, imagePath);
	}
	
	private class UploadImage extends AsyncTask<String, String, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... club) {
			//get club name from Club_pictures, if it exist then just put into bucket and insert domain
			//if it doesn't exist then create bucket and also insertion in domain
			String clubName = club[0];
			String userEmail = club[1];
			String path = club[2];
			
			String sql ="select club_name from clubs_pictures where club_name='"+clubName+"'";
			clubs_pics =  db.RunSQLStatement(sql);
			if(clubs_pics == null)
			{
				return false;
			}
			else if(clubs_pics.size() > 0)
			{
				//put image into bucket and update domain club_picture
				
				final File file = new File(path);
				String imageName = file.getName();
				ImageName = file.getName();
				imageUpload.putImage(clubName, userEmail, path);
				db.putIntoClubPicturesDomain(""+getNewClubPictureId(), clubName, userEmail, imageName);
				
		         return true;
			}
			else
			{
				//create bucket, put image into bucket, insert into domain
				final File file = new File(path);
				String imageName = file.getName();
				ImageName = file.getName();
				imageUpload.putImage(clubName, userEmail, path);
				db.putIntoClubPicturesDomain(""+getNewClubPictureId(), clubName, userEmail, imageName);
				
		         return true;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result == false)
			{
				Toast.makeText(activity.getApplicationContext(), "Could not uploaded picture", Toast.LENGTH_SHORT).show();
		         Toast.makeText(activity.getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			else if(result == true)
			{
				Toast.makeText(activity.getApplicationContext(), ImageName+" have been uploaded", Toast.LENGTH_SHORT).show();
		         Toast.makeText(activity.getApplicationContext(), "Go to Cpeople to see Uploaded pictures", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private int getNewClubPictureId()
	{
		int id_user  = 00;
		// get user_details id here to increment
		String sql = "select id from `clubs_pictures` where id is not null order by id desc limit 1";
		List<Item> club =  db.RunSQLStatement(sql);
		String id = grabUserAtrribute(club);
		
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
}
