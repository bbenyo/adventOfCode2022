package bb.aoc2022.handler;

import java.util.HashMap;
import java.util.Objects;

import org.apache.log4j.Logger;

/**
 * Can't simulate a 1 trillion high grid. 
 *   First, we can cut the grid when we get enough blocks covering all the width
 *     Everything under the last level can be cut, e.g. 
 *     
 *     |.#...##|
       |###.###|
       |.#####.|
       
       Everything under this can be cut, since no block can drop below this
       
 * That still won't get us to 1 trillion, it would take forever.
 *    1 mil takes ~35 seconds, so that's 35 million seconds = ~405 days
 *    
 * There has to be a cycle, we see the above pattern often looking at the grid.
 * There are wind.size() * 5 rocks possible states.  
 *   For each state, hash the grid.  When we get to that state again, is the grid the same?
 *   If so, this is a cycle, and we can skip ahead
 *   
 * For each state (wind index + rock index), store the last time we were here (rock count) and the grid hash
 *    
 * @author bbenyo
 *
 */
public class Day17b extends Day17 {
	static private Logger logger = Logger.getLogger(Day17b.class.getName());
	long minY = 0; // Y value of the bottom of the grid
	
	public class GridState {
		long hash;
		long rockCount;
		long height;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(hash, height, rockCount);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GridState other = (GridState) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return hash == other.hash && height == other.height && rockCount == other.rockCount;
		}
		private Day17b getEnclosingInstance() {
			return Day17b.this;
		}
		
	}
	
	HashMap<String, GridState> gridHash = new HashMap<>();
	boolean foundCycle = false;
	
	@Override
	public void initialize() {
		rocksToDrop = 1000000000000l;
		super.initialize();
	}
	
	protected int gridHash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(grid.size(), highest);
		for (char[] row : grid) {
			int rHash = Objects.hash(row[1], row[2], row[3], row[4], row[5], row[6], row[7]);
			result = prime * result + rHash;
		}
		return result;		
	}
		
	@Override
	protected void tetris(Rock r1) {
		super.tetris(r1);
		trimGrid();
		long hash = gridHash();
		logger.info("HASH: "+hash);
		// This key is our state, which wind we're on and which rock we're on
		// If the last time we were here is the same grid, we found a cycle
		if (foundCycle) {
			return;
		}
		String key = r1.typeIndex + "_" + windIndex;
		GridState oldState = this.gridHash.get(key);
		if (oldState == null || hash != oldState.hash) {
			if (oldState != null) {
				logger.info("Key: "+key+" OldState: "+oldState.hash+" curState: "+hash);
			}
			oldState = new GridState();
			oldState.hash = hash;
			oldState.height = this.minY;
			oldState.rockCount = rocksDropped;
			gridHash.put(key, oldState);
		} else {
			logger.warn("FOUND A CYCLE!!! AT "+rocksDropped);
			foundCycle = true;
			long lastCount = oldState.rockCount;
			logger.info("\tLast found at "+lastCount);
			long cycleLength = (rocksDropped - lastCount);
			logger.info("\tLast Height: "+oldState.height);
			long cycleHeight = this.minY - oldState.height;
			logger.info("\tCycle Height: "+cycleHeight);
			logger.info("\tCycle Length: "+cycleLength);
			long rocksLeft = rocksToDrop - rocksDropped;
			long cyclesToSkip = (rocksLeft / cycleLength);
			logger.info("\tCycles to skip: "+cyclesToSkip);
			rocksDropped += (cyclesToSkip * cycleLength);
			this.minY += (cyclesToSkip * cycleHeight);
		}
	}
	
	protected boolean allTrue(int[] bArray) {
		for (int i=0; i<bArray.length; ++i) {
			if (bArray[i] == 0) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean isEmpty(char[] row) {
		for (int i=1; i<8; ++i) {
			if (row[i] != '.') {
				return false;
			}
		}
		return true;
	}
	
	protected void trimGrid() {
		// Trim the grid
		// Start at the top, find the last row where we have a block in every column for the last 4 rows only
		//  Last 4 rows since the highest block is 4, and if a block could move around an obstacle via wind
		int[] blocked = new int[7];
		// Don't need to do this, it should default to false
		for (int i=0; i<7; ++i) {
			blocked[i] = 0;
		}
		int curY = grid.size() - 1;
		while (curY >= 0) {
			char[] row = grid.get(curY);
			for (int i=1; i<8; ++i) {
				if (row[i] == '#') {
					blocked[i-1] = curY;
				}
			}
			curY--;
			if (allTrue(blocked)) {
				break;
			}
			// Clear any block value where Y > curY + 4
			for (int i=0; i<7; ++i) {
				if (blocked[i] >= (curY + 4)) {
					blocked[i] = 0;
				}
			}
		}
		if (curY > 0) {
			logger.info("All blocked at Y="+curY);
			for (int i=0; i<curY; ++i) {
				grid.remove(0);
			}
			this.minY += curY;
			computeHighest();
		}
		// Trim the top too, remove any empty rows
		curY = grid.size() - 1;
		char[] row = grid.get(curY);
		while (isEmpty(row)) {
			grid.remove(curY);
			curY = grid.size() - 1;
			row = grid.get(curY);
		}
		logger.info("\t New MinY is "+minY);
	}

	@Override
	public void output() {
		super.output();
		logger.info("Real Highest is "+(minY + highest));
	}
	
}
