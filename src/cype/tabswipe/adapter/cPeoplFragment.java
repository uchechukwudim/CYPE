package cype.tabswipe.adapter;





import java.util.HashMap;
import java.util.List;


import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.ProfilePicture;
import com.cyper.www.R;
import com.cyper.www.UpdateDetails;

import constants.Constants;

import cype.cpeople.adapter.CpeopleExpandableAdapter;
import cype.cpeople.adapter.ProcessCpeople;
import cype.cpeople.adapter.Users;
import cype.cpeople.adapter.cpeople;
import cype.session.SessionManager;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class cPeoplFragment extends Fragment {
	CpeopleExpandableAdapter listAdapter;
    ExpandableListView expListView;
    List<cpeople> listDataHeader;
    HashMap<cpeople, List<Users>> listDataChild;
    private SessionManager sessionManager;
    public static final String KEY_NAME = "name";
	  private static final String PREF_NAME = "CYPE";
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		View RootView = inflater.inflate(R.layout.cpoeple_layout, container, false);
		sessionManager = new SessionManager(getActivity().getApplicationContext());
		BugSenseHandler.initAndStartSession(getActivity(), Constants.BUGSENSEAPIKEY );

		new ProcessCpeople(getActivity(),RootView);
	     //ProcessCp.processCpeople();
		
		
		return RootView;
	}
	

	
	@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
       //.inflate(R.menu.action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.logout:
				sessionManager.logoutUser(getActivity());
				return true;
			case R.id.action_settings:
				Settings();
				return true;
			case R.id.uploader:
				SharedPreferences share = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		        String user = share.getString(KEY_NAME, "");
		        if(user.equals(PREF_NAME))
		        {
		        	S3Uploader();
		        }
		        else
		        {
		        	Toast.makeText(getActivity().getApplicationContext(), "Sorry you can't upload Profile picture as Facebook user", Toast.LENGTH_LONG).show();
		        }
			default:
				return false;
		}
		
	
	}

	private void S3Uploader() {
		//move on to next activity after authentication succeeds. else show message
		Intent goToUploader = new Intent(getActivity().getApplicationContext(), ProfilePicture.class );
		
		goToUploader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	     
	        // Add new Flag to start new Activity
		goToUploader.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//give any info needed for th next page here
		startActivity(goToUploader);
	}
	
	private void Settings()
	{
		Intent goToUploader = new Intent(getActivity().getApplicationContext(),UpdateDetails.class );
		
		goToUploader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
            // Add new Flag to start new Activity
		goToUploader.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//give any info needed for the next page here
		startActivity(goToUploader);
	}
}
