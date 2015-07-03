package cype.cpeople.adapter;

public class cpeople {
  int girlsAttendingCount;
  int boysAttendingCount;
  
  int girlsCheckedInCount;
  int boysCheckedInCount;
  
  int cpeopleTotal;
  
  String clubName;
  String clubPhotos;
  
  public cpeople(int girlsAttendingCount, int boysAttendingCount, int girlsCheckedInCount, int boysCheckedInCount,
		         int cpeopleTotal, String clubName, String clubPhotos){
	  this.boysAttendingCount = boysAttendingCount;
	  this.girlsAttendingCount = girlsAttendingCount;
	  this.girlsCheckedInCount = girlsCheckedInCount;
	  this.boysCheckedInCount = boysCheckedInCount;
	  this.cpeopleTotal = cpeopleTotal;
	  this.clubName = clubName;
	  this.clubPhotos = clubPhotos;
  }
  
  public cpeople()
  {
	  this.boysAttendingCount = 0;
	  this.girlsAttendingCount =0;
	  this.girlsCheckedInCount = 0;
	  this.boysCheckedInCount = 0;
	  this.cpeopleTotal = 0;
	  this.clubName = "";
	  this.clubPhotos = "";
  }

public int getGirlsAttendingCount() {
	return girlsAttendingCount;
}

public void setGirlsAttendingCount(int girlsAttendingCount) {
	this.girlsAttendingCount = girlsAttendingCount;
}

public int getBoysAttendingCount() {
	return boysAttendingCount;
}

public void setBoysAttendingCount(int boysAttendingCount) {
	this.boysAttendingCount = boysAttendingCount;
}

public int getGirlsCheckedInCount() {
	return girlsCheckedInCount;
}

public void setGirlsCheckedInCount(int girlsCheckedInCount) {
	this.girlsCheckedInCount = girlsCheckedInCount;
}

public int getBoysCheckedInCount() {
	return boysCheckedInCount;
}

public void setBoysCheckedInCount(int boysCheckedInCount) {
	this.boysCheckedInCount = boysCheckedInCount;
}

public int getCpeopleTotal() {
	return cpeopleTotal;
}

public void setCpeopleTotal(int cpeopleTotal) {
	this.cpeopleTotal = cpeopleTotal;
}

public String getClubName() {
	return clubName;
}

public void setClubLogo(String clubName) {
	this.clubName = clubName;
}

public String getClubPhotos() {
	return clubPhotos;
}

public void setClubPhotos(String clubPhotos) {
	this.clubPhotos = clubPhotos;
}
  
  
}
