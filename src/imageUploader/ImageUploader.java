package imageUploader;




import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import constants.Constants;


public class ImageUploader{
	

	BasicAWSCredentials awsCredentials;
	AmazonS3Client s3Client;
	String userEmail;
    URL Url;
    Bitmap image;
    String Folder;
    S3Object object;
    String pictureName = "";
    private static final String DEFAULTIMAGE = "default_imagee.png";
	
	public ImageUploader(String userEmail)
	{
		 awsCredentials = new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY);
		 s3Client = new AmazonS3Client(awsCredentials);
		 this.userEmail = userEmail;
		 Folder = userEmail+"/";
		 object = new S3Object();
		
	}
	
	public ImageUploader()
	{
		 awsCredentials = new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY);
		 s3Client = new AmazonS3Client(awsCredentials);
		 Folder = "/";
		 object = new S3Object();
		
	}
	
	public void createBucket()
	{
		new AsyncTask<String, String, Boolean>()
		{
			@Override
			protected Boolean doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				s3Client.createBucket(Constants.getPictureBucket());
				return null;
			}
			
		}.execute();
		
	}
	public void createBucket(final String bucket)
	{
		new AsyncTask<String, String, Boolean>()
		{
			@Override
			protected Boolean doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				s3Client.createBucket(bucket);
				return null;
			}
			
		}.execute();
		
	}
	
	public void putImage(String path)
	{
		final File file = new File(path);
		
		new AsyncTask<String, String, Boolean>()
		{
			@Override
			protected Boolean doInBackground(String... params) {
				PutObjectRequest put = new PutObjectRequest(Constants.getPictureBucket(), Folder+file.getName(), file);
				s3Client.putObject(put);
				
				return null;
			}
			
		}.execute();
		
	}
	
	public void putImage(String clubName, String userEmail, String path)
	{
	    final File file = new File(path);

	    setFolder(userEmail, clubName );
		PutObjectRequest put = new PutObjectRequest(Constants.getPictureBucket(), getFolder()+file.getName(), file);
		s3Client.putObject(put);
		 Folder = "/";
	}
	
	public void putImage(String userEmail, String path)
	{
	    final File file = new File(path);

	    setFolderforUser(userEmail);
		PutObjectRequest put = new PutObjectRequest(Constants.getPictureBucket(), getFolder()+file.getName(), file);
		s3Client.putObject(put);
		 Folder = "/";
	}
	
	public void putImage(String userEmail, File file)
	{
	    setFolderforUser(userEmail);
		PutObjectRequest put = new PutObjectRequest(Constants.getPictureBucket(), getFolder()+file.getName(), file);
		s3Client.putObject(put);
		Folder = "/";
	}
	
	public S3Object getImageFromBucket(String imageName, String userEmail)
	{
		setFolderforUser(userEmail);
		 S3Object ob = s3Client.getObject(Constants.getPictureBucket(), userEmail+"/"+imageName);
		 Folder = "/";
	    return ob;
	    
	}
	
	public S3Object getDefaultImage()
	{
		S3Object ob = s3Client.getObject(Constants.getPictureBucket(), DEFAULTIMAGE);
		return ob;
	}
	
	public void DelectImage(String imageName, String email)
	{
		
		 setFolderforUser(userEmail);
		 s3Client.deleteObject(Constants.getPictureBucket(), getFolder()+imageName);
		 Folder = "/";
	}
	public void DelectImage(final String clubName, final String imageName, final String email)
	{
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params) {
				setFolder(email, clubName );
				 s3Client.deleteObject(Constants.getPictureBucket(), getFolder()+imageName);
				 Folder = "/";
				return null;
			}
			
		}.execute();
		 
	}
	public S3Object getImageFromBucket(String clubName, String email, String imageName)
	{
		setFolder(email, clubName );
	    return s3Client.getObject(Constants.getPictureBucket(), getFolder()+imageName);
	}
	
	public void setS3Object(S3Object object) {
		// TODO Auto-generated method stub
		this.object = object;
	}
	
	public S3Object getS3Objact()
	{
		return this.object;
	}
	
	public InputStream getS3ObjectContent()
	{
		return object.getObjectContent();
	}
	
	public void setFolder(String email, String clubName)
	{
		Folder = clubName+"/"+email+"/";
	}
	
	public void setFolderforUser(String email)
	{
		Folder = email+"/";
	}
	
	public String getFolder()
	{
		return Folder;
	}
	
	public Bitmap CreateImageFromUrl(final URL Url)
	{
		
	   new AsyncTask<String, String, Boolean>()
	   {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				image = BitmapFactory.decodeStream(Url.openConnection().getInputStream());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		   
	   }.execute();
		
			return image;
	}
	
	public URL getURl()
	{
		return Url;
	}
}
