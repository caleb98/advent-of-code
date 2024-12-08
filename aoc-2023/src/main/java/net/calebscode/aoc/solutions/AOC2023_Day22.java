package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day22 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day22() {
		input = new QuestionInput("/inputs/day22.txt");
	}
	
	@Override
	public Long solveFirst() {
		var bricks = input.getLines().parallelStream()
			.map(this::parseBrick)
			.toList();
		
		iterate(bricks);
		
//		var x = bricks.parallelStream()
//			.map(brick -> iterate(copyBricksWithout(bricks, brick)))
//			.toList();
////			.filter(count -> count == 0)
////			.count();
//		
//		return 0L;
		
		// First attempt
//		boolean brickMoved = true;
//		while (brickMoved) {
//			brickMoved = false;
//			
//			for (var brick : bricks) {
//				if (brick.isOnFloor()) continue;
//				var canMoveDown = true;
//				
//				for (var belowPoint : brick.getBelow()) {					
//					for (var otherBrick : bricks) {
//						if (otherBrick == brick) continue;
//						if (otherBrick.points.contains(belowPoint)) {
//							canMoveDown = false;
//							break;
//						}
//					}
//					
//					if (!canMoveDown) break;
//				}
//				
//				if (canMoveDown) {
//					brick.moveDown();
//					brickMoved = true;
//				}
//			}
//		}
//		
		var brickMap = new HashMap<Point, Brick>();
		
		for (var brick : bricks) {
			for (var point : brick.points) {
				brickMap.put(point, brick);
			}
		}
		
		var supportsGraph = new HashMap<Brick, Set<Brick>>();
		var supportedByGraph = new HashMap<Brick, Set<Brick>>();
		
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
			}
		}
		
		return canRemoveCount;
	}

	@Override
	public Long solveSecond() {
		return -1L;
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
