package bb.aoc2022.handler;

public class Day2b extends Day2 {

	@Override
	protected RPS getPlayerFromStrategy(String s, RPS opponent) {
		switch(s) {
		case "X":
			// We need to lose
			switch (opponent) {
			case ROCK : return RPS.SCISSORS;
			case SCISSORS : return RPS.PAPER;
			case PAPER : return RPS.ROCK;
			default: return RPS.INVALID;
			}

		case "Y":
			// We need to draw
			return opponent;
		case "Z":
			// We need to win
			switch (opponent) {
			case ROCK : return RPS.PAPER;
			case SCISSORS : return RPS.ROCK;
			case PAPER : return RPS.SCISSORS;
			default: return RPS.INVALID;
			}
		default:
			return RPS.INVALID;
		}
	}
}
