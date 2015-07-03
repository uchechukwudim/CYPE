package cype.database;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.*;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

import cype.date.CypeDate;

public class Database {
	
	private AmazonSimpleDBClient db;
	
	private static final String accessKey = "AKIAIQIPCBB22ONWI4OQ";
	private static final String secretKey = "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";
	private static final String endpoint = "sdb.us-west-2.amazonaws.com";
	private static final String isACTIVATED = "true";
	
	private static final String [] DOMAINS = {"users", "user_details", "clubs_pictures", "clubs_bars", "club_function", "club_future_functions", "club_function_results", "tell_us", "gcm_reg",
												"party_Wiv_Me_count", "check_partyWivMe_click", "places"};
    private List<Item> items;
    private CypeDate cypeDate;
			
	public Database()
	{
		BasicAWSCredentials credential = new BasicAWSCredentials(accessKey, secretKey);
		db = new AmazonSimpleDBClient(credential);
		db.setEndpoint(endpoint);
		cypeDate = new CypeDate();
	}
	
	
	
	public void createTable(String domainName)
	{
		if(domainName == null || domainName.equals(""))
			throw new NullPointerException();
		else
		{
			CreateDomainRequest domainRequest = new CreateDomainRequest(domainName);
			db.createDomain(domainRequest);
		}
	}

