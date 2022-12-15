package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location;
import bb.aoc2022.Utilities;

public class Day15 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day15.class.getName());
	
	// We only care about one specific row
	int relevantRow = 2000000;
	char[] row;
	// char[][] grid;
	// Calculate the bounding box for our grid
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;
	int buffer = 0;

	protected class Sensor {
		Location sensorLoc;
		Location nearestBeacon;
		
		int beaconDist;
		
		public Sensor(int x, int y) {
			sensorLoc = new Location(x,y);
		}
		
		@Override
		public String toString() {
			return "Sensor at "+sensorLoc+" nearest beacon at: "+nearestBeacon;
		}
		
		protected void updateGrid(int x, int y, char c) {
			if (y == (relevantRow - minY)) {
				if (row[x] == '.') row[x] = c;
			}
		}
						
		protected void addToGrid() {
			Location sLoc = getGridLoc(sensorLoc);
			updateGrid(sLoc.getX(), sLoc.getY(), 'S');
			Location bLoc = getGridLoc(nearestBeacon);
			updateGrid(bLoc.getX(), bLoc.getY(), 'B');
			// Start straight up or down to the relevant row
			int mDist = sLoc.manhattanDistance(bLoc);
			int x = sLoc.getX();
			int y = (relevantRow - minY);
			Location rowLoc = new Location(x, y);
			int curDist = sLoc.manhattanDistance(rowLoc);
			// Go left
			while (x >= 0 && curDist <= mDist) {
				updateGrid(x, y, '#');
				x--;
				rowLoc = new Location(x, y);
				curDist = sLoc.manhattanDistance(rowLoc);
			}
			
			// And go Right
			x = sLoc.getX();
			y = (relevantRow - minY);
			rowLoc = new Location(x, y);
			curDist = sLoc.manhattanDistance(rowLoc);
			// Go left
			while (x < row.length && curDist <= mDist) {
				updateGrid(x, y, '#');
				x++;
				rowLoc = new Location(x, y);
				curDist = sLoc.manhattanDistance(rowLoc);
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
		StringBuilder sb = new StringBuilder(System.lineSeparator());
		for (int i=0; i<row.length; ++i) {
			sb.append(row[i]);
		}
		logger.info(sb.toString());
	}
	
	protected void constructGrid() {
		minX = minX - buffer;
		maxX = maxX + buffer;
		minY = minY - buffer;
		maxY = maxY + buffer;
		int width = (maxX - minX); 
		int height = (maxY - minY);
		logger.info("Grid has dimensions "+width+","+height);
		row = new char[width];
		for (int j=0; j<width; ++j) {
			row[j] = '.';
		}
		for (Sensor sensor : sensors) {
			logger.info("Adding "+sensor+" to grid");
			sensor.addToGrid();
		}
	}
	
	protected int countBlockedLocations() {
		int count = 0;
		for (int j=0; j<row.length; ++j) {
			if (row[j] != '.' && row[j] != 'B') {
				count++;
			}
		}
		return count;
	}
	

	@Override
	public void output() {
		constructGrid();
		// writeGrid();
		
		logger.info("For row y="+relevantRow+" blocked locations: "+countBlockedLocations());
	}

}
