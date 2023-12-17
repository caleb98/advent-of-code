package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Point;
import net.calebscode.aoc.util.Triple;

public class AOC2023_Day17 extends Solution<Integer> {

	private QuestionInput input;

	public AOC2023_Day17() {
		input = new QuestionInput("/inputs/day17.txt");
	}

	@Override
	public Integer solveFirst() {
		var layout = input.asCharArray();
		var heatLosses = asInts(layout);

		var width = heatLosses[0].length;
		var height = heatLosses.length;

		Point end = new Point(width - 1, height - 1);

		return shortestPath(
			heatLosses,
			node -> getAdjacentRegular(width, height, node),
			node -> end.equals(node.a)
		);
	}

	@Override
	public Integer solveSecond() {
		var layout = input.asCharArray();
		var heatLosses = asInts(layout);

		var width = heatLosses[0].length;
		var height = heatLosses.length;

		Point end = new Point(width - 1, height - 1);

		return shortestPath(
			heatLosses,
			node -> getAdjacentUltra(width, height, node),
			node -> end.equals(node.a) && node.c >= 4
		);
	}

	private int[][] asInts(char[][] chars) {
		int[][] ints = new int[chars.length][];

		for (int row = 0; row < chars.length; row++) {
			ints[row] = new int[chars[row].length];
			for (int col = 0; col < chars[row].length; col++) {
				ints[row][col] = chars[row][col] - '0';
			}
		}

		return ints;
	}

	private int shortestPath(
		int[][] heatLosses,
		Function<Triple<Point, Direction, Integer>, List<Triple<Point, Direction, Integer>>> adjacencyFunc,
		Predicate<Triple<Point, Direction, Integer>> isTerminal
	) {
		var startNodeA = Triple.of(new Point(1, 0), Direction.RIGHT, 1);
		var startNodeB = Triple.of(new Point(0, 1), Direction.DOWN, 1);

		HashMap<Triple<Point, Direction, Integer>, Integer> totalLosses = new HashMap<>();
		totalLosses.put(startNodeA, heatLosses[0][1]);
		totalLosses.put(startNodeB, heatLosses[1][0]);

		Comparator<Triple<Point, Direction, Integer>> sorter = (a, b) -> {
			var aDist = totalLosses.getOrDefault(a, Integer.MAX_VALUE);
			var bDist = totalLosses.getOrDefault(b, Integer.MAX_VALUE);
			var distCompare = Integer.compare(aDist, bDist);

			if (distCompare != 0) return distCompare;

			return Integer.compare(a.hashCode(), b.hashCode());
		};

		HashMap<Triple<Point, Direction, Integer>, Triple<Point, Direction, Integer>> paths = new HashMap<>();
		Set<Triple<Point, Direction, Integer>> visited = new HashSet<>();
		SortedSet<Triple<Point, Direction, Integer>> unvisited = new TreeSet<>(sorter);
		unvisited.add(startNodeA);
		unvisited.add(startNodeB);

		Triple<Point, Direction, Integer> current;
		while (!unvisited.isEmpty()) {
			current = unvisited.removeFirst();
			int currentLoss = totalLosses.get(current);

			var adjacent = adjacencyFunc.apply(current).stream()
								.filter(n -> !visited.contains(n))
								.toList();

			for (var node : adjacent) {
				int cellLoss = heatLosses[node.a.getY()][node.a.getX()];
				int totalLoss = currentLoss + cellLoss;

				int tentativeLoss = totalLosses.getOrDefault(node, Integer.MAX_VALUE);

				if (totalLoss < tentativeLoss) {
					totalLosses.put(node, totalLoss);
					paths.put(node, current);
				}
			}

			unvisited.removeAll(adjacent);
			unvisited.addAll(adjacent);
			visited.add(current);
		}

		var endpoints = visited.stream()
						.filter(node -> isTerminal.test(node))
						.toList();

		Triple<Point, Direction, Integer> shortestEnd = null;
		int shortest = Integer.MAX_VALUE;
		for (var endpoint : endpoints) {
			int distance = totalLosses.get(endpoint);
			if (shortest > distance) {
				shortest = distance;
				shortestEnd = endpoint;
			}
		}

		var order = new LinkedList<String>();
		var path = shortestEnd;
		while (totalLosses.get(path) != null) {
			order.addFirst(path.b.toString());
			path = paths.get(path);
		}

		for (var dir : order) {
			System.out.println(dir);
		}

		return shortest;
	}

