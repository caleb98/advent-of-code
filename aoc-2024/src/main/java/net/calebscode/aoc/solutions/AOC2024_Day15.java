package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Grid;
import net.calebscode.aoc.data.OrthogonalDirection;
import net.calebscode.aoc.geometry.Point2D;

public class AOC2024_Day15 extends BasicSolution<Long> {
	
	public AOC2024_Day15() {
		super(15);

	}

	@Override
	public Long solveFirst() {
		var data = input.getLinesSplitByBlank();
		
		var movements = data.get(1).stream().collect(Collectors.joining());

		var roomData = data.get(0).stream()
				.map(line -> (List<Character>) new ArrayList<>(line.chars().mapToObj(c -> Character.valueOf((char) c)).toList()))
				.toList();
		
		var columns = new ArrayList<List<Character>>();
		for (int row = 0; row < roomData.size(); row++) {
			for (int col = 0; col < roomData.get(row).size(); col++) {
				while (columns.size() <= col) columns.add(new ArrayList<Character>());
				columns.get(col).add(roomData.get(row).get(col));
			}
		}
		
		roomData = columns;
		
		var baseRoom = new Grid<>(roomData, false);
		var room = baseRoom.mapElements(c -> switch (c) {
			case 'O', '@' -> '.';
			default -> c;
		});
		
		var boxes = baseRoom.getAllPointsWhere(c -> c == 'O');
		var robot = baseRoom.getAllPointsWhere(c -> c == '@').iterator().next();
		
		System.out.println("Begin:");
		printRoom(robot, boxes, room);
		System.out.println();
		
		doMovements(robot, boxes, movements, room);
		
		System.out.println("\nEnd:");
		printRoom(robot, boxes, room);
		System.out.println();
		
		return boxes.parallelStream()
			.mapToLong(box -> box.getX() + box.getY() * 100)
			.sum();
	}

	@Override
	public Long solveSecond() {
		var data = input.getLinesSplitByBlank();
		
		var movements = data.get(1).stream().collect(Collectors.joining());

		var roomData = data.get(0).stream()
				.map(line -> line.chars()
					.mapToObj(c -> (char) c)
					.flatMap(c -> switch (c) {
						case '#' -> Stream.of('#', '#'); 
						case 'O' -> Stream.of('[', ']');
						case '.' -> Stream.of('.', '.');
						case '@' -> Stream.of('@', '.');
						default -> throw new IllegalArgumentException("Unexpected value: " + c);
					})
					.toList()
				)
				.toList();
		
		var columns = new ArrayList<List<Character>>();
		for (int row = 0; row < roomData.size(); row++) {
			for (int col = 0; col < roomData.get(row).size(); col++) {
				while (columns.size() <= col) columns.add(new ArrayList<Character>());
				columns.get(col).add(roomData.get(row).get(col));
			}
		}
		
		roomData = columns;
		
		var baseRoom = new Grid<>(roomData, false);
		var room = baseRoom.mapElements(c -> switch (c) {
			case '[', ']', '@' -> '.';
			default -> c;
		});
		
		var boxes = baseRoom.getAllPointsWhere(c -> c == '[');
		var robot = baseRoom.getAllPointsWhere(c -> c == '@').iterator().next();
		
		System.out.println("Begin:");
		printRoomDoubled(robot, boxes, room);
		System.out.println();
		
		doMovementsDoubled(robot, boxes, movements, room);
		
		System.out.println("\nEnd:");
		printRoomDoubled(robot, boxes, room);
		System.out.println();
		
		return boxes.parallelStream()
				.mapToLong(box -> box.getX() + box.getY() * 100)
				.sum();
	}
	
	private static void printRoom(Point2D robot, Set<Point2D> boxes, Grid<Character> layout) {
		for (int y = 0; y < layout.getHeight(); y++) {
			for (int x = 0; x < layout.getWidth(); x++) {
				var point = new Point2D(x, y);
				
				if (robot.equals(point)) {
					System.out.print('@');
				}
				else if (boxes.contains(point)) {
					System.out.print('O');
				}
				else {
					System.out.print(layout.get(point));
				}
			}
			System.out.println();
		}
	}
	
	private static void printRoomDoubled(Point2D robot, Set<Point2D> boxes, Grid<Character> layout) {
		for (int y = 0; y < layout.getHeight(); y++) {
			for (int x = 0; x < layout.getWidth(); x++) {
				var point = new Point2D(x, y);
				
				if (robot.equals(point)) {
					System.out.print('@');
				}
				else if (boxes.contains(point)) {
					System.out.print('[');
				}
				else if (boxes.contains(point.left())) {
					System.out.print(']');
				}
				else {
					System.out.print(layout.get(point));
				}
			}
			System.out.println();
		}
	}
	
