package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.Location;

public class Day9b extends Day9 {

	static private Logger logger = Logger.getLogger(Day9b.class.getName());

	Location[] Tails = null;
	
	@Override
	public void handleInput(String line) {
		// Initialization
		if (Tails == null) {
			Tails = new Location[9];
			for (int i=0; i<8; ++i) {
				Tails[i] = new Location(0,0);
			}
			Tails[8] = T;
		}
		super.handleInput(line);
	}
	
	@Override
	protected void moveTail() {
		Location head = H;
		for (int i=0; i<9; ++i) {
			Location tail = Tails[i];
			moveTail(head, tail);
			head = tail;			
		}
	}
}
