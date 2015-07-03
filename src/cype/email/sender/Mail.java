package cype.email.sender;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;



public class Mail extends javax.mail.Authenticator{
	private final static String MESSAGESENT = "Message Sent. Please check your email";
	private String _user; 
	  private String _pass; 
	 
	  private String[] _to; 
	  private String _from; 
	 
	  private String _port; 
	  private String _sport; 
	 
	  private String _host; 
	 
	  private String _subject; 
	  private String _body; 
	 
	  private boolean _auth; 
	   
	  private boolean _debuggable; 
	 
	  private Multipart _multipart; 
	  private String _Codesubject ;
	  
	  public Mail() 
	  { 
		    _host = "smtp.gmail.com"; // default smtp server 
		    _port = "465"; // default smtp port 
		    _sport = "465"; // default socketfactory port 
		 
		    _user = "uchechukwu.dim@gmail.com"; // username 
		    _pass = "Crackcocain1986"; // password 
		    _from = "noreply@cype.com"; // email sent from 
		    _subject = "Password Code"; // email subject 
		    _Codesubject = "Cype Confirmation Code";
		    _body = ""; // email body 
		 
		    _debuggable = false; // debug mode on or off - default off 
		    _auth = true; // smtp authentication - default on 
		 
		    _multipart = new MimeMultipart(); 
		 
		    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
		    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
		    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
		    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
		    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
		    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
		    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
		    CommandMap.setDefaultCommandMap(mc);
		  } 
	  
	  public Mail(String user, String pass)
	  { 
		    this(); 
		 
		    _user = user; 
		    _pass = pass; 
	  } 
	  
	  public void send(String email, String password, FragmentManager Fm, Activity ac) throws Exception
	  { 
		    Properties props = _setProperties(); 
		 
		    
		      Session session = Session.getInstance(props, this); 
		 
		      MimeMessage msg = new MimeMessage(session); 
		 
		      msg.setFrom(new InternetAddress(_from)); 
		       
		      InternetAddress addressTo = new InternetAddress(email); 
		       
		        msg.setRecipient(RecipientType.TO, addressTo); 
		 
		      msg.setSubject(_subject); 
		      msg.setSentDate(new Date()); 
		 
		      // setup message body 
		      BodyPart messageBodyPart = new MimeBodyPart(); 
		      messageBodyPart.setText("Hello Cyper, \n\nPlease copy the code below to sign into your CYPE account with your email address." +
						"\n\nPlease do well to change your password once your logged in"
						+ "\n\n PASSWORD: "+password+"" +
								" \n\n\n Thank you for using CYPE" +
								"\n\n CYPE Team"); 
		      _multipart.addBodyPart(messageBodyPart); 
		 
		      // Put parts in message 
		      msg.setContent(_multipart); 
		 
		      // send email 
		      Transport.send(msg); 
		      dialogB box = new dialogB(ac);
			  box.setMessage(MESSAGESENT);
			  box.show(Fm, MESSAGESENT);
		 
		   
	} 
	  
	  public void sendSignupCode(String code, String recieverEmail) throws Exception
	  {
		  Properties props = _setProperties(); 
			 
		    
	      Session session = Session.getInstance(props, this); 
	 
	      MimeMessage msg = new MimeMessage(session); 
	 
	      msg.setFrom(new InternetAddress(_from)); 
	       
	      InternetAddress addressTo = new InternetAddress(recieverEmail); 
	       
	        msg.setRecipient(RecipientType.TO, addressTo); 
	 
	      msg.setSubject(_Codesubject ); 
	      msg.setSentDate(new Date()); 
	 
	      // setup message body 
	      BodyPart messageBodyPart = new MimeBodyPart(); 
	      messageBodyPart.setText("Hello Cyper, \n\nPlease copy the code below to complete signup for your CYPE account." +
					 "\n\nCODE: "+code+"" +
							"\n\n\nThank you for using CYPE" +
							"\n\nCYPE Team"); 
	      _multipart.addBodyPart(messageBodyPart); 
	 
	      // Put parts in message 
	      msg.setContent(_multipart); 
	 
	     
	  }
	  
	  @Override 
	  public PasswordAuthentication getPasswordAuthentication() { 
	    return new PasswordAuthentication(_user, _pass); 
	  } 
	 
	  private Properties _setProperties() { 
	    Properties props = new Properties(); 
	 
	    props.put("mail.smtp.host", _host); 
	 
	    if(_debuggable) { 
	      props.put("mail.debug", "true"); 
	    } 
	 
	    if(_auth) { 
	      props.put("mail.smtp.auth", "true"); 
	    } 
	 
	    props.put("mail.smtp.port", _port); 
	    props.put("mail.smtp.socketFactory.port", _sport); 
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
	    props.put("mail.smtp.socketFactory.fallback", "false"); 
	 
	    return props; 
	  } 
	  
		@SuppressLint("ValidFragment")
		private class dialogB extends DialogFragment {
				
			 private String message;
			 Activity ac;
		     public dialogB(Activity ac)
		     {
		    	 this.ac = ac;
		     }
			
			    @Override
			    public Dialog onCreateDialog(Bundle savedInstanceState) {
			        // Use the Builder class for convenient dialog construction
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage(message)
			               .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			                   @Override
							public void onClick(DialogInterface dialog, int id) {
			                       dialog.cancel();
			                       ac.finish();
			                   }
			               });
			               
			        // Create the AlertDialog object and return it
			        return builder.create();
			    }
			 
			 public void setMessage(String message)
			 {
				 this.message = message;
			 }
		}//dialog box class ends here
}
