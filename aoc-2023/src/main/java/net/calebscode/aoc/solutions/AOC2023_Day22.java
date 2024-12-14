package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.calebscode.aoc.BasicSolution;

public class AOC2023_Day22 extends BasicSolution<Long> {

	private List<Brick> bricks;
	private Map<Point, Brick> brickMap;
	private Map<Brick, Set<Brick>> supportsGraph;		// maps a brick to the set of bricks it supports
	private Map<Brick, Set<Brick>> supportedByGraph;	// maps a brick to the set of bricks it is supported by
	private Set<Brick> canRemove;
	
	public AOC2023_Day22() {
		super(22);
	}
	
	@Override
	public Long solveFirst() {
		bricks = input.getLines().parallelStream()
				.map(this::parseBrick)
				.toList();
		
		iterate(bricks);

		brickMap = new HashMap<Point, Brick>();
		
		for (var brick : bricks) {
			for (var point : brick.points) {
				brickMap.put(point, brick);
			}
		}
		
		supportsGraph = new HashMap<Brick, Set<Brick>>();
		supportedByGraph = new HashMap<Brick, Set<Brick>>();
		
		for (var brick : bricks) {
			for (var belowPoint : brick.getBelow()) {
				if (brickMap.containsKey(belowPoint)) {
					var supportingBrick = brickMap.get(belowPoint);
					if (supportingBrick == brick) continue;
					
					var supportsSet = supportsGraph.computeIfAbsent(supportingBrick, b -> new HashSet<Brick>());
					supportsSet.add(brick);
					
					var supportedBySet = supportedByGraph.computeIfAbsent(brick, b -> new HashSet<Brick>());
					supportedBySet.add(supportingBrick);
				}
			}
		}
		
		canRemove = new HashSet<>();
		Long canRemoveCount = 0L;
		for (var brick : bricks) {
			// Brick supports no other bricks
			if (!supportsGraph.containsKey(brick)) {
				canRemoveCount++;
				continue;
			}
			
			// Check that any bricks this brick supports
			// have alternative bricks supporting them.
			var supports = supportsGraph.get(brick);
			boolean supportsHaveAlternatives = true;
			for (var supportedBrick : supports) {
				if (supportedByGraph.get(supportedBrick).size() <= 1) {
					supportsHaveAlternatives = false;
					break;
				}
			}
			
			if (supportsHaveAlternatives) {
				canRemoveCount++;
				canRemove.add(brick);
			}
		}
		
		return canRemoveCount;
	}

	@Override
	public Long solveSecond() {
		return bricks.stream()
			.filter(brick -> !canRemove.contains(brick)) // Only process bricks we can't remove without causing a fall
			.mapToLong(this::countChainFalling)
			.sum();
	}
	
	private long countChainFalling(Brick remove) {
		var bricksLessRemoved = new HashSet<>(bricks);
		bricksLessRemoved.remove(remove);
		
		var falling = new HashSet<Brick>();
		var safe = new HashSet<Brick>();
		var check = new LinkedList<>(bricksLessRemoved);
		
		falling.add(remove);
		check.addAll(supportsGraph.getOrDefault(remove, Set.of()));
		
		while (check.size() > 0) {
			var current = check.pop();
			var supportedBy = supportedByGraph.get(current);
			
			// Any brick on the floor is safe.
			// Any brick supported by at least one safe brick is safe.
			if (current.isOnFloor() || supportedBy.stream().anyMatch(safe::contains)) {
				safe.add(current);
			}
			// Any brick supported only by falling bricks is falling.
			else if (supportedBy.stream().allMatch(falling::contains)) {
				falling.add(current);
			}
			// At this point we know:
			//   - This brick is not on the floor
			//   - Not all supporting bricks are falling -> there is at least one brick that is safe or still needs checking
			//   - Not a single supporting brick is safe -> eliminates the possibility that a single brick is safe, so we know at least one still needs checking
			// So, lets shift this guy to the back until we have more info.
			else {
				check.add(current);
				continue;
			}
		}
		
		// Subtract 1 to account for the fact that the initially
		// removed brick isn't technically "falling" based on the
		// question description.
		return falling.size() - 1;
	}
	
	List<Brick> copyBricksWithout(List<Brick> bricks, Brick remove) {
		var copy = new ArrayList<Brick>(bricks.parallelStream().map(Brick::copy).toList());
		copy.remove(remove);
		return copy;
	}
	
	int iterate(List<Brick> bricks) {
		int iterations = 0;
		boolean brickMoved = true;
		while (brickMoved) {
			brickMoved = false;
			
			for (var brick : bricks) {
				if (brick.isOnFloor()) continue;
				var canMoveDown = true;
				
				for (var belowPoint : brick.getBelow()) {					
					for (var otherBrick : bricks) {
						if (otherBrick == brick) continue;
						if (otherBrick.points.contains(belowPoint)) {
							canMoveDown = false;
							break;
						}
					}
					
					if (!canMoveDown) break;
				}
				
				if (canMoveDown) {
					brick.moveDown();
					brickMoved = true;
				}
			}
			
			if (brickMoved) iterations++;
		}
		
		return iterations;
	}

	private Brick parseBrick(String data) {
		var pointStrings = data.split("~");
		var startValues = pointStrings[0].split(",");
		var endValues = pointStrings[1].split(",");
		
		var startPoint = new Point(Integer.parseInt(startValues[0]), Integer.parseInt(startValues[1]), Integer.parseInt(startValues[2]));
		var endPoint = new Point(Integer.parseInt(endValues[0]), Integer.parseInt(endValues[1]), Integer.parseInt(endValues[2]));
		
		var points = new ArrayList<Point>();
		
		for (int x = Math.min(startPoint.x, endPoint.x); x <= Math.max(startPoint.x, endPoint.x); x++) {
			for (int y = Math.min(startPoint.y, endPoint.y); y <= Math.max(startPoint.y, endPoint.y); y++) {
				for (int z = Math.min(startPoint.z, endPoint.z); z <= Math.max(startPoint.z, endPoint.z); z++) {
					points.add(new Point(x, y, z));
				}
			}
		}
		
		return new Brick(points);
	}
	
	private static class Point {
		
		int x;
		int y;
		int z;
		
		Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public String toString() {
			return String.format("(%d, %d, %d)", x, y, z);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y, z);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			return x == other.x && y == other.y && z == other.z;
		}
		
	}
	
	private record Brick(List<Point> points) {
		
		boolean isOnFloor() {
			for (var point : points) {
				if (point.z == 1) return true;
			}
			return false;
		}
		
		void moveDown() {
			for (var point : points) {
				point.z -= 1;
			}
		}
		
		List<Point> getBelow() {
			return points.parallelStream()
					.map(point -> new Point(point.x, point.y, point.z - 1))
					.toList();
		}
		
		Brick copy() {
			return new Brick(points.stream().map(p -> new Point(p.x, p.y, p.z)).toList());
		}

	}

}
