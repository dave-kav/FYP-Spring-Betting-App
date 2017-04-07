package dk.cit.fyp.domain;

/**
 * Horse entity.
 * 
 * @author Dave Kavanagh
 *
 */
public class Horse {
	
	private int selectionID;
	private String name;
	// unique ID of race in which horse is running
	private int raceID;
	// The horses assigned number within the race
	private int number;
	
	public int getSelectionID() {
		return selectionID;
	}

	public void setSelectionID(int selectionID) {
		this.selectionID = selectionID;
	}

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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return "Horse [name=" + name + ", raceID=" + raceID + ", number=" + number + "]";
	}
}
