package cype.cpeople.adapter;

import imageUploader.ImageUploader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Users {
	private static String FACEBOOK = "Facebook";
	private static String CYPE = "CYPE";
	
	public static boolean isCypeSet = false;
	public static boolean isFbSet= false;
	
	ImageUploader uploader;
	 private String  userName;
	 private String accountType;
	 private String gender;
	 private String userImageSrc;
	 private String status;
	 private String email;
	 private Bitmap myBitmap;
	 private Context context;
	 private String clubName;
	 private String partyWivMeCount;
 
	 public Users(Context context, String userName, String accountType, String gender, String userImage, String status, String email, String clubName, String partyWivMeCount)
	 {
		 this.userName = userName;
		 this.accountType = accountType;
		 this.gender = gender;
		 this.userImageSrc = userImage;
		 this.status = status;
		 this.context = context;
		this.partyWivMeCount = partyWivMeCount;
	
	 }
	 
	 public Users(Context context)
	 {
		 this.userName = "";
		 this.accountType = "";
		 this.gender = "";
		 this.userImageSrc = "";
		 this.status = "";
		 this.email = "";
		 this.context = context;
		 this.clubName = "";
	 }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
		
		
			isCypeSet = true;
			isFbSet = false;
		
	
		if(this.accountType.equals(FACEBOOK))
			isFbSet = true;
		    isCypeSet = false;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUserImageSrc() {
		return userImageSrc;
	}

	public void setUserImageSrc(String userImage) {
		this.userImageSrc = userImage;
	}
	
	public void setImage(String src, String email)
	{
		//if(isCypeSet)
			
	
		if(isFbSet)
		{
			getBitmapFromURLFB(src);
		
		}
		else
		{
			getBitmapFromURLCYPE(src, email);
		}
	}
	
	public Bitmap getImage()
	{
		return myBitmap;
	}
	
	public void getBitmapFromURLFB(final String src) 
	{
		        
			   new  AsyncTask<String, String, InputStream >()
			   {
			        
					@Override
					protected InputStream  doInBackground(String... arg0) 
					{	
						
				        InputStream input = null;
				        URL url =null;
						try {
								 url = new URL(src);
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								HttpURLConnection.setFollowRedirects(HttpURLConnection.getFollowRedirects());
						        connection.setDoInput(true);
						        connection.toString();
								input = connection.getInputStream();
								
								myBitmap= BitmapFactory.decodeStream(input);
							} catch (IOException e) {e.printStackTrace();}
				        	
						return input;
					}
				
			 }.execute();
		
	}
	
	public void getBitmapFromURLCYPE(final String src, final String email) 
	{
		new AsyncTask<String, String, byte[]>()
		{

			@Override
			protected byte[] doInBackground(String... arg) {
				uploader = new ImageUploader(email);
				InputStream in =  uploader.getImageFromBucket(src, email).getObjectContent();
				
				 byte[] bytes = null;
				 try {
					  bytes = IOUtils.toByteArray(in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 return bytes;
			}
			
			@Override
			protected void onPostExecute(byte[]  result) {
				byte[] bytes; 
				try{
					bytes = result;
					
				
						//check for byte heap length
						BitmapFactory.Options options = new BitmapFactory.Options();
						  options.inJustDecodeBounds = true;
						  BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
						
						  // Calculate inSampleSize
						    options.inSampleSize = calculateInSampleSize(options, 300, 300);
		
						 // Decode bitmap with inSampleSize set
						  options.inJustDecodeBounds = false;
						  options.inPreferredConfig = Config.RGB_565;
						 myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
					
				}catch(Exception e){};
				
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getPartyWivMeCount() {
		return partyWivMeCount;
	}

	public void setPartyWivMeCount(String partyWivMeCount) {
		this.partyWivMeCount = partyWivMeCount;
	}
	 
	 
}
