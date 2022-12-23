package bb.aoc2022.handler;
import java.util.HashMap;

import org.apache.log4j.Logger;

import bb.aoc2022.Location;

public class Day22b extends Day22 {
	static private Logger logger = Logger.getLogger(Day22b.class.getName());
	
	protected class CubeFace {
		Location topLeft;
		Location bottomRight;
		int index;
		
		// If I move off this cube that way, which cube face will I be on?
		CubeFace left;
		CubeFace right;
		CubeFace up;
		CubeFace down;
		
		// If I move off this cube this heading, which way will I be facing?
		char leftDir;
		char rightDir;
		char upDir;
		char downDir;
		
		public CubeFace(int index) {
			this.index = index;
		}
		
		public boolean inside(Location l) {
			return l.getX() >= topLeft.getX() && l.getX() <= bottomRight.getX() &&
				   l.getY() >= topLeft.getY() && l.getY() <= bottomRight.getY();
		}
		
		// If we step on this cube, facing this direction, where are we?
		// If we're facing right, then we stepped on the left cube side
		public Location stepOn(Location relativeLoc, char oldFacing, char facing) {
			Location newLoc = new Location(-1,-1);
			
			switch (facing) {
			case '<' : // We're on the right edge
				newLoc.setX(bottomRight.getX());
				switch (oldFacing) {
				case '>' : newLoc.setY(bottomRight.getY() - relativeLoc.getY()); break;
				case 'v' : newLoc.setY(topLeft.getY() + relativeLoc.getX()); break;
				default:
					throw new RuntimeException("Unknown facing pair: stepping onto "+index+" from "+oldFacing+" on the right edge");
				}
				return newLoc;			
			
			case '>' : // we're on the left edge
				newLoc.setX(topLeft.getX());
				switch (oldFacing) {
				case '^' : newLoc.setY(topLeft.getY() + relativeLoc.getX()); break;
				case '<' : newLoc.setY(bottomRight.getY() - relativeLoc.getY()); break;
				default:
					throw new RuntimeException("Unknown facing pair: stepping onto "+index+" from "+oldFacing+" on the left edge");
				}
				return newLoc;
				
			case '^' :
				newLoc.setY(bottomRight.getY());
				switch (oldFacing) {
				case '^' :
					newLoc.setX(topLeft.getX() + relativeLoc.getX()); break;
				case '>' :
					newLoc.setX(topLeft.getX() + relativeLoc.getY()); break;
				default:
					throw new RuntimeException("Unknown facing pair: stepping onto "+index+" from "+oldFacing+" on the bottom edge");
				}
				return newLoc;
				
			case 'v' :
				newLoc.setY(topLeft.getY());
				switch(oldFacing) {
				case '<' :
					newLoc.setX(topLeft.getX() + relativeLoc.getY()); break;
				case 'v' :
					newLoc.setX(topLeft.getX() + relativeLoc.getX()); break;
				default:
					throw new RuntimeException("Unknown facing pair: stepping onto "+index+" from "+oldFacing+" on the top edge");
				}
				return newLoc;
			default: 
				throw new RuntimeException("Unknown facing: "+facing);
			}
		}
	}
	
	HashMap<Integer, CubeFace> cubes;
	
	@Override
	public void initialize() {
		super.initialize();
		cubes = new HashMap<>();
		
		// Instead of trying to figure out the folding automatically, we'll hardcode it for this input
		// Figuring out arbitrary cube folds from the grid input is left as an exercise for the reader =)
		
		/**  the input is (50x50)
		 *        11112222 
		          11112222
		          11112222
		          3333
		          3333
		          3333 
		      44445555
		      44445555
		      44445555
		      6666
		      6666
		      6666
		      
		      Make a paper cutout, write side numbers and directions on it, fold it to a cube 
		        to generate the facings and connections below
		  **/     
		CubeFace c1 = new CubeFace(1);
		c1.topLeft = new Location(50, 0);
		c1.bottomRight = new Location(99, 49);
		CubeFace c2 = new CubeFace(2);
		c2.topLeft = new Location(100,0);
		c2.bottomRight = new Location(149, 49);
		CubeFace c3 = new CubeFace(3);
		c3.topLeft = new Location(50, 50);
		c3.bottomRight = new Location(99, 99);
		CubeFace c4 = new CubeFace(4);
		c4.topLeft = new Location(0, 100);
		c4.bottomRight = new Location(49, 149);
		CubeFace c5 = new CubeFace(5);
		c5.topLeft = new Location(50, 100);
		c5.bottomRight = new Location(99, 149);
		CubeFace c6 = new CubeFace(6);
		c6.topLeft = new Location(0, 150);
		c6.bottomRight = new Location(49, 199);
		// Only care about the faces that are not directly connected
		c1.left = c4;
		c1.leftDir = '>';  // right
		c1.up = c6;
		c1.upDir = '>'; // right
		
		c2.up = c6;
		c2.upDir = '^'; //up
		c2.right = c5;
		c2.rightDir = '<'; // left
		c2.down = c3;
		c2.downDir = '<'; // left;
		
		c3.left = c4;
		c3.leftDir = 'v'; // down
		c3.right = c2;
		c3.rightDir = '^'; // up
		
		c4.up = c3;
		c4.upDir = '>'; // right
		c4.left = c1;
		c4.leftDir = '>'; // right
		
		c5.right = c2;
		c5.rightDir = '<'; // left
		c5.down = c6;
		c5.downDir = '<'; // left
		
		c6.left = c1;
		c6.leftDir = 'v'; // down
		c6.down = c2;
		c6.downDir = 'v'; // down
		c6.right = c5;
		c6.rightDir = '^'; // up
		
		cubes.put(c1.index, c1);
		cubes.put(c2.index, c2);
		cubes.put(c3.index, c3);
		cubes.put(c4.index, c4);
		cubes.put(c5.index, c5);
		cubes.put(c6.index, c6);
	}
	
