package dk.cit.fyp.domain;

public enum Status {
	OPEN,
	WINNER,
	LOSER;

	public static int valueOf(Status status) {
		switch(status) {
			case OPEN: return 1;
			case WINNER: return 2;
			case LOSER: return 3;
			default: return 0;
		}
	}
}
