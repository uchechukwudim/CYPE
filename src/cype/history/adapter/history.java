package cype.history.adapter;

public class history {
  private String date;
  
  public history(String date)
  {
	  this.date = date;
  }
  
  public history()
  {
	  this.date = "";
  }

public String getDate() {
	return date;
}

public void setDate(String date) {
	this.date = date;
}
}
