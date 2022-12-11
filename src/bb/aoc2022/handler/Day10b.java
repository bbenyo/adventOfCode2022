package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day10b extends Day10 {
	static private Logger logger = Logger.getLogger(Day10b.class.getName());
	
	boolean[][] CRT = new boolean[40][7];
	
	@Override
	public void initialize() {
		// We skip cycle 1 in Day10, so handle that here if initialized = false
		CRT[0][0] = true; // registerX = 1
	}
		
	@Override
	protected void checkCycle() {
		if (cycle > 240) {
			return;
		}
		int x = ((cycle - 1) % 40);
		int y = (int)Math.floor((cycle - 1) / 40.0f);
		boolean vis = isVisible(x);
		logger.info("Cycle: "+cycle+" Pixel: "+x+","+y+" visible: "+vis);
		if (vis) {
			CRT[x][y] = true;
		}
	}
	
	protected boolean isVisible(int x) {
		if (Math.abs(x - registerX) <= 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public void output() {
		StringBuffer sb = new StringBuffer();
		for (int y=0; y<6; ++y) {
			sb.append(System.lineSeparator());
			for (int x=0; x<40; ++x) {
				if (CRT[x][y]) {
					sb.append("#");
				} else {
					sb.append(".");
				}
			}
		}
		logger.info(sb.toString());
	}
}
