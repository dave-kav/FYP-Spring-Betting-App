package dk.cit.fyp.domain;

public class Horse {
	
	private String name;
	// unique ID of race in which horse is running
	private int raceID;
	// used for calculating winnings
	private int oddsEnumerator;
	// used for calculating winnings
	private int oddsDenominator;
	// The horses assigned number within the race
	private int number;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getRaceID() {
		return raceID;
	}
	
	public void setRaceID(int raceID) {
		this.raceID = raceID;
	}
	
	public int getOddsEnumerator() {
		return oddsEnumerator;
	}
	
	public void setOddsEnumerator(int oddsEnumerator) {
		this.oddsEnumerator = oddsEnumerator;
	}
	
	public int getOddsDenominator() {
		return oddsDenominator;
	}
	
	public void setOddsDenominator(int oddsDenominator) {
		this.oddsDenominator = oddsDenominator;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
