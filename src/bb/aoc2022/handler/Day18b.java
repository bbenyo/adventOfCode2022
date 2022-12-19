package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.GameState;
import bb.aoc2022.Location3D;

public class Day18b extends Day18 {
	static private Logger logger = Logger.getLogger(Day18b.class.getName());
	
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;
	int minZ = Integer.MAX_VALUE;
	int maxZ = Integer.MIN_VALUE;
	
	// Do a breath first search from a cube location, can we get outside?
	protected class Cube extends GameState {
		
		Location3D loc;
		
		public Cube(Location3D loc) {
			super(loc.toString());
			this.loc = loc;
		}

		public Cube(GameState oState, Location3D loc) {
			super(oState, loc.toString());
			this.loc = loc;
		}

		@Override
		public boolean win() {
			if (isOutside(loc)) {
				return true;
			}
			return false;			
		}

		@Override
		public List<GameState> generatePossibleMoves() {
			List<GameState> moves = new ArrayList<>();
			Location3D[] sides = getConnectedSides(loc);
			for (Location3D side : sides) {
				if (!cubes.contains(side)) {
					GameState g2 = new Cube(this, side);
					moves.add(g2);
				}
			}
			return moves;
		}
		
	}
	
	// Get the min/max X,Y,Z values, everything outside is air
	protected void computeBoundingBox() {
		for (Location3D cube : cubes) {
			if (cube.getX() < minX) {
				minX = cube.getX();
			}
			if (cube.getX() > maxX) {
				maxX = cube.getX();
			}
			if (cube.getY() < minY) {
				minY = cube.getY();
			}
			if (cube.getY() > maxY) {
				maxY = cube.getY();
			}
			if (cube.getZ() < minZ) {
				minZ = cube.getZ();
			}
			if (cube.getZ() > maxZ) {
				maxZ = cube.getZ();
			}
		}
		logger.info("Min X: "+minX+" Max X: "+maxX+" Min Y: "+minY+" Max Y: "+maxY+" Min Z: "+minZ+" Max Z: "+maxZ);
	}
	
	protected boolean isOutside(Location3D loc) {
		if (loc.getX() <= minX || loc.getX() >= maxX ||
			loc.getY() <= minY || loc.getY() >= maxY ||
			loc.getZ() <= minZ || loc.getZ() >= maxZ) {
			return true;
		}
		return false;
	}
	
	HashMap<Location3D, Boolean> outsideCache = new HashMap<>();
	
	@Override
	// Exposed sides only count if they're outside, exposed to air
	//  Do a BFS from the potential exposed side, can we get outside to air?
	protected int exposedSides(Location3D cube) {
		Location3D[] connected = getConnectedSides(cube);
		int count = 0;
		for (Location3D ccube : connected) {
			if (!cubes.contains(ccube)) {
				GameState cState = new Cube(ccube);
				Boolean cached = outsideCache.get(ccube);
				boolean isOutside = false;
				if (cached != null) {
					isOutside = cached.booleanValue();
				} else {
					// Do the search, BFS
					GameState air = GameState.bfs(cState);
					if (air != null) {
						logger.info("Found a path outside for "+ccube);
						isOutside = true;
						outsideCache.put(ccube, Boolean.TRUE);
					} else {
						logger.info("Cube "+ccube+" is internal");
						isOutside = false;
						outsideCache.put(ccube, Boolean.FALSE);
					}
				}
				if (isOutside) {
					count++;
				}
			}
		}
		return count;
	}
	
	@Override
	public void output() {
		computeBoundingBox();
		super.output();
	}
}
