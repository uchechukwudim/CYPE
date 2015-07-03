package cype.clubs.adapter;

public class club {
    private String clubName;
    private String clubAddress;
    private String clubId;
    private String clubType;
	private String image;
	private String mood;
	private String time;
	private String attendingCount;
	private String checkedInCount;
	private String rateMusicCount;
	private String city;
	private String country;
	private String email;
	
	public club(String image, String mood, String time, String attendingCount, String checkedInCount,
			    String rateMusicCount, String city, String country, String email){
		this.image = image;
		this.mood = mood;
		this.time = time;
		this.attendingCount = attendingCount;
		this.checkedInCount = checkedInCount;
		this.rateMusicCount = rateMusicCount;
		this.city = city;
		this.country = country;
		this.email = email;
	}
	
	public club()
	{
		this.image = "";
		this.mood = "";
		this.time = "";
		this.attendingCount = "";
		this.checkedInCount = "";
		this.rateMusicCount = "";
		this.city = "";
		this.country = "";
		this.email = "";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image ;
	}

	public void setName(String image) {
		this.image = image;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAttendingCount() {
		return attendingCount;
	}

	public void setAttendingCount(String attendingCount) {
		this.attendingCount = attendingCount;
	}

	public String getCheckedInCount() {
		return checkedInCount;
	}

	public void setCheckedInCount(String checkedInCount) {
		this.checkedInCount = checkedInCount;
	}

	public String getRateMusicCount() {
		return rateMusicCount;
	}

	public void setRateMusicCount(String rateMusicCount) {
		this.rateMusicCount = rateMusicCount;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubAddress() {
		return clubAddress;
	}

	public void setClubAddress(String clubAddress) {
		this.clubAddress = clubAddress;
	}

	public String getClubId() {
		return clubId;
	}

	public void setClubId(String clubId) {
		this.clubId = clubId;
	}

	public String getClubType() {
		return clubType;
	}

	public void setClubType(String clubType) {
		this.clubType = clubType;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	
}
