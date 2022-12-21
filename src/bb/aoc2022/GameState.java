package bb.aoc2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

abstract public class GameState {
	static private Logger logger = Logger.getLogger(GameState.class.getName());
	protected String label;
	protected long score;
	// To avoid wasted search going back and forth between two identical states
	protected List<GameState> priorStates;
	protected int stateHash;
	protected boolean noMoves = false;
	
	public GameState(String loc) {
		this.label = loc;
		this.score = 0;
		priorStates = new ArrayList<GameState>();
		setStateHash();
	}
	
	public GameState(GameState oState, String label) {
		this.label = label;
		score = oState.score;
		priorStates = new ArrayList<GameState>();
		priorStates.addAll(oState.priorStates);
		priorStates.add(oState);
		setStateHash();
	}
	
	// Do we want to keep track of states we've searched or not?
	//  If true (ignore already searched), we won't maintain the hashset of states
	//   This is useful if we're incrementing time as part of the state, so we never really search the same state
	public boolean ignoreAlreadySearchedDFS() {
		return false;
	}
	
	public void setNoMoves(boolean flag) {
		this.noMoves = flag;
	}
		
	public void setStateHash() {
		// TODO: Could and should make a much faster state hash function
		//   We just want this to produce the same hash if the game state is the same
		//  Printing out the as a string will work, but this could be sped up
		String hash = toString();
		stateHash = hash.hashCode();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(label);
		sb.append(" score: "+this.score);
		return sb.toString();
	}
	
	abstract public boolean win(); // Is this an end state?
	
	public long getScore() {
		return score;
	}
	
	public long getBestScoreFromHere() {
		return score;
	}
	
	// If we found this state earlier in the search with a this score, should we prune this state?
	// If the old score is better, we're better off from that state, and don't need to search more from here
	// Default implementation is that score = cost, so lower is better
	public boolean worseScoreThan(GameState oState) {
		long oScore = oState.getScore();
		if (oScore <= getScore()) {
			return true;
		}
		return false;
	}
	
	public List<GameState> getPath() {
		return priorStates;
	}
	
	public String getLabel() {
		return label;
	}
		
	// Order heuristically, first move here should be the estimated best move, if you can order them
	abstract public List<GameState> generatePossibleMoves();
		
	// Not calling this .equals since it doesn't meet the equals contract 
	// Should hash the state to make this faster, hash would just be each position
	public boolean sameState(GameState o) {
		if (stateHash == o.stateHash) {
			return true;
		}
		return false;
	}
	
	public boolean seenBefore(GameState o) {
		for (GameState p : priorStates) {
			if (p.sameState(o)) {
				return true;
			}
		}
		return false;
	}
		
	// Get the best possible score for this state
	public long getMinScore() {
		return score;
	}
	
	static public GameState dfs(GameState initialState) {
		int index = 0;
		Stack<GameState> workList = new Stack<>();
		workList.clear();
		
		// States that we've already searched, and their score
		// If we get to the same state again, but a better score, we still search it
		Map<Integer, GameState> alreadySearched = new HashMap<>();
		workList.push(initialState);
		GameState winner = null;
		while (!workList.isEmpty()) {
			index++;
			GameState state = workList.pop();
			if (!state.ignoreAlreadySearchedDFS()) {
				alreadySearched.put(state.stateHash, state);
			}
			List<GameState> moves = state.generatePossibleMoves();
			
			if (moves.isEmpty()) {
				state.noMoves = true;
			}

			if (state.win()
				&& (winner == null || (winner.worseScoreThan(state)))) {
				winner = state;
				logger.info("Found new winner at Search step: "+index+" worklist size: "+workList.size()+" winner score: "+(winner.getScore()));
				continue;
			}
			
			if (winner != null && state.worseScoreThan(winner)) {
				// Winner is already better than this, we're done on this branch
				logger.info("Existing winner is better than this state: "+state.getScore()+" winner: "+winner.getScore());
				continue;
			}
						
			for (int m1 = 0; m1 < moves.size(); ++m1) {
				// Iterate from the end of the list
				//   The possible moves are ordered heuristically, we want to 
				//   investigate the first one first, so it needs to be last on the stack
				GameState nState = moves.get(moves.size() - 1 - m1);
				if (winner != null && nState.worseScoreThan(winner)) {
					// Current winner is already better that a win from this state could be
					continue;
				}
				GameState oScore = alreadySearched.get(nState.stateHash);
				if (oScore != null && nState.worseScoreThan(oScore)) {
					continue;
				}
				workList.add(nState);
				if (!state.ignoreAlreadySearchedDFS()) {
					alreadySearched.put(nState.stateHash, nState);
				}
			}
			StringBuffer sb = new StringBuffer("Search step: "+index);
			sb.append(" worklist size: "+workList.size());
			if (winner != null) {
				sb.append(" Winner score: "+winner.getScore());
			}
			logger.info(sb.toString());
		}
		return winner;
	}
	
	/**
	 * Breath first search, stops as soon as it hits an end state
	 * @param initialState
	 * @return
	 */
	static public GameState bfs(GameState initialState) {
		int index = 0;
		List<GameState> workList = new ArrayList<>();
		workList.clear();
		HashSet<Integer> alreadySearched = new HashSet<>();
		
		workList.add(initialState);
		GameState winner = null;
		while (!workList.isEmpty()) {
			index++;
			GameState state = workList.remove(0);
			if (state.win()) {
				winner = state;
				break;
			}
			List<GameState> moves = state.generatePossibleMoves();
			for (int m1 = 0; m1 < moves.size(); ++m1) {
				// Iterate from the end of the list
				//   The possible moves are ordered heuristically, we want to 
				//   investigate the first one first, so it needs to be last on the stack
				GameState nState = moves.get(moves.size() - 1 - m1);
				if (alreadySearched.contains(nState.stateHash)) {
					continue;					
				}
				workList.add(nState);
				alreadySearched.add(nState.stateHash);
			}
			logger.info("Search step: "+index+" worklist size: "+workList.size());
		}
		return winner;
	}
}