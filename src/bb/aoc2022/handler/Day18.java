package bb.aoc2022.handler;

import java.util.HashSet;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Location3D;

public class Day18 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day18.class.getName());
	protected HashSet<Location3D> cubes;

	@Override
	public void initialize() {
		cubes = new HashSet<>();
	}

	@Override
	public void handleInput(String line) {
		Location3D loc = new Location3D(line);
		cubes.add(loc);
	}
	
	/**
	 *  Return the location of cubes that would be connected to this one
	 *  (X - 1), Y, Z
	 *  (X + 1), Y, X
	 *  X, (Y-1), Z
	 *  X, (Y+1), Z
	 *  X, Y, (Z-1)
	 *  X, Y, (Z+1)
	 **/
	protected Location3D[] getConnectedSides(Location3D cube) {
		Location3D[] connected = new Location3D[6];
		int x = cube.getX();
		int y = cube.getY();
		int z = cube.getZ();
		connected[0] = new Location3D(x - 1, y, z);
		connected[1] = new Location3D(x + 1, y, z);
		connected[2] = new Location3D(x, y - 1, z);
		connected[3] = new Location3D(x, y + 1, z);
		connected[4] = new Location3D(x, y, z - 1);
		connected[5] = new Location3D(x, y, z + 1);
		return connected;		
	}
	
	protected int exposedSides(Location3D cube) {
		Location3D[] connected = getConnectedSides(cube);
		int count = 0;
		for (Location3D ccube : connected) {
			if (!cubes.contains(ccube)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void output() {
		int exposed = 0;
		for (Location3D cube : cubes) {
			exposed += exposedSides(cube);
		}
		logger.info("Exposed sides: "+exposed);
	}

}
