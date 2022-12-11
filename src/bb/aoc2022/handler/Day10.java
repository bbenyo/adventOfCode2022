package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.OpCode;

public class Day10 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day10.class.getName());
	int cycle = 1;
	protected int registerX = 1;
	
	protected int startCycle = 20;
	protected int cyclePeriod = 40;
	protected int nextCycle = 20;
	
	protected long signalStrength = 0;
	
	@Override
	public void handleInput(String line) {
		OpCode op = new OpCode(line);
		if (op.getCmd() == null) {
			return;
		}
		switch (op.getCmd()) {
		case "noop" :
			logger.debug(cycle+" -> noop");
			cycle++;
			break;
		case "addx" :
			cycle++;
			checkCycle();
			cycle++;
			Integer val = op.argAsInteger(0);
			if (val == null) {
				logger.error("Processor is in an error state!");
				return;
			}
			registerX += val;
			break;
		default: 
			logger.error("Invalid opCode: "+op.getCmd());
		}

		checkCycle();
	}
	
	protected void checkCycle() {
		if (cycle == nextCycle) {
			nextCycle = nextCycle + cyclePeriod;
			int curSignalStrength = registerX * cycle;
			logger.info("Adding "+registerX+" * "+cycle+" = "+curSignalStrength+" to signal strength at cycle "+cycle);
			signalStrength += curSignalStrength;
		}
	}

	@Override
	public void output() {
		logger.info("Total Signal Strength: "+signalStrength);
	}

	@Override
	public void initialize() {		
	}

}
