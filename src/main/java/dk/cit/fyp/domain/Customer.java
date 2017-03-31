package dk.cit.fyp.domain;

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class Customer {
	
	@NotNull
	private String username;
	@Length(min=8)
	private String password;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private Date DOB;
	@Min(0)
	private double credit;
	List<Bet> bets;
	
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
	
	public double getCredit() {
		return credit;
	}
	
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	public List<Bet> getBets() {
		return bets;
	}

	public void setBets(List<Bet> bets) {
		this.bets = bets;
	}

	@Override
	public String toString() {
		return "Customer [username=" + username + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", DOB=" + DOB + ", credit=" + credit + "]";
	}	
}
