package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day23b extends Day23 {
	static private Logger logger = Logger.getLogger(Day23b.class.getName());
	
	@Override
	public void output() {
		logElves();
		oneMoved = true;
		int round = 0;
		while (oneMoved) {
			round++;
			movePhase1();
			movePhase2();
			movePhaseEnd();
			currentFirstDirection++;
			if (currentFirstDirection == 4) {
				currentFirstDirection = 0;
			}
			computeBoundingBox();
			logElves();
		}
		computeBoundingBox();
		logElves();
		logger.info("Top Left: "+topLeft);
		logger.info("Bottom Right: "+bottomRight);
		int width = (bottomRight.getX() - topLeft.getX()) + 1;
		int height = (bottomRight.getY() - topLeft.getY()) + 1;
		logger.info("Width: "+width+" Height: "+height);
		int area = height * width;
		logger.info("Area: "+area);
		int empty = area - elves.size();
		logger.info("Empty: "+empty);
		logger.info("Last Round: "+round);
	}
	
}
