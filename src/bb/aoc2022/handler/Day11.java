package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day11 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day11.class.getName());
	
	public enum Operation {MINUS, PLUS, MULTIPLY, DIVIDE, NOOP}
	
	protected class Monkey {
		int name;
		List<Long> items;  // Value is the current worry level
		// Assuming all operations are new = old OPERATION VALUE
		Operation op = Operation.NOOP;
		
		// VALUE is a number or a variable [old]
		int opArgument = -1;
		String opArgumentVariable = null;
		
		// Assuming all tests are "divisible by X"
		int testDivisor = -1;
		// Assuming all tests are throw to monkey X
		int throwTrue = -1;
		int throwFalse = -1;
		
		long inspections = 0;
		
		public Monkey() {
			items = new ArrayList<>();
		}
		
		@Override
		public String toString() {
			return "Monkey "+name+": "+Utilities.listToString(items, ",");
		}
		
		protected void parseLine(String token, String line) {
			switch (token) {
			case "Starting items: ": parseStartingItems(line); break;
			case "Operation: ": parseOperation(line); break;
			case "Test: ": parseTest(line); break;
			case "If true: ": parseThrow(line, true); break;
			case "If false: ": parseThrow(line, false); break;
			default:
				logger.error("Unrecognized token: "+token);
			}			
		}
		
		protected void parseStartingItems(String line) {
			String[] itemsStr = line.split(",");
			items.clear();
			for (String item : itemsStr) {
				items.add(Long.parseLong(item.trim()));
			}
		}
		
		// assume: new = old [*,+,-,/] (int)Y
		protected void parseOperation(String line) {
			String[] args = line.split(" ");
			if (args.length != 5) {
				logger.error("Unable to parse Operation: "+line);
				return;
			}
			
			switch (args[3]) {
			case "-" : op = Operation.MINUS; break;
			case "+" : op = Operation.PLUS; break;
			case "*" : op = Operation.MULTIPLY; break;
			case "/" : op = Operation.DIVIDE; break;
			default: op = Operation.NOOP; logger.error("Unable to parse operation: "+args[3]); break;
			}
			
			// OK to throw an exception here if we fail to parse, want to fail immediately
			Integer value = Utilities.parseIntOrNull(args[4]);
			if (value == null) {
				// It's a variable, should be old
				if (args[4].equals("old")) {
					opArgumentVariable = args[4];
				} else {
					logger.error("Unable to parse argument variable: "+args[4]);
				}
			} else {
				opArgument = value.intValue();
			}
		}
		
		// Assuming: divisible by X
		protected void parseTest(String line) {
			if (line.startsWith("divisible by ")) {
				String[] args = line.split(" ");
				testDivisor = Utilities.parseIntOrNull(args[2]); 
			} else {
				logger.error("Unable to parse test: "+line);
			}
		}
		
		// Assume: throw to monkey X
		protected void parseThrow(String line, boolean trueTest) {
			if (line.startsWith("throw to monkey ")) {
				String[] args = line.split(" ");
				int monkeyNum = Utilities.parseIntOrNull(args[3]);
				if (trueTest) {
					throwTrue = monkeyNum;
				} else {
					throwFalse = monkeyNum;
				}
			} else {
				logger.error("Unable to parse throw line: "+line);
			}
		}
		
		protected void takeTurn() {
			List<Long> toThrow = new ArrayList<>(items);
			items.clear();
			for (Long item : toThrow) {
				handleItem(item);
			}
		}
		
		protected long doOperation(Long item) {
			Long arg = null;
			if (opArgument != -1) {
				arg = (long)opArgument;
			} else if (opArgumentVariable.equals("old")) {
				arg = item;
			}
			switch (op) {
			case DIVIDE:
				return item / arg;
			case MINUS:
				return item - arg;
			case MULTIPLY:
				return item * arg;
			case NOOP:
				return item;
			case PLUS:
				return item + arg;
			default:
				break;
			}
			return item;
		}
				
		protected void handleItem(Long item) {
			// Increase worry
			long worry = doOperation(item);
			inspections++;
			// Relief
			worry = relief(worry);
			// Test divisible
			if ((worry % this.testDivisor) == 0) {
				monkeyThrow(worry, true);
			} else {
				monkeyThrow(worry, false);
			}			
		}
	
		protected void monkeyThrow(long item, boolean flag) {
			int to = flag ? throwTrue : throwFalse;
			
			// Throw an exception here if we throw to an invalid monkey to fail fast
			Monkey receiver = monkeys.get(to);
			receiver.items.add(item);
			logger.info("Monkey "+name+" throws item with worry level "+item+" to "+to);
		}
	}

	protected long relief(long item) {
		return item / reliefFactor;
	}

	protected int reliefFactor = 3;
	
	List<Monkey> monkeys = new ArrayList<>();
	Monkey currentMonkey = null;
	
	String[] startTokens = {"Starting items: ", "Operation: ", "Test: ", "If true: ", "If false: "};

	@Override
	public void initialize() {
		monkeys.clear();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		try {
			if (line.startsWith("Monkey ")) {
				if (currentMonkey != null) {
					monkeys.add(currentMonkey);
				}
				currentMonkey = new Monkey();
				String name = line.substring(7, line.indexOf(":"));
				currentMonkey.name = Integer.parseInt(name);
			} else {
				for (String token : startTokens) {
					if (line.startsWith(token)) {
						currentMonkey.parseLine(token, line.substring(token.length()).trim());
					}
				}
			} 
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
	}
	
	protected int round = 0;
	protected int endRound = 20;

	protected void doRound() {
		for (Monkey m : monkeys) {
			m.takeTurn();
		}
		round++;
		logRound();
	}
	
	protected void logRound() {
		StringBuffer sb = new StringBuffer("Round "+round+System.lineSeparator());
		for (Monkey m : monkeys) {
			sb.append(m.toString()+System.lineSeparator());
		}
		logger.info(sb.toString());
	}
	
	@Override
	public void output() {
		monkeys.add(currentMonkey);
		logRound();
		
		for (int i=0; i<endRound; ++i) {
			doRound();
		}
		
		monkeys.sort(new Comparator<Monkey>() {
			@Override
			public int compare(Monkey o1, Monkey o2) {
				if (o1.inspections > o2.inspections) {
					return -1;
				} else if (o1.inspections == o2.inspections) {
					return 0;
				}
				return 1;
			}			
		});
		
		Monkey top1 = monkeys.get(0);
		Monkey top2 = monkeys.get(1);
		
		logger.info("Monkey Business: "+top1.inspections * top2.inspections);
	}

}
