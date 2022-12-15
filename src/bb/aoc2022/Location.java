package bb.aoc2022;

import java.util.Objects;

public class Location  {
	int x;
	int y;
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Location(String xy) {
		String[] coords = xy.split(",");
		if (coords.length != 2) {
			throw new IllegalArgumentException("Line doesn't have 2 coordinates: "+coords.length);
		}
		try {
			x = Integer.parseInt(coords[0]);
			y = Integer.parseInt(coords[1]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(ex.toString());
		}
	}
	
	public String toString() {
		return x+","+y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void moveUp(int steps) {
		y = y - steps;
	}
	
	public void moveDown(int steps) {
		y = y + steps;
	}
	
	public void moveLeft(int steps) {
		x = x - steps;
	}
	
	public void moveRight(int steps) {
		x = x + steps;
	}
	
	public boolean isAdjacent(Location loc) {
		if (loc.x >= x - 1 && loc.x <= x + 1 &&
			loc.y >= y - 1 && loc.y <= y + 1) {
			return true;
		}
		return false;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int manhattanDistance(Location l2) {
		return Math.abs(l2.getX() - getX()) + Math.abs(l2.getY() - getY());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(x, y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return x == other.x && y == other.y;
	}
	
}