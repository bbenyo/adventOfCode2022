package bb.aoc2022;

import java.util.Objects;

public class LocationFacing extends Location {
	
	public enum Direction {UP, DOWN, LEFT, RIGHT};
	
	protected Direction facing;


	public LocationFacing(int x, int y) {
		super(x, y);
		facing = Direction.UP;
	}
	
	public LocationFacing(LocationFacing clone) {
		super(clone.getX(), clone.getY());
		facing = clone.facing;
	}


	public Direction getFacing() {
		return facing;
	}
	
	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	public String toString() {
		return x+","+y+" "+getFacingChar();
	}
	
	public char getFacingChar() {
		switch (facing) {
		case UP: return '^';
		case DOWN: return 'v';
		case LEFT: return '<';
		case RIGHT: return '>';
		default:
			return '?';
		}
	}
	
	// Move forward 1 step.  If we hit the top/left/bottom/right rows:
	//   If wrap is true, we wrap around, if false, we're stuck
	public void forward(Location topLeft, Location bottomRight, boolean wrap) {
		int nx = x;
		int ny = y;
		switch (facing) {
		case DOWN:
			ny ++; 
			if (ny > bottomRight.getY()) {
				if (wrap) {
					ny = topLeft.getY();
				} else {
					ny = y;
				}
			}
			break;
		case LEFT:
			nx --; 
			if (nx < topLeft.getX()) {
				if (wrap) {
					nx = bottomRight.getX();
				} else {
					nx = x;
				}
			}
			break;
		case RIGHT:
			nx ++; 
			if (nx > bottomRight.getX()) {
				if (wrap) {
					nx = topLeft.getX();
				} else {
					nx = x;
				}
			}
			break;
		case UP:
			ny --; 
			if (ny < topLeft.getY()) {
				if (wrap) {
					ny = bottomRight.getY();
				} else {
					ny = y;
				}
			}
			break;
		default:
			break;		
		}
		setX(nx);
		setY(ny);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(facing);
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
		LocationFacing other = (LocationFacing) obj;
		return facing == other.facing;
	}

}
