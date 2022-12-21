package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day20 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day20.class.getName());
	
	// Number with a value and we store it's original position
	//   We keep a temporary position here during a mixing operation
	protected class PositionalNumber {
		long value;
		int originalPosition;
		
		// Don't use this to get the current position in the list
		int temporaryPosition = -1;
		
		public PositionalNumber(long val, int i) {
			this.value = val;
			this.originalPosition = i;
		}
		
		@Override
		public String toString() {
			return String.valueOf(value);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(originalPosition, value);
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
			PositionalNumber other = (PositionalNumber) obj;
			return originalPosition == other.originalPosition
					&& value == other.value;
		}

	}
	
	List<Long> nums;
	List<PositionalNumber> mixed;

	protected int[] groveCoords = new int[3];
			
	@Override
	public void initialize() {
		nums = new ArrayList<>();
		mixed = new ArrayList<>();
		groveCoords[0] = 1000;
		groveCoords[1] = 2000;
		groveCoords[2] = 3000;
	}

	@Override
	public void handleInput(String line) {
		Long i = Long.parseLong(line.trim());
		nums.add(i);
	}
	
	// Remove the element with the given index
	protected PositionalNumber removeIndex(int index) {
		PositionalNumber found = null;
		for (PositionalNumber p : mixed) {
			if (p.originalPosition == index) {
				found = p;
				break;
			}
		}
		if (found != null) {
			found.temporaryPosition = mixed.indexOf(found);
			mixed.remove(found);
		}
		return found;
	}
	
	protected void initMixed() {
		for (int i=0; i<nums.size(); ++i) {
			Long val = nums.get(i);
			PositionalNumber p = new PositionalNumber(val, i);
			mixed.add(p);
			p.temporaryPosition = mixed.size() - 1;
		}
	}

	protected void mix() {
		logger.info("Initial State: "+Utilities.listToString(mixed, ","));
		for (int i=0; i<nums.size(); ++i) {
			Long val = nums.get(i);
			PositionalNumber p = removeIndex(i);
			int newPos = (int)((p.temporaryPosition + val) % mixed.size());
			if (newPos < 0) {
				newPos = mixed.size() + newPos;
			} else if (newPos == 0) {
				newPos = mixed.size();
			}
			mixed.add(newPos, p);
			// logger.info("  Moved "+val+" in position "+i+" to position "+newPos);
			// logger.debug("Mixed: "+Utilities.listToString(mixed, ","));
		}		
	}
	
	protected int findZero() {
		for (int i=0; i<mixed.size(); ++i) {
			PositionalNumber p = mixed.get(i);
			if (p.value == 0) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void output() {
		initMixed();
		mix();
		long sum = 0;
		int zeroPos = findZero();
		if (zeroPos == -1) {
			logger.error("Unable to find the zero in mixed!");
		}
		for (int i=0; i<groveCoords.length; ++i) {
			int pos = (zeroPos + groveCoords[i]) % mixed.size();
			logger.info("Grove Coordinte["+groveCoords[i]+"] = "+mixed.get(pos));
			sum += mixed.get(pos).value;
		}
		logger.info("Grove Coordinates sum: "+sum);
	}

}
