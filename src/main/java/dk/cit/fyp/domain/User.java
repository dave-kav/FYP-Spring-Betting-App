package dk.cit.fyp.domain;

/**
 * User of web-application
 * 
 * @author Dave Kavanagh
 *
 */
public class User {
	
	private int id;
	private String username;
	private String password;
	private boolean admin;
	private Bet currentBet;

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

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
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public Bet getCurrentBet() {
		return currentBet;
	}

	public void setCurrentBet(Bet currentBet) {
		this.currentBet = currentBet;
	}

	@Override
	public String toString() {
		return "User [employeeID=" + id + ", username=" + username + ", password=" + password + ", admin="
				+ admin + "]";
	}
}