	public void putIntoUsersDomain(String itemName, String email, String password, String accountType)
	{
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(zero_padding(itemName));
	
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute Password = new ReplaceableAttribute("password", password, true);
		ReplaceableAttribute AccountType = new ReplaceableAttribute("accountType", accountType, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id",zero_padding(itemName), true);
		ReplaceableAttribute isActivated = new ReplaceableAttribute("isActivated ",isACTIVATED , true);
	
		
		item.withAttributes(Email, Password, AccountType, id, isActivated);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[0], addList));
		}catch(Exception e)
		{
			
		}
	}
	
	public void putIntoFBusersIntoUsersDomain(String itemName, String email, String password, String accountType)
	{
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(zero_padding(itemName));
	
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute Password = new ReplaceableAttribute("password", password, true);
		ReplaceableAttribute AccountType = new ReplaceableAttribute("accountType", accountType, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", zero_padding(itemName), true);
        ReplaceableAttribute isActivated = new ReplaceableAttribute("isActivated ",isACTIVATED , true);
	
		
		item.withAttributes(Email, Password, AccountType, id, isActivated);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[0], addList));
		}catch(Exception e)
		{
			
		}
	}
	
	public void putUserImageIntoUserDetailsDomain(String itemName, String email, String imageName)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(itemName);
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute Picture = new ReplaceableAttribute("picture", imageName, true);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		attr.add(Email);
		attr.add(Picture);
		
		item.setAttributes(attr);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[1], add));
		}catch(Exception e)
		{
			
		}
	}
	public void putIntoUserDetailsDomain(String ItemName, String FirstName, String LastName, String gender, String city, String picture, String email, String accountType)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(ItemName));
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute firstName = new ReplaceableAttribute("firstName", FirstName, true);
		ReplaceableAttribute lastName = new ReplaceableAttribute("lastName", LastName, true);
		ReplaceableAttribute Gender = new ReplaceableAttribute("gender", gender, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute Picture = new ReplaceableAttribute("picture", picture, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", zero_padding(ItemName), true);
		ReplaceableAttribute AccountType = new ReplaceableAttribute("account_type", accountType, true);
		
		item.withAttributes(firstName, lastName, Gender, City, Picture, Email, id, AccountType);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[1], add));
		}catch(Exception e)
		{
			
		}
	}// void putIntoUserDetailsDomain ends here
	
	
	public void putIntoClubsBarsDomain(String itemName, String clubName, String clubAddress, String clubLogo)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute ClubAddress = new ReplaceableAttribute("club_address", clubAddress, true);
		ReplaceableAttribute ClubLogo = new ReplaceableAttribute("club_logo", clubLogo, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", zero_padding(itemName), false);
		
		
		item.withAttributes(ClubName, ClubAddress, ClubLogo, id);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[3], add));
		}catch(Exception e)
		{
			
		}
	}//putIntoClubsBarsDomain ends here
	
	public void putIntoClubFunctionDomainAttending(String ItemName, String clubName, String UserEmail,String attending, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		
		item.setName(ItemName);
		
		ReplaceableAttribute Attending = new ReplaceableAttribute("attending", attending, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
	
		attr.add(ClubName);
		attr.add(Attending);
		attr.add(Email);
		attr.add(City);

		item.setAttributes(attr);
		add.add(item);
	 try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[4], add));
		}catch(Exception e)
		{
			
		}
		
	}//putIntoClubFunctionDomain ends here
	
	public void putIntoClubFunctionDomainCheckedin(String ItemName, String clubName, String UserEmail, String checkedin, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		
		item.setName(ItemName);
		
		ReplaceableAttribute Checkedin = new ReplaceableAttribute("checkedin", checkedin, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
	
		attr.add(ClubName);
		attr.add(Checkedin);
		attr.add(Email);
		attr.add(City);

		item.setAttributes(attr);
		add.add(item);
		
		 try{
			 db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[4], add));
			}catch(Exception e)
			{
				
			}
	}//putIntoClubFunctionDomain ends here
	
	public void putIntoClubFunctionDomainCmood(String ItemName, String clubName, String UserEmail, String Cmood, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		
		item.setName(ItemName);
		
		ReplaceableAttribute CMood = new ReplaceableAttribute("user_cmood_vote", Cmood, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
	
		attr.add(ClubName);
		attr.add(CMood);
		attr.add(Email);
		attr.add(City);

		item.setAttributes(attr);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[4], add));
		}catch(Exception e)
		{
			
		}
		
	}//putIntoClubFunctionDomain ends here
	
	public void putIntoClubFunctionMusicRating (String ItemName, String clubName, String UserEmail, String musicRating, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		
		item.setName(ItemName);
		
		ReplaceableAttribute MusicRating = new ReplaceableAttribute("user_music_rating", musicRating, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
	
		attr.add(ClubName);
		attr.add(MusicRating);
		attr.add(Email);
		attr.add(City);

		item.setAttributes(attr);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[4], add));
		   }catch(Exception e)
			{
				
			}
	}
	
	public void putNewIntoClubFunctionDomain(String ItemName, String Id, String clubName, String UserEmail,String attending, String checkedin,
			 String userCmoodVote, String userMusicRating,String city, String date)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		if(ItemName.length() == 0)
			ItemName = Id;
		
		item.setName(ItemName);
		ReplaceableAttribute Date = new ReplaceableAttribute("date", date, true);
		ReplaceableAttribute Attending = new ReplaceableAttribute("attending", attending, true);
		ReplaceableAttribute Checkedin = new ReplaceableAttribute("checkedin", checkedin, true);
		ReplaceableAttribute UserMusicRating = new ReplaceableAttribute("user_music_rating", userMusicRating, true);
		ReplaceableAttribute UserCmoodVote = new ReplaceableAttribute("user_cmood_vote", userCmoodVote, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", Id, true);
		ReplaceableAttribute country = new ReplaceableAttribute("country", "", true);
		
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		attr.add(id);
		attr.add(ClubName);
		attr.add(Attending);
		attr.add(Email);
		attr.add(UserCmoodVote);
		attr.add(UserMusicRating);
		attr.add(Checkedin);
		attr.add(City);
		attr.add(Date);
		attr.add(country);
		
		item.setAttributes(attr);
		add.add(item);
		
		 try{
				db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[4], add));
			}catch(Exception e)
			{
				
			}
	}
	//putIntoClubFunctionDomain methods ends here
	
	public String timeStamp()
	{
		Format format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStamp = format.format(new Date());
		
		return timeStamp;
	}
	
	public String date()
	{
		//check that its not 6am the next day 
		//clubs in cype run from 6am
	
		//get time and date and compare it to the 
		//plus 1 the current day for 6am
		//if current date is equal or greater than the compared date
		//then cype its in a new date....
		
		
		Format format  = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		
		return date;
	}
	
	//club_function result methods start here
	public void putIntoClubFunctionResults(String itemName, String id, String checkedin, String attending, String userCmoodVote,
											String userMusicRating, String clubName, String city, String country, String Date)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute Country = new ReplaceableAttribute("country", country, true);
		ReplaceableAttribute Attending = new ReplaceableAttribute("attending_result", attending, true);
		ReplaceableAttribute Checkedin = new ReplaceableAttribute("checkedin_result", checkedin, true);
		ReplaceableAttribute UserMusicRating = new ReplaceableAttribute("music_rating_result", userMusicRating,true);
		ReplaceableAttribute UserCmoodVote = new ReplaceableAttribute("cmood_vote_result", userCmoodVote, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Id = new ReplaceableAttribute("id", zero_padding(itemName), true);
		ReplaceableAttribute date = new ReplaceableAttribute("date", Date, true);
	
		item.withAttributes(City, Attending, Checkedin, UserMusicRating, UserCmoodVote , ClubName, Country, Id, date);
		add.add(item);
		
		 try{
				db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[6], add));
			}catch(Exception e)
			{
				
			}
	}
	
	public void putIntoClubFunctionResultsAttending(String itemName, String clubName, String attending, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Attending = new ReplaceableAttribute("attending_result", attending, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(Attending);
		attr.add(City);
		item.setAttributes(attr);
		add.add(item);
		
		 try{
			 db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[6], add));
			}catch(Exception e)
			{
				
			}
	}
	
	public void putIntoClubFunctionResultsCheckedin(String itemName, String clubName, String checkedin, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Checkedin = new ReplaceableAttribute("checkedin_result", checkedin, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(Checkedin);
		attr.add(City);
		item.setAttributes(attr);
		add.add(item);
		
		 try{
			 db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[6], add));
			}catch(Exception e)
			{
			
			}
	}
	
	public void putIntoClubFunctionResultsCmood(String itemName, String clubName, String cmood, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Cmood = new ReplaceableAttribute("cmood_vote_result", cmood, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(Cmood);
		attr.add(City);
		item.setAttributes(attr);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[6], add));
			}catch(Exception e)
			{
				
			}
	}
	
	public void putIntoClubFunctionResultsMusicRating(String itemName, String clubName, String musicRating, String city)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(itemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute musicR = new ReplaceableAttribute("music_rating_result", musicRating, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(musicR);
		attr.add(City);
		item.setAttributes(attr);
		add.add(item);
		
		try{
		db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[6], add));
		}catch(Exception e)
		{
			
		}
	}
	//club_function_results methods here:::::::::::::::::
	
	public void putIntoClubFunctionFutureDomain(String ItemName, String date,String attending, String checkedin,
		       String club, String userMusicRating, String userCmoodVote, String clubName, String UserEmail)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(ItemName));
		ReplaceableAttribute Date = new ReplaceableAttribute("date", date, true);
		ReplaceableAttribute Attending = new ReplaceableAttribute("attending", attending, true);
		ReplaceableAttribute Checkedin = new ReplaceableAttribute("checkedin", checkedin, true);
		ReplaceableAttribute UserMusicRating = new ReplaceableAttribute("user_music_rating", userMusicRating, true);
		ReplaceableAttribute UserCmoodVote = new ReplaceableAttribute("user_cmood_vote", userCmoodVote, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", UserEmail, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", zero_padding(ItemName), false);
	
		
		
		item.withAttributes(Date, Attending, Checkedin, UserMusicRating, UserCmoodVote , ClubName, Email, id);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[5], add));
		}catch(Exception e)
		{
			
		}
	}// putIntoClubFunctionFutureDomain ends here
	
	public void putIntoClubPicturesDomain(String ItemName, String clubName, String email, String imageName)
	{
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(ItemName));
		ReplaceableAttribute ImageName = new ReplaceableAttribute("image_name", imageName, true);
		ReplaceableAttribute Date = new ReplaceableAttribute("date", cypeDate.CypeNewDate(), true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Email = new ReplaceableAttribute("user_email", email, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", zero_padding(ItemName), false);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(ImageName);
		attr.add(Email);
		attr.add(Date);
		attr.add(id);
		item.setAttributes(attr);
	
		add.add(item);
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[2], add));
		}catch(Exception e)
		{
			
		}
		
	}//putIntoClubPicturesDomain ends here
	
	public void putIntoDomainTellUs(String id, String clubName, String address, String city, String state, String zipCode, String country)
	{
		String ItemName = id;
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(zero_padding(ItemName));
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Address = new ReplaceableAttribute("address", address, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute State= new ReplaceableAttribute("state", state, true);
		ReplaceableAttribute Zipcode = new ReplaceableAttribute("zip_code", zipCode, true);
		ReplaceableAttribute Country = new ReplaceableAttribute("country", country, true);
		ReplaceableAttribute Id = new ReplaceableAttribute("id", zero_padding(ItemName), false);
		List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
		
		
		attr.add(ClubName);
		attr.add(Address);
		attr.add(City);
		attr.add(State);
		attr.add(Zipcode);
		attr.add(Country);
		attr.add(Id);
		item.setAttributes(attr);
		
		
		add.add(item);
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[7], add));
		}catch(Exception e)
		{
			
		}
	}
	
	public void putIntoDomaingcm_reg(final String id, final String userEmail, final String reg_id)
	{
		
		new AsyncTask<String, String, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params) {
				String ItemName = id;
				List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
				
				ReplaceableItem item = new ReplaceableItem();
				item.setName(zero_padding(ItemName));
				ReplaceableAttribute UserEmail = new ReplaceableAttribute("user_email", userEmail, true);
				ReplaceableAttribute RegId = new ReplaceableAttribute("reg_id", reg_id, true);
				ReplaceableAttribute Id = new ReplaceableAttribute("id", zero_padding(ItemName), true);
				List<ReplaceableAttribute> attr = new ArrayList<ReplaceableAttribute>();
				
				
				attr.add(UserEmail);
				attr.add(RegId);
				attr.add(Id);
				item.setAttributes(attr);
				
				
				add.add(item);
				db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[8], add));
				return null;
			}
			
		}.execute();
			
	
	}
	public List<Item> RunSQLStatement(String sql)
	{
		try{
		SelectRequest request = new SelectRequest(sql);
	     items = db.select(request).getItems();
		}
		catch(Exception e)
		{
			//Log.d("INTERNET", "No INternet");
		}
		return items;
	}
	
	public void DeletImageRecord(final String id)
	{
		try{
			db.deleteAttributes(new DeleteAttributesRequest(DOMAINS[2], id));
		}catch(Exception e){}
	}
	
	public void DeletGcmRegRecord(final String id)
	{
		try{
			db.deleteAttributes(new DeleteAttributesRequest(DOMAINS[8], id));
		}catch(Exception e){}
	}
	

	public void UpdateUserDetailsDomain(String ItemName, String email,
			String firstName, String lastName, String user_email, String city, String accountType) {
		List<ReplaceableItem> add = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem();
		item.setName(ItemName);
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute FirstName = new ReplaceableAttribute("firstName", firstName, true);
		ReplaceableAttribute LastName = new ReplaceableAttribute("lastName", lastName, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id", ItemName, true);
		ReplaceableAttribute AccountType = new ReplaceableAttribute("account_type", accountType, true);
		
		item.withAttributes(FirstName, LastName, City, Email, id, AccountType);
		add.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[1], add));
		}catch(Exception e)
		{
			
		}
		
	}
	
	public void UpdateUsersDomain(String itemName, String email, String password, String accountType)
	{
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(itemName);
	
		ReplaceableAttribute Email = new ReplaceableAttribute("email", email, true);
		ReplaceableAttribute Password = new ReplaceableAttribute("password", password, true);
		ReplaceableAttribute AccountType = new ReplaceableAttribute("accountType", accountType, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id",itemName, true);
		ReplaceableAttribute isActivated = new ReplaceableAttribute("isActivated ",isACTIVATED , true);
	
		
		item.withAttributes(Email, Password, AccountType, id, isActivated);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[0], addList));
		}catch(Exception e)
		{
			
		}
	}



	public void putIntoPartyWivMeCountDomain(String itemName,
			String userEmail, String clubName, String defaultcount, String date) {
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(itemName);
	
		ReplaceableAttribute Email = new ReplaceableAttribute("email", userEmail, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Count = new ReplaceableAttribute("count", defaultcount, true);
		ReplaceableAttribute Date = new ReplaceableAttribute("date", date, true);
		ReplaceableAttribute id = new ReplaceableAttribute("id",itemName, true);
		
	
		
		item.withAttributes(Email, ClubName, Count, Date, id);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[9], addList));
		}catch(Exception e)
		{
			
		}
		
	}



	public void UpdatePartyWivMeDomain(String id, String userEmail, String clubName,  int pwmCount) {
		
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(id);
	
		ReplaceableAttribute Email = new ReplaceableAttribute("email", userEmail, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute Count = new ReplaceableAttribute("count", ""+pwmCount, true);
		ReplaceableAttribute Date = new ReplaceableAttribute("date", cypeDate.CypeNewDate(), true);
		ReplaceableAttribute Id = new ReplaceableAttribute("id",id, true);
		
	
		
		item.withAttributes(Email, ClubName, Count, Date, Id);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[9], addList));
		}catch(Exception e)
		{
			
		}
	}



	public void putIntoCheckPartyWivMeClick(String itemName, String sender_email,
			String reciever_email, String clubName, String date) {
		
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem(itemName);
	
		ReplaceableAttribute S_Email = new ReplaceableAttribute("pwm_sender_email", sender_email, true);
		ReplaceableAttribute ClubName = new ReplaceableAttribute("club_name", clubName, true);
		ReplaceableAttribute R_Email = new ReplaceableAttribute("pwm_reciever_email", reciever_email, true);
		ReplaceableAttribute Date = new ReplaceableAttribute("date", cypeDate.CypeNewDate(), true);
		ReplaceableAttribute Id = new ReplaceableAttribute("id",itemName, true);
		
	
		
		item.withAttributes(S_Email , ClubName, R_Email, Date, Id);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[10], addList));
		}catch(Exception e)
		{
			
		}
	}
	
	public void PutIntoPlacesDomain(String itemName, String isUsed, String city, String country, String lat, String lng)
	{
		List<ReplaceableItem> addList = new ArrayList<ReplaceableItem>();
		
		ReplaceableItem item = new ReplaceableItem( zero_padding(itemName));
	
		ReplaceableAttribute IsUsed = new ReplaceableAttribute("isUsed", isUsed, true);
		ReplaceableAttribute City = new ReplaceableAttribute("city", city, true);
		ReplaceableAttribute Country = new ReplaceableAttribute("country", country, true);
		ReplaceableAttribute Lat = new ReplaceableAttribute("lat", lat, true);
		ReplaceableAttribute Lng = new ReplaceableAttribute("lng", lng, true);
		ReplaceableAttribute Id = new ReplaceableAttribute("id", zero_padding(itemName), true);
		
	
		
		item.withAttributes(IsUsed , City, Country, Lat, Lng, Id);
		addList.add(item);
		
		try{
			db.batchPutAttributes(new BatchPutAttributesRequest(DOMAINS[11], addList));
		}catch(Exception e)
		{
			
		}
	}
	

	private String zero_padding(String no_zeros_id) {
	    String zero_padded_id = "";

	    int id_length = no_zeros_id.length();
	    if (id_length == 1) {
	        zero_padded_id = "00000"+no_zeros_id; 
	    } else if (id_length == 2) {
	        zero_padded_id = "0000"+no_zeros_id;
	    } else if (id_length == 3) {
	        zero_padded_id = "000"+no_zeros_id;
	    } else if (id_length == 4) {
	        zero_padded_id = "00"+no_zeros_id;
	    } else if (id_length == 5) {
	        zero_padded_id = "0"+no_zeros_id;
	    } else if (id_length == 6) {
	        zero_padded_id = no_zeros_id;
	    }

	    return zero_padded_id;
	}
	
}


