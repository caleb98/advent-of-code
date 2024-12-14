package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.List;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Triple;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;

public class AOC2023_Day17 extends BasicSolution<Integer> {
	private final int[][] heatLosses;
	private final int mapWidth;
	private final int mapHeight;
	private final Point2D endpoint;
	private final List<Triple<Point2D, Direction, Integer>> startNodes;

	public AOC2023_Day17() {
		super(17);
		
		heatLosses = input.asIntArray();
		mapWidth = heatLosses[0].length;
		mapHeight = heatLosses.length;
		endpoint = new Point2D(mapWidth - 1, mapHeight -  1);

		startNodes = List.of(
			Triple.of(new Point2D(0, 0), Direction.RIGHT, 0),
			Triple.of(new Point2D(0, 0), Direction.DOWN, 0)
		);
	}

	@Override
	public Integer solveFirst() {
		var pathfinder = new DijkstraPathfinder<Triple<Point2D, Direction, Integer>>(
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
		var pathfinder = new DijkstraPathfinder<Triple<Point2D, Direction, Integer>>(
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

	private int getTransitionCost(Triple<Point2D, Direction, Integer> from, Triple<Point2D, Direction, Integer> to) {
		return heatLosses[to.first.getY()][to.first.getX()];
	}

	private List<Triple<Point2D, Direction, Integer>> getAdjacentRegularCrucible(Triple<Point2D, Direction, Integer> node) {
		var adjacent = new ArrayList<>(switch (node.second) {
			case UP, DOWN -> {
				Triple<Point2D, Direction, Integer> left = Triple.of(node.first.translate(-1, 0), Direction.LEFT, 1);
				Triple<Point2D, Direction, Integer> right = Triple.of(node.first.translate(1, 0), Direction.RIGHT, 1);
				yield List.of(left, right);
			}
			case LEFT, RIGHT -> {
				Triple<Point2D, Direction, Integer> up = Triple.of(node.first.translate(0, -1), Direction.UP, 1);
				Triple<Point2D, Direction, Integer> down = Triple.of(node.first.translate(0, 1), Direction.DOWN, 1);
				yield List.of(up, down);
			}
		});

		if (node.third < 3) {
			adjacent.add(switch (node.second) {
				case UP -> Triple.of(node.first.translate(0, -1), Direction.UP, node.third + 1);
				case DOWN -> Triple.of(node.first.translate(0, 1), Direction.DOWN, node.third + 1);
				case LEFT -> Triple.of(node.first.translate(-1, 0), Direction.LEFT, node.third + 1);
				case RIGHT -> Triple.of(node.first.translate(1, 0), Direction.RIGHT, node.third + 1);
			});
		}

		return adjacent.stream()
			.filter(n ->
				n.first.getX() >= 0 &&
				n.first.getY() >= 0 &&
				n.first.getX() < mapWidth &&
				n.first.getY() < mapHeight)
			.toList();
	}

	private boolean isTerminalRegularCrucible(Triple<Point2D, Direction, Integer> node) {
		return node.first.equals(endpoint);
	}

	private List<Triple<Point2D, Direction, Integer>> getAdjacentUltraCrucible(Triple<Point2D, Direction, Integer> node) {
		List<Triple<Point2D, Direction, Integer>> adjacent;

		// Ultra crucible must go at least four spaces. If we're not there,
		// then the only option is going forward.
		if (node.third < 4) {
			adjacent = switch (node.second) {
				case UP -> List.of(Triple.of(node.first.translate(0, -1), Direction.UP, node.third + 1));
				case DOWN -> List.of(Triple.of(node.first.translate(0, 1), Direction.DOWN, node.third + 1));
				case LEFT -> List.of(Triple.of(node.first.translate(-1, 0), Direction.LEFT, node.third + 1));
				case RIGHT -> List.of(Triple.of(node.first.translate(1, 0), Direction.RIGHT, node.third + 1));
			};
		}
		// Otherwise, behaves the same as a normal crucible, but with
		// max of 10 consecutive blocks.
		else {
			adjacent = new ArrayList<>(switch (node.second) {
				case UP, DOWN -> {
					Triple<Point2D, Direction, Integer> left = Triple.of(node.first.translate(-1, 0), Direction.LEFT, 1);
					Triple<Point2D, Direction, Integer> right = Triple.of(node.first.translate(1, 0), Direction.RIGHT, 1);
					yield List.of(left, right);
				}
				case LEFT, RIGHT -> {
					Triple<Point2D, Direction, Integer> up = Triple.of(node.first.translate(0, -1), Direction.UP, 1);
					Triple<Point2D, Direction, Integer> down = Triple.of(node.first.translate(0, 1), Direction.DOWN, 1);
					yield List.of(up, down);
				}
			});

			if (node.third < 10) {
				adjacent.add(switch (node.second) {
					case UP -> Triple.of(node.first.translate(0, -1), Direction.UP, node.third + 1);
					case DOWN -> Triple.of(node.first.translate(0, 1), Direction.DOWN, node.third + 1);
					case LEFT -> Triple.of(node.first.translate(-1, 0), Direction.LEFT, node.third + 1);
					case RIGHT -> Triple.of(node.first.translate(1, 0), Direction.RIGHT, node.third + 1);
				});
			}
		}

		return adjacent.stream()
			.filter(n ->
				n.first.getX() >= 0 &&
				n.first.getY() >= 0 &&
				n.first.getX() < mapWidth &&
				n.first.getY() < mapHeight)
			.toList();
	}

	private boolean isTerminalUltraCrucible(Triple<Point2D, Direction, Integer> node) {
		return node.first.equals(endpoint) && node.third >= 4;
	}

	private static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

}
