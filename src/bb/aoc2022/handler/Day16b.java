package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.GameState;

public class Day16b extends Day16 {
	static private Logger logger = Logger.getLogger(Day16b.class.getName());

	protected class CaveStateE extends CaveState {

		Room elephant; // Where the elephant is
		Room myDestination = null; // Where I'm going
		Room elephantDestination = null;  // Where the elephant is going
		int myDuration = 0;  // How long until I get there and open the valve
		int elephantDuration = 0; // How long until the elephant gets there and opens the valve
		
		public CaveStateE(String room) {
			super(room);
			this.timeLeft = 26;
			this.elephant = rooms.get(room);
		}
		
		public CaveStateE(CaveState state) {
			super(state);
			CaveStateE stateE = (CaveStateE)state;
			elephant = stateE.elephant;
			myDestination = stateE.myDestination;
			elephantDestination = stateE.elephantDestination;
			myDuration = stateE.myDuration;
			elephantDuration = stateE.elephantDuration;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (room == null) {
				sb.append("BAD STATE: NULL room");
				return sb.toString();
			}
			if (elephant == null) {
				sb.append("BAD STATE: elephant NULL");
				return sb.toString();
			}
			sb.append("Old Room "+room.valve);
			if (myDestination != null) {
				sb.append(" Destination: "+myDestination.valve);
				sb.append(" in "+myDuration);
			}
			sb.append(System.lineSeparator());
			sb.append("\tElephant: "+elephant.valve);
			if (elephantDestination != null) {
				sb.append(" Elephant Destination: "+elephantDestination.valve);
				sb.append(" in "+elephantDuration);
			}
			sb.append(System.lineSeparator());
			sb.append("\tCurrent Flow Rate: "+flowRate);
			sb.append(" Total Flow: "+totalFlow);
			sb.append(" Time Left: "+timeLeft);
			sb.append(" Open Valves: "+String.join(",", openValves));
			return sb.toString();
		}
		
		@Override
		public boolean worseScoreThan(GameState oState) {
			return super.worseScoreThan(oState);
		}
				
		// What is the best we could do from here?  Assume we could open each remaining valve turn after turn
		// And that elephant can open one turn after turn too
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
				if (time <= 1) {
					continue;
				}
				if (this.openValves.contains(uValve)) {
					continue;
				}
				// One of us (elephant and I) open the next valve and the other one moves
				bestScore += bestFlow;
				time --;
				bestFlow += rooms.get(uValve).flowRate;
			}
			bestScore += (time * bestFlow);
			return bestScore;			
		}
		
		@Override
		public boolean win() {
			if (this.noMoves) {
				// Need to move the last mover forward, if there are no more valves for one to open, the other may open its valve
				this.timeLeft--;
				propagateForward(true);
				return true;
			}
			if (this.timeLeft <= 0) {
				return true;
			}
			if (Math.min(myDuration, elephantDuration) > timeLeft) {
				return true;
			}
			if (this.openValves.size() >= usefulValves.size()) {
				return true;
			}
			return false;
		}
		
		// Propagate this state forward until the closest mover to its destination reaches it
		// If this is the end
		protected void propagateForward(boolean w1) {
			int moveDuration = myDuration;
			// Do I get to my destination first, or the elephant?
			if (moveDuration == 0 || (moveDuration > elephantDuration && elephantDuration > 0)) {
				moveDuration = elephantDuration;
			}
			if (moveDuration == 0) {
				// All done, no more movement
				return;
			}
			if (timeLeft < moveDuration) {
				// we can't get there before the end
				return;
			}
			timeLeft -= moveDuration;			
			totalFlow += (flowRate * (moveDuration - 1));
			// If we're done (final state propagation, we do a final update since we're not going to open a valve and update on the next propagate
			if (w1) {
				totalFlow += flowRate;
			}
			if (myDuration > 0) {
				myDuration -= moveDuration;
				// Did I make it and turn on a valve?
				if (myDuration == 0) {
					room = myDestination;
					flowRate += room.flowRate;
					openValves.add(room.valve);
					myDuration = 0;
				}
			}
			// Did the elephant?
			if (elephantDuration > 0) {
				elephantDuration -= moveDuration;
				if (elephantDuration == 0) {
					elephant = elephantDestination;
					flowRate += elephant.flowRate;
					openValves.add(elephant.valve);
					elephantDuration = 0;
				}
			}
				
			setStateHash();
		}
		
		@Override
		public List<GameState> generatePossibleMoves() {
			List<GameState> moves = new ArrayList<>();
			if (this.win()) {
				return moves; 
			}
			
			// Start out with both of us going towards our destinations, at least one of us should already be there
			CaveStateE initial = new CaveStateE(this);
			initial.timeLeft = this.timeLeft;
			if (myDuration == 0) { // I'm done and need to find another place to move
				for (String target : room.shortestPaths.keySet()) {
					if (this.openValves.contains(target) || (elephantDestination != null && target.equals(elephantDestination.valve))) {
						continue;
					}
					// Valid option is to move to target
					List<GameState> path = room.shortestPaths.get(target);
					if (path != null) {
						// My new destination is target
						CaveStateE tgt = new CaveStateE(initial);
						tgt.timeLeft = this.timeLeft;
						tgt.myDestination = rooms.get(target);
						tgt.myDuration = path.size() + 1;  // open the valve too
						moves.add(tgt);
						// We'll actually move later
					}
				}
			} else {
				// I'm still moving
				moves.add(initial);
			}

			// For every move, we move the elephant too
			List<GameState> doubleMoves = new ArrayList<>();
			for (GameState move : moves) {
				CaveStateE moveE = (CaveStateE)move;
				if (elephantDuration == 0) { // That elephant needs to move too
					for (String target : elephant.shortestPaths.keySet()) {
						if (this.openValves.contains(target) || (moveE.myDestination != null && target.equals(moveE.myDestination.valve))) {
							continue;
						}
						// Valid option is to move to target
						List<GameState> path = elephant.shortestPaths.get(target);
						if (path != null) {
							CaveStateE dMove = new CaveStateE(moveE);
							dMove.timeLeft = this.timeLeft;
							dMove.elephantDestination = rooms.get(target);
							dMove.elephantDuration = path.size() + 1;
							doubleMoves.add(dMove);
						}
					}
				} else {
					// Elephant is still moving
					doubleMoves.add(moveE);
				}
			}
			
			List<GameState> finalMoves = new ArrayList<>();
			// For each double move, propgate us both forward in time until someone reaches their destination
			for (GameState move : doubleMoves) {
				CaveStateE dMove = (CaveStateE)move;
				dMove.totalFlow = initial.totalFlow;
				dMove.priorStates.clear();
				dMove.priorStates.addAll(this.getPath());
				dMove.priorStates.add(this);
				dMove.propagateForward(false);				
				finalMoves.add(dMove);
			}

			finalMoves.sort(new Comparator<GameState>() {

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
			return finalMoves;
		}		
		
	}
	
	@Override
	protected void valveSearch() {		
		CaveStateE start = new CaveStateE("AA");
		logger.info("Start: "+start);
		GameState end = GameState.dfs(start);
		// No-op until minute 26
		CaveStateE endState = (CaveStateE)end;
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
