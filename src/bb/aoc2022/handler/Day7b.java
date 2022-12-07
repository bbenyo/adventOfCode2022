package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day7b extends Day7 {
	static private Logger logger = Logger.getLogger(Day7b.class.getName());
	
	@Override
	public void output() {
		if (lastCommand != null) {
			handleMultiLineCommand();
		}
		logger.info(root.toString(""));
		
		long freeSpace = 70000000 - root.size();
		long neededSpace = 30000000 - freeSpace;
		logger.info("Free Space: "+freeSpace+" Needed to free: "+neededSpace);
		
		long minSize = 0;
		Directory toDelete = null;
		for (Directory d : dirs) {
			long sz = d.size();
			if (sz > neededSpace) {
				logger.info("Could delete "+d.name+" size: "+sz);
				if (toDelete == null || sz < minSize) {
					toDelete = d;
					minSize = sz;
					logger.info("Currently planning to delete: "+d.name);
				}
			}
		}
		
		if (toDelete != null) {
			logger.info("Deleting "+toDelete.name+" space: "+minSize);
		} else {
			logger.error("Didn't find any directory to delete!");
		}
	}
}
