package net.calebscode.aoc.data;

public enum Direction {
	NORTH,
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST;
	
	Direction clockwise() {
		return Direction.clockwise(this);
	}
	
	Direction counterClockwise() {
		return Direction.counterClockwise(this);
	}
	
	static Direction clockwise(Direction facing) {
		return switch (facing) {
			case NORTH		-> NORTHEAST;
			case NORTHEAST	-> EAST;
			case EAST		-> SOUTHEAST;
			case SOUTHEAST	-> SOUTH;
			case SOUTH		-> SOUTHWEST;
			case SOUTHWEST	-> WEST;
			case WEST		-> NORTHWEST;
			case NORTHWEST	-> NORTH;
		};
	}
	
	static Direction counterClockwise(Direction facing) {
		return switch (facing) {
			case NORTH		-> NORTHWEST;
			case NORTHEAST	-> NORTH;
			case EAST		-> NORTHEAST;
			case SOUTHEAST	-> EAST;
			case SOUTH		-> SOUTHEAST;
			case SOUTHWEST	-> SOUTH;
			case WEST		-> SOUTHWEST;
			case NORTHWEST	-> WEST;
		};
	}
}
