package dk.cit.fyp.wrapper;

import java.util.ArrayList;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;

public class RaceWrapper {

	private Horse winner;
	private ArrayList<Horse> horseList;
	private Race race;

	public Horse getWinner() {
		return winner;
	}

	public void setWinner(Horse winner) {
		this.winner = winner;
	}

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
