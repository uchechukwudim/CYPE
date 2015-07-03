package com.cyper.www;


import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.R;

import cype.database.Database;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TellUsForm extends Activity {

	final static String EMPTY ="";
	private EditText clubName;
	private EditText address;
	private EditText city;
	private EditText state;
	private EditText zipCode;
	private EditText country;
	
	
	Button submit;
	Button cancel;
	Database db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tell_us_form);
		
		db = new Database();
		
		clubName = (EditText) findViewById(R.id.reg_clubname);
		address = (EditText) findViewById(R.id.reg_clubaddress);
		city = (EditText) findViewById(R.id.reg_clubcity);
		state = (EditText) findViewById(R.id.reg_clubstate);
		zipCode = (EditText) findViewById(R.id.reg_clubzipcode);
		country = (EditText) findViewById(R.id.reg_clubcountry);
		
		submit = (Button) findViewById(R.id.btnRegister);
		cancel = (Button) findViewById(R.id.btnCancel);
		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
					final String Clubname = clubName.getText().toString().replace("/'\'", "");
					final String Address = address.getText().toString().replace("/'\'", "");
					final String City = city.getText().toString().replace("/'\'", "");
					final String State = state.getText().toString().replace("/'\'", "");
					final String ZipCode = zipCode.getText().toString().replace("/'\'", "");
					final String Country = country.getText().toString().replace("/'\'", "");
					
					if(isEmpty(Clubname, Address, City, State,ZipCode,Country))
					{
						Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
					}
					else 
					{	
						new AsyncTask<String, String, Boolean>()
						{

							@Override
							protected Boolean doInBackground(String... arg0) {
								db.putIntoDomainTellUs(""+getUsersId(), Clubname, Address, City, State, ZipCode, Country);
								return null;
							}
							
							@Override
					        protected void onPostExecute(Boolean result) {
								clubName.setText(EMPTY);
								address.setText(EMPTY);
								city.setText(EMPTY);
								state.setText(EMPTY);
								zipCode.setText(EMPTY);
								country.setText(EMPTY);
								Toast.makeText(getApplicationContext(), "Thank you for telling us about "+Clubname, Toast.LENGTH_SHORT).show();
							}
							
						}.execute();
						
					}
				
			}});
		
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				final String Clubname = clubName.getText().toString().replace("/'\'", "");
				final String Address = address.getText().toString().replace("/'\'", "");
				final String City = city.getText().toString().replace("/'\'", "");
				final String State = state.getText().toString().replace("/'\'", "");
				final String ZipCode = zipCode.getText().toString().replace("/'\'", "");
				final String Country = country.getText().toString().replace("/'\'", "");
				
				if(allEmpty(Clubname, Address, City, State,ZipCode,Country))
				{
					finish();
				}
				else if(isEmpty(Clubname, Address, City, State,ZipCode,Country))
				{
					clubName.setText(EMPTY);
					address.setText(EMPTY);
					city.setText(EMPTY);
					state.setText(EMPTY);
					zipCode.setText(EMPTY);
					country.setText(EMPTY);
					
				}
			
				
				
				
			}});
		
	}
	
	
	private int getUsersId()
	{
		//get user id here to increment
	
		String sql = "select id from `tell_us` where id is not null order by id desc limit 1";
		List<Item> Id = db.RunSQLStatement(sql);
		String id = grabUserAtrribute(Id);
		
		if(id.equals(EMPTY) || id.equals(null))
			return 0;
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
	
	private boolean isEmpty(String Clubname, String Address, String City, String State, String ZipCode, String Country)
	{
		return Clubname.equals("") || Address.equals("") || City.equals("") || ZipCode.equals("") || Country.equals("");
	}
	
	private boolean notEmpty(String Clubname, String Address, String City, String State, String ZipCode, String Country)
	{
		return !Clubname.equals("") || !Address.equals("") || !City.equals("") || !ZipCode.equals("") || !Country.equals("");
	}
	
	private boolean allEmpty(String Clubname, String Address, String City, String State, String ZipCode, String Country)
	{
		return Clubname.equals("") && Address.equals("") && City.equals("") && ZipCode.equals("") && Country.equals("");
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tell_us_form, menu);
		return true;
	}
	
	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }

}
