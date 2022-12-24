package bb.aoc2022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A Star search node on a Grid of Locations
 * To use, override methods as needed
 * @author bbenyo
 *
 */
public class Node extends Location {
	static private Logger logger = Logger.getLogger(Node.class.getName());
	
	String label;
	
	protected int gScore = -1;
	protected int hScore = 0;
	
	protected Set<Node> neighbors;
	Node backPath;
		
	public Node(Location l1) {
		super(l1.getX(), l1.getY());
		this.label = l1.toString();
		neighbors = new HashSet<Node>();
		gScore = getWorstScore(l1);
	}
			
	public void setNeighbors(Set<Node> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(Node neighbor) {
		this.neighbors.add(neighbor);
	}
	
	// Heuristic, guess on the risk score for the path to the end
	//   We'll just do a right/down distance, and assume average risk of 5
	protected void computeHScore() {
		int dist = computeHeuristic();
		// Could calculate the actual risk for going this way, but this is fine, it's just a heuristic
		//   and we don't care about optimizing this search for speed today
		int h = (dist * 5);
		hScore = h;
	}

	// Compute the worst possible score from this node to initialize G
	// To be overridden by implementations, this is a default
	public int getWorstScore(Location l1) {
		return getGridSizeX() * getGridSizeY(); // Assume we have to visit every square
	}

	// Return a hueristic to compute the likely score from this node
	public int computeHeuristic() {
		int right = getGridSizeX() - x;
		int down = getGridSizeY() - y;
		return right + down;
	}
	
	// Get the maximum X value for a Location
	public int getGridSizeX() {
		return 10;
	}
	
	// Get the maximum Y value for a Location
	public int getGridSizeY() {
		return 10;
	}
	
	// The value/risk/cost of going to location cur
	public int getValue(Location cur) {
		return 1;
	}

			
	public int getF() {
		return gScore + hScore;
	}

	public int getBackPathLength() {
		int giveUp = getWorstScore(this);
		int steps = 0;
		Node current = this;
		while (current != null && steps < giveUp) {
			steps++;
			current = current.backPath;
		}
		return steps - 1; // Start doesn't count
	}
	
	public List<Node> getBackPath() {
		int giveUp = getWorstScore(this);
		List<Node> path = new ArrayList<>();
		Node current = this;
		int steps = 0;
		while (current != null && steps < giveUp) {
			path.add(current);
			current = current.backPath;
			steps++;
		}
		return path;
	}
		
	// Add neighbors based on where we can go from here.
	// Default implementation assumes a grid, and we can go up/down/left/right
	public void gatherNeighbors() {
		int x = getX() - 1;  //left
		int y = getY();
		if (x > -1) {
			Location lup = new Location(x, y);
			addNeighbor(lup);
		}
		x = getX();
		y = getY() - 1; //up
		if (y > -1) {
			Location lup = new Location(x, y);
			addNeighbor(lup);
		}
		
		x = getX() + 1;
		y = getY(); // right
		if (x < getGridSizeX()) {
			Location lup = new Location(x, y);
			addNeighbor(lup);
		}
		
		x = getX();
		y = getY() + 1; // down
		if (y < getGridSizeY()) {
			Location lup = new Location(x, y);
			addNeighbor(lup);
		}			
	}
	
	protected void addNeighbor(Location lup) {
		Node uNode = nodes.get(lup.toString());
		if (uNode != null) {
			neighbors.add(uNode);
		} else {
			uNode = createNode(lup);
			neighbors.add(uNode);
			nodes.put(lup.toString(), uNode);				
		}
	}
	
	protected Node createNode(Location lup) {
		return new Node(lup);
	}
		
	// Bottom right
	public boolean isEnd(Location loc) {
		if ((loc.getY() == getGridSizeY() - 1) &&
			(loc.getX() == getGridSizeX() - 1)) {
			return true;
		}
		return false;
	}
	
	static Map<String, Node> nodes = new HashMap<String, Node>();

	// A*

	static public Node search(Node start) {

		List<Node> openSet = new ArrayList<Node>();
		openSet.add(start);
		start.gScore = 0;
		
		while (!openSet.isEmpty()) {
			// Get the lowest f score
			Collections.sort(openSet, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2) {
					int f1 = o1.getF();
					int f2 = o2.getF();
					if (f1 < f2) {
						return -1;
					} else if (f1 == f2) {
						return 0;
					}
					return 1;
				}
			});
			
			Node next = openSet.remove(0);
			next.gatherNeighbors();
			next.computeHScore();
			
			logger.info("Searching: "+next+" OpenSet: "+openSet.size());
			if (start.isEnd(next)) {
				return next;
			}
			for (Node n1 : next.neighbors) {
				int g = next.gScore + start.getValue(n1);
				logger.info("Neighbor "+n1+" g: "+g);
				// If the path from next to n1 is better than any other path we've found to n1:
				if (g < n1.gScore) {
					n1.backPath = next;
					n1.gScore = g;
					if (!openSet.contains(n1)) {
						openSet.add(n1);
					}
				}
			}
		}
		
		logger.error("Unable to find a path to the end!");
		return null;
	}
}
