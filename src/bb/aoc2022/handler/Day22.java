package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;

public class Day22 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day22.class.getName());
	
	protected List<String> grid;
	
	@Override
	public void initialize() {
		grid = new ArrayList<>();
	}
	
	abstract protected class Command {
		private Command() {};
		abstract void execute(); 
	}
	
	protected class Move extends Command {
		Integer steps = null;
		
		public Move(int s) {
			super();
			steps = s;
		}
		
		@Override
		public String toString() {
			return "Move "+steps;
		}

		@Override
		void execute() {
			for (int i=0; i<steps; ++i) {
				if (!forward()) {
					// We're stuck at a wall
					return;
				}
			}
		}
	}
	
	protected boolean forward() {
		Location newLoc = new Location(me.getX(), me.getY());
		switch(facing) {
		case '>' : newLoc.setX(me.getX() + 1); handleRightWrap(newLoc); break;
		case '<' : newLoc.setX(me.getX() - 1); handleLeftWrap(newLoc); break;
		case 'v' : newLoc.setY(me.getY() + 1); handleDownWrap(newLoc); break;
		case '^' : newLoc.setY(me.getY() - 1); handleUpWrap(newLoc); break;
		default:
			logger.error("Unknown facing: "+facing);
		}
		String row = grid.get(newLoc.getY());
		if (row.charAt(newLoc.getX()) == '#') {
			// Stuck at a wall, we don't move
			return false;
		} else if (row.charAt(newLoc.getX()) != '.') {
			logger.error("Invalid cell: "+newLoc);
		}
		me = newLoc;
		return true;
	}
	
	protected void handleRightWrap(Location l) {
		String row = grid.get(l.getY());
		if (l.getX() >= row.length()) {
			l.setX(0);
		}
		while (row.charAt(l.getX()) == ' ') {
			l.setX(l.getX() + 1);
			if (l.getX() == row.length()) {
				l.setX(0);
			}
		}
	}
	
	protected void handleLeftWrap(Location l) {
		String row = grid.get(l.getY());
		if (l.getX() < 0) {
			l.setX(row.length() - 1);
		}
		while (row.charAt(l.getX()) == ' ') {
			l.setX(l.getX() - 1);
			if (l.getX() < 0) {
				l.setX(row.length() - 1);
			}
		}
	}
	
	protected void handleDownWrap(Location l) {
		int x = l.getX();
		if (l.getY() >= grid.size()) {
			l.setY(0);
		}
		String row = grid.get(l.getY());
		while (row.charAt(x) == ' ') {
			l.setY(l.getY() + 1);
			if (l.getY() == grid.size()) {
				l.setY(0);
			}
			row = grid.get(l.getY());
		}
	}
	
	protected void handleUpWrap(Location l) {
		int x = l.getX();
		if (l.getY() < 0) {
			l.setY(grid.size() - 1);
		}
		String row = grid.get(l.getY());
		while (row.charAt(x) == ' ') {
			l.setY(l.getY() - 1);
			if (l.getY() < 0) {
				l.setY(grid.size() - 1);
			}
			row = grid.get(l.getY());
		}
	}
	
	protected class Turn extends Command {
		boolean left = true;
		
		public Turn(char c) {
			super();
			if (c == 'L') {
				left = true;
			} else if (c == 'R') {
				left = false;
			} else {
				throw new IllegalArgumentException("Unrecognized command: "+c);
			}
		}
		
		@Override
		public String toString() {
			if (left) {
				return "Turn Left";
			} else {
				return "Turn Right";
			}
		}

		@Override
		void execute() {
			switch (facing) {
			case '>': if (left) facing = '^'; else facing = 'v'; break;
			case '<': if (left) facing = 'v'; else facing = '^'; break;
			case 'v': if (left) facing = '>'; else facing = '<'; break;
			case '^': if (left) facing = '<'; else facing = '>'; break;
			default:
				logger.error("Unknown facing: "+facing);
			}
		}
	}
	
	protected boolean gridDone = false;
	protected List<Command> program = new ArrayList<>();

	@Override
	public void handleInput(String line) {
		if (line.length() == 0) {
			gridDone = true;
			return;				
		}
		
		if (!gridDone) {
			grid.add(line);
		} else {
			StringBuffer nextInt = new StringBuffer();
			for (int i=0; i<line.length(); ++i) {
				char c = line.charAt(i);
				if (c == 'R' || c == 'L') {
					String steps = nextInt.toString();
					if (steps.length() > 0) {
						program.add(new Move(Integer.parseInt(steps)));
					}
					nextInt = new StringBuffer();
					program.add(new Turn(c));
				} else if (Character.isDigit(c)) {
					nextInt.append(c);
				}
			}
			String steps = nextInt.toString();
			if (steps.length() > 0) {
				program.add(new Move(Integer.parseInt(steps)));
			}
		}
	}
		
	protected String writeGrid() {
		StringBuffer sb = new StringBuffer();
		for (String g : grid) {
			sb.append(System.lineSeparator());
			sb.append(g);
		}
		return sb.toString();
	}

	protected Location me = null;
	char facing = '>';
	
	@Override
	public void output() {
		// Simplfy the logic a bit, make all rows the same size by right padding with spaces
		//  Left is already padded
		int maxSize = 0;
		for (String r : grid) {
			if (r.length() > maxSize) {
				maxSize = r.length();
			}
		}
		for (int i=0; i<grid.size(); ++i) {
			String row = grid.remove(i);
			StringBuffer sb = new StringBuffer(row);
			for (int j=row.length(); j<maxSize; ++j) {
				sb.append(" ");
			}
			grid.add(i, sb.toString());
		}
			
		logger.info(writeGrid());
		String topRow = grid.get(0);
		int myPos = topRow.indexOf(".");
		me = new Location(myPos, 0);
		for (Command c : program) {
			logger.info("Executing "+c);
			c.execute();
			logger.info(" I'm now at "+me+" "+facing);
			String cRow = grid.get(me.getY());
			char cCol = cRow.charAt(me.getX());
			if (cCol != '.') {
				logger.error("Invalid loc: "+me);
			}
		}
		logger.info("Final location (0 indexed, so add 1s): "+me+" Final facing: "+facing);
		
		int password = (1000 * (me.getY()+1)) + (4 * (me.getX()+1));
		switch(facing) {
		case '>': break;
		case 'v' : password += 1; break;
		case '<' : password += 2; break;
		case '^' : password += 3; break;
		default:
			logger.error("Unknown facing: "+facing);
		}
		logger.info("Password: "+password);
	}

}
