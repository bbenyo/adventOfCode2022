package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;

public class Day17 implements InputHandler {

	static private Logger logger = Logger.getLogger(Day17.class.getName());
	
	protected List<char[]> grid;
	long highest = 0; // height of the highest rock
	List<Rock> baseRocks; // Base rock types, not in the grid yed
	
	// Create a new Rock 
	protected class Rock {
		// Rock coordinates from the left edge of the row it appears on, 3 row up from the highest rock
		List<Location> pixels;
		int typeIndex;
		int height; // Height of the rock
		
		public Rock(int typeIndex, List<Location> pixels) {
			this.typeIndex = typeIndex;
			this.pixels = new ArrayList<>();
			this.pixels.addAll(pixels);
			computeHeight();
		}
		
		public Rock(Rock r) {
			this.typeIndex = r.typeIndex;
			pixels = new ArrayList<>();
			pixels.addAll(r.pixels);
			height = r.height;
		}
		
		protected void computeHeight() {
			int maxY = 0;
			for (Location l : pixels) {
				if (l.getY() > maxY) {
					maxY = l.getY();
				}
			}
			height = maxY + 1;
		}
		
		// Insert this rock into the grid
		protected boolean insert() {
			int yPos = (int)highest + 4;
			while (grid.size() < (yPos + height)) {
				newRow('|', '.');
			}
			List<Location> gridPixels = new ArrayList<>();
			for (Location loc : pixels) {
				// Loc is offset from 1,yPos
				int x = 1+loc.getX();
				int y = yPos+loc.getY();
				char[] row = grid.get(y);
				if (row[x] != '.') {
					logger.error("Invalid rock position, this should be empty: "+x+","+y);
					logger.error(Arrays.toString(row));
					return false;
				}
				Location gl = new Location(x, y);
				gridPixels.add(gl);
				row[x] = '@';
			}
			pixels = gridPixels;
			computeHighest();
			return true;
		}
		
		protected boolean push(int rightPixels) {
			List<Location> newPixels = new ArrayList<>();
			for (Location p : pixels) {
				Location nPixel = new Location(p.getX()+rightPixels, p.getY());
				if (open(nPixel)) {
					newPixels.add(nPixel);
				} else {
					return false;
				}
			}
			// Move was successful
			updateGrid(pixels, newPixels);
			pixels = newPixels;
			return true;
		}
		
		protected boolean drop(int downPixels) {
			List<Location> newPixels = new ArrayList<>();
			for (Location p : pixels) {
				Location nPixel = new Location(p.getX(), p.getY() - downPixels);
				if (open(nPixel)) {
					newPixels.add(nPixel);
				} else {
					return false;
				}
			}
			// Move was successful
			updateGrid(pixels, newPixels);
			pixels = newPixels;
			return true;
		}
	}
			
	protected void tetris(Rock r1) {
		boolean done = false;
		while (!done) {
			// Get the next wind direction
			if (wind.length() <= windIndex) {
				windIndex = 0;
			}
			char curWind = wind.charAt(windIndex++);
			int rightPixels = 0;
			switch (curWind) {
			case '>' : rightPixels = 1; break;
			case '<' : rightPixels = -1; break;
			default :
				throw new RuntimeException("Unrecognized wind: "+curWind);
			}
			
			r1.push(rightPixels);
			if (!r1.drop(1)) {
				done = true;
			}
		}
		for (Location l : r1.pixels) {
			updateCell(l, '#');
		}
		computeHighest();
	}

	protected boolean open(Location l) {
		char[] row = grid.get(l.getY());
		char cell = row[l.getX()];
		if (cell == '.' || cell == '@') {
			return true;			
		}
		return false;
	}
	
	protected void updateCell(Location l, char val) {
		char[] row = grid.get(l.getY());
		row[l.getX()] = val;
	}

	protected void updateGrid(List<Location> oldLocs, List<Location> newLocs) {
		for (Location l : oldLocs) {
			updateCell(l, '.');
		}
		for (Location l : newLocs) {
			updateCell(l, '@');
		}		
	}
	
	protected long computeHighest() {
		for (int i=grid.size() - 1; i>0; --i) {
			char[] row = grid.get(i);
			for (int j=0; j<row.length; ++j) {
				if (row[j] == '#' || row[j] == '@') {
					highest = i;
					return highest;
				}
			}
		}
		highest = 1;
		return highest;
	}
	
	@Override
	public void initialize() {
		grid = new ArrayList<>();
		newRow('+', '-');
		for (int i=0; i<4; ++i) {
			newRow('|', '.');
		}
		
		baseRocks = new ArrayList<>();
		// Rock 1: ####
		List<Location> locs = new ArrayList<>();
		locs.add(new Location(2,0));
		locs.add(new Location(3,0));
		locs.add(new Location(4,0));
		locs.add(new Location(5,0));
		Rock r1 = new Rock(1, locs);
		baseRocks.add(r1);
		
		/** 
		 * Rock 2 .#.
                  ###
                  .#.
		 */
		locs = new ArrayList<>();
		locs.add(new Location(3,0));
		locs.add(new Location(3,1));
		locs.add(new Location(2,1));
		locs.add(new Location(4,1));
		locs.add(new Location(3,2));
		Rock r2 = new Rock(2, locs);
		baseRocks.add(r2);
		
		/**
		 * Rock 3: ..#
                   ..#
                   ###
		 */
		locs = new ArrayList<>();
		locs.add(new Location(2,0));
		locs.add(new Location(3,0));
		locs.add(new Location(4,0));
		locs.add(new Location(4,1));
		locs.add(new Location(4,2));
		Rock r3 = new Rock(3, locs);
		baseRocks.add(r3);
		
		/**
		 * Rock 4: #
                   #
                   #
                   #
		 */
		locs = new ArrayList<>();
		locs.add(new Location(2,0));
		locs.add(new Location(2,1));
		locs.add(new Location(2,2));
		locs.add(new Location(2,3));
		Rock r4 = new Rock(4, locs);
		baseRocks.add(r4);
		
		/**
		 * Rock 5: ##
                   ##
		 */
		locs = new ArrayList<>();
		locs.add(new Location(2,0));
		locs.add(new Location(3,0));
		locs.add(new Location(3,1));
		locs.add(new Location(2,1));
		Rock r5 = new Rock(5, locs);
		baseRocks.add(r5);
		
	}
	
	public void newRow(char edges, char mid) {
		char[] row = new char[9];
		row[0] = edges;
		for (int i=1; i<8; ++i) {
			row[i] = mid;
		}
		row[8] = edges;
		grid.add(row);				
	}
	
	protected String drawGrid() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<grid.size(); ++i) {
			sb.append(System.lineSeparator());
			char[] row = grid.get(grid.size() - 1 - i);
			for (char c : row) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	StringBuffer windBuf = new StringBuffer();
	String wind = null;
	int windIndex = 0;
	
	@Override
	public void handleInput(String line) {
		windBuf.append(line.trim());
	}
	
	protected long rocksToDrop = 2022;
	protected long rocksDropped = 0;

	@Override
	public void output() {
		logger.info(drawGrid());
		wind = windBuf.toString();
	
		int rockIndex = 0;
		for (rocksDropped=0; rocksDropped<rocksToDrop; ++rocksDropped) {
			if (rockIndex >= baseRocks.size()) {
				rockIndex = 0;
			}
			Rock base = baseRocks.get(rockIndex++);
			Rock n1 = new Rock(base);
			n1.insert();
			tetris(n1);
		}
		logger.info(drawGrid());
		logger.info("Highest: "+highest);
	}

}
