package cype.history.adapter;

public class HistoryChildren {
  
	String clubLogo;
	String attendedCount;
	String checkedInCount;
	String musicRatingCount;
	String clubMood;
	String date;

	
	public HistoryChildren(String clubLogo, String attendedCount, String checkedInCount, String musicRatingCount, 
						   String clubMood, String date)
	{
		this.clubLogo = clubLogo;
		this.attendedCount = attendedCount;
		this.checkedInCount = checkedInCount;
		this.musicRatingCount = musicRatingCount;
		this.clubMood = clubMood;
		this.date = date;
	}
	
	public HistoryChildren()
	{
		this.clubLogo = "";
		this.attendedCount = "";
		this.checkedInCount = "";
		this.musicRatingCount = "";
		this.clubMood = "";
		this.date = "";
		
	}

	public String getClubLogo() {
		return clubLogo;
	}

	public void setClubLogo(String clubLogo) {
		this.clubLogo = clubLogo;
	}

	public String getAttendedCount() {
		return attendedCount;
	}

	public void setAttendedCount(String attendedCount) {
		this.attendedCount = attendedCount;
	}

	public String getCheckedInCount() {
		return checkedInCount;
	}

	public void setCheckedInCount(String checkedInCount) {
		this.checkedInCount = checkedInCount;
	}

	public String getMusicRatingCount() {
		return musicRatingCount;
	}

	public void setMusicRatingCount(String musicRatingCount) {
		this.musicRatingCount = musicRatingCount;
	}

	public String getClubMood() {
		return clubMood;
	}

	public void setClubMood(String clubMood) {
		this.clubMood = clubMood;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}



		
	
}
