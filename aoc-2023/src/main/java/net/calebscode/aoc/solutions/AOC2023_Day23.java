package net.calebscode.aoc.solutions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.MapGrid;
import net.calebscode.aoc.data.OrthogonalDirection;
import net.calebscode.aoc.data.Pair;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;

public class AOC2023_Day23 extends BasicSolution<Long> {

	private MapGrid<Character> grid;
	
	public AOC2023_Day23() {
		super(23);
		grid = input.asCharacterGrid(false, false, MapGrid.outOfBounds(), (x, y) -> '#');
	}
	
	@Override
	public Long solveFirst() {
		var pathfinder = new DijkstraPathfinder<Pair<Point2D, OrthogonalDirection>>(
			this::getAdjacent,
			this::getTransitionCost,
			this::isTerminalState
		);
		
		var path = pathfinder.pathfindLongest(List.of(Pair.of(new Point2D(1, 0), OrthogonalDirection.DOWN)));
		var points = path.getPath().stream().map(x -> x.first).collect(Collectors.toSet());
		
		for (int y = 0; y < grid.getHeight(); y++) {
			for (int x = 0; x < grid.getWidth(); x++) {
				if (points.contains(new Point2D(x, y))) {
					System.out.print('O');
				}
				else {
					System.out.print(grid.get(x, y));
				}
			}
			System.out.println();
		}
		
		return (long) path.getPath().size();
	}

	@Override
	public Long solveSecond() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Pair<Point2D, OrthogonalDirection>> getAdjacent(Pair<Point2D, OrthogonalDirection> location) {
		var currentChar = grid.get(location.first);
		var currentPoint = location.first;
		var currentFacing = location.second;
		
		var forward = Pair.of(currentPoint.moveForward(currentFacing), currentFacing);
		var left = Pair.of(currentPoint.moveForward(currentFacing.counterClockwise()), currentFacing.counterClockwise());
		var right = Pair.of(currentPoint.moveForward(currentFacing.clockwise()), currentFacing.clockwise());
		
		if (currentChar.equals('.')) {
			return Stream.of(forward, left, right).filter(loc -> !grid.get(loc.first).equals('#')).toList();
		}
		else if (currentChar.equals('>')) {
			if (currentFacing == OrthogonalDirection.RIGHT) return List.of(forward);
		}
		else if (currentChar.equals('v')) {
			if (currentFacing == OrthogonalDirection.DOWN) return List.of(forward);
		}
		else if (currentChar.equals('<')) {
			if (currentFacing == OrthogonalDirection.LEFT) return List.of(forward);
		}
		else if (currentChar.equals('^')) {
			if (currentFacing == OrthogonalDirection.UP) return List.of(forward);
		}
		
		return List.of();
	}
	
	private int getTransitionCost(Pair<Point2D, OrthogonalDirection> from, Pair<Point2D, OrthogonalDirection> to) {
		return 1;
	}
	
	private boolean isTerminalState(Pair<Point2D, OrthogonalDirection> location) {
		return location.first.getX() == grid.getWidth() - 2
			&& location.first.getY() == grid.getHeight() - 1;
		
	}

}
