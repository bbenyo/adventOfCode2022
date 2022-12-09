package bb.aoc2022.handler;

import java.util.HashSet;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;
import bb.aoc2022.Utilities;

public class Day9 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day9.class.getName());
	
	protected Location H = new Location(0,0);
	protected Location T = new Location(0,0);
	
	HashSet<String> visited = new HashSet<>();
		
	@Override
	public void handleInput(String line) {
		// Initialization
		if (visited.isEmpty()) {
			visited.add(T.toString());
		}
		String[] cmd = line.trim().split(" ");
		if (cmd.length != 2) {
			logger.error("Invalid command: "+line);
			return;
		}
		Integer steps = Utilities.parseIntOrNull(cmd[1]);
		if (steps == null) {
			logger.error("Invalid command: number of steps not parseable: "+cmd[1]);
			return;
		}
		
		for (int i=0; i<steps; ++i) {
			switch (cmd[0]) {
			case "U" : H.moveUp(1); moveTail(); break;
			case "D" : H.moveDown(1); moveTail(); break;
			case "L" : H.moveLeft(1); moveTail(); break;
			case "R" : H.moveRight(1); moveTail(); break;
			default : 
				logger.error("Invalid command: move command unrecognized: "+cmd[0]);
				return;
			}
			logger.info("Moved "+cmd[0]+" H: "+H+" T: "+T);
			visited.add(T.toString());
		}
	}
	
	protected void moveTail() {
		moveTail(H, T);
	}
	
	protected void moveTail(Location head, Location tail) {
		if (head.isAdjacent(tail)) {
			return;
		}
		int deltaX = head.getX() - tail.getX();
		int deltaY = head.getY() - tail.getY();
		
		if (deltaY < 0) {
			tail.moveUp(1);
		} else if (deltaY > 0) {
			tail.moveDown(1);
		}
		
		if (deltaX < 0) {
			tail.moveLeft(1);
		} else if (deltaX > 0) {
			tail.moveRight(1);
		}
	}

	@Override
	public void output() {
		logger.info("Number of locations visited by the tail: "+visited.size());
	}

}
