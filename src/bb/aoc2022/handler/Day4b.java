package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day4b extends Day4 {
	
	static private Logger logger = Logger.getLogger(Day4b.class.getName());
	
	protected class RangeB extends Range {

		public RangeB(String lh) {
			super(lh);
		}
		
		public boolean overlaps(Range r) {
			if ((low > r.high) || (high < r.low)) {
				return false;
			}
			return true;
		}
		
	}
	
	@Override
	public void handleInput(String line) {
		String[] split = line.split(",");
		if (split.length == 2) {
			RangeB r1 = new RangeB(split[0]);
			RangeB r2 = new RangeB(split[1]);
			if (r1.overlaps(r2)) {
				logger.info(r1+" overlaps "+r2);
				containsCount++;
			} else if (r2.overlaps(r1)) {
				logger.info(r2+" overlaps "+r1);
				containsCount++;
			}			
		} else {
			logger.error("Invalid line: "+line);
		}
	}
	
}
