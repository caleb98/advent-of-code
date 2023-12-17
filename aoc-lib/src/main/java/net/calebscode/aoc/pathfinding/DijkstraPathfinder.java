package net.calebscode.aoc.pathfinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class DijkstraPathfinder<T> {

	private final Function<T, List<T>> getAdjacent;
	private final BiFunction<T, T, Integer> getTransitionCost;
	private final Predicate<T> isTerminalState;

	public DijkstraPathfinder(
		Function<T, List<T>> getAdjacent,
		BiFunction<T, T, Integer> getTransitionCost,
		Predicate<T> isTerminalState
	) {
		this.getAdjacent = getAdjacent;
		this.getTransitionCost = getTransitionCost;
		this.isTerminalState = isTerminalState;
	}

	public DijkstraPath pathfind(Iterable<T> startNodes) {
		var totalCosts = new HashMap<T, Integer>();
		var bestPaths = new HashMap<T, T>();
		var visited = new HashSet<T>();
		var unvisited = new TreeSet<T>(createSorter(totalCosts));

		for (var node : startNodes) {
			totalCosts.put(node, 0);
			unvisited.add(node);
		}

		T current;
		while (!unvisited.isEmpty()) {
			current = unvisited.removeFirst();

			var currentTotalCost = totalCosts.get(current);
			var adjacentNodes = getAdjacent.apply(current).stream()
									.filter(n -> !visited.contains(n))
									.toList();

			for (var node : adjacentNodes) {
				unvisited.remove(node);
				var transitionCost = getTransitionCost.apply(current, node);

				var totalCostToAdjacent = currentTotalCost + transitionCost;
				var prevTotalCostToAdjacent = totalCosts.getOrDefault(node, Integer.MAX_VALUE);

				if (totalCostToAdjacent < prevTotalCostToAdjacent) {
					totalCosts.put(node, totalCostToAdjacent);
					bestPaths.put(node, current);
				}
			}

			unvisited.addAll(adjacentNodes);
			visited.add(current);
		}

		// Collect the end nodes
		var endNodes= visited.parallelStream()
						.filter(node -> isTerminalState.test(node))
						.toList();

		// Find the best end node with the lowest cost
		T bestEndpoint = null;
		int lowestCost = Integer.MAX_VALUE;

		for (var endpoint : endNodes) {
			int endpointCost = totalCosts.get(endpoint);

			if (lowestCost > endpointCost) {
				bestEndpoint = endpoint;
				lowestCost = endpointCost;
			}
		}

		// Construct the shortest path using the best endpoint
		var path = new LinkedList<T>();
		current = bestEndpoint;
		while (totalCosts.get(current) != 0) {
			path.addFirst(current);
			current = bestPaths.get(current);
		}
		path.addFirst(current); // Add the starting node

		return new DijkstraPath(totalCosts.get(bestEndpoint), path);
	}

	private Comparator<T> createSorter(Map<T, Integer> costMap) {
		return (a, b) -> {
			var aCost = costMap.getOrDefault(a, Integer.MAX_VALUE);
			var bCost = costMap.getOrDefault(b, Integer.MAX_VALUE);
			var comp = Integer.compare(aCost, bCost);

			return comp != 0 ? comp : Integer.compare(a.hashCode(), b.hashCode());
		};
	}

	public class DijkstraPath {

		private final int totalCost;
		private final List<T> path;

		private DijkstraPath(int totalCost, List<T> path) {
			this.totalCost = totalCost;
			this.path = path;
		}

		public int getTotalCost() {
			return totalCost;
		}

		public List<T> getPath() {
			return Collections.unmodifiableList(path);
		}

	}

}
