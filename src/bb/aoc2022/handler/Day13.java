package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day13 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day13.class.getName());
	
	class Packet {
		List<Object> data;  // List or Integer
		
		public Packet(String input) {
			data = parseList(input);
		}
		
		protected List<Object> parseList(String input) {
			if (!input.startsWith("[")) {
				logger.error("Invalid input, list doesn't start with [" +input);
			}
			if (!input.endsWith("]")) {
				logger.error("Invalid input, list doesn't end with ]" +input);
			}

			List<Object> lst = new ArrayList<>();
			String i2 = input.substring(1, input.length() - 1);
			String[] vals = i2.split(",");
			int openBracketCount = 0;
			String curList = "";
			for (String val : vals) {
				val = val.trim();
				// If we're parsing a List, add to the list, and check if we're done (balanced brackets)
				if (openBracketCount > 0) {
					curList = curList + "," + val;
					openBracketCount += Utilities.countChar(val, '[');
					openBracketCount -= Utilities.countChar(val, ']');
					if (openBracketCount == 0) {
						// Balanced, we have our full list
						lst.add(parseList(curList));
						curList = "";
					}
				} else if (val.startsWith("[")) {
					// Start on a new list
					openBracketCount = Utilities.countChar(val, '[');
					openBracketCount -= Utilities.countChar(val, ']');
					if (openBracketCount == 0) {
						// single element list
						lst.add(parseList(val));
					} else {
						// Need to reconstruct the full list
						curList = val;
					}
				} else if (val.length() > 0) {
					// Has to be an integer
					lst.add(Integer.parseInt(val));  // throw exception if invalid, to fail fast
				}
			}			
			return lst;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			listToString(sb, data);
			return sb.toString();
		}
		
		@SuppressWarnings("rawtypes")
		public String listToString(StringBuilder sb, List data) {
			sb.append("[");
			boolean first = true;
			for (Object d : data) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				if (d instanceof Integer) {
					sb.append(d);
				} else if (d instanceof List) {
					listToString(sb, (List)d);
				}			
			}
			sb.append("]");
			return sb.toString();
		}
	}
	
	class PacketPair {
		Packet p1 = null;
		Packet p2 = null;
		int index = 0;
		
		public PacketPair(int idx) {
			index = idx;
		}
		
		public boolean correctOrder() {
			logger.info("P1: "+p1);
			logger.info("P2: "+p2);
			return (compare(p1.data, p2.data) == -1);
		}
		
		// -1: Correct order
		// 0: Keep looking
		// 1: Incorrect order
		public int compare(Object left, Object right) {
			// If both values are integers, the lower integer should come first
			if (left instanceof Integer && right instanceof Integer) {
				int leftI = (Integer)left;
				int rightI = (Integer)right;
				if (leftI < rightI) {
					return -1;
				} else if (leftI == rightI) {
					return 0;
				}
				return 1;
		    // If both values are lists, compare the first value of each list, 
		    //     then the second value, and so on.
			} else if (left instanceof List && right instanceof List) {
				List<?> leftL = (List<?>)left;
				List<?> rightL = (List<?>)right;
				for (int i=0; i<leftL.size(); ++i) {
					Object o1 = leftL.get(i);
					if (rightL.size() <= i) {
						// Right ran out of items first, incorrect order
						return 1;
					}
					Object o2 = rightL.get(i);
					int itemCompare = compare(o1, o2);
					if (itemCompare != 0) {
						return itemCompare;
					}
				}
				if (rightL.size() > leftL.size()) {
					// Left ran out of items first, correct order
					return -1;
				}
				return 0;
				
			// If exactly one value is an integer, convert the integer to a list which contains that integer as its only value
			} else {
				List<Object> wrappedInt = new ArrayList<>();
				if (left instanceof Integer) {
					wrappedInt.add(left);
					return compare(wrappedInt, right);
				} else if (right instanceof Integer) {
					wrappedInt.add(right);
					return compare(left, wrappedInt);
				}
				logger.error("Invalid comparison: "+left.getClass()+" vs "+right.getClass());
			}
			return 0;
		}
	}

	PacketPair currentPair = null;
	int indexSum = 0;

	@Override
	public void initialize() {
		currentPair = new PacketPair(1);
	}
		
	@Override
	public void handleInput(String line) {
		line = line.trim();
		if (line.length() == 0) {
			return;
		}
		if (currentPair.p1 == null) {
			currentPair.p1 = new Packet(line);
		} else if (currentPair.p2 == null) {
			currentPair.p2 = new Packet(line);
			int idx = currentPair.index;
			if (currentPair.correctOrder()) {
				logger.info("Pair "+currentPair.index+" is in the correct order");
				indexSum += idx;
			}
			currentPair = new PacketPair(idx+1);
		}		
	}

	@Override
	public void output() {
		logger.info("Index Sum: "+indexSum);
	}

}
