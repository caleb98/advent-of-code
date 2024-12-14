package net.calebscode.aoc.data;

public enum OrthogonalDirection {
	UP, DOWN, LEFT, RIGHT;
	
	public OrthogonalDirection clockwise() {
		return OrthogonalDirection.clockwise(this);
	}
	
	public OrthogonalDirection counterClockwise() {
		return OrthogonalDirection.counterClockwise(this);
	}
	
	static OrthogonalDirection clockwise(OrthogonalDirection facing) {
		return switch (facing) {
			case UP -> RIGHT;
			case RIGHT -> DOWN;
			case DOWN -> LEFT;
			case LEFT -> UP;			
		};
	}
	
	static OrthogonalDirection counterClockwise(OrthogonalDirection facing) {
		return switch (facing) {
			case UP -> LEFT;
			case LEFT -> DOWN;			
			case DOWN -> RIGHT;
			case RIGHT -> UP;
		};
	}
}
