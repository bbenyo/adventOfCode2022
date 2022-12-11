package bb.aoc2022.handler;

import java.util.Arrays;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day6 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day6.class.getName());
	
	protected int packetMarkerPosition = -1;
	protected int markerSize = 4;
	
	@Override
	public void handleInput(String line) {
		line = line.trim();
		char[] potentialMarker = new char[markerSize];
		for (int i=markerSize; i<line.length(); ++i) {
			for (int j=markerSize; j>0; --j) {
				potentialMarker[markerSize-j] = line.charAt(i-j);
			}
			if (unique(potentialMarker)) {
				logger.info(Arrays.toString(potentialMarker)+" are all unique, packet marker is at "+i);
				packetMarkerPosition = i;
				break;
			}			
		}
		if (packetMarkerPosition == -1) {
			logger.error("No packet marker by end of line: "+line);
		}
	}
	
	protected boolean unique(char[] marker) {
		for (int i=0; i<marker.length; ++i) {
			char m1 = marker[i];
			for (int j=i+1; j<marker.length; ++j) {
				if (m1 == marker[j]) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void output() {
		logger.info("Packet Marker at position: "+packetMarkerPosition);

	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
