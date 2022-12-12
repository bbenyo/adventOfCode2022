package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.Location;
import bb.aoc2022.Node;

/**
 * Flip the search to go from end to start, and we have no specific "end" node
 *   So we search all nodes in the list, then get the best path
 * 
 * We could be a bit smarter and prune the search if we get g score > the min path length so far...
 * @author bbenyo
 *
 */
public class Day12b extends Day12 {
	static private Logger logger = Logger.getLogger(Day12b.class.getName());
	
	@Override
	public void output() {
		// Flip start/end, we'll start at the end and look backwards
		start = end;
		end = new Location(-1, -1); // There's no specific end node, we'll search until we can't anymore
		
		super.output();
		
		int minSteps = Integer.MAX_VALUE;
		Node startNode = null;
		for (Node l : visitedStarts) {
			int steps = l.getBackPathLength();
			if (steps < minSteps) {
				minSteps = steps;
				startNode = l;
			}
		}
		
		if (startNode != null) {
			logger.info("Min Start Node: "+startNode);
			List<Node> path = startNode.getBackPath();
			for (Node n : path) {
				logger.info("Path: "+n);
			}
			logger.info("Min path Length: "+startNode.getBackPathLength());
		} else {
			logger.error("Unable to find any path to a start");
		}
	}
	
	List<Node> visitedStarts = new ArrayList<>();
		
	@Override
	protected boolean isValidStep(Node cur, Location next) {
		// If we're an end node ("a"), we have no neighbors
		if (getHeight(cur) == 'a') {
			visitedStarts.add(cur);
			return false;
		}
		// Reverse it
		Node n = new HillNode(next);
		return super.isValidStep(n, cur);
	}
		

}
