package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Grid;
import net.calebscode.aoc.data.MapGrid;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder.DijkstraPath;

public class AOC2024_Day20 extends BasicSolution<Long> {

	public AOC2024_Day20() {
		super(20);
	}
	
	@Override
	public Long solveFirst() {
		var racetrack = new MapGrid<>(input.asCharacterArray(), false, (x, y) -> '#');
		var startLocations = racetrack.getPointsWhere(c -> c == 'S');
		
		var pathfinder = new DijkstraPathfinder<Point2D>(
			node -> List.of(node.up(), node.down(), node.left(), node.right()).stream().filter(pos -> racetrack.get(pos) != '#').toList(),
			(from, to) -> 1,
			node -> racetrack.get(node) == 'E' 
		);
		
		var pathWithoutSkips = pathfinder.pathfind(startLocations);
		var timeWithoutSkips = pathWithoutSkips.getTotalCost();
		
		var skips = racetrack.getPointsWhere((x, y, v) -> {
			if (v == '.') return false;
			
			boolean isHorizontalSkip = racetrack.get(x - 1, y) != '#' && racetrack.get(x + 1, y) != '#';
			boolean isVerticalSkip = racetrack.get(x, y - 1) != '#' && racetrack.get(x, y + 1) != '#';
			
			return isHorizontalSkip || isVerticalSkip;
		});
		
		System.out.println("Time without skips: " + timeWithoutSkips);
		
		var skipPaths = skips.parallelStream()
			.map(skip -> getPathWithSkip(racetrack, startLocations, skip))
			.collect(Collectors.groupingBy(path -> timeWithoutSkips - path.getTotalCost()));
		
		long savedOneHundred = 0;
		var timesSaved = new ArrayList<>(skipPaths.keySet());
		Collections.sort(timesSaved);
		for (var timeSaved : timesSaved) {
			var numPaths = skipPaths.get(timeSaved).size();
			System.out.printf("%3d skips that save %3d picoseconds.\n", numPaths, timeSaved);
			
			if (timeSaved >= 100) {
				savedOneHundred += numPaths;
			}
		}
		
		System.out.println("---");
		System.out.printf("%3d skips saving at least 100 picoseconds.\n", savedOneHundred);
		
		return savedOneHundred;
	}

	@Override
	public Long solveSecond() {
		var racetrack = new MapGrid<>(input.asCharacterArray(), false, (x, y) -> '#');
		var startLocations = racetrack.getPointsWhere(c -> c == 'S');
		var goal = racetrack.getPointsWhere(c -> c == 'E').iterator().next();
		
		var pathfinder = new DijkstraPathfinder<Point2D>(
			node -> List.of(node.up(), node.down(), node.left(), node.right()).stream().filter(pos -> racetrack.get(pos) != '#').toList(),
			(from, to) -> 1,
			node -> racetrack.get(node) == 'E' 
		);
		
		var pathWithoutSkips = pathfinder.pathfind(startLocations);
		var timeWithoutSkips = pathWithoutSkips.getTotalCost();
		
		
		var allPaths = new ArrayList<SkipState>();
		var visited = new HashSet<SkipState>();
		var visitQueue = new LinkedList<SkipState>();	
		
		var startStates = startLocations.stream().map(pos -> new SkipState(pos, 20, false, Set.of())).toList();
		visited.addAll(startStates);
		visitQueue.addAll(startStates);
		
		while (!visitQueue.isEmpty()) {
			System.out.println("To visit: " + visitQueue.size());
			var current = visitQueue.poll();
			
			// Found end, log it!
			if (racetrack.get(current.pos) == 'E') {
				allPaths.add(current);
				continue;
			}
			
			var adjacent = getAdjacent(racetrack, current, goal, timeWithoutSkips - 50);
			for (var state : adjacent) {
				if (!visited.contains(state)) {
					visited.add(state);
					visitQueue.add(state);
				}
			}
		}
		
		var groupedAllPaths = allPaths.parallelStream()
				.filter(state -> state.visited.size() < timeWithoutSkips)
				.collect(Collectors.groupingBy(state -> state.visited.size()));
		
		var times = new ArrayList<>(groupedAllPaths.keySet());
		Collections.sort(times, Comparator.reverseOrder());
		for (var time : times) {
			var numPaths = groupedAllPaths.get(time).size();
			System.out.printf("%3d skips that save %3d picoseconds.\n", numPaths, timeWithoutSkips - time);
		}
		
		return -1L;
	}
	
