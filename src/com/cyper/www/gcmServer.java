package com.cyper.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;

import cype.database.Database;

import android.util.Log;

public class gcmServer {
	private static final String API_KEY = "key=AIzaSyDSsNwqvTZyPW2MN-ucJxMctb2a_06x7R0";
	private static final String ASKDANCEMESSAGE = " is asking for a Dance";
	private static final String ASKDRINKEMESSAGE = " is asking for a Drink";
	private static final String ACCEPTEDREQUESTMESSAGE = " has accepted your request to ";
	private final static  String EMPTY = "";
	private final static String MESSAGE = "data";
	private final static String USER = "data1";
	private final static String GCM_SENDER = "https://android.googleapis.com/gcm/send";
	private final static String REG_ID ="registration_id";
	private final static String TEXTFORMAT = "application/x-www-form-urlencoded;charset=UTF-8";
	private final static String CONTENTTYPE = "Content-Type";
	private final static String AUTHORIZATION = "Authorization";
	private final static String DANCEREQUEST = "Dance Request";
	private final static String DRINKREQUEST = "Drink Request";
	private final static String ACCEPTEDQUEST = "Accepted Request";
	private final static String REQUEST = "data2";
	
	String Reg_Id;
	Database db;
	
	public gcmServer()
	{
		db = new Database();
	}
	
	public void AskForADance(String senderEmail, String recieverEmail, String reciever_gcm_reg_id, String userName)
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"https://android.googleapis.com/gcm/send");
		
		
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair(REG_ID, reciever_gcm_reg_id));
			
			nameValuePairs.add(new BasicNameValuePair(MESSAGE, userName+ASKDANCEMESSAGE));
			nameValuePairs.add(new BasicNameValuePair(USER,senderEmail));
			nameValuePairs.add(new BasicNameValuePair(REQUEST,DANCEREQUEST));
			
			
	
			post.setHeader(AUTHORIZATION,API_KEY);
			post.setHeader(CONTENTTYPE, TEXTFORMAT);
			
			
			

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			InputStreamReader inputst = new InputStreamReader(response.getEntity().getContent());
			BufferedReader rd = new BufferedReader(inputst);
				

			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.e("HttpResponse", line);
				
					String s = line.substring(0);
					Log.i("GCM response",s);
					//Toast.makeText(v.getContext(), s, Toast.LENGTH_LONG).show();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void AskForADrink(String senderEmail, String recieverEmail, String reciever_gcm_reg_id, String userName)
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(GCM_SENDER);
		
		
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair(REG_ID, reciever_gcm_reg_id));
			
			nameValuePairs.add(new BasicNameValuePair(MESSAGE, userName+ASKDRINKEMESSAGE));
			nameValuePairs.add(new BasicNameValuePair(USER, senderEmail));
			nameValuePairs.add(new BasicNameValuePair(REQUEST,DRINKREQUEST));
			
			
			
	
			post.setHeader(AUTHORIZATION,API_KEY);
			post.setHeader(CONTENTTYPE, TEXTFORMAT);
			
			
			

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			InputStreamReader inputst = new InputStreamReader(response.getEntity().getContent());
			BufferedReader rd = new BufferedReader(inputst);
				

			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.e("HttpResponse", line);
				
					
					String s = line.substring(0);
					Log.i("GCM response",s);
					//Toast.makeText(v.getContext(), s, Toast.LENGTH_LONG).show();
				

			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void AcceptedRequest(String userName, String Reg_id, String request)
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(GCM_SENDER);
		
		
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair(REG_ID,Reg_id));
			
			nameValuePairs.add(new BasicNameValuePair(MESSAGE, userName+ACCEPTEDREQUESTMESSAGE+request ));
			nameValuePairs.add(new BasicNameValuePair(USER, userName));
			nameValuePairs.add(new BasicNameValuePair(REQUEST,ACCEPTEDQUEST));
			
			
	
			post.setHeader(AUTHORIZATION,API_KEY);
			post.setHeader(CONTENTTYPE, TEXTFORMAT);
			
			
			

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			InputStreamReader inputst = new InputStreamReader(response.getEntity().getContent());
			BufferedReader rd = new BufferedReader(inputst);
				

			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.e("HttpResponse", line);
				
					
					String s = line.substring(0);
					Log.i("GCM response",s);
					//Toast.makeText(v.getContext(), s, Toast.LENGTH_LONG).show();
				

			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
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
