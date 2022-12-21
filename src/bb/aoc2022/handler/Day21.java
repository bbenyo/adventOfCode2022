package bb.aoc2022.handler;
import java.util.HashMap;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day21 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day21.class.getName());
	
	protected class Monkey {
		String name;
		Long number;
		String operator;
		String[] args;
		Long[] boundArgs;
		
		public Monkey(String id) {
			this.name = id;
			this.number = null;
			boundArgs = new Long[2];
			args = new String[2];
		}
		
		@Override
		public String toString() {
			if (number != null) {
				return name+": "+number;
			} else {
				return name+": "+args[0] + " "+operator+" "+args[1];
			}
		}
				
		public void bindArguments() {
			if (number != null) {
				// Already bound
				return;
			}
			if (operator != null) {
				boolean allBound = true;
				for (int i=0; i<2; ++i) {
					Monkey m = monkeys.get(args[i]);
					if (m != null) {
						if (m.number != null) {
							boundArgs[i] = m.number;
						} else {
							allBound = false;
						}
					} else {
						logger.error("No monkey found named: "+args[i]);
						allBound = false;
					}
				}
				if (allBound) {
					number = computeValue();
					anyChanged = true;
				}				
			}
		}
		
		protected Long computeValue() {
			switch(operator) {
			case "+": return boundArgs[0] + boundArgs[1];
			case "-": return boundArgs[0] - boundArgs[1];
			case "*": return boundArgs[0] * boundArgs[1];
			case "/": return boundArgs[0] / boundArgs[1];
			default:
				return null;
			}
		}
		
		protected boolean oneBound() {
			if (boundArgs[0] != null || boundArgs[1] != null) {
				return true;
			}
			return false;
		}
		
		// Given the result and one bound argument, what should the other argument be
		protected Long reverseEngineer(Long target) {
			if (number != null) {
				// Already know the value
				return number;
			}
			Long oneBound = boundArgs[0];
			int unboundIndex = 1;
			if (oneBound == null) {
				oneBound = boundArgs[1];
				unboundIndex = 0;
			}
			if (oneBound == null) {
				// Hopefully they're not this evil, we'd have to so some searching if both are unbound...
				throw new RuntimeException("Can't reverse enginner with 2 unbound args");
			}
			switch(operator) {
			case "+" : boundArgs[unboundIndex] = target - oneBound; break;
			case "-" : 
				if (oneBound.equals(boundArgs[0])) {
					boundArgs[unboundIndex] = -1 * (target - oneBound);
				} else {
					boundArgs[unboundIndex] = target + oneBound;
				}
				break;
			case "*" : boundArgs[unboundIndex] = target / oneBound; break;
			case "/" : 
				if (oneBound.equals(boundArgs[0])) {
					boundArgs[unboundIndex] = oneBound / target;
				} else {
					boundArgs[unboundIndex] = oneBound * target;
				}
				break;
			case "=" :
				boundArgs[unboundIndex] = oneBound; break;
			default:
				logger.error("Unrecognized operator: "+operator);
			}
			
			this.number = target;
			anyChanged = true;
			return boundArgs[unboundIndex];
		}
	}

	HashMap<String, Monkey> monkeys;
	boolean anyChanged = false;
	
	@Override
	public void initialize() {
		monkeys = new HashMap<>();
	}

	@Override
	public void handleInput(String line) {
		int cPos = line.indexOf(":");
		String name = line.substring(0, cPos);
		Monkey m = new Monkey(name);
		if (Character.isDigit(line.charAt(cPos+2))) {
			m.number = (long)Utilities.parseInt(line, cPos, " ", "");
		} else {
			String equation = line.substring(cPos+2);
			String[] args = equation.split(" ");
			if (args.length != 3) {
				logger.error("Unable to parse equation: "+equation);
			} else {
				m.operator = args[1];
				m.args[0] = args[0];
				m.args[1] = args[2];
			}
		}
		monkeys.put(m.name, m);
	}
	
	@Override
	public void output() {
		Monkey root = monkeys.get("root");
		while (root.number == null) {
			anyChanged = false;
			for (Monkey m : monkeys.values()) {
				m.bindArguments();
			}
			if (!anyChanged && root.number == null) {
				logger.error("Deadlock, no further computation possible");
				return;
			}
		}
		logger.info("Root shouts: "+root.number);
	}
	
}
