package bb.aoc2022.handler;

import java.util.HashSet;

import org.apache.log4j.Logger;

public class Day3b extends Day3 {
	static private Logger logger = Logger.getLogger(Day3b.class.getName());
	
	HashSet<Character> sack1 = null;
	HashSet<Character> sack2 = null;
	HashSet<Character> sack3 = null;
	
	int badgeScore = 0;
	
	@Override
	public void handleInput(String line) {
		// Use the Day3 handler to load the 2 compartments for each sack
		super.handleInput(line);
		// Combind all items in either compartment into 1 set for the next 3 sacks
		if (sack1 == null) {
			sack1 = new HashSet<>();
			loadSack(sack1);
		} else if (sack2 == null) {
			sack2 = new HashSet<>();
			loadSack(sack2);
		} else {
			sack3 = new HashSet<>();			
			loadSack(sack3);
			// Now that we have 3 sacks, find the common item
			Character badge = findBadge();
			int pri = priority(badge);
			logger.info("Badge is "+badge+" for a priority of "+pri);
			badgeScore += pri;

			// Get ready for the next 3 sacks
			sack1 = null;
			sack2 = null;
			sack3 = null;
		}		
	}
	
	private void loadSack(HashSet<Character> sack) {
		for (Character c : rucksack1.keySet()) {
			sack.add(c);
		}
		for (Character c : rucksack2.keySet()) {
			sack.add(c);
		}
	}
	
	private Character findBadge() {
		HashSet<Character> common = sack1;
		
		for (Character c : common) {
			if (!sack2.contains(c)) {
				continue;
			}
			if (!sack3.contains(c)) {
				continue;
			}
			logger.info("Common item in all 3 sacks: "+c);
			return c;
		}
		logger.error("No common item found in the last 3 sacks: "+sack1+" "+sack2+" "+sack3);
		return null;
	}
	
	@Override
	public void output() {
		logger.info("Total Badge Score: "+badgeScore);
	}
}
