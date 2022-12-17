package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import bb.aoc2022.GameState;
import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

/**
 * Step 1: Find the shortest path from each non-zero flow valve + room AA to each other non-zero flow valve
 *   These will be our moves, we don't want to bother wandering around the cave
 *   A "move" will be to either open a valve or move straight to another closed valve with an non zero flow
 *   We can find these shortest paths easily with a quick DFS first
 *   
 * Step 2: Do the main search, moves from Valve X will be to open valve X or to move to another closed valve
 * @author bbenyo
 *
 */
public class Day16 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day16.class.getName());
	
	int maxScore = 0;
	
	protected class CaveState extends GameState {
		Room room;
		int timeLeft;
		int flowRate;
		int totalFlow;
		List<String> openValves;
		
		public CaveState(String room) {
			super(room);
			this.room = rooms.get(room);
			this.timeLeft = 30;
			this.flowRate = 0;
			this.totalFlow = 0;
			this.score = timeLeft;			
			openValves = new ArrayList<>();
		}
		
		public CaveState(CaveState state) {
			super(state.room.valve);
			this.room = state.room;
			this.timeLeft = state.timeLeft - 1;
			this.flowRate = state.flowRate;
			this.totalFlow = state.totalFlow + state.flowRate;
			this.openValves = new ArrayList<>(state.openValves);
			priorStates = new ArrayList<GameState>();
			priorStates.addAll(state.priorStates);
			priorStates.add(state);
			super.setStateHash();
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (room == null) {
				sb.append("BAD STATE: NULL room");
				return sb.toString();
			}
			sb.append("Room "+room.valve);
			sb.append(" Current Flow Rate: "+flowRate);
			sb.append(" Total Flow: "+totalFlow);
			sb.append(" Time Left: "+timeLeft);
			sb.append(" Open Valves: "+String.join(",", openValves));
			return sb.toString();
		}

		@Override
		public boolean win() {
			if (this.noMoves) {
				return true;
			}
			if (this.timeLeft <= 0) {
				return true;
			}
			if (this.openValves.size() >= usefulValves.size()) {
				return true;
			}
			return false;
		}
		
		@Override
		public long getScore() {
			return (long)this.totalFlow + (this.timeLeft * this.flowRate);
		}
		
		@Override
		public boolean worseScoreThan(GameState oState) {
			long bFromHere = this.getBestScoreFromHere();
			long oFromHere = ((CaveState)oState).getBestScoreFromHere();
			if (bFromHere <= oFromHere) {
				return true;
			}
			return false;
		}
		
		// What is the best we could do from here?  Assume we could open each remaining valve turn after turn
		@Override
		public long getBestScoreFromHere() {
			if (this.noMoves) {
				return getScore();
			}
			if (openValves.size() == usefulValves.size()) {
				return getScore();
			}
			int bestFlow = this.flowRate;
			int time = this.timeLeft;
			int bestScore = this.totalFlow;
			for (String uValve : usefulValves) {
				if (time <= 2) {
					continue;
				}
				if (this.openValves.contains(uValve)) {
					continue;
				}
				// Move to uValve
				bestScore += bestFlow;
				time--;
				// Open uValve
				bestScore += bestFlow;
				bestFlow += rooms.get(uValve).flowRate;
				time--;
			}
			bestScore += (timeLeft * bestFlow);
			return bestScore;			
		}
		
		@Override
		public List<GameState> generatePossibleMoves() {
			List<GameState> moves = new ArrayList<>();
			if (this.win()) {
				return moves; 
			}
			// Open this valve
			if (room.flowRate > 0 && !this.openValves.contains(room.valve)) {
				CaveState openValve = new CaveState(this);
				openValve.openValves.add(room.valve);
				openValve.totalFlow += openValve.flowRate;
				openValve.flowRate += room.flowRate;
				openValve.setStateHash();
				openValve.getPath().add(this);
				moves.add(openValve);				
			}
			for (String target : room.shortestPaths.keySet()) {
				if (this.openValves.contains(target)) {
					continue;
				}
				// Valid option is to move to target
				List<GameState> path = room.shortestPaths.get(target);
				if (path != null) {
					CaveState moveTo = new CaveState(this);
					// Move to the valve
					for (int i=1; i<path.size(); ++i) {
						moveTo.timeLeft -= 1;
						moveTo.totalFlow += moveTo.flowRate;
					}
					moveTo.room = rooms.get(target);
					// And open it, no point in moving there and then going to another 
					moveTo.timeLeft--;
					moveTo.totalFlow += moveTo.flowRate;
					moveTo.flowRate += moveTo.room.flowRate;
					moveTo.openValves.add(target);
					moveTo.setStateHash();
					if (moveTo.timeLeft > 0) { // 0 is useless, since we can't open the valve
						moves.add(moveTo);
					}					
				}
			}

			moves.sort(new Comparator<GameState>() {

				@Override
				public int compare(GameState o1, GameState o2) {
					CaveState c1 = (CaveState)o1;
					CaveState c2 = (CaveState)o2;
					if (c1.room.flowRate > c2.room.flowRate) {
						return -1;
					} else if (c1.room.flowRate == c2.room.flowRate) {
						return 0;
					}
					return 1;
				}
				
			});
			return moves;
		}		
	}
	
	protected class Room {
		String valve;
		int flowRate;
		
		List<String> tunnels;
		Map<String, List<GameState>> shortestPaths;
		
		String currentTargetValve = null;
		
		public Room(String v, int flow) {
			this.valve = v;
			this.flowRate = flow;
			tunnels = new ArrayList<>();
			shortestPaths = new HashMap<>();
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Room "+valve);
			sb.append(": FlowRate: "+flowRate);
			sb.append(": Tunnels to "+String.join(",", tunnels));
			return sb.toString();
		}
	}
	
	// Used to do a simple breath first search to find the shortest path to a specific end valve
	protected class RoomSearchState extends GameState {
		Room room;
		String target;
		
		public RoomSearchState(Room r, String tgt) {
			super(r.valve);
			this.room = r;
			this.target = tgt;
		}

		@Override
		public boolean win() {
			return room.valve.equals(target);
		}

		@Override
		public List<GameState> generatePossibleMoves() {
			List<GameState> moves = new ArrayList<>();
			for (String tunnel : room.tunnels) {
				Room r2 = rooms.get(tunnel);
				if (r2 == null) {
					logger.error("Unable to find the room for "+tunnel);
				} else {
					RoomSearchState rss2 = new RoomSearchState(r2, target);
					moves.add(rss2);
				}
			}
			return moves;
		}		
	}
	
	HashMap<String, Room> rooms;
	List<String> usefulValves = new ArrayList<>();

	@Override
	public void initialize() {
		rooms = new HashMap<>();
	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		String valve = Utilities.parseString(line, 0, "Valve ", " has");
		Integer rate = Utilities.parseInt(line, "rate=", ";");
		String tunnelStr = Utilities.parseString(line, 0, List.of("to valves ","to valve "), "");
		String[] tunnels = tunnelStr.split(",");
		Room r1 = new Room(valve, rate);
		for (String tunnel : tunnels) {
			r1.tunnels.add(tunnel.trim());
		}
		rooms.put(r1.valve, r1);
	}

	Stack<CaveState> workList = new Stack<>();
	
	protected String writePath(List<GameState> path) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (GameState p : path) {
			if (first) first = false; else sb.append(" -> ");
			sb.append(p.getLabel());
		}
		return sb.toString();
	}
	
	@Override
	public void output() {
		List<String> valves = new ArrayList<>(rooms.keySet());
		Collections.sort(valves);
		// Get all Valves with a non zero flow, these are the only ones we care about for possible moves
		usefulValves = new ArrayList<>();
		for (String v : valves) {
			Room r1 = rooms.get(v);
			logger.info(r1);
			if (r1.flowRate > 0) {
				usefulValves.add(r1.valve);
			}
		}
		Collections.sort(usefulValves, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Room r1 = rooms.get(o1);
				Room r2 = rooms.get(o2);
				if (r1.flowRate > r2.flowRate) {
					return -1;
				} else if (r1.flowRate == r2.flowRate) {
					return 0;
				}
				return 1;
			}			
		});
				
		logger.info("Non zero flow valves: "+String.join(",", usefulValves));
		
		for (String v : valves) {
			Room r1 = rooms.get(v);
			for (String uv : usefulValves) {
				if (r1.valve.equals(uv)) {
					continue;
				}
				// Breath first search from r1 to uv, what's the shortest path
				RoomSearchState rss = new RoomSearchState(r1, uv);
				GameState end = GameState.bfs(rss);
				if (end == null) {
					logger.warn("Unable to find a path from "+r1.valve+" to "+uv);
				} else {
					r1.shortestPaths.put(uv, end.getPath());
					logger.info("Shortest path from "+r1.valve+" to "+uv+" = "+writePath(end.getPath()));
				}
			}
		}
		
		valveSearch();
	}
	
	protected void valveSearch() {		
		CaveState start = new CaveState("AA");
		logger.info("Start: "+start);
		GameState end = GameState.dfs(start);
		// No-op until minute 30
		CaveState endState = (CaveState)end;
		while (endState.timeLeft > 0) {
			endState.totalFlow += endState.flowRate;
			endState.timeLeft--;
		}
		logger.info("End State: "+endState);
		for (GameState p : end.getPath()) {
			logger.info(p);
		}
	}

}
