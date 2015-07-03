package com.cyper.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.R;

import constants.Constants;

public class ClubDetails extends Activity {
	private static final String ADDRESS = "Address";
	private static final String CLUBNAME = "Clubname";
	TextView ClubName;
	TextView Address;
	Button map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		
		 
		setContentView(R.layout.activity_club_details);
		Bundle bundle = getIntent().getExtras();
		final String clubName = bundle.getString(CLUBNAME);
		String address = bundle.getString(ADDRESS);
		
		ClubName = (TextView) findViewById(R.id.det_club_name);
		Address = (TextView) findViewById(R.id.det_address);
		map = (Button) findViewById(R.id.map_view);
		
		ClubName.setText(clubName);
		Address.setText(address);
		
		map.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MapView.class);
				intent.putExtra(ADDRESS, Address.getText().toString());
				intent.putExtra(CLUBNAME, ClubName.getText().toString());
				startActivity(intent);
				
			}});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.club_details, menu);
		return true;
	}

}
