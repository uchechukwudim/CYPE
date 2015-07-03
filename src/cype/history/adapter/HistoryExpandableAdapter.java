package cype.history.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.cyper.www.ImageFlipper;
import com.cyper.www.R;

import cype.database.Database;

public class HistoryExpandableAdapter extends BaseExpandableListAdapter {

	private static final String CLUBNAME = "clubname";
	private static final String EMAILS = "emails";
	private static final String IMAGES = "images";
	private static final String NAMES = "names";
	
	private Context context;
	private List<history> historyHeader;
	private HashMap<history, List<HistoryChildren>> users;
	private Database db;
	
	private  ArrayList<String> emails;
	private  ArrayList<String> names;
	private  ArrayList<String> images;
	
	public HistoryExpandableAdapter(Context context, List<history> historyHeader, HashMap<history, List<HistoryChildren>> users)
	{
		this.context = context;
		this.historyHeader = historyHeader;
		this.users = users;
		db = new Database();
		
		 emails = new ArrayList<String>();
		 images = new ArrayList<String>();
		 names = new ArrayList<String>();
	}
	
	
	
	
	
	
	@Override
	public Object getChild(int groupPosition, int childPosititon) {
        return this.users.get(this.historyHeader.get(groupPosition))
                .get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final HistoryChildren children = (HistoryChildren) getChild(groupPosition, childPosition);
		
		if(convertView == null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.history_child_list, null);
		}
		
		//Initialize variables for HistoryChildren here
		TextView clubName = (TextView) convertView.findViewById(R.id.club_name_his);
		clubName.setText(children.getClubLogo());
		TextView cmood = (TextView) convertView.findViewById(R.id.club_mood_was);
		cmood.setText(children.getClubMood());
		TextView atttendCount = (TextView) convertView.findViewById(R.id.attended_h_count);
		atttendCount.setText(children.getAttendedCount());
		TextView checkedinCount = (TextView) convertView.findViewById(R.id.checkedin_h_count);
		checkedinCount.setText(children.getCheckedInCount());
		TextView musciRateCount = (TextView) convertView.findViewById(R.id.rateMusic_h_count);
		musciRateCount.setText(children.getMusicRatingCount());
		
		ImageView starRate = (ImageView) convertView.findViewById(R.id.star_rating);
		
		if(children.getMusicRatingCount().equals("0"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.zero_star));
		}
		else if(children.getMusicRatingCount().equals("1"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.one_star));
		}
		else if(children.getMusicRatingCount().equals("2"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.two_star));
		}
		else if(children.getMusicRatingCount().equals("3"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.three_star));
		}
		else if(children.getMusicRatingCount().equals("4"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.four_star));
		}
		else if(children.getMusicRatingCount().equals("5"))
		{
			starRate.setImageDrawable(context.getResources().getDrawable(R.drawable.five_star));
		}

		TextView pictures = (TextView) convertView.findViewById(R.id.viewpic_h_click);
		
		pictures.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AsyncTask<String, String, List<Item>>()
				{

					@Override
					protected List<Item> doInBackground(String... arg0) {
						String sql = "select image_name, user_email from clubs_pictures where club_name='"+children.getClubLogo()+"' and date='"+children.getDate()+"'";
						List<Item> images = db.RunSQLStatement(sql);
						
						return images;
					}
					
					@Override
					protected void onPostExecute(List<Item>  result) {
						
						if(result == null)
						{
							Toast.makeText(context, "Unable to load", Toast.LENGTH_SHORT).show();
							Toast.makeText(context, "please check your internet connection ", Toast.LENGTH_SHORT).show();
						}
						else if(result.size()> 0)
						{
							getImageAndUserEmail(children.getClubLogo(),result);	
						}
						else
						{
							Toast.makeText(context, "There was no uploaded images for "+children.getClubLogo(), Toast.LENGTH_SHORT).show();
						}
						
				     }
					
				}.execute();
			}});
		
		return convertView;
	}
	
	private void getImageAndUserEmail(final String clubName, List<Item> result)
	{
		for(Item item: result)
		{
			
			List<Attribute> att = item.getAttributes();
			
			images.add(att.get(0).getValue());
			emails.add(att.get(1).getValue()); 
			final String Email = att.get(1).getValue();
			
			//get user names
			new AsyncTask<String, String, List<Item>>()
			{

				@Override
				protected List<Item> doInBackground(String... arg0) {
					
					String sql = "select firstName from user_details where email='"+Email+"'";
					List<Item> Names = db.RunSQLStatement(sql);
					if(Names == null)
					{
						return Names;
					}
					else if(Names.size() > 0)
					{
						Item name = Names.get(0);
						
							List<Attribute> atten = name.getAttributes();
					
							names.add(atten.get(0).getValue()); 
			
					}
					return null;
				}
				@Override
				protected void onPostExecute(List<Item>  result) {
					
					Intent intent = new Intent(context, ImageFlipper.class);
					
					intent.putExtra(CLUBNAME, clubName);
					intent.putStringArrayListExtra(IMAGES, images);
					intent.putStringArrayListExtra(EMAILS, emails);
					intent.putStringArrayListExtra(NAMES, names);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		             
		            // Add new Flag to start new Activity
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
					
				
			}.execute();
		
		}
	}
	

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		int count  = 0;
		try{
			count = this.users.get(this.historyHeader.get(groupPosition)).size();
			
			if(count == 0)
			{
				String date = this.historyHeader.get(groupPosition).getDate();
				Toast.makeText(context, "No History for "+date+"", Toast.LENGTH_SHORT ).show();
			}
		}catch(NullPointerException e)
		{
			String date = this.historyHeader.get(groupPosition).getDate();
			Toast.makeText(context, "No History for "+date+"", Toast.LENGTH_SHORT ).show();
		}
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return this.historyHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.historyHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		//
		history History = (history) getGroup(groupPosition);
		
		if(convertView ==null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.history_items_holder, null);
		}
		
		//link varables here
		TextView date = (TextView) convertView.findViewById(R.id.history_date);
		date.setText(History.getDate());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	
}
