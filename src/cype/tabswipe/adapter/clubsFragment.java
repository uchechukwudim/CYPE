package cype.tabswipe.adapter;
import googleClubSearch.GoogleClubSearch;

import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.cyper.www.ClubSearchInitializer;
import com.cyper.www.MainActivity;
import com.cyper.www.ProfilePicture;
import com.cyper.www.R;
import com.cyper.www.TellUsForm;
import com.cyper.www.UpdateDetails;

import constants.Constants;

import cype.clubs.adapter.club;
import cype.session.SessionManager;

public class clubsFragment extends Fragment {
	ArrayList<club> clubs = new ArrayList<club>();
	private ListView listview;
	private TextView tellUs;
	private SessionManager sessionManager;
	
	  public static final String KEY_NAME = "name";
	  private static final String PREF_NAME = "CYPE";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		View RootView = inflater.inflate(R.layout.clubs_layout, container, false);
		tellUs = (TextView) RootView.findViewById(R.id.tell_us);
		sessionManager = new SessionManager(getActivity().getApplicationContext());
		tellUsListener();
		new ClubSearchInitializer(getActivity(), RootView, listview);
		
		
		
		return RootView;
	}

		private void tellUsListener()
		{
			tellUs.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent goToTellUsForm = new Intent(getActivity(), TellUsForm.class);
					startActivity(goToTellUsForm);
				}});
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
			Intent goToUploader = new Intent(getActivity().getApplicationContext(),ProfilePicture.class );
			
			goToUploader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	         
	            // Add new Flag to start new Activity
			goToUploader.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//give any info needed for the next page here
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
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    //Handle Code
		}
		
}
