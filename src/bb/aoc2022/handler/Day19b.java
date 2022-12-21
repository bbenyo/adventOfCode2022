package bb.aoc2022.handler;

import org.apache.log4j.Logger;

import bb.aoc2022.GameState;

public class Day19b extends Day19 {
	static private Logger logger = Logger.getLogger(Day19b.class.getName());
	
	@Override
	public void initialize() {
		super.initialize();
		endTime = 32;
	}
	
	@Override
	public void output() {
		long score = 1;
		long[] scores = new long[3];
		for (int i=0; i<3; ++i) {
			if (blueprints.size() <= i) {
				continue;
			}
			Blueprint bp = blueprints.get(i);
			logger.info(bp.toString());
			logger.info(bp.initialState.toString());
			
			GameState endState = GameState.dfs(bp.initialState);
			bp.finalState = (RobotState)endState;
			
			logger.info("Final State: "+bp.finalState.toString());
			scores[i] = bp.finalState.getScore();
			logger.info("Geodes: "+scores[i]);
			score = score * scores[i];
		}
		logger.info("Total Score: "+score);
		logger.info("Blueprint 1: "+scores[0]);
		logger.info("Blueprint 2: "+scores[1]);
		logger.info("Blueprint 3: "+scores[2]);		
	}
	
}
