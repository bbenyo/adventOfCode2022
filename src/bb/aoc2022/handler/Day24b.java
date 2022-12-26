package bb.aoc2022.handler;

import java.util.HashSet;
import java.util.Objects;

import org.apache.log4j.Logger;

import bb.aoc2022.Location;
import bb.aoc2022.Node;

public class Day24b extends Day24 {
	static private Logger logger = Logger.getLogger(Day24b.class.getName());
	protected class BBNode extends BNode {
		
		/** 
		 *  0 = first trip to end
		 *  1 = return to start
		 *  2 = final trip to end
		 */
		int phase = 0;		

		public BBNode(Location l1, int time, int phase) {
			super(l1, time);
			setPhase(phase);
		}
		
		public void setPhase(int p) {
			this.phase = p;
			this.gScore = getWorstScore(this);
		}
		
		@Override
		public String toString() {
			return super.toString()+" time: "+time+" phase: "+phase;
		}
		
		@Override
		public int getWorstScore(Location l1) {
			return time + phasePenalty();
			// Assume we have to visit every square twice per phase, rough metric
		}
		
		public int phasePenalty() {
			return (2 - phase)*(getGridSizeX() * getGridSizeY() * 4);
		}
		
		@Override
		protected void computeHScore() {
			hScore = time + computeHeuristic();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Objects.hash(phase);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			BBNode other = (BBNode) obj;
			return phase == other.phase;
		}

		// Return a heuristic to compute the likely score from this node
		@Override
		public int computeHeuristic() {
			int right = getGridSizeX() - x;
			int down = getGridSizeY() - y;
			int gx = getGridSizeX();
			int gy = getGridSizeY();
			int curDist = (right + down);
			int hDist = curDist * 3;
			int heuristicFullDist = (gx + gy) * 3;
			if (phase == 0) {
				return hDist + heuristicFullDist * 2; // end, then back to start and back to end;
			} else if (phase == 1) {
				return (((x-1) + (y-1))*3) + heuristicFullDist; // to start then to end;
			} else {
				return hDist;
			}
		}
		
		@Override
		public boolean isEnd(Node loc) {
			BBNode bNode = (BBNode)loc;
			if (bNode.phase == 2 && super.isEnd(loc)) {
				return true;
			}
			return false;
		}
		
		// The value/risk/cost of going to location cur
		@Override
		public int getCost(Location cur) {
			return 1;
		}
		
		@Override
		protected void addNeighbor(TimeState nextState, Location lup) {
			if (!isValidLocation(lup)) {
				return;
			}
			if (!nextState.isClear(lup)) {
				return;
			}
			BBNode node = new BBNode(lup, time + 1, this.phase);
			if (searched.contains(node)) {
				return;
			}
			if (super.isEnd(node) && (node.phase == 0)) {
				node.setPhase(1);
			}
			if (lup.getX() == 1 && lup.getY() == 0 && node.phase == 1) {
				node.setPhase(2);
			}
			if (node.phase == 1) {
				logger.info("Phase 1!");
			}
			if (node.phase == 2) {
				logger.info("Phase 2!");
			}
			this.neighbors.add(node);
			searched.add(node);
		}

	}
	
	HashSet<BBNode> searched = new HashSet<>();
	
	@Override
	public void output() {
		computeBoundingBox();
		TimeState t0 = createBlizzards();
		boardStates.put(0, t0);
		drawGrid(t0);
		
		BBNode startNode = new BBNode(new Location(1,0), 0, 0);
		Node end = Node.search(startNode);
		BBNode endNode = (BBNode)end;		
		TimeState tEnd = this.getBoardState(endNode.time);
		drawGrid(tEnd);
		logger.info("End Time: "+endNode.time);
	}
	
}