	private List<Triple<Point, Direction, Integer>> getAdjacentRegular(int width, int height, Triple<Point, Direction, Integer> node) {
		var adjacent = new ArrayList<>(switch (node.b) {
			case UP, DOWN -> {
				Triple<Point, Direction, Integer> left = Triple.of(node.a.translate(-1, 0), Direction.LEFT, 1);
				Triple<Point, Direction, Integer> right = Triple.of(node.a.translate(1, 0), Direction.RIGHT, 1);
				yield List.of(left, right);
			}
			case LEFT, RIGHT -> {
				Triple<Point, Direction, Integer> up = Triple.of(node.a.translate(0, -1), Direction.UP, 1);
				Triple<Point, Direction, Integer> down = Triple.of(node.a.translate(0, 1), Direction.DOWN, 1);
				yield List.of(up, down);
			}
		});

		if (node.c < 3) {
			adjacent.add(switch (node.b) {
				case UP -> Triple.of(node.a.translate(0, -1), Direction.UP, node.c + 1);
				case DOWN -> Triple.of(node.a.translate(0, 1), Direction.DOWN, node.c + 1);
				case LEFT -> Triple.of(node.a.translate(-1, 0), Direction.LEFT, node.c + 1);
				case RIGHT -> Triple.of(node.a.translate(1, 0), Direction.RIGHT, node.c + 1);
			});
		}

		return adjacent.stream()
			.filter(n ->
				n.a.getX() >= 0 &&
				n.a.getY() >= 0 &&
				n.a.getX() < width &&
				n.a.getY() < height)
			.toList();
	}

	private List<Triple<Point, Direction, Integer>> getAdjacentUltra(int width, int height, Triple<Point, Direction, Integer> node) {
		List<Triple<Point, Direction, Integer>> adjacent;

		// Ultra crucible must go at least four spaces. If we're not there,
		// then the only option is going forward.
		if (node.c < 4) {
			adjacent = switch (node.b) {
				case UP -> List.of(Triple.of(node.a.translate(0, -1), Direction.UP, node.c + 1));
				case DOWN -> List.of(Triple.of(node.a.translate(0, 1), Direction.DOWN, node.c + 1));
				case LEFT -> List.of(Triple.of(node.a.translate(-1, 0), Direction.LEFT, node.c + 1));
				case RIGHT -> List.of(Triple.of(node.a.translate(1, 0), Direction.RIGHT, node.c + 1));
			};
		}
		// Otherwise, behaves the same as a normal crucible, but with
		// max of 10 consecutive blocks.
		else {
			adjacent = new ArrayList<>(switch (node.b) {
				case UP, DOWN -> {
					Triple<Point, Direction, Integer> left = Triple.of(node.a.translate(-1, 0), Direction.LEFT, 1);
					Triple<Point, Direction, Integer> right = Triple.of(node.a.translate(1, 0), Direction.RIGHT, 1);
					yield List.of(left, right);
				}
				case LEFT, RIGHT -> {
					Triple<Point, Direction, Integer> up = Triple.of(node.a.translate(0, -1), Direction.UP, 1);
					Triple<Point, Direction, Integer> down = Triple.of(node.a.translate(0, 1), Direction.DOWN, 1);
					yield List.of(up, down);
				}
			});

			if (node.c < 10) {
				adjacent.add(switch (node.b) {
					case UP -> Triple.of(node.a.translate(0, -1), Direction.UP, node.c + 1);
					case DOWN -> Triple.of(node.a.translate(0, 1), Direction.DOWN, node.c + 1);
					case LEFT -> Triple.of(node.a.translate(-1, 0), Direction.LEFT, node.c + 1);
					case RIGHT -> Triple.of(node.a.translate(1, 0), Direction.RIGHT, node.c + 1);
				});
			}
		}

		return adjacent.stream()
			.filter(n ->
				n.a.getX() >= 0 &&
				n.a.getY() >= 0 &&
				n.a.getX() < width &&
				n.a.getY() < height)
			.toList();
	}

	private static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

}
