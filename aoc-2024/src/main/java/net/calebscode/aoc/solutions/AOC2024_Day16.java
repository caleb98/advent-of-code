package net.calebscode.aoc.solutions;

import java.util.HashSet;
import java.util.List;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.MapGrid;
import net.calebscode.aoc.data.OrthogonalDirection;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;

public class AOC2024_Day16 extends BasicSolution<Long> {

	public AOC2024_Day16() {
		super(16);
	}
	
	@Override
	public Long solveFirst() {
		MapGrid<Character> maze = input.asCharacterGrid(false, false);
		
		var pathfinder = new DijkstraPathfinder<State>(
			state -> getNextStates(state, maze),
			this::getTransitionCost,
			state -> isTerminalState(state, maze)
		);
		
		var startNodes = maze.getPointsWhere(c -> c == 'S').stream().map(pos -> new State(pos, OrthogonalDirection.RIGHT)).toList();
		var result = pathfinder.pathfind(startNodes);
		
		return (long) result.getTotalCost();
	}

	@Override
	public Long solveSecond() {
		MapGrid<Character> maze = input.asCharacterGrid(false, false);
		
		var pathfinder = new DijkstraPathfinder<State>(
			state -> getNextStates(state, maze),
			this::getTransitionCost,
			state -> isTerminalState(state, maze)
		);
		
		var startNodes = maze.getPointsWhere(c -> c == 'S').stream().map(pos -> new State(pos, OrthogonalDirection.RIGHT)).toList();
		var allPaths = pathfinder.pathfindAllBest(startNodes);
		
		var pointsOnBestPaths = new HashSet<Point2D>();
		
		for (var path : allPaths) {
			var points = path.getPath().stream().map(State::pos).toList();
			pointsOnBestPaths.addAll(points);
		}
		
		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				var point = new Point2D(x, y);
				if (pointsOnBestPaths.contains(point)) {
					System.out.print('O');
				}
				else {
					System.out.print(maze.get(point));
				}
			}
			System.out.println();
		}
		
		return (long) pointsOnBestPaths.size();
	}
	
	private List<State> getNextStates(State current, MapGrid<Character> maze) {
		var turnLeft = new State(current.pos, current.facing.counterClockwise());
		var turnRight = new State(current.pos, current.facing.clockwise());
		var forward = new State(current.pos.moveForward(current.facing), current.facing);
		
		if (maze.get(forward.pos) == '#') {
			return List.of(turnLeft, turnRight);
		}
		else {
			return List.of(turnLeft, turnRight, forward);
		}
	}
	
	private int getTransitionCost(State current, State next) {
		return current.facing == next.facing ? 1 : 1000;
	}
	
	private boolean isTerminalState(State state, MapGrid<Character> maze) {
		return maze.get(state.pos) == 'E';
	}
	
	private static record State(Point2D pos, OrthogonalDirection facing) {}

}
