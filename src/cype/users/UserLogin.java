package cype.users;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;

public class UserLogin {

	private String email;
	private String password;
	private String accountType;
	List<Item> user;
	final static String EMPTY ="";
	
	private Database db;

	
	public UserLogin(String email, String password, String accountType)
	{
		this.email = email;
		this.password = password;
		this.accountType = accountType;
		db = new Database();
	}
	
	public UserLogin()
	{
		this.email = "";
		this.password = "";
		this.accountType = "";
	}

	public void insertIntoDatabaseUserLoginFB()
	{
		new AsyncTask<String,String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				db.putIntoFBusersIntoUsersDomain(""+getUsersId(), email, password, accountType);
				return null;
			}
			
		}.execute();
		
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccountTpe() {
		return accountType;
	}

	public void setAccountTpe(String accountType) {
		this.accountType = accountType;
	}

	private int getUsersId()
	{
		//get user id here to increment
		int id_user  = 00;
		String sql_user = "select id from `users` where id is not null order by id desc limit 1";
		user = db.RunSQLStatement(sql_user);
		String id = grabUserAtrribute(user);
		
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
