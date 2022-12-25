package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;
import bb.aoc2022.LocationFacing;
import bb.aoc2022.Node;

public class Day24 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day24.class.getName());
	
	List<char[]> grid;
	int time = 0;

	Location topLeft = null;
	Location bottomRight = null;
	
	protected class TimeState {
		Set<LocationFacing> blizzards;
		int time;
		
		public TimeState(int t) {
			blizzards = new HashSet<>();
			this.time = t;
		}
				
		protected void propagateForward(TimeState t) {
			blizzards.clear();
			for (LocationFacing blizz : t.blizzards) {
				LocationFacing b2 = new LocationFacing(blizz);
				this.blizzards.add(b2);				
			}
			for (LocationFacing bliz : blizzards) {
				bliz.forward(topLeft, bottomRight, true);
			}
		}
		
		public boolean isClear(Location l) {
			int x = l.getX();
			int y = l.getY();
			for (LocationFacing lf : blizzards) {
				if (lf.getX() == x && lf.getY() == y) {
					return false;
				}
			}
			return true;
		}
	}
	
	Map<Integer, TimeState> boardStates; // State of the board at time X
	// If memory is an issue, we can save only some of these and compute the rest
	
	
	// A*
	class BNode extends Node {
		
		int time;

		public BNode(Location l1, int time) {
			super(l1);
			this.time = time;
		}
		
		@Override
		public String toString() {
			return super.toString()+" time: "+time;
		}
		
		// Heuristic, guess on the risk score for the path to the end
		//   We'll just do a right/down distance, and assume average risk of 5
		@Override
		protected void computeHScore() {
			int dist = computeHeuristic();
			int h = (dist * 2);
			hScore = time + h;
		}

		// Compute the worst possible score from this node to initialize G
		// To be overridden by implementations, this is a default
		@Override
		public int getWorstScore(Location l1) {
			return time + (getGridSizeX() * getGridSizeY()); // Assume we have to visit every square, rough metric
		}

		// Return a heuristic to compute the likely score from this node
		@Override
		public int computeHeuristic() {
			int right = getGridSizeX() - x;
			int down = getGridSizeY() - y;
			return right + down;
		}
		
		// Get the maximum X value for a Location
		@Override
		public int getGridSizeX() {
			return grid.get(0).length - 1;
		}
		
		// Get the maximum Y value for a Location
		@Override
		public int getGridSizeY() {
			return grid.size() - 1;
		}
		
		// Bottom right
		@Override
		public boolean isEnd(Location loc) {
			if ((loc.getY() == getGridSizeY() - 1) &&
				(loc.getX() == getGridSizeX() - 1)) {
				return true;
			}
			return false;
		}
		
		boolean isValidLocation(Location l) {
			int x = l.getX();
			int y = l.getY();
			if (x == 1 && y == 0) {
				return true;
			}
			if (x >= topLeft.getX() && x <= bottomRight.getX() &&
				y >= topLeft.getY() && y <= bottomRight.getY()) {
				return true;
			}
			return false;					
		}
				
		// Add neighbors based on where we can go from here.
		// Default implementation assumes a grid, and we can go up/down/left/right
		@Override
		public void gatherNeighbors() {
			TimeState nextState = getBoardState(time + 1);
			int x = getX() - 1;  //left
			int y = getY();
			Location lup = new Location(x, y);
			addNeighbor(nextState, lup);
			
			x = getX();
			y = getY() - 1; //up
			lup = new Location(x, y);
			addNeighbor(nextState, lup);
						
			x = getX() + 1;
			y = getY(); // right
			lup = new Location(x, y);
			addNeighbor(nextState, lup);
						
			x = getX();
			y = getY() + 1; // down
			lup = new Location(x, y);
			addNeighbor(nextState, lup);
					
			// Stay here
			x = getX();
			y = getY();
			
			addNeighbor(nextState, new Location(x,y));
		}
		
		protected void addNeighbor(TimeState nextState, Location lup) {
			if (!isValidLocation(lup)) {
				return;
			}
			if (!nextState.isClear(lup)) {
				return;
			}
			BNode node = new BNode(lup, time + 1);
			this.neighbors.add(node);
		}
		
	}
	
	protected TimeState getBoardState(int time) {
		TimeState nextState = boardStates.get(time);
		if (nextState == null) {
			TimeState thisState = boardStates.get(time - 1);
			if (thisState == null) {
				// If we remove states from the cache (due to memory limitations), we can recompute here
				// Just go back as far as we need to, and propagate forward
				// For now, we assume that we won't need to remove any
				throw new RuntimeException("TimeState for "+(time-1)+" is missing!");
			}
			nextState = new TimeState(time + 1);
			nextState.propagateForward(thisState);
			boardStates.put(time, nextState);
		}
		return nextState;
	}
	
	@Override
	public void initialize() {
		grid = new ArrayList<>();
		boardStates = new HashMap<>();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		if (line.length() > 0) {
			char[] row = new char[line.length()];
			for (int i=0; i<row.length; ++i) {
				row[i] = line.charAt(i);
			}
			grid.add(row);
		}
	}
	
	protected void computeBoundingBox() {
		topLeft = new Location(1,1);
		char[] row = grid.get(grid.size() - 2);
		bottomRight = new Location(row.length-2, grid.size()-2);
	}

	protected TimeState createBlizzards() {
		TimeState t0 = new TimeState(0);
		for (int i=0; i<grid.size(); ++i) {
			char[] row = grid.get(i);
			for (int j=0; j<row.length; ++j) {
				char cell = row[j];
				if (cell != '.' && cell != '#') {
					LocationFacing b1 = new LocationFacing(j,i);
					switch(cell) {
					case '>' : b1.setFacing(LocationFacing.Direction.RIGHT); break;
					case '<' : b1.setFacing(LocationFacing.Direction.LEFT); break;
					case '^' : b1.setFacing(LocationFacing.Direction.UP); break;
					case 'v' : b1.setFacing(LocationFacing.Direction.DOWN); break;
					default:
						throw new IllegalArgumentException("Unknown cell value: "+cell);
					}
					t0.blizzards.add(b1);				
				}
			}
		}
		return t0;
	}

	protected void clearGrid() {
		for (int i=topLeft.getX(); i <= bottomRight.getX(); ++i) {
			for (int j=topLeft.getY(); j <= bottomRight.getY(); ++j) {
				grid.get(j)[i] = '.';
			}
		}
	}
	
	protected void drawGrid(TimeState t) {
		StringBuffer sb = new StringBuffer(System.lineSeparator());
		clearGrid();
		for (LocationFacing bliz : t.blizzards) {
			char[] row = grid.get(bliz.getY());
			char cell = row[bliz.getX()];
			switch (cell) {
			case '.' : row[bliz.getX()] = bliz.getFacingChar(); break;
			case '>' :
			case '<' :
			case '^' :
			case 'v' :
				row[bliz.getX()] = '2';
				break;
			case '2' : 
				row[bliz.getX()] = '3';
				break;
			case '3' :
				row[bliz.getX()] = '4';
				break;
			default:
				logger.error("Unrecognized cell: "+cell);	
			}
		}
		
		for (int y=0; y<grid.size(); ++y) {
			sb.append(grid.get(y));
			sb.append(System.lineSeparator());
		}
		logger.info(sb.toString());
	}
	
	@Override
	public void output() {
		computeBoundingBox();
		TimeState t0 = createBlizzards();
		boardStates.put(0, t0);
		drawGrid(t0);
		
		BNode startNode = new BNode(new Location(1,0), 0);
		Node end = Node.search(startNode);
		BNode endNode = (BNode)end;		
		TimeState tEnd = this.getBoardState(endNode.time);
		drawGrid(tEnd);
		logger.info("End Time - 1: "+endNode.time);
		logger.info("Move down 1, endTime: "+(endNode.time + 1));
	}

}
