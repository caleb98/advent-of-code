package net.calebscode.aoc.solutions;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.util.Utils;

public class AOC2023_Day14 extends BasicSolution<Long> {

	public AOC2023_Day14() {
		super(14);
	}

	@Override
	public Long solveFirst() {
		long start = System.currentTimeMillis();

		// Added transposing here to account for breaking change in QuestionInput api
		var chars = input.asCharArray();
		Utils.transpose(chars);
		var dish = new Dish(chars);
		
		var tilted = tilt(0, -1, dish);
		var load = tilted.calculateLoad();

		long end = System.currentTimeMillis();
		System.out.printf("Computation time: %dms\n", end - start);

		return load;
	}

	@Override
	public Long solveSecond() {
		long start = System.currentTimeMillis();

		// Added transposing here to account for breaking change in QuestionInput api
		var chars = input.asCharArray();
		Utils.transpose(chars);
		var dish = new Dish(chars);

		var cycleMap = new HashMap<Dish, Dish>();
		var dishIter = new HashMap<Dish, Integer>();
		var iterDish = new HashMap<Integer, Dish>();
		int cycleStart = -1;
		int cycleEnd = -1;
		int cycleLen = -1;

		for (int i = 0; i < 1_000_000_000; i++) {
			var next = spinCycle(dish);

			dishIter.put(dish, i);
			iterDish.put(i, dish);
			cycleMap.put(dish, next);

			if (cycleMap.containsKey(next)) {
				System.out.println("Found a cycle!");
				cycleStart = dishIter.get(next);
				cycleEnd = dishIter.get(dish);
				cycleLen = cycleEnd - cycleStart + 1;
				System.out.printf(
					"From:\t%d\nTo:\t%d\nLen:\t%d\n",
					cycleStart, cycleEnd, cycleLen
				);
				break;
			}

			dish = next;
		}

		if (cycleLen == -1) {
			throw new IllegalArgumentException("Dish has no cycles or cycle is longer than 1 billion.");
		}

		int billionthDishIndex = (1_000_000_000 - cycleStart) % cycleLen + cycleStart;
		var billionthDish = iterDish.get(billionthDishIndex);

		var load = billionthDish.calculateLoad();
		long end = System.currentTimeMillis();
		System.out.printf("Computation time: %dms\n", end - start);

		return load;
	}

	private Dish spinCycle(Dish dish) {
		dish = tilt( 0, -1, dish);
		dish = tilt(-1,  0, dish);
		dish = tilt( 0,  1, dish);
		dish = tilt( 1,  0, dish);
		return dish;
	}

	private Dish tilt(int dx, int dy, Dish initial) {
		Queue<Point2D> rocks = new LinkedList<>();
		Set<Point2D> impeded = new HashSet<>();

		char[][] copy = new char[initial.layout.size()][];

		// Collect all the rocks first
		for (int i = 0; i < initial.layout.size(); i++) {
			char[] row = new char[initial.layout.get(i).size()];
			copy[i] = row;

			for (int j = 0; j < initial.layout.get(i).size(); j++) {
				row[j] = initial.charAt(j, i);
				if (row[j] == 'O') {
					rocks.add(new Point2D(j, i));
				}
			}
		}

		var result = new Dish(copy);

		// Start the algorithm
		Point2D current;
		while ((current = rocks.poll()) != null) {
			var next = current.translate(dx, dy);
			char block = result.charAt(next.getX(), next.getY());

			// If nothing there, just roll
			if (block == '.') {
				result.setChar(next.getX(), next.getY(), 'O');
				result.setChar(current.getX(), current.getY(), '.');
				rocks.add(new Point2D(next.getX(), next.getY()));
			}
			// If blocker is a wall, stop and impede
			else if (block == '#') {
				impeded.add(current);
			}
			// Blocker is an impeded rock
			else if (block == 'O' && impeded.contains(next)) {
				impeded.add(current);
			}
			// Blocker is an unimpeded rock, so we'll run this rock again later
			else {
				rocks.add(current);
			}
		}

		return result;
	}

	private static class Dish {

		List<List<Character>> layout;

		Dish(char[][] data) {
			layout = new ArrayList<>(data.length);
			for (char[] row : data) {
				layout.add(new ArrayList<>(CharBuffer.wrap(row).chars().mapToObj(i -> (char) i).toList()));
			}
		}

		char charAt(int x, int y) {
			if (y < 0 || y >= layout.size() || x < 0 || x >= layout.get(y).size()) {
				return '#';
			} else {
				return layout.get(y).get(x);
			}
		}

		void setChar(int x, int y, char c) {
			layout.get(y).set(x, c);
		}

		long calculateLoad() {
			long load = 0;
			long rowLoad = layout.size();
			for (var row : layout) {
				long rockCount = 0;
				for (char c : row) {
					if (c == 'O') rockCount++;
				}
				load += rockCount * rowLoad;
				rowLoad--;
			}
			return load;
		}

		void print() {
			for (var row : layout) {
				for (var c : row) {
					System.out.print(c);
				}
				System.out.println();
			}
		}

		@Override
		public int hashCode() {
			return layout.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj == null || !(obj instanceof Dish other)) {
				return false;
			}

			return Objects.equals(layout, other.layout);
		}

	}

	// private static class Dish {

	// 	Cell[][] cells;

	// 	Dish(char[][] data) {
	// 		cells = new Cell[data.length][];
	// 		for (int i = 0; i < data.length; i++) {

	// 		}
	// 	}

	// }

	// private static class Cell {

	// 	char type;
	// 	boolean rolls;

	// 	Cell(char type) {
	// 		this.type = type;
	// 		rolls = (type == 'O');
	// 	}

	// }

}
