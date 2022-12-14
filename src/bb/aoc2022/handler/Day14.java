package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;

public class Day14 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day14.class.getName());
	char[][] grid;

	protected class RockPath {
		List<Location> path;
		
		public RockPath() {
			path = new ArrayList<>();
		}
		
		public void addToGrid() {
			Location start = null;
			for (Location l : path) {
				if (start == null) {
					start = l;
					continue;
				}
				setRock(start, l);
				start = l;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Location l : path) {
				if (first) first = false; else sb.append(" -> ");
				sb.append(l);
			}
			return sb.toString();
		}
	}

	// Convert a location to a grid x,y spot
	protected Location getGridLoc(Location l) {
		int x = l.getX() - minX;
		int y = l.getY() - minY;
		return new Location(x,y);
	}
	
	protected Location getGridLoc(int x, int y) {
		int gx = x - minX;
		int gy = y - minY;
		return new Location(gx, gy);
	}

	protected void setRock(Location start, Location end) {
		Location gStart = getGridLoc(start);
		if (start.getX() == end.getX()) {
			// Vertical
			int height = Math.abs(end.getY() - start.getY()) + 1;
			// Up or down?
			boolean up = true;
			if (start.getY() > end.getY()) {
				up = false;
			}
			for (int i=0; i<height; ++i) {
				int y = 0;
				if (up) {
					y = gStart.getY() + i;
				} else {
					y = gStart.getY() - i;
				}
				grid[y][gStart.getX()] = '#';
			}			
		} else {
			// Horizontal
			int width = Math.abs(end.getX() - start.getX()) + 1;
			boolean right = true;
			if (start.getX() > end.getX()) {
				right = false;
			}
			for (int i=0; i<width; ++i) {
				int x = 0;
				if (right) {
					x = gStart.getX() + i;
				} else {
					x = gStart.getX() - i;
				}
				grid[gStart.getY()][x] = '#';
			}		
		}
	}
	
	protected List<RockPath> rockPaths;

	// Calculate the bounding box for our grid
	// We have a known point 500,0 that we can use to initialize
	int minX = 500;
	int maxX = 500;
	int minY = 0;
	int maxY = 0;
	
	@Override
	public void initialize() {
		rockPaths = new ArrayList<>();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		String[] locs = line.split(" -> ");
		RockPath rp = new RockPath();
				
		for (String l : locs) {
			Location loc = new Location(l);
			if (loc.getX() < minX) {
				minX = loc.getX();
			}
			if (loc.getX() > maxX) {
				maxX = loc.getX();
			}
			if (loc.getY() < minY) {
				minY = loc.getY();
			}
			if (loc.getY() > maxY) {
				maxY = loc.getY();
			}
			rp.path.add(loc);
		}
		rockPaths.add(rp);
	}
		
	protected void constructGrid() {
		int width = (maxX - minX) + 2; 
		int height = (maxY - minY) + 2;
		logger.info("Grid has dimensions "+width+","+height);
		grid = new char[height][width];
		for (int i=0; i<height; ++i) {
			for (int j=0; j<width; ++j) {
				grid[i][j] = '.';
			}
		}
		for (RockPath rp : rockPaths) {
			rp.addToGrid();
		}
		// Add sand source
		Location sandSource = getGridLoc(500,0);
		grid[sandSource.getY()][sandSource.getX()] = '+';
	}
	
	protected boolean dropSand() {
		Location sandLoc = getGridLoc(500,0);
		boolean atRest = false;
		do {
			Location down1 = new Location(sandLoc.getX(), sandLoc.getY() + 1);
			if (isOpen(down1)) {
				sandLoc = down1;
			} else {
				Location downLeft = new Location(sandLoc.getX() - 1, sandLoc.getY() + 1);
				if (isOpen(downLeft)) {
					sandLoc = downLeft;
				} else {
					Location downRight = new Location(sandLoc.getX() + 1, sandLoc.getY() + 1);
					if (isOpen(downRight)) {
						sandLoc = downRight;
					} else {
						atRest = true;
					}
				}
			}
			if (inAbyss(sandLoc)) {
				return true;
			}
		} while (!atRest);
		if (sandLoc != null) {
			grid[sandLoc.getY()][sandLoc.getX()] = 'o';
		}
		return false;
	}
	
	protected boolean isOpen(Location loc) {
		if (inAbyss(loc)) {
			return true;
		}
		return grid[loc.getY()][loc.getX()] == '.';
	}
	
	protected boolean inAbyss(Location loc) {
		if (loc.getX() < 0 || loc.getX() >= grid[0].length ||
			loc.getY() < 0 || loc.getY() >= grid.length) {
			return true;			
		}
		return false;
	}
	
	protected void writeGrid() {
		int width = (maxX - minX) + 2;
		int height = (maxY - minY) + 2;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<height; ++i) {
			sb.append(System.lineSeparator());
			for (int j=0; j<width; ++j) {
				sb.append(grid[i][j]);
			}
		}
		logger.info(sb.toString());
	}

	@Override
	public void output() {
		constructGrid();
		writeGrid();
		boolean inAbyss = false;
		int grains = 0;
		while (!inAbyss) {
			inAbyss = dropSand();
			if (!inAbyss) {
				grains++;
			}
		}
		writeGrid();
		logger.info("Dropped grains: "+grains);
	}

}
