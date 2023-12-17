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
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;
import net.calebscode.aoc.util.Point;
import net.calebscode.aoc.util.Triple;

public class AOC2023_Day17 extends Solution<Integer> {

	private final QuestionInput input;
	private final int[][] heatLosses;
	private final int mapWidth;
	private final int mapHeight;
	private final Point endpoint;
	private final List<Triple<Point, Direction, Integer>> startNodes;

	public AOC2023_Day17() {
		input = new QuestionInput("/inputs/day17.txt");
		heatLosses = asInts(input.asCharArray());
		mapWidth = heatLosses[0].length;
		mapHeight = heatLosses.length;
		endpoint = new Point(mapWidth - 1, mapHeight -  1);

		startNodes = List.of(
			Triple.of(new Point(0, 0), Direction.RIGHT, 0),
			Triple.of(new Point(0, 0), Direction.DOWN, 0)
		);
	}

	@Override
	public Integer solveFirst() {
		var pathfinder = new DijkstraPathfinder<Triple<Point, Direction, Integer>>(
			this::getAdjacentRegularCrucible,
			this::getTransitionCost,
			this::isTerminalRegularCrucible
		);

		// Run the pathfinding
		var path = pathfinder.pathfind(startNodes);
		return path.getTotalCost();
	}

	@Override
	public Integer solveSecond() {
		// Same process as before, but use pathfinder with updated adjacency
		// function and terminal node filter.
		var pathfinder = new DijkstraPathfinder<Triple<Point, Direction, Integer>>(
			this::getAdjacentUltraCrucible,
			this::getTransitionCost,
			this::isTerminalUltraCrucible
		);

		var path = pathfinder.pathfind(startNodes);
		return path.getTotalCost();
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

	private int getTransitionCost(Triple<Point, Direction, Integer> from, Triple<Point, Direction, Integer> to) {
		return heatLosses[to.a.getY()][to.a.getX()];
	}

	private List<Triple<Point, Direction, Integer>> getAdjacentRegularCrucible(Triple<Point, Direction, Integer> node) {
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
				n.a.getX() < mapWidth &&
				n.a.getY() < mapHeight)
			.toList();
	}

	private boolean isTerminalRegularCrucible(Triple<Point, Direction, Integer> node) {
		return node.a.equals(endpoint);
	}

	private List<Triple<Point, Direction, Integer>> getAdjacentUltraCrucible(Triple<Point, Direction, Integer> node) {
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
				n.a.getX() < mapWidth &&
				n.a.getY() < mapHeight)
			.toList();
	}

	private boolean isTerminalUltraCrucible(Triple<Point, Direction, Integer> node) {
		return node.a.equals(endpoint) && node.c >= 4;
	}

	private static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

}
