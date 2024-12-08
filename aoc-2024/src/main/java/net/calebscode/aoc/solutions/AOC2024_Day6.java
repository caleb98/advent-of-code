package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Pair;
import net.calebscode.aoc.util.Point2D;

public class AOC2024_Day6 extends Solution<Integer> {

	private QuestionInput input;
	private char[][] map;
	private Point2D start;
	
	private Set<Point2D> originalObstacles = new HashSet<>();
	private List<Pair<Point2D, Facing>> path = new ArrayList<>();
	
	public AOC2024_Day6() {
		input = new QuestionInput("/inputs/day6.txt");
		map = input.asCharArray();
	}
	
	@Override
	public Integer solveFirst() {
		Facing facing = Facing.UP;
		
		for (int x = 0; x < map.length; x++) {			
			for (int y = 0; y < map[x].length; y++) {
				if (map[y][x] == '^') {
					start = new Point2D(x, y);
				}
				else if (map[y][x] == '#') {
					originalObstacles.add(new Point2D(x, y));
				}
			}
		}
		
		var position = start;
		var visited = new HashSet<Point2D>();
		visited.add(position);
		
		while (isInside(position)) {
			path.add(Pair.of(position, facing));
			var move = getNextMoveLocation(position, facing);
			
			if (!isInside(move)) break;
			
			while (isObstruction(move)) {
				facing = getNextFacing(facing);
				move = getNextMoveLocation(position, facing);
			}
			
			position = move;
			visited.add(position);
		}
		
		return visited.size();
	}

	@Override
	public Integer solveSecond() {
		var newObstacles = new HashSet<Point2D>();
		
		for (var step : path) {
			if (!originalObstacles.contains(step.a) && formsLoop(step.a)) {
				newObstacles.add(step.a);
			}
		}
		
		return newObstacles.size();
	}
	
	private boolean formsLoop(Point2D newObstacle) {
		var allObstacles = new HashSet<Point2D>();
		allObstacles.addAll(originalObstacles);
		allObstacles.add(newObstacle);
		
		var position = start;
		var facing = Facing.UP;
		var visited = new HashSet<Pair<Point2D, Facing>>();
		visited.add(Pair.of(position, facing));
		
		while (isInside(position)) {
			var move = getNextMoveLocation(position, facing);
			
			if (!isInside(move)) return false;
			
			while (allObstacles.contains(move)) {
				facing = getNextFacing(facing);
				move = getNextMoveLocation(position, facing);
			}
			
			position = move;
			var newVisit = Pair.of(position, facing);
			if (visited.contains(newVisit)) {
				return true;
			}
			visited.add(newVisit);
		}
		
		return false;
	}
	
	private Point2D getCollisionPoint(Point2D obstacle, Facing collisionFacing) {
		return switch (collisionFacing) {
			case DOWN -> new Point2D(obstacle.getX(), obstacle.getY() - 1);
			case LEFT -> new Point2D(obstacle.getX() + 1, obstacle.getY());
			case RIGHT -> new Point2D(obstacle.getX() - 1, obstacle.getY());
			case UP -> new Point2D(obstacle.getX(), obstacle.getY() + 1);
			default -> throw new IllegalArgumentException("Invalid facing");
		};
	}
	
	private List<Point2D> range(Point2D from, Point2D to) {
		var startX = Math.min(from.getX(), to.getX());
		var endX = Math.max(from.getX(), to.getX());
		var startY = Math.min(from.getY(), to.getY());
		var endY = Math.max(from.getY(), to.getY());
		
		var range = new ArrayList<Point2D>();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				range.add(new Point2D(x, y));
			}
		}
		return range;
	}
	
	private Facing getNextFacing(Facing facing) {
		return switch (facing) {
			case DOWN -> Facing.LEFT;
			case LEFT -> Facing.UP;
			case RIGHT -> Facing.DOWN;
			case UP -> Facing.RIGHT;
			default -> throw new IllegalArgumentException("Invalid facing");
		};
	}
	
	private Point2D getNextMoveLocation(Point2D location, Facing facing) {
		return switch (facing) {
			case DOWN -> new Point2D(location.getX(), location.getY() + 1);
			case LEFT -> new Point2D(location.getX() - 1, location.getY());
			case RIGHT -> new Point2D(location.getX() + 1, location.getY());
			case UP -> new Point2D(location.getX(), location.getY() - 1);
			default -> throw new IllegalArgumentException("Invalid facing");
		};
	}
	
	private boolean isObstruction(Point2D location) {
		return map[location.getY()][location.getX()] == '#';
	}
	
	private boolean isInside(Point2D location) {
		return location.getY() >= 0
			&& location.getY() < map.length
			&& location.getX() >= 0
			&& location.getX() < map[0].length;
	}
	
	enum Facing {
		UP, DOWN, LEFT, RIGHT
	}

}
