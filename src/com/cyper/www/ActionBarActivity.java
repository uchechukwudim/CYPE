package com.cyper.www;



import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.R;

import constants.Constants;

import cype.clubs.adapter.ClubSharePicHandler;

import cype.tabswipe.adapter.TabsPagerAdapter;
import cype.tabswipe.adapter.cPeoplFragment;
import cype.tabswipe.adapter.clubsFragment;
import cype.tabswipe.adapter.historyFragment;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActionBarActivity  extends Activity{
	
	//variables
	// ViewPager viewPager;
	private TabsPagerAdapter tabsAdapter;
	private ActionBar actionBar;
	Map<Integer, Fragment> cpaper;
	private ClubSharePicHandler Share; 
	
	
	private  final String [] TABS = {"Clubs", "Cpeople", "History"};
	
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BugSenseHandler.initAndStartSession(this, Constants.BUGSENSEAPIKEY );
	
		setContentView(R.layout.activity_main);
		Share = new ClubSharePicHandler(this);
	     // ActionBar
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // create new tabs and set up the titles of the tabs
        ActionBar.Tab clubs = actionbar.newTab().setIcon(R.drawable.home);
        ActionBar.Tab cpeople = actionbar.newTab().setIcon(R.drawable.cpeopl);
        ActionBar.Tab history = actionbar.newTab().setIcon(R.drawable.history);
     

        // bind the fragments to the tabs - set up tabListeners for each tab
        clubs.setTabListener(new MyTabsListener(new clubsFragment(),getApplicationContext()));
        cpeople.setTabListener(new MyTabsListener(new cPeoplFragment(), getApplicationContext()));
        history.setTabListener(new MyTabsListener(new historyFragment(),getApplicationContext()));


        // add the tabs to the action bar
        actionbar.addTab(clubs);
        actionbar.addTab(cpeople);
        actionbar.addTab(history);
    
		
	}
	/*
	  ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // When the tab is selected, switch to the
	            // corresponding page in the ViewPager.
	           viewPager.setCurrentItem(tab.getPosition());
	            Toast.makeText(getApplicationContext(), ""+tab.getPosition(), Toast.LENGTH_SHORT).show();
	        }

			@Override
			public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
	        
	    };
	    */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void actionbarSetup()
	{
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}


	
	 
	 
	private class MyTabsListener implements ActionBar.TabListener {
         public Fragment fragment;
         public Context context;

         public MyTabsListener(Fragment fragment, Context context) {
                     this.fragment = fragment;
                     this.context = context;

         }

         @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		@Override
         public void onTabReselected(Tab tab, FragmentTransaction ft) {
                     //get fragment and refresh it;
                     ft.detach(fragment);
                     ft.attach(fragment);
         }

         @Override
         public void onTabSelected(Tab tab, FragmentTransaction ft) {
                     //Toast.makeText(context, "Selected!", Toast.LENGTH_SHORT).show();
                     ft.replace(R.id.fragment_container, fragment);
         }

         @Override
         public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                     //Toast.makeText(context, "Unselected!", Toast.LENGTH_SHORT).show();
                     ft.remove(fragment);
         }
         
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     
        if(requestCode == 0 &&  resultCode == Activity.RESULT_OK)
	    {
        	 // Get the Uri of the selected file 
            Uri uri = data.getData();
            
            SharedPreferences share = this.getSharedPreferences("share", Context.MODE_PRIVATE);
            String userEmail = share.getString("user_email", "");
            String club_name = share.getString("club_name", "");
            
            // Get the path
            String imagePath;
			try {
				
				imagePath = getPath(getApplicationContext(), uri);
		         Share.uploadImage(club_name, userEmail, imagePath);
		         File file = new File(imagePath);
		         String imageName = file.getName();
		         share.edit().remove("user_email");
		         share.edit().remove("club_name");
		         
			} 
			catch (URISyntaxException e) {e.printStackTrace();}}
        
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
	
	

}
