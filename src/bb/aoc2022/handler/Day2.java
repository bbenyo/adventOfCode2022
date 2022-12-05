package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day2 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day2.class.getName());
	
	int totalScore = 0;
	
	public enum RPS {ROCK, PAPER, SCISSORS, INVALID}
	class RockPaperScissors {
		RPS opponent;
		RPS player;
		int score;
		
		public RockPaperScissors(String[] input) {
			if (input.length == 2) {
				setOpponentFromStrategy(input[0]);
				setPlayerFromStrategy(input[1], opponent);
			} else {
				opponent = RPS.INVALID;
				player = RPS.INVALID;
			}
		}
		
		public void setOpponentFromStrategy(String s) {
			switch (s) {
			case "A" : opponent = RPS.ROCK; break;
			case "B" : opponent = RPS.PAPER; break;
			case "C" : opponent = RPS.SCISSORS; break;
			default : 
				opponent = RPS.INVALID;
				logger.error("Invalid opponent input: "+s);
			}
		}
		
		// Use a enclosing class method so it can be overridden easily by Day2b
		public void setPlayerFromStrategy(String s, RPS opponent) {
			player = getPlayerFromStrategy(s, opponent);
		}
				
		public void computeScore() {
			score = 0;
			switch (player) {
			case ROCK : score = 1; break;
			case PAPER : score = 2; break;
			case SCISSORS : score = 3; break;
			default:
				break;
			}
			if (player.equals(opponent)) {
				score += 3; // draw;
			} else {
				switch (player) {
				case ROCK: if (opponent == RPS.SCISSORS) score += 6; break;
				case PAPER: if (opponent == RPS.ROCK) score += 6; break;
				case SCISSORS: if (opponent == RPS.PAPER) score += 6; break;
				default: break;
				}
			}			
		}
	}

	// Day2 version ignores the opponent play, the strategy is independent
	protected RPS getPlayerFromStrategy(String s, RPS opponent) {
		switch (s) {
		case "X" : return RPS.ROCK;
		case "Y" : return RPS.PAPER;
		case "Z" : return RPS.SCISSORS;
		default : 
			logger.error("Invalid player input: "+s);
			return RPS.INVALID;
		}
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		String[] strategy = line.split(" ");
		RockPaperScissors rps = new RockPaperScissors(strategy);
		rps.computeScore();
		totalScore += rps.score;
	}
	
	@Override
	public void output() {
		logger.info("Total Score: "+totalScore);
	}

}
