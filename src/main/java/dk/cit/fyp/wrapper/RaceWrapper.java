package dk.cit.fyp.wrapper;

import java.util.ArrayList;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;

public class RaceWrapper {

	private ArrayList<Horse> horseList;
	private Race race;

	public ArrayList<Horse> getHorseList() {
		return horseList;
	}

	public void setHorseList(ArrayList<Horse> horseList) {
		this.horseList = horseList;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}	
}