	private Set<SkipState> getAdjacent(Grid<Character> racetrack, SkipState current, Point2D goal, int maxLength) {
		var nextVisited = new HashSet<>(current.visited);
		nextVisited.add(current.pos);
		
		// End search, we can't make it to the goal fast enough
		if (current.visited.size() >= maxLength) {
			return Set.of();
		}
		
		// No possible way to get to the goal in max length
		if (current.pos.manhattanDistance(goal) + current.visited.size() > maxLength) {
			return Set.of();
		}
		
		// Not skipping but in a wall. This is a terminal state
		if (racetrack.get(current.pos) == '#' && !current.skipping) {
			return Set.of();
		}
		
		if (current.skipping) {
			if (current.skipsLeft == 0) {
				System.out.println("Tremendous error.");
			}
			
			// Last picosecond of a skip, so we can only move back into a path
			if (current.skipsLeft == 1) {
				return current.pos.orthogonallyAdjacent().stream()
						.filter(pos -> racetrack.isInside(pos))
						.filter(pos -> !nextVisited.contains(pos))
						.filter(pos -> racetrack.get(pos) != '#')
						.map(pos -> new SkipState(pos, 0, false, nextVisited))
						.collect(Collectors.toSet());
			}
			// Otherwise, we can continue to skip
			else {
				return current.pos.orthogonallyAdjacent().stream()
						.filter(pos -> racetrack.isInside(pos))
						.filter(pos -> !nextVisited.contains(pos))
						.map(pos -> new SkipState(pos, current.skipsLeft - 1, true, nextVisited))
						.collect(Collectors.toSet());
			}
			
		}
		// We have skips left and we're not in a wall, which means we haven't started any skip yet
		else if (current.skipsLeft > 0) {
			return current.pos.orthogonallyAdjacent().stream()
					.filter(pos -> racetrack.isInside(pos))
					.filter(pos -> !nextVisited.contains(pos))
					.map(pos -> racetrack.get(pos) == '#' ? // If moving into a wall, then we're starting the skip and need to set the next states skipsLeft correctly
							new SkipState(pos, current.skipsLeft - 1, current.skipsLeft - 1 > 0, nextVisited) :
							new SkipState(pos, current.skipsLeft, false, nextVisited))
					.collect(Collectors.toSet());
		}
		// Not in a wall and no more skips left. We have to follow the path only now
		else {
			return current.pos.orthogonallyAdjacent().stream()
					.filter(pos -> racetrack.isInside(pos))
					.filter(pos -> !nextVisited.contains(pos))
					.filter(pos -> racetrack.get(pos) != '#')
					.map(pos -> new SkipState(pos, 0, false, nextVisited))
					.collect(Collectors.toSet());
		}
	}

	
	private DijkstraPath getPathWithSkip(Grid<Character> racetrack, Set<Point2D> startLocations, Point2D skip) {
		var pathfinder = new DijkstraPathfinder<Point2D>(
			node -> List.of(node.up(), node.down(), node.left(), node.right()).stream()
						.filter(pos -> pos.equals(skip) || racetrack.get(pos) != '#').toList(),
			(from, to) -> 1,
			node -> racetrack.get(node) == 'E' 
		);
		
		return pathfinder.pathfind(startLocations);
	}
	
	private record SkipState(Point2D pos, int skipsLeft, boolean skipping, Set<Point2D> visited) {}

}