	private void doMovements(Point2D robot, Set<Point2D> boxes, String moves, Grid<Character> room) {
		for (char move : moves.toCharArray()) {
			var moveDir = switch (move) {
				case '^' -> OrthogonalDirection.UP;
				case '>' -> OrthogonalDirection.RIGHT;
				case '<' -> OrthogonalDirection.LEFT;
				case 'v' -> OrthogonalDirection.DOWN;
				default -> throw new IllegalArgumentException("Unexpected value: " + move);
			};
			
			var nextPos = robot.moveForward(moveDir);
			
			// Can't move into a wall.
			if (room.get(nextPos) == '#') continue;
			
			// If we're moving into a box, see if we can move the boxes.
			if (boxes.contains(nextPos)) {
				var ray = nextPos;
				while (boxes.contains(ray)) ray = ray.moveForward(moveDir);
				
				// If our line of boxes ends in a wall, we can't move
				if (room.get(ray) == '#') continue;
				
				// Otherwise, we can move the line of boxes
				// (equivalent to moving the first one to the back)
				boxes.remove(nextPos);
				boxes.add(ray);
			}
			
			// Safe to move if we get here because we know it's not a wall
			// and any box we will hit can be moved.
			robot.setX(nextPos.getX());
			robot.setY(nextPos.getY());
		}
	}
	
	/**
	 * This method runs for the doubled room size. The points here represent the left
	 * piece ('[') of each box.
	 */
	private void doMovementsDoubled(Point2D robot, Set<Point2D> boxes, String moves, Grid<Character> room) {
		for (char move : moves.toCharArray()) {
			var moveDir = switch (move) {
				case '^' -> OrthogonalDirection.UP;
				case '>' -> OrthogonalDirection.RIGHT;
				case '<' -> OrthogonalDirection.LEFT;
				case 'v' -> OrthogonalDirection.DOWN;
				default -> throw new IllegalArgumentException("Unexpected value: " + move);
			};
			
			var nextPos = robot.moveForward(moveDir);
			
			// Can't move into a wall.
			if (room.get(nextPos) == '#') continue;
			
			// Horizontal Movement
			if (moveDir == OrthogonalDirection.LEFT || moveDir == OrthogonalDirection.RIGHT) {
				var check = nextPos;
				
				// Moving left, we can only collide into the right side of a box,
				// so we'll need to check on more over
				if (moveDir == OrthogonalDirection.LEFT) {
					check = check.moveForward(moveDir);
				}
				
				var movingBoxes = new HashSet<Point2D>();
				
				// Keep moving until we don't hit a box, keeping track of the boxes we do hit
				while (boxes.contains(check)) {
					movingBoxes.add(check);
					check = check.moveForward(moveDir).moveForward(moveDir);
				}
				
				// Move back one since we'll have jumped past the last box
				if (moveDir == OrthogonalDirection.LEFT) {
					check = check.moveBackward(moveDir);
				}
				
				// The other side of the box line is a wall, so we can't move and push.
				if (room.get(check) == '#') continue;
				
				// Move any boxes
				boxes.removeAll(movingBoxes);
				boxes.addAll(movingBoxes.stream().map(box -> box.moveForward(moveDir)).toList());
				
				// Move robot
				robot.setX(nextPos.getX());
				robot.setY(nextPos.getY());
			}
			// Vertical movement
			else {
				var check = new HashSet<Point2D>();
				check.add(nextPos);			// Moving into the left side of a box
				check.add(nextPos.left());	// Moving into the right side of a box
				
				var movingBoxes = new HashSet<Point2D>();
				while (check.size() > 0) {
					var current = check.iterator().next();
					check.remove(current);
					
					// If we hit a box, we then need to check for boxes which
					// that box would then collide with.
					if (boxes.contains(current)) {
						movingBoxes.add(current);
						var moveInto = current.moveForward(moveDir);
						var moveInfoLeft = moveInto.left();
						var moveIntoRight = moveInto.right();
						
						check.add(moveInto);
						check.add(moveInfoLeft);
						check.add(moveIntoRight);
					}
				}
				
				// See if any moving boxes would move into a wall
				var hitWall = false;
				for (var box : movingBoxes) {
					var nextLeft = box.moveForward(moveDir);
					var nextRight = nextLeft.right();
					
					if (room.get(nextLeft) == '#' || room.get(nextRight) == '#') {
						hitWall = true;
						break;
					}
				}
				
				// If we hit a wall, we can't move
				if (hitWall) continue;
				
				// Move any boxes
				boxes.removeAll(movingBoxes);
				boxes.addAll(movingBoxes.stream().map(box -> box.moveForward(moveDir)).toList());
				
				// Move robot
				robot.setX(nextPos.getX());
				robot.setY(nextPos.getY());
			}
			
//			System.out.println();
//			printRoomDoubled(robot, boxes, room);
		}
	}

}
