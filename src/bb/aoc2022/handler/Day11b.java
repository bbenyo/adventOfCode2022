package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day11b extends Day11 {
	static private Logger logger = Logger.getLogger(Day11b.class.getName());
	
	int reliefFactorMod = -1;
	
	@Override
	public void initialize() {
		this.reliefFactor = 1;
		this.endRound = 10000;		
	}
	
	// Mod by the product of all relief factors, since they're all prime
	// Proving this is valid is left as an "exercise for the reader" =)
	@Override
	protected long relief(long item) {
		// Calculate the mod factor once
		if (reliefFactorMod == -1) {
			reliefFactorMod = 1;
			for (Monkey m : monkeys) {
				reliefFactorMod = reliefFactorMod * m.testDivisor;
			}
		}
		return item % reliefFactorMod;
	}
	
	@Override
	protected void logRound() {
		StringBuffer sb = new StringBuffer("Round "+round+System.lineSeparator());
		for (Monkey m : monkeys) {
			sb.append(m.name+" inspected "+m.inspections+" times"+System.lineSeparator());
		}
		logger.info(sb.toString());
	}

}
