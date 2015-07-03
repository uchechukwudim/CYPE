package cype.date;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/*
 * CYpe time is from 18:00 to 6:00
 */
public class CypeDate {
	
	private final String DATEFORMAT = "yyyy-MM-dd";
	private final String TIMEFORMAT = "HH:mm:ss";
	
	
	
	private Date date;
	
	public CypeDate()
	{
		date = new Date();
	}
	
	public String getCurrentTime()
	{
		Format format  = new SimpleDateFormat(TIMEFORMAT, Locale.getDefault());
		String time = format.format(new Date());
		
		return time;
	}
	
	public String getCurrentDate()
	{
		Format format  = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
		String date = format.format(new Date());
		
		return date;
	}
	
	public  boolean isCheckInTime()
	{
		return isCheckInTime(getCurrentTime());
	}
	private boolean isCheckInTime(String curruntTime)
	{
		int hour = getHour(curruntTime);
		
		if(hour >= 18 && hour <= 24 || hour >=00 && hour <= 06)
			return true; 
		
		return false;
	}
	
	public String CypeNewDate()
	{
		return CypeCheckedinDate(getCurrentDate(), getCurrentTime());
	}
	private String CypeCheckedinDate(String currentDate, String currentTime)
	{
		String newCypeDate = "";
		
		int hour = getHour(currentTime);
		if(hour >= 07 && hour <= 24 )
			newCypeDate = currentDate;
		else if(hour >=00 && hour <= 06)
		{
			//minus to the previous date
			/*
			 * to do the minusing, check for end of days, months and year
			 */
			int year = getYear(currentDate);
			int day = getDay(currentTime);
			int month = getMonth();
			//int prevMonthDays = getmonthNumberofDays(month,year);
			if(day == 01)
			{
				//get month number and minus it by 1
				//find out how many days are in the month and give it to day
				//if month is 01 then sign month 12 and minus yea
				if(month == 01){
					month = 12;
				 	year = year-1;
				 	day =  getmonthNumberofDays(month,year);
				 	
				 	String Month = ""+month;
					if(Month.length() ==1)
						Month = "0"+Month;
					String Day = ""+day;
					if(Day.length()==1)
						Day = "0"+Day;
					
				 	newCypeDate = year+"-"+Month+"-"+Day;
				}
				else
				{
					month = month-1;
					day =  getmonthNumberofDays(month,year);
					
					String Month = ""+month;
					if(Month.length() ==1)
						Month = "0"+Month;
					
					String Day = ""+day;
					if(Day.length()==1)
						Day = "0"+Day;
					
					newCypeDate = year+"-"+Month+"-"+Day;
				}
			}
			else
			{
				//just minus day and return 
				day = day-1;
				String Month = ""+month;
				if(Month.length() ==1)
					Month = "0"+Month;
				
				String Day = ""+day;
				if(Day.length()==1)
					Day = "0"+Day;
				
				newCypeDate = year+"-"+Month+"-"+Day;
			}
		}
		return newCypeDate;
	}
	
	private int getHour(String currentTime)
	{
		Format format  = new SimpleDateFormat("HH", Locale.getDefault());
		String hour = format.format(new Date());
		
		   
		return Integer.parseInt(hour);
	}
	
	private int getYear(String currentDate)
	{
		Format format  = new SimpleDateFormat("yyyy", Locale.getDefault());
		String time = format.format(new Date());
	
		return Integer.parseInt(time);
	}
	
	public int getMonth()
	{
		Format format  = new SimpleDateFormat("MM", Locale.getDefault());
		String month = format.format(new Date());

		return Integer.parseInt(month);
	}
	

	public int getDay(String currentDate)
	{
		Format format  = new SimpleDateFormat("dd", Locale.getDefault());
		String time = format.format(new Date());
		
		return Integer.parseInt(time);
	}
	
	private int getmonthNumberofDays(int month, int year)
	{
		boolean leapYear = (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
		if (month == 4 || month == 6 || month == 9 || month == 11)
			return 30;
		else if (month == 2) 
			return (leapYear) ? 29 : 28;
		else 
			return 31;
	}
	
	
}
