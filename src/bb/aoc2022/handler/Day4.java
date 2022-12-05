package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day4 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day4.class.getName());
	
	protected class Range {
		int low = -1;
		int high = -1;
		boolean valid = false;
		
		public Range(String lh) {
			String[] nums = lh.split("-");
			if (nums.length == 2) {
				try {
					low = Integer.parseInt(nums[0]);
					high = Integer.parseInt(nums[1]);
					valid = true;
				} catch (NumberFormatException ex) {
					logger.error(ex.toString(), ex);
				}
			} else {
				logger.error("Invalid range: "+lh);
			}
		}
		
		public boolean fullyContains(Range r2) {
			if (!r2.valid) {
				return false;
			}
			if (low <= r2.low && high >= r2.high) {
				return true;
			}
			return false;
		}
				
		@Override
		public String toString() {
			return low+"-"+high;
		}
	}
	
	int containsCount = 0;
	
	@Override
	public void handleInput(String line) {
		line = line.trim();
		String[] split = line.split(",");
		if (split.length == 2) {
			Range r1 = new Range(split[0]);
			Range r2 = new Range(split[1]);
			if (r1.fullyContains(r2)) {
				logger.info(r1+" fully contains "+r2);
				containsCount++;
			} else if (r2.fullyContains(r1)) {
				logger.info(r2+" fully contains "+r1);
				containsCount++;
			}			
		} else {
			logger.error("Invalid line: "+line);
		}

	}

	@Override
	public void output() {
		logger.info("Fully Contains count: "+containsCount);
	}

}
