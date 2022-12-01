package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class Day1b extends Day1 {

	static private Logger logger = Logger.getLogger(Day1b.class.getName());
	
	@Override
	public void output() {
		// Sort the elves by calories
		List<Elf> elfList = new ArrayList<>(elves);
		Collections.sort(elfList, new Comparator<Elf>() {

			@Override
			public int compare(Elf o1, Elf o2) {
				if (o1.calories > o2.calories) {
					return -1;
				} else if (o1.calories == o2.calories) {
					return 0;
				}
				return 1;
			}
			
		});
		
		long calories = 0;
		for (int i=0; i<3; ++i) {
			if (elfList.size() > i) {
				Elf e = elfList.get(i);
				calories += e.calories;
				logger.info("High calorie elf: "+e);
			}
		}
		
		logger.info("Total calories for the top 3 elves: "+calories);
		
	}

}
