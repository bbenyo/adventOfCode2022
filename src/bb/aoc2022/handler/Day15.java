package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;
import bb.aoc2022.Utilities;

public class Day15 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day15.class.getName());
	char[][] grid;
	// Calculate the bounding box for our grid
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;
	int buffer = 0;

	protected class Sensor {
		Location sensorLoc;
		Location nearestBeacon;
		
		public Sensor(int x, int y) {
			sensorLoc = new Location(x,y);
		}
		
		@Override
		public String toString() {
			return "Sensor at "+sensorLoc+" nearest beacon at: "+nearestBeacon;
		}
		
		protected void updateGrid(int x, int y) {
			if (grid[y][x] == '.') grid[y][x] = '#';
		}
				
		protected void addToGrid() {
			Location sLoc = getGridLoc(sensorLoc);
			grid[sLoc.getY()][sLoc.getX()] = 'S';
			Location bLoc = getGridLoc(nearestBeacon);
			grid[bLoc.getY()][bLoc.getX()] = 'B';
			int mDist = sLoc.manhattanDistance(bLoc);
			for (int i=1; i<=mDist; ++i) {
				// Start up from sensorLoc
				int y = sLoc.getY() - i;
				int x = sLoc.getX();
				updateGrid(x, y);
				// Diagonal down right
				while (y != sLoc.getY()) {
					y = y + 1;
					x = x + 1;
					updateGrid(x, y);					
				}
				// Diagonal down left
				while (y < sLoc.getY() + i) {
					y = y + 1;
					x = x - 1;
					updateGrid(x, y);
				}
				// Diagonal up right
				while ( y != sLoc.getY()) {
					y = y - 1;
					x = x - 1;
					updateGrid(x, y);
				}
				// Back to vertical
				while ( y > sLoc.getY() - i) {
					y = y - 1;
					x = x + 1;
					updateGrid(x, y);
				}
			}
		}
	}
	
	protected void updateBoundingBox(Location l) {
		int x = l.getX();
		int y = l.getY();
		if (minX > x) {
			minX = x;
		}
		if (maxX < x) {
			maxX = x;
		}
		if (minY > y) {
			minY = y;
		}
		if (maxY < y) {
			maxY = y;
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
		
	List<Sensor> sensors;
	
	@Override
	public void initialize() {
		sensors = new ArrayList<>();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		if (line.length() == 0) {
			return;
		}
		Integer sX = Utilities.parseInt(line, "Sensor at x=", ",");
		Integer sY = Utilities.parseInt(line, "y=", ":");
		Sensor sensor = new Sensor(sX, sY);
		Integer bX = Utilities.parseInt(line, "closest beacon is at x=", ",");
		int bPos = line.indexOf("beacon");
		Integer bY = Utilities.parseInt(line, bPos, "y=", "");
		Location beacon = new Location(bX, bY);
		sensor.nearestBeacon = beacon;
		sensors.add(sensor);
		logger.info("Adding sensor: "+sensor);
		int mDist = sensor.sensorLoc.manhattanDistance(beacon);
		if (mDist > buffer) {
			buffer = mDist + 2;
		}
		updateBoundingBox(sensor.sensorLoc);
		updateBoundingBox(sensor.nearestBeacon);
	}
	
	protected void writeGrid() {
		int width = (maxX - minX) + (buffer * 5);
		int height = (maxY - minY) + (buffer * 5);
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<height; ++i) {
			sb.append(System.lineSeparator());
			for (int j=0; j<width; ++j) {
				sb.append(grid[i][j]);
			}
		}
		logger.info(sb.toString());
	}
	
	protected void constructGrid() {
		int width = (maxX - minX) + (buffer * 5); 
		int height = (maxY - minY) + (buffer * 5);
		logger.info("Grid has dimensions "+width+","+height);
		grid = new char[height][width];
		for (int i=0; i<height; ++i) {
			for (int j=0; j<width; ++j) {
				grid[i][j] = '.';
			}
		}
		for (Sensor sensor : sensors) {
			sensor.addToGrid();
		}
	}
	

	@Override
	public void output() {
		constructGrid();
		writeGrid();
	}

}
