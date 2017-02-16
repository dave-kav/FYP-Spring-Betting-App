package dk.cit.fyp.domain;

import java.sql.Date;

public class User {
	
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Date DOB;
	private float credit;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Date getDOB() {
		return DOB;
	}
	
	public void setDOB(Date dOB) {
		DOB = dOB;
	}
	
	public float getCredit() {
		return credit;
	}
	
	public void setCredit(float credit) {
		this.credit = credit;
	}
	
}
