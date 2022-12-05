package bb.aoc2022.handler;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;

public class Day5b extends Day5 {
	static private Logger logger = Logger.getLogger(Day5b.class.getName());
	
	@Override
	protected void doMove(Move m) {
		// Move multiple crates in the reverse order
		Deque<Character> toMove = new ArrayDeque<>();
		for (int i=0; i<m.count; ++i) {
			Character crate = crates.get(m.from - 1).pop();
			toMove.push(crate);
		}
		
		Character crate = toMove.poll();
		while (crate != null) {
			logger.info("Moving "+crate+" from "+m.from+" to "+m.to);
			crates.get(m.to - 1).push(crate);
			crate = toMove.poll();
		}
	}
}
