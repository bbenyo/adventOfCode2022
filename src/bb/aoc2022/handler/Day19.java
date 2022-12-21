package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bb.aoc2022.GameState;
import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day19 implements InputHandler {

	static private Logger logger = Logger.getLogger(Day19.class.getName());
	
	protected int endTime = 24;
	
	public enum ResourceType {GEODE, OBSIDIAN, CLAY, ORE};
		
	protected class Robot {
		ResourceType type;
		Map<ResourceType, Integer> costs;
		
		public Robot(ResourceType type) {
			this.type = type;
			costs = new HashMap<>();
			for (ResourceType rtype : ResourceType.values()) {
				costs.put(rtype, 0);
			}
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer("Each "+type+" robot costs: ");
			sb.append(costs);
			return sb.toString();
		}
	}
	
	public static int stateCount = 0;

	protected class RobotState extends GameState {
		
		Blueprint bp;
		Map<ResourceType, Integer> botCounts;
		Map<ResourceType, Integer> resources;
		int time = 1;
				
		public RobotState(Blueprint bp, String label) {
			super(label);
			this.bp = bp;
			initializeCounts();
			this.time = 1;
			stateHash = String.valueOf(stateCount).hashCode();
			stateCount++;
			priorStates.clear();  // Ignore backtracking to save time/space
		}

		public RobotState(GameState oState, String label) {
			super(oState, label);
			RobotState oRobotState = (RobotState)oState;
			this.bp = oRobotState.bp;
			this.time = oRobotState.time + 1;
			botCounts = new HashMap<>();
			resources = new HashMap<>();
			botCounts.putAll(oRobotState.botCounts);
			resources.putAll(oRobotState.resources);
			stateHash = String.valueOf(stateCount).hashCode();
			stateCount++;
			priorStates.clear();
			// Ignore backtracking to save time/space
		}
		
		@Override
		public boolean ignoreAlreadySearchedDFS() {
			return true;
		}
		
		public void initializeCounts() {
			botCounts = new HashMap<>();
			initializeCounts(botCounts);
			botCounts.put(ResourceType.ORE, 1);
			resources = new HashMap<>();	
			initializeCounts(resources);
		}
		
		public void initializeCounts(Map<ResourceType, Integer> counts) {
			for (ResourceType type: ResourceType.values()) {
				counts.put(type, 0);
			}
		}
		
		// Each robot collects ore
		public void collect(int cycles) {
			for (ResourceType type : ResourceType.values()) {
				Integer bots = botCounts.get(type);
				Integer res = resources.get(type);
				resources.put(type, res + (bots * cycles));
			}
		}

		// Can we build a robot of this type next?
		public boolean canBuild(ResourceType type) {
			Robot r = bp.robots.get(type);
			for (ResourceType rtype : ResourceType.values()) {
				Integer cost = r.costs.get(rtype);
				if (cost > 0) {
					Integer ore = resources.get(rtype);
					if (cost > ore) {
						return false;
					}
				}
			}
			return true;
		}
		
		// When can we next build type X?
		public void whenCanBuild(ResourceType type) {
			Robot r = bp.robots.get(type);
			// Do we at least have 1 bot of the types we need?
			for (ResourceType rtype : ResourceType.values()) {
				Integer cost = r.costs.get(rtype);
				if (cost > 0 && botCounts.get(rtype) == 0) {
					time = -1;
					return;
				}
			}
			while (time < endTime && !canBuild(type)) {
				collect(1);
				time++;
			}
		}
		
		// Build a bot
		public void buildBot(ResourceType type) {
			Robot r = bp.robots.get(type);
			for (ResourceType rtype : ResourceType.values()) {
				Integer cost = r.costs.get(rtype);
				if (cost > 0) {
					Integer ore = resources.get(rtype);
					if (ore < cost) {
						throw new RuntimeException("Invalid state: trying to build a bot without enough resources!");
					}
					resources.put(rtype, ore - cost);
				}
			}
			botCounts.put(type, botCounts.get(type) + 1);
		}
		
		@Override
		public boolean win() {
			return time == endTime + 1;
		}
		
		@Override
		public long getScore() {
			return resources.get(ResourceType.GEODE);
		}
		
		@Override
		public long getBestScoreFromHere() {
			int geodes = resources.get(ResourceType.GEODE);
			int geodeBots = botCounts.get(ResourceType.GEODE);
			for (int i=time; i<endTime + 1; ++i) {
				geodes += geodeBots;
				geodeBots++; // Assume we have enough resources to make 1 e/o turn, best possible case
			}			
			return geodes;
		}
		
		// If we found this state earlier in the search with a this score, should we prune this state?
		// If the old score is better, we're better off from that state, and don't need to search more from here
		// Default implementation is that score = cost, so lower is better
		@Override
		public boolean worseScoreThan(GameState oState) {
			RobotState rState = (RobotState)oState;
			int geodeBots = botCounts.get(ResourceType.GEODE);
			if (rState.time < time && rState.botCounts.get(ResourceType.GEODE) > geodeBots) {
				// Other state has more geode bots, this state can't be better
				return true;
			}
			long mScore = getBestScoreFromHere();
			long oScore = oState.getBestScoreFromHere();
			if (mScore < oScore) {
				return true;
			}
			return false;
		}

		@Override
		public List<GameState> generatePossibleMoves() {
			List<GameState> moves = new ArrayList<>();
			if (time > endTime) {
				return moves;
			}		
			
			// Can we build each robot?
			RobotState oreBot = createBuildMove(ResourceType.ORE);
			// No point in an ore bot at 22 (too late to be of use)
			if (oreBot != null && oreBot.time >= endTime - 4) {
				oreBot = null;
			}
			RobotState cBot = createBuildMove(ResourceType.CLAY);
			// No point in a clay bot at 22, clay -> obsidian -> geode
			if (cBot != null && cBot.time >= endTime - 3) {
				cBot = null;
			}
			RobotState oBot = createBuildMove(ResourceType.OBSIDIAN);
			// No point in an obsidian bot at 23
			if (oBot != null && oBot.time >= endTime - 2) {
				oBot = null;
			}
			RobotState geodeBot = createBuildMove(ResourceType.GEODE);
			if (geodeBot != null && geodeBot.time > endTime - 1) {
				geodeBot = null;
			}
			// if we can build a geode bot, there's no point in doing anything else
			if (geodeBot != null) {
				moves.add(geodeBot);
			} 
			if (oBot != null) {
				moves.add(oBot);
			}
			if (cBot != null) {
				moves.add(cBot);
			}
			if (oreBot != null) {
				moves.add(oreBot);
			}
			if (moves.isEmpty()) {
				RobotState collect = new RobotState(this, this.time+"_collect");
				collect.collect(endTime-time);
				collect.time = endTime + 1;
				moves.add(collect);
			}
			return moves;
		}
		
		protected RobotState createBuildMove(ResourceType type) {
			RobotState build = new RobotState(this, this.time+"_build"+type);
			//build.collect(1);
			build.whenCanBuild(type);
			if (build.time >= endTime || build.time == -1) {
				return null;
			}
			build.collect(1);
			build.buildBot(type);
			return build;
		}
		
		@Override
		public void setStateHash() {
			// Since time only goes forward, there's no way to revisit a state
		}
		
		@Override
		public boolean sameState(GameState o) {
			return false;
		}
		
		@Override
		public boolean seenBefore(GameState o) {
			return false;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(System.lineSeparator());
			sb.append(label+" Time: "+time);
			sb.append(" Resources: "+resources);
			sb.append(" Bots: "+botCounts);
			return sb.toString();
		}
		
	}
	
	public class Blueprint {
		Map<ResourceType, Robot> robots;
		RobotState initialState;
		RobotState finalState;
		int index;
		long qualityLevel;
		
		public Blueprint(int index) {
			this.index = index;
			this.qualityLevel = 0;
			robots = new HashMap<>();
		}
		
		@Override
		public String toString() {
			return "Blueprint "+index+": quality: "+qualityLevel;
		}
	}
	
	List<Blueprint> blueprints;

	@Override
	public void initialize() {
		blueprints = new ArrayList<>();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		int index = Utilities.parseInt(line, 0, "Blueprint ", ":");
		Blueprint bp = new Blueprint(index);
		
		int oreCost = Utilities.parseInt(line, 0, "Each ore robot costs ", " ore.");
		int clayCost = Utilities.parseInt(line, 0, "Each clay robot costs ", " ore.");
		int oOreCost = Utilities.parseInt(line, 0, "Each obsidian robot costs ", " ");
		int obsidianPos = line.indexOf("Each obsidian ");
		int oClay = Utilities.parseInt(line, obsidianPos, "and ", " clay.");
		int gOreCost = Utilities.parseInt(line, obsidianPos, "Each geode robot costs ", " ");
		int geodePos = line.indexOf("Each geode ");
		int gObsidian = Utilities.parseInt(line, geodePos,  "and ", " obsidian.");
		
		Robot oreBot = new Robot(ResourceType.ORE);
		oreBot.costs.put(ResourceType.ORE, oreCost);
		Robot clayBot = new Robot(ResourceType.CLAY);
		clayBot.costs.put(ResourceType.ORE, clayCost);
		Robot obsidianBot = new Robot(ResourceType.OBSIDIAN);
		obsidianBot.costs.put(ResourceType.ORE, oOreCost);
		obsidianBot.costs.put(ResourceType.CLAY, oClay);
		Robot geodeBot = new Robot(ResourceType.GEODE);
		geodeBot.costs.put(ResourceType.ORE, gOreCost);
		geodeBot.costs.put(ResourceType.OBSIDIAN, gObsidian);
		
		RobotState initial = new RobotState(bp, String.valueOf(index));
		bp.robots.put(ResourceType.ORE, oreBot);
		bp.robots.put(ResourceType.GEODE, geodeBot);
		bp.robots.put(ResourceType.CLAY, clayBot);
		bp.robots.put(ResourceType.OBSIDIAN, obsidianBot);
		bp.initialState = initial;
		initial.collect(1);
		blueprints.add(bp);
	}

	@Override
	public void output() {
		long quality = 0;
		for (Blueprint bp : blueprints) {
			logger.info(bp.toString());
			logger.info(bp.initialState.toString());
			
			GameState endState = GameState.dfs(bp.initialState);
			bp.finalState = (RobotState)endState;
			
			logger.info("Final State: "+bp.finalState.toString());
			bp.qualityLevel = bp.finalState.getScore() * bp.index;
			
			logger.info("Quality Level: "+bp.qualityLevel);
			quality += bp.qualityLevel;
		}
		
		logger.info("Total Quality Sum: "+quality);
	}

}
