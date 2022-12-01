package bb.aoc2022.handler;

import java.util.HashSet;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day1 implements InputHandler {

	static private Logger logger = Logger.getLogger(Day1.class.getName());

	protected int elfIndex = 0; // Used to name the elves

	// Depending on subsequent days, may move Elf to utilities
	protected class Elf {
		String id;
		long calories;
		
		public Elf() {
			id = String.valueOf(elfIndex++);
		}
		
		public String toString() {
			return id+" calories: "+calories;
		}
	}
	
	// Store all elves and calories in a set
	protected HashSet<Elf> elves = new HashSet<>();
	protected Elf currentElf; // Elf we're currently parsing
	
	protected Elf mostCaloriesElf; // Which elf currently is carrying the most
		
	@Override
	public void handleInput(String line) {
		if (currentElf == null) {
			currentElf = new Elf();
		}
		if (line.length() == 0) {
			// This elf is done
			elves.add(currentElf);
			logger.info("Added Elf "+currentElf);
			if (mostCaloriesElf == null || mostCaloriesElf.calories <= currentElf.calories) {
				mostCaloriesElf = currentElf;
			}
			currentElf = new Elf();
		} else {
			try {
				Long calories = Long.parseLong(line);
				currentElf.calories += calories;
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void output() {
		logger.info("Most Calories: "+mostCaloriesElf);
	}

}
