package bb.aoc2022.handler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day5 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day5.class.getName());
	
	List<Deque<Character>> crates = new ArrayList<>();
	
	Deque<String> crateInputs = new ArrayDeque<>();
	
	protected class Move {
		int from;
		int to;
		int count;
		
		public Move(String line) {
			String[] tokens = line.split(" ");
			if (tokens.length == 6) {
				try {
					from = Integer.parseInt(tokens[3]);
					to = Integer.parseInt(tokens[5]);
					count = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException ex) {
					logger.error(ex.toString(), ex);
				}
			} else {
				logger.error("Invalid move line: "+line);
			}
		}		
	}

	@Override
	public void handleInput(String line) {
		// Keep going until we get the crate number line, this tells us how many crates to expect
		if (crates.isEmpty()) {
			if (line.trim().startsWith("1")) {
				loadInitialCrates(line);
			} else {
				crateInputs.push(line);
			}
		} else {
			Move m = new Move(line);
			doMove(m);
		}

	}
	
	protected void loadInitialCrates(String line) {
		// Last number is the number of crates
		line = line.trim();
		int lPos = line.lastIndexOf(" ");
		line = line.substring(lPos+1);
		int crateCount = Integer.parseInt(line);
		
		for (int i=0; i<crateCount; ++i) {
			crates.add(new ArrayDeque<Character>());
		}
		
		while (!crateInputs.isEmpty()) {
			String cline = crateInputs.pop();
			loadCrates(cline);
		}
	}
	
	protected void loadCrates(String cline) {
		for (int i=0; i<crates.size(); ++i) {
			int c = i*4; // The index in the line of this crate stack
			if (cline.length() < (c+3)) {
				// No more crates
				return;
			}
			// Strip off the [], get the element inside
			String crate = cline.substring(c+1, c+2);
			if (crate.equals(" ")) {
				// No crate here
				continue;
			}
			Character crateChar = crate.charAt(0);
			logger.info("Pushing "+crateChar+" onto stack "+i);
			crates.get(i).push(crateChar);
		}
	}
	
	protected void doMove(Move m) {
		for (int i=0; i<m.count; ++i) {
			Character crate = crates.get(m.from - 1).pop();
			logger.info("Moving "+crate+" from "+m.from+" to "+m.to);
			crates.get(m.to - 1).push(crate);
		}
	}

	@Override
	public void output() {
		StringBuffer sb = new StringBuffer();
		for (Deque<Character> stack : crates) {
			sb.append(stack.pop());
		}
		
		logger.info("Top of each stack: "+sb.toString());
	}

}
