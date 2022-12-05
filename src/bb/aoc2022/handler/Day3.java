package bb.aoc2022.handler;

import java.util.HashMap;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day3 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day3.class.getName());
	
	// Item types in this rucksack, value is the number of this item type
	// (w,2) means 2 item w's are in this sack

	// Currently, we don't need counts of each item, but maybe we will for 3b
	protected HashMap<Character, Integer> rucksack1;
	protected HashMap<Character, Integer> rucksack2;
	
	protected int totalScore = 0;
	
	@Override
	public void handleInput(String line) {
		int end1 = line.length() / 2;
		rucksack1 = parseRucksack(line.substring(0, end1));
		rucksack2 = parseRucksack(line.substring(end1));
		// Find the common item
		for (Character c : rucksack1.keySet()) {
			if (rucksack2.containsKey(c)) {
				int pri = priority(c);
				logger.info("Common item is "+c+" with priority "+pri);
				totalScore += pri;
			}
		}
	}
	
	protected HashMap<Character, Integer> parseRucksack(String s) {
		HashMap<Character, Integer> sack = new HashMap<>();
		for (int i=0; i<s.length(); ++i) {
			char c = s.charAt(i);
			Integer count = sack.get(c);
			if (count == null) {
				sack.put(c, 1);
			} else {
				sack.put(c, count+1);
			}
		}
		return sack;
	}

	protected int priority(Character c) {
		if (Character.isUpperCase(c)) {
			return 26 + ((int)c) - 64; // 65 is a
		} else {
			return ((int)c) - 96; // 97 is a
		}
	}
	
	@Override
	public void output() {
		logger.info("Total Score: "+totalScore);
	}

}
