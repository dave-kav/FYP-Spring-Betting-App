package dk.cit.fyp.domain;

public class Bet {
	
	// Unique number auto-assigned to each bet via DB
	private int betID;
	private String timePlaced;
	// E.g. horse that is being bet on
	private String selection;
	// particular race to which the selection and bet relate
	private int raceID;
	// amount being bet
	private float stake;
	//location of captured betting slip image
	private String image;
	private boolean eachWay;
	// indicates whether bet has won or lost
	private boolean winner;
	// indicates whether bet has been translated i.e. details entered into system
	private boolean translated;
	// indicates whether bet was placed via app
	private boolean onlineBet;
	// amount returned if bet is a winner
	private float winnings;
	// indicates whether bet has been placed by a customer of interest e.g. high staking customer
	private boolean monitoredCustomer;
	// indicates if bet needs to be settled
	private boolean open;
	//indicates if bet has been paid out or not
	private boolean paid;
	
	public int getBetID() {
		return betID;
	}
	
	public void setBetID(int betID) {
		this.betID = betID;
	}
	
	public String getTimePlaced() {
		return timePlaced;
	}

	public void setTimePlaced(String timePlaced) {
		this.timePlaced = timePlaced;
	}

	public String getSelection() {
		return selection;
	}
	
	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	public int getRaceID() {
		return raceID;
	}
	
	public void setRaceID(int raceID) {
		this.raceID = raceID;
	}
	
	public float getStake() {
		return stake;
	}
	
	public void setStake(float stake) {
		this.stake = stake;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public boolean isEachWay() {
		return eachWay;
	}

	public void setEachWay(boolean eachWay) {
		this.eachWay = eachWay;
	}

	public boolean isWinner() {
		return winner;
	}
	
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	
	public boolean isTranslated() {
		return translated;
	}
	
	public void setTranslated(boolean translated) {
		this.translated = translated;
	}
	
	public boolean isOnlineBet() {
		return onlineBet;
	}
	
	public void setOnlineBet(boolean onlineBet) {
		this.onlineBet = onlineBet;
	}
	
	public float getWinnings() {
		return winnings;
	}
	
	public void setWinnings(float winnings) {
		this.winnings = winnings;
	}
	
	public boolean isMonitoredCustomer() {
		return monitoredCustomer;
	}
	
	public void setMonitoredCustomer(boolean monitoredCustomer) {
		this.monitoredCustomer = monitoredCustomer;
	}
	
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	
}
