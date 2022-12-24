package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

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
		List<LocationFacing> blizzards;
		int time;
		
		public TimeState(int t) {
			blizzards = new ArrayList<>();
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
	}
	
	// A*
	class BNode extends Node {
		
		int time;

		public BNode(Location l1) {
			super(l1);
		}
		
		// Heuristic, guess on the risk score for the path to the end
		//   We'll just do a right/down distance, and assume average risk of 5
		protected void computeHScore() {
			int dist = computeHeuristic();
			int h = (dist * 5);
			hScore = h;
		}

		// Compute the worst possible score from this node to initialize G
		// To be overridden by implementations, this is a default
		public int getWorstScore(Location l1) {
			return getGridSizeX() * getGridSizeY(); // Assume we have to visit every square?
		}

		// Return a heuristic to compute the likely score from this node
		public int computeHeuristic() {
			int right = getGridSizeX() - x;
			int down = getGridSizeY() - y;
			return right + down;
		}
		
		// Get the maximum X value for a Location
		public int getGridSizeX() {
			return grid.get(0).length - 1;
		}
		
		// Get the maximum Y value for a Location
		public int getGridSizeY() {
			return grid.size() - 1;
		}
		
	}
	
	@Override
	public void initialize() {
		grid = new ArrayList<>();
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
		drawGrid(t0);
		TimeState t1 = new TimeState(1);
		t1.propagateForward(t0);
		drawGrid(t1);
	}

}
