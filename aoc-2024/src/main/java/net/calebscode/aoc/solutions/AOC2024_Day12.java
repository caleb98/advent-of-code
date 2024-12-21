package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.MapGrid;
import net.calebscode.aoc.data.Pair;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.util.Utils;

public class AOC2024_Day12 extends BasicSolution<Long> {

	private MapGrid<Character> garden;
	private List<Set<Point2D>> plots = new ArrayList<>();
	
	public AOC2024_Day12() {
		super(12);
		garden = input.asCharacterGrid(false, false, (x, y) -> '.');
		
		var allPoints = new HashSet<Point2D>();
		for (int x = 0; x < garden.getWidth(); x++) {
			for (int y = 0; y < garden.getHeight(); y++) {
				allPoints.add(new Point2D(x, y));
			}
		}
		
		plots = Utils.getAdjacentGroups(
			allPoints, 
			point -> point.orthogonallyAdjacent(), 
			(a, b) -> garden.get(a).equals(garden.get(b))
		);
	}
	
	@Override
	public Long solveFirst() {
		return plots.stream()
			.mapToLong(points -> {				
				var area = points.size();
				var perimeter = points.stream()
						.mapToLong(position -> getOpenSides(position, points))
						.sum();
				var letter = garden.get(points.iterator().next());
				System.out.printf("%s -> A=%d | P=%d\n", letter, area, perimeter);
				
				return area * perimeter;
			})
			.sum();
	}

	@Override
	public Long solveSecond() {
		return plots.stream()
				.mapToLong(points -> {				
					var area = points.size();
					var sides = getNumSides(points);
					var letter = garden.get(points.iterator().next());
					System.out.printf("%s -> A=%d | S=%d\n", letter, area, sides);
					
					return area * sides;
				})
				.sum();
	}
	
	private long getOpenSides(Point2D position, Set<Point2D> others) {
		long sum = 0;
		if (!others.contains(position.up())) sum++;
		if (!others.contains(position.down())) sum++;
		if (!others.contains(position.left())) sum++;
		if (!others.contains(position.right())) sum++;
		return sum;
	}
	
	private long getNumSides(Set<Point2D> points) {
		// Find all the top edges. We'll pick a random one
		// and walk around the perimeter to count the number
		// of sides.
		// If there's still unvisited top edges after a full
		// walk, it means our shape has holes. So, keep
		// grabbing unvisited top edges and walking the
		// perimeter until we've visited all of the top
		// edges that we calculated.
		var unvisitedTops = points.parallelStream()
				.map(point -> {
					do {
						point = point.up();
					} while (points.contains(point));
					return point;
				})
				.collect(Collectors.toSet());

		long numSides = 0;
		while (unvisitedTops.size() > 0) {
			// Grab a top edge
			var startPoint = unvisitedTops.iterator().next();
			
			// Walk around
			var facing = Facing.RIGHT; 
			var start = Pair.of(startPoint, facing);
			var current = start.copy();
			
			do {
				// Visit the point
				unvisitedTops.remove(current.first);
				
				// Look forward
				var sightPoints = getSight(current.first, current.second, points);
				var sight = Pair.of(
					points.contains(sightPoints.first),
					points.contains(sightPoints.second)
				);
				
				// .> O>
				// XO XO
				// Empty forward right means outer corner. Move around the corner
				if (!sight.second) {
					current.first = sightPoints.second;
					current.second = Facing.clockwise(current.second);
					numSides++;
				}
				// ^O
				// XO
				// Has no forward but has forward right. Continue
				else if (!sight.first) {
					current.first = sightPoints.first;
				}
				// OO
				// <O
				// Forward and forward right are present, so stay in same place and rotate counter clockwise
				else {
					current.second = Facing.counterClockwise(current.second);
					numSides++;
				}
			} while (!current.equals(start));
		}
		
		return numSides;
	}
	
	private Pair<Point2D, Point2D> getSight(Point2D position, Facing facing, Set<Point2D> points) {
		return switch (facing) {
			case Facing.UP -> Pair.of(position.up(), position.up().right());
			case Facing.RIGHT -> Pair.of(position.right(), position.right().down());
			case Facing.DOWN -> Pair.of(position.down(), position.down().left());
			case Facing.LEFT -> Pair.of(position.left(), position.left().up());
		};
	}

	private static enum Facing {
		UP, DOWN, LEFT, RIGHT;
		
		static Facing clockwise(Facing facing) {
			return switch (facing) {
				case UP -> RIGHT;
				case RIGHT -> DOWN;
				case DOWN -> LEFT;
				case LEFT -> UP;			
			};
		}
		
		static Facing counterClockwise(Facing facing) {
			return switch (facing) {
				case UP -> LEFT;
				case LEFT -> DOWN;			
				case DOWN -> RIGHT;
				case RIGHT -> UP;
			};
		}
	}
	
}
