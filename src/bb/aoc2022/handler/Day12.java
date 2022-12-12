package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;
import bb.aoc2022.Node;

public class Day12 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day12.class.getName());
	
	// Don't know how big the grid will be, allow it to be non-square
	List<int[]> grid = new ArrayList<>();
	
	protected class HillNode extends Node {

		public HillNode(Location l1) {
			super(l1);
		}
		
		@Override
		public Node createNode(Location l1) {
			return new HillNode(l1);
		}
		
		@Override
		public int getGridSizeX() {
			if (grid.isEmpty()) {
				logger.error("Haven't parsed grid yet!");
				return 0;
			}
			return grid.get(0).length;  // Assuming equal length rows
		}
		
		@Override
		public int getGridSizeY() {
			return grid.size();
		}
		
		// Guess we can move directly to the end
		@Override
		public int computeHeuristic() {
			return Math.abs(end.getX() - getX()) + Math.abs(end.getY() - getY());
		}
		
		@Override
		// Add neighbors based on where we can go from here.
		// Is this a valid move?  We can move down any number, up only 1
		protected void addNeighbor(Location next) {
			if (isValidStep(this, next)) {
				super.addNeighbor(next);
			}
		}
		
		// We have a specific end
		@Override
		public boolean isEnd(Location loc) {
			if (loc.getX() == end.getX() && loc.getY() == end.getY()) {
				return true;
			}
			return false;
		}
	}
	
	protected boolean isValidStep(Node cur, Location next) {
		int current = getHeight(cur);
		int neighbor = getHeight(next);
		if (current >= (neighbor - 1)) {
			return true;
		}
		return false;
	}
	
	protected int getHeight(Location l) {
		int x = l.getX();
		int y = l.getY();
		if (grid.size() > y) {
			int[] row = grid.get(y);
			if (row.length > x) {
				int val = row[x];
				if (val == 'S') {
					// Start is a
					return 'a';
				} else if (val == 'E') {
					// End is z
					return 'z';
				}
				return val;
			}
		}
		logger.error("Location outside of grid!: "+l);
		return -1;		
	}
	
	Location start = null;
	Location end = null;

	@Override
	public void initialize() {
		// no-op
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		int[] row = line.chars().toArray();
		for (int i=0; i<row.length; ++i) {
			if (row[i] == 'S') {
				start = new Location(i, grid.size());
			} else if (row[i] == 'E') {
				end = new Location(i, grid.size());
			}
		}
		grid.add(row);
	}

	protected void printGrid() {
		StringBuilder sb = new StringBuilder(System.lineSeparator());
		for (int[] row : grid) {
			for (int c=0;c<row.length; ++c) {
				sb.append((char)row[c]);
			}
			sb.append(System.lineSeparator());
		}
		logger.info(sb.toString());
	}
	
	@Override
	public void output() {
		printGrid();
		
		HillNode startNode = new HillNode(start);
		// Probably quicker to go backwards from the end, but this is fine for now
		Node foundEnd = startNode.search(start);
		if (foundEnd != null) {
			displayResult(foundEnd);
		}
	}
	
	protected void displayResult(Node foundEnd) {
		List<Node> path = foundEnd.getBackPath();
		for (Node n : path) {
			logger.info("Path: "+n);
		}
		logger.info("Steps: "+foundEnd.getBackPathLength());
	}

}
