package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.Location;

public class Day15b extends Day15 {
	static private Logger logger = Logger.getLogger(Day15b.class.getName());
	
	protected int maxRow = 4000000;
	
	// How deep inside a sensor's blackout radius am I?  
	// blackout radius is the region around a sensor within the distance to it's nearest beacon
	// No other beacon can be within the blackout distance.
	// Get the largest blackout distance, because we can then skip that many locations
	protected int blackoutDistance(Location loc) {
		int dist = -1;
		for (Sensor s : sensors) {
			int sDist = s.sensorLoc.manhattanDistance(loc);
			int bDist = s.beaconDist - sDist;
			if (dist < bDist) { 
				dist = bDist;
			}
		}
		return dist;
	}
	
	@Override
	public void output() {
		for (Sensor s : sensors) {
			s.beaconDist = s.sensorLoc.manhattanDistance(s.nearestBeacon);
		}
		for (int i=0; i<maxRow; ++i) {
			for (int j=0; j<maxRow; ++j) {
				Location l1 = new Location(i,j);
				int blackout = blackoutDistance(l1);
				if (blackout < 0) {
					logger.info("Fond open location at "+i+","+j);
					logger.info("Tuning Freq: "+(((long)i*4000000)+j));
					return;
				} else {
					// Can skip ahead blackout, all these locations are within the same blackout radius
					j += blackout;
				}
			}

			// Could also skip down too, but that's a bit more tricky, lets see if this is fast enoug
			if ((i % 1000) == 0) {
				logger.info("On row: "+i);
			}
		}
		logger.error("No open spot found!");
	}
	
}