	// If i'm at grid position l, which cube face am I in?
	public CubeFace getCubeFace(Location l) {
		for (CubeFace c : cubes.values()) {
			if (c.inside(l)) {
				return c;
			}
		}
		throw new RuntimeException("Location "+l+" is not in any cube!");
	}
	
	@Override
	protected void handleRightWrap(Location l) {
		String row = grid.get(l.getY());
		if ((l.getX() >= row.length()) ||
			row.charAt(l.getX()) == ' ') {
			moveRight(l);
		}
	}
	
	protected void moveRight(Location l) {
		// Step back on the cube face we just stepped off
		l.setX(l.getX() - 1);
		CubeFace c = getCubeFace(l);
		if (c.right == null) {
			// directly connected cube face, nothing special to do
			l.setX(0);
		} else {
			Location relativeLoc = new Location(l.getX() - c.topLeft.getX(), l.getY() - c.topLeft.getY());
			facing = c.rightDir;
			c = c.right;
			Location newLoc = c.stepOn(relativeLoc, '>', facing);
			l.setX(newLoc.getX());
			l.setY(newLoc.getY());
		}
	}
	
	@Override
	protected void handleLeftWrap(Location l) {
		String row = grid.get(l.getY());
		if ((l.getX() < 0) ||
			(row.charAt(l.getX()) == ' ')) {
			moveLeft(l);
		}
	}
	
	protected void moveLeft(Location l) {
		// Step back on the cube face we just stepped off
		String row = grid.get(l.getY());
		l.setX(l.getX() + 1);
		CubeFace c = getCubeFace(l);
		if (c.left == null) {
			// directly connected cube face, nothing special to do
			l.setX(row.length() - 1);
		} else {
			Location relativeLoc = new Location(l.getX() - c.topLeft.getX(), l.getY() - c.topLeft.getY());
			facing = c.leftDir;
			c = c.left;
			Location newLoc = c.stepOn(relativeLoc, '<', facing);
			l.setX(newLoc.getX());
			l.setY(newLoc.getY());
		}
	}
	
	@Override
	protected void handleDownWrap(Location l) {
		if (l.getY() >= grid.size()) {
			moveDown(l);
			return;
		}
		String row = grid.get(l.getY());
		if (row.charAt(l.getX()) == ' ') {
			moveDown(l);
		}
	}
	
	public void moveDown(Location l) {
		// Step back on the cube face we just stepped off
		l.setY(l.getY() - 1);
		CubeFace c = getCubeFace(l);
		if (c.down == null) {
			// directly connected cube face, nothing special to do
			l.setY(0);
		} else {
			Location relativeLoc = new Location(l.getX() - c.topLeft.getX(), l.getY() - c.topLeft.getY());
			facing = c.downDir;
			c = c.down;
			Location newLoc = c.stepOn(relativeLoc, 'v', facing);
			l.setX(newLoc.getX());
			l.setY(newLoc.getY());
		}
	}
	
	@Override
	protected void handleUpWrap(Location l) {
		if (l.getY() < 0) {
			moveUp(l);
			return;
		}
		String row = grid.get(l.getY());
		if (row.charAt(l.getX()) == ' ') {
			moveUp(l);
		}
	}

	public void moveUp(Location l) {
		// Step back on the cube face we just stepped off
		l.setY(l.getY() + 1);
		CubeFace c = getCubeFace(l);
		if (c.up == null) {
			// directly connected cube face, nothing special to do
			l.setY(grid.size() - 1);
		} else {
			Location relativeLoc = new Location(l.getX() - c.topLeft.getX(), l.getY() - c.topLeft.getY());
			facing = c.upDir;
			c = c.up;
			Location newLoc = c.stepOn(relativeLoc, '^', facing);
			l.setX(newLoc.getX());
			l.setY(newLoc.getY());
		}
	}
	
	// Override output to test 
	//@Override
	public void outputTest() {
		program.clear();
		super.output();
		// Test facings, moving 200 in each direction should get you back to where you started
		Move m200 = new Move(200);
		
		for (int i=0; i<150; i++) {
			for (int j=0; j<=199; ++j) {
				Location l = new Location(i,j);
				String row = grid.get(j);
				if (row.charAt(i) != ' ') {
					
					me.setX(l.getX());
					me.setY(l.getY());
					facing = '^';
					m200.execute();
					logger.info("Move ^ 200: "+me+" facing: "+facing);
					if (me.getX() != l.getX() || me.getY() != l.getY() || facing != '^') {
						throw new RuntimeException("Failed at "+l+" ^");
					}
						
					me.setX(l.getX());
					me.setY(l.getY());
					facing = '>';
					m200.execute();
					logger.info("Move > 200: "+me+" facing: "+facing);
					if (me.getX() != l.getX() || me.getY() != l.getY() || facing != '>') {
						throw new RuntimeException("Failed at "+l+" >");
					}
						
					me.setX(l.getX());
					me.setY(l.getY());
					facing = '<';
					m200.execute();
					logger.info("Move < 200: "+me+" facing: "+facing);
					if (me.getX() != l.getX() || me.getY() != l.getY() || facing != '<') {
						throw new RuntimeException("Failed at "+l+" <");
					}
						
					me.setX(l.getX());
					me.setY(l.getY());
					facing = 'v';
					m200.execute();
					logger.info("Move v 200: "+me+" facing: "+facing);
					if (me.getX() != l.getX() || me.getY() != l.getY() || facing != 'v') {
						throw new RuntimeException("Failed at "+l+" v");
					}
				}
			}
		}
		logger.info("All good!");
	}
}
