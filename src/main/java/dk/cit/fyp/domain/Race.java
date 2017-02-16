package dk.cit.fyp.domain;

import java.sql.Date;

public class Race {
	
	private int raceID;
	private Date time;
	private String racetrack;
	
	public int getRaceID() {
		return raceID;
	}
	
	public void setRaceID(int raceID) {
		this.raceID = raceID;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date date) {
		this.time = date;
	}
	
	public String getRacetrack() {
		return racetrack;
	}
	
	public void setRacetrack(String racetrack) {
		this.racetrack = racetrack;
	}

}
