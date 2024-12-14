package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.geometry.Point2D;

public class AOC2024_Day14 extends BasicSolution<Long> {

	private static final Pattern ROBOT_DEFN = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
	
	private static final int ROOM_WIDTH = 101;
	private static final int WIDTH_MIDDLE = ROOM_WIDTH / 2;
	private static final int ROOM_HEIGHT = 103;
	private static final int HEIGHT_MIDDLE = ROOM_HEIGHT / 2;
	
	private List<Robot> robots;
	
	public AOC2024_Day14() {
		super(14);
		
		robots = input.getLines().parallelStream()
			.map(line -> {
				var matcher = ROBOT_DEFN.matcher(line);
				matcher.matches();
				var pos = new Point2D(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
				var vel = new Point2D(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
				return new Robot(pos, vel);
			})
			.toList();
	}
	
	@Override
	public Long solveFirst() {
		var resultQuadrants = robots.parallelStream()
			.map(robot -> robot.move(100))
			.collect(Collectors.groupingBy(Robot::position));
		
		long topLeft = 0;
		long topRight = 0;
		long botLeft = 0;
		long botRight = 0;
		
		for (var entry : resultQuadrants.entrySet()) {
			var pos = entry.getKey();
			if (pos.getX() < WIDTH_MIDDLE) {
				
				if (pos.getY() < HEIGHT_MIDDLE) {
					topLeft += entry.getValue().size();
				}
				else if (pos.getY() > HEIGHT_MIDDLE) {
					botLeft += entry.getValue().size();
				}
				
			}
			else if (pos.getX() > WIDTH_MIDDLE) {
				
				if (pos.getY() < HEIGHT_MIDDLE) {
					topRight += entry.getValue().size();
				}
				else if (pos.getY() > HEIGHT_MIDDLE) {
					botRight += entry.getValue().size();
				}
				
			}
		}
		
		return topLeft * topRight * botLeft * botRight;
	}

	@Override
	public Long solveSecond() {
		long elapsed = 0;
		List<Robot> bots = new ArrayList<Robot>(robots);
		for (int i = 0; i < 5; i++) {
			while (hasOverlappingRobots(bots)) {
				elapsed++;
				bots = bots.parallelStream().map(robot -> robot.move(1)).toList();
			}
			System.out.printf("After %d seconds:\n", elapsed);
			printBots(bots);
			
			elapsed++;
			bots = bots.parallelStream().map(robot -> robot.move(1)).toList();
		}
		// Check the console output and find the first instance of a christmas tree
		// This one felt a bit... eh
		return -1L;
	}
	
	private void iterate(List<Robot> bots) {
		List<Robot> iterateBots = new ArrayList<>(bots);
		for (int i = 0; i < 100000; i++) {
			iterateBots = iterateBots.parallelStream().map(robot -> robot.move(1)).toList();
			printBots(iterateBots);
		}
	}
	
	private boolean hasOverlappingRobots(List<Robot> bots) {
		var maxInSpace = bots.parallelStream()
			.collect(Collectors.groupingBy(Robot::position))
			.values()
			.stream()
			.mapToLong(List::size)
			.max();
		
		return maxInSpace.getAsLong() > 1;
	}
	
	private void printBots(List<Robot> bots) {
		var botPositions = bots.parallelStream().collect(Collectors.groupingBy(Robot::position));
		
		System.out.println();
		for (int y = 0; y < ROOM_HEIGHT; y++) {
			for (int x = 0; x < ROOM_WIDTH; x++) {					
				var point = new Point2D(x, y);
				if (botPositions.containsKey(point)) {
					System.out.print(botPositions.get(point).size());
				}
				else {
					System.out.print('.');
				}
			}
			System.out.println();
		}
	}
	
	private record Robot(Point2D position, Point2D velocity) {
		
		Robot move(int seconds) {
			var movement = new Point2D(velocity.getX() * seconds, velocity.getY() * seconds);
			var newPosition = position.translate(movement);
			newPosition.setX(Math.floorMod(newPosition.getX(), ROOM_WIDTH));
			newPosition.setY(Math.floorMod(newPosition.getY(), ROOM_HEIGHT));
			return new Robot(newPosition, new Point2D(velocity.getX(), velocity.getY()));
		}
		
	}

}
