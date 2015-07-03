package cype.SmsManager;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SMSmanager {
	private String CODE ;
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd;
	final static int CODE_LENGTH = 6;
	Context context;

	final static String CONFIRMATION_CODE_MESSAGE = "your Confirmation code is: ";
	private SmsManager smsManager;
	
	public SMSmanager(Context context)
	{
		smsManager = SmsManager.getDefault();
		rnd = new Random();
		this.context = context;
	}

	public void sendSignUpCode(String mobileNumber)
	{
		String code = randomString(CODE_LENGTH);
		CODE = code;
		/*
		 * 
		 
		  try 
	         {

	            String SENT      = "SMS_SENT";
	        	PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

			       context.registerReceiver(new BroadcastReceiver() 
			        {
			            @Override
			            public void onReceive(Context arg0, Intent arg1) 
			            {
			                int resultCode = getResultCode();
			                switch (resultCode) 
			                {
				                 case Activity.RESULT_OK: 
				                 Toast.makeText(context, "SMS sent",Toast.LENGTH_LONG).show();
				                 break;
				                 case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
				                 Toast.makeText(context, "Generic failure",Toast.LENGTH_LONG).show();
				                 break;
				                 case SmsManager.RESULT_ERROR_NO_SERVICE:       
				                 Toast.makeText(context, "No service",Toast.LENGTH_LONG).show();
				                 break;
				                 case SmsManager.RESULT_ERROR_NULL_PDU:        
				                 Toast.makeText(context, "Null PDU",Toast.LENGTH_LONG).show();
				                 break;
				                 case SmsManager.RESULT_ERROR_RADIO_OFF:        
				                 Toast.makeText(context, "Radio off",Toast.LENGTH_LONG).show();
				                 break;
			                }
			            }
			        }, new IntentFilter(SENT));
		
			           smsManager.sendTextMessage(mobileNumber, null, CONFIRMATION_CODE_MESSAGE+code, sentPI, null);
		
			       }
	         catch (Exception e) 
	         {
	            Toast.makeText(context, e.getMessage()+"!\n"+"Failed to send SMS", Toast.LENGTH_LONG).show();
	            e.printStackTrace();
	         }
		  */
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", CONFIRMATION_CODE_MESSAGE+code);
		sendIntent.setType("vnd.android-dir/mms-sms");
		sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Add new Flag to start new Activity
		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sendIntent);
		
	}
	

	private String randomString( int len ) 
	{
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	
	public String getCode()
	{
		return CODE;
	}
}
