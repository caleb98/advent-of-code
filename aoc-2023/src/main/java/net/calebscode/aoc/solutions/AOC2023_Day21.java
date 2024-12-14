package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.geometry.Point2D;

public class AOC2023_Day21 extends BasicSolution<Long> {

	public AOC2023_Day21() {
		super(21);
	}

	@Override
	public Long solveFirst() {
		var map = input.asCharArray();
		var start = getStart(map);

		var currentPoints = new HashSet<Point2D>();
		currentPoints.add(start);

		for (int i = 0; i < 64; i++) {
			// printSpots(map, currentPoints);

			var nextPoints = new HashSet<Point2D>();
			for (var point : currentPoints) {
				nextPoints.addAll(getOpenAdjacent(map, point));
			}
			currentPoints = nextPoints;
		}
		// printSpots(map, currentPoints);

		return (long) currentPoints.size();
	}

	@Override
	public Long solveSecond() {
		var map = input.asCharArray();
		var start = getStart(map);

		System.out.printf("%dx%d\n", map.length, map[0].length);

		var currentPoints = new HashSet<Point2D>();
		currentPoints.add(start);

		var actual = new ArrayList<Long>();
		var expected = new ArrayList<Long>();
		var d1 = new ArrayList<Long>();

		for (int i = 0; i < 64 * 10; i++) {
			actual.add((long) currentPoints.size());
			expected.add((long) Math.pow(i + 1, 2));
			d1.add((long) (currentPoints.size() - Math.pow(i + 1, 2)));

			var nextPoints = new HashSet<Point2D>();
			for (var point : currentPoints) {
				nextPoints.addAll(getOpenAdjacentLooping(map, point));
			}
			currentPoints = nextPoints;
		}

		var d2 = new ArrayList<Long>();
		var d3 = new ArrayList<Long>();
		var d4 = new ArrayList<Long>();
		var d5 = new ArrayList<Long>();

		for (int i = 1; i < d1.size(); i++) {
			d2.add(d1.get(i) - d1.get(i - 1));
		}

		for (int i = 1; i < d2.size(); i++) {
			d3.add(d2.get(i) - d2.get(i - 1));
		}

		for (int i = 1; i < d3.size(); i++) {
			d4.add(d3.get(i) - d3.get(i - 1));
		}

		for (int i = 1; i < d4.size(); i++) {
			d5.add(d4.get(i) - d4.get(i - 1));
		}

		System.out.printf("%3s %10s %10s %10s %10s %10s %10s %10s\n", "s", "actual", "expected", "d1", "d2", "d3", "d4", "d5");
		for (int i = 0; i < d1.size(); i++) {
			var delta1 = d1.get(i);
			var delta2 = i - 1 >= 0 ? d2.get(i - 1) : -1;
			var delta3 = i - 2 >= 0 ? d3.get(i - 2) : -1;
			var delta4 = i - 3 >= 0 ? d4.get(i - 3) : -1;
			var delta5 = i - 4 >= 0 ? d5.get(i - 4) : -1;

			System.out.printf("%3d %10d %10d %10d %10d %10d %10d %10d\n", i, actual.get(i), expected.get(i), delta1, delta2, delta3, delta4, delta5);
		}

		System.out.println("Actual: " + actual);
		System.out.println();
		System.out.println("Expected: " +expected);
		System.out.println();
		System.out.println("d1: " + d1);
		System.out.println();
		System.out.println("d2: " + d2);
		System.out.println();
		System.out.println("d3: " + d3);
		System.out.println();
		System.out.println("d4: " + d4);
		System.out.println();
		System.out.println("d5: " + d5);

		System.out.printf("%3s %10s %10s %10s %10s %10s %10s %10s\n", "s", "actual", "expected", "d1", "d2", "d3", "d4", "d5");
		for (int i = 65; i < d1.size(); i += 131) {
			var delta1 = d1.get(i);
			var delta2 = i - 1 >= 0 ? d2.get(i - 1) : -1;
			var delta3 = i - 2 >= 0 ? d3.get(i - 2) : -1;
			var delta4 = i - 3 >= 0 ? d4.get(i - 3) : -1;
			var delta5 = i - 4 >= 0 ? d5.get(i - 4) : -1;

			System.out.printf("%3d %10d %10d %10d %10d %10d %10d %10d\n", i, actual.get(i), expected.get(i), delta1, delta2, delta3, delta4, delta5);
		}

		// There was some crazy thing with a polynomial here. This code
		// doesn't fully solve the problem but I can't remember what
		// all the next steps were...
		return (long) currentPoints.size();
	}

	private void printSpots(char[][] map, Set<Point2D> current) {
		System.out.println("=========================");
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map.length; col++) {
				System.out.print(
					current.contains(new Point2D(col, row)) ?
						'O' : map[row][col]
				);
			}
			System.out.println();
		}
	}

	private List<Point2D> getOpenAdjacent(char[][] map, Point2D point) {
		var north = point.translate(0, -1);
		var south = point.translate(0, 1);
		var east = point.translate(1, 0);
		var west = point.translate(-1, 0);

		return List.of(north, south, east, west).stream()
			.filter(p -> charAt(map, p) != '#')
			.toList();
	}

	private List<Point2D> getOpenAdjacentLooping(char[][] map, Point2D point) {
		var north = point.translate(0, -1);
		var south = point.translate(0, 1);
		var east = point.translate(1, 0);
		var west = point.translate(-1, 0);

		return List.of(north, south, east, west).stream()
			.filter(p -> charAtLooping(map, p) != '#')
			.toList();
	}

	private char charAtLooping(char[][] map, Point2D point) {
		int row = modulus(point.getY(), map.length);
		int col = modulus(point.getX(), map[row].length);

		return map[row][col];
	}

	private int modulus(int a, int b) {
		return (((a % b) + b) % b);
	}

	private char charAt(char[][] map, Point2D point) {
		if (point.getX() < 0
		|| point.getY() < 0
		|| point.getY() >= map.length
		|| point.getX() >= map[point.getY()].length) {
			return '#';
		}

		return map[point.getY()][point.getX()];
	}

	private Point2D getStart(char[][] map) {
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				if (map[row][col] == 'S') {
					return new Point2D(col, row);
				}
			}
		}
		throw new IllegalArgumentException("No starting point found.");
	}

}
