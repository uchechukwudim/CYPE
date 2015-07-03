package cype.users;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;

public class UserDetails {
  
	private String FirstName;
	private String LastName;
	private String gender;
	private String city;
	private String image;
	private String email;
	private Database db;
	List<Item> user_details;
	final static String EMPTY ="";
	private String accountType;

	
	public UserDetails(String FirstName, String LastName, String gender, String city, String image, String email, String accountType)
	{
		this.FirstName = FirstName;
		this.LastName = LastName;
		this.gender = gender;
		this.city = city;
		this.image = image;
		this.email = email;
		db = new Database();
		this.accountType = accountType;
	}
	
	public UserDetails()
	{
		this.FirstName = "";
		this.LastName = "";
		this.gender = "";
		this.city = "";
		this.image = null;
		this.email = "";
		db = new Database();
		this.accountType = "";
	}

	//work on this method to get id number for incrementing
	public void insertIntoDatabaseUserDetialsFB()
	{
		 new AsyncTask<String, String, Boolean>()
		 {

			@Override
			protected Boolean doInBackground(String... params) {
				String sql = "select email from user_details where email ='"+email+"'";
				
				List<Item> res = db.RunSQLStatement(sql);
				if(res.size() == 0){
				   db.putIntoUserDetailsDomain(""+getUserDetailsId(), FirstName, LastName, gender, city, image, email, accountType);
				}
				return null;
			}
			 
		 }.execute();
		
		
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
	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}


	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
