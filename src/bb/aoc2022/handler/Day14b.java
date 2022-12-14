package bb.aoc2022.handler;

import bb.aoc2022.Location;

public class Day14b extends Day14 {

	@Override
	protected void constructGrid() {
		maxY += 2;
		// Give a longer width, see if we can just give a bigger number rather than handling -Infinity
		minX -= 500;
		maxX += 500;
		
		RockPath floor = new RockPath();
		Location l1 = new Location(minX, maxY);
		Location l2 = new Location(maxX, maxY);
		floor.path.add(l1);
		floor.path.add(l2);
		rockPaths.add(floor);

		super.constructGrid();
	}
	
	@Override
	protected boolean inAbyss(Location loc) {
		Location sandLoc = getGridLoc(500,0);
		// Also if the sand input loc is filled, we're done
		if (loc.getX() == sandLoc.getX() && loc.getY() == sandLoc.getY()) {
			return true;
		}
		return super.inAbyss(loc);
	}
}
