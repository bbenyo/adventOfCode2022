package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;

public class Day23 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day23.class.getName());
	
	// 0 = North, 1 = South, 2 = West, 3 = East
	public int currentFirstDirection = 0;
	
	protected class Elf {
		Location loc;
		Location proposedMove;
		
		public Elf(Location l) {
			this.loc = l;
			elfLocs.add(l);
			this.proposedMove = null;
		}
		
		@Override
		public String toString() {
			return loc.toString();
		}
		
		public void proposeMove() {
			if (neighborsEmpty()) {
				proposedMove = null;
			} else {
				int curDir = currentFirstDirection;
				for (int i=0; i<4; ++i) {
					curDir = (currentFirstDirection + i) % 4;
					proposedMove = proposeMove1(curDir);
					if (proposedMove != null) {
						return;
					}
				}				
			}
		}
		
		public Location proposeMove1(int curDir) {
			int x = loc.getX();
			int y = loc.getY();
			Location l1 = null;
			Location l2 = null;
			Location l3 = null;
			switch(curDir) {
			case 0 : // North
				l1 = new Location(x-1, y-1);
				l2 = new Location(x, y-1);
				l3 = new Location(x+1, y-1);
				break;
			case 1 : // South
				l1 = new Location(x-1, y+1);
				l2 = new Location(x, y+1);
				l3 = new Location(x+1, y+1);
				break;
			case 2 : // West
				l1 = new Location(x-1, y-1);
				l2 = new Location(x-1, y);
				l3 = new Location(x-1, y+1);
				break;
			case 3:
				l1 = new Location(x+1, y-1);
				l2 = new Location(x+1, y);
				l3 = new Location(x+1, y+1);
				break;
			}
			if (elfLocs.contains(l1)) {
				return null;
			}
			if (elfLocs.contains(l2)) {
				return null;
			}
			if (elfLocs.contains(l3)) {
				return null;
			}
			return l2;
		}
		
		public boolean neighborsEmpty() {
			int x = loc.getX();
			int y = loc.getY();
			for (int i=x-1; i <= x+1; ++i) {
				for (int j=y-1; j<=y+1; ++j) {
					if (i != x || j != y) {
						Location l2 = new Location(i,j);
						if (elfLocs.contains(l2)) {
							return false; 
						}						
					}
				}
			}
			return true;
		}
	}
	
	protected List<Elf> elves;
	protected HashSet<Location> elfLocs;
	
	@Override
	public void initialize() {
		elves = new ArrayList<>();
		elfLocs = new HashSet<>();
	}

	int curY = 0;

	// Elf bounding box
	protected Location topLeft = new Location(0,0);
	protected Location bottomRight = new Location(0,0);

	protected void computeBoundingBox() {
		Location e0 = elves.get(0).loc;
		topLeft = new Location(e0.getX(), e0.getY());
		bottomRight = new Location(e0.getX(), e0.getY());
		for (Elf e : elves) {
			if (e.loc.getX() < topLeft.getX()) {
				topLeft.setX(e.loc.getX());
			}
			if (e.loc.getX() > bottomRight.getX()) {
				bottomRight.setX(e.loc.getX());
			}
			if (e.loc.getY() < topLeft.getY()) {
				topLeft.setY(e.loc.getY());
			}
			if (e.loc.getY() > bottomRight.getY()) {
				bottomRight.setY(e.loc.getY());
			}
		}
	}
	
	@Override
	public void handleInput(String line) {
		for (int x=0; x<line.length(); ++x) {
			if (line.charAt(x) == '#') {
				Location elfLoc = new Location(x, curY);
				Elf e = new Elf(elfLoc);
				elves.add(e);
			}
		}
		curY++;
	}
			
	protected void logElves() {
		computeBoundingBox();
		logger.info("Top Left: "+topLeft+" Bottom Right: "+bottomRight);
		StringBuffer sb = new StringBuffer();
		for (Elf e : elves) {
			sb.append(e.loc);
			sb.append(System.lineSeparator());
		}
		logger.info(sb.toString());
	}
	
	protected void movePhase1() {
		for (Elf e : elves) {
			e.proposeMove();
		}
	}
	
	protected void movePhase2() {
		HashSet<Location> proposedLocs = new HashSet<>();
		HashSet<Location> multipleLocs = new HashSet<>();
		
		for (Elf e : elves) {
			if (e.proposedMove != null) {
				if (proposedLocs.contains(e.proposedMove)) {
					// Someone else proposed moving here
					multipleLocs.add(e.proposedMove);
				} else {
					proposedLocs.add(e.proposedMove);
				}
			}
		}
		
		for (Elf e : elves) {
			if (e.proposedMove != null && multipleLocs.contains(e.proposedMove)) {
				// We can't move, someone else is trying to move here too
				e.proposedMove = null;
			}
		}
	}
	
	boolean oneMoved = false;
	protected void movePhaseEnd() {
		oneMoved = false;
		for (Elf e : elves) {
			if (e.proposedMove != null) {
				oneMoved = true;
				e.loc = e.proposedMove;
			}
			e.proposedMove = null;
		}
		
		elfLocs.clear();
		for (Elf e : elves) {
			elfLocs.add(e.loc);
		}
	}
	
	protected int roundsToSimulate = 10;
	
	@Override
	public void output() {
		logElves();
		for (int i=0; i<roundsToSimulate; ++i) {
			movePhase1();
			movePhase2();
			movePhaseEnd();
			currentFirstDirection++;
			if (currentFirstDirection == 4) {
				currentFirstDirection = 0;
			}
			computeBoundingBox();
			logElves();
		}
		computeBoundingBox();
		logElves();
		logger.info("Top Left: "+topLeft);
		logger.info("Bottom Right: "+bottomRight);
		int width = (bottomRight.getX() - topLeft.getX()) + 1;
		int height = (bottomRight.getY() - topLeft.getY()) + 1;
		logger.info("Width: "+width+" Height: "+height);
		int area = height * width;
		logger.info("Area: "+area);
		int empty = area - elves.size();
		logger.info("Empty: "+empty);
	}

}
