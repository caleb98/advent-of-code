package net.calebscode.aoc.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
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
	
	/**
	 * DOESNT WORK CURRENTLY, WIP FOR AOC 2023 Day 23
	 * @param startNodes
	 * @return
	 */
	public DijkstraPath pathfindLongest(Iterable<T> startNodes) {
		var totalCosts = new HashMap<T, Integer>();
		var worstPaths = new HashMap<T, T>();
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

				if (totalCostToAdjacent > prevTotalCostToAdjacent) {
					totalCosts.put(node, totalCostToAdjacent);
					worstPaths.put(node, current);
				}
			}

			unvisited.addAll(adjacentNodes);
			visited.add(current);
		}

		// Collect the end nodes
		var endNodes = visited.parallelStream()
						.filter(node -> isTerminalState.test(node))
						.toList();

		// Find the worst end node with the highest cost
		T worstEndpoint = null;
		int highestCost = Integer.MIN_VALUE;

		for (var endpoint : endNodes) {
			int endpointCost = totalCosts.get(endpoint);

			if (highestCost < endpointCost) {
				worstEndpoint = endpoint;
				highestCost = endpointCost;
			}
		}

		// Construct the longest path
		var path = new LinkedList<T>();
		current = worstEndpoint;
		while (totalCosts.get(current) != 0) {
			path.addFirst(current);
			current = worstPaths.get(current);
		}
		path.addFirst(current); // Add the starting node

		return new DijkstraPath(totalCosts.get(worstEndpoint), path);
	}

	/**
	 * Standard dijkstra's algorithm. Finds <b><em>a single</em></b> best path and returns it
	 * @param startNodes
	 * @return
	 */
	public DijkstraPath pathfind(Iterable<T> startNodes) {
		var totalCosts = new HashMap<T, Integer>();
		var bestPaths = new HashMap<T, T>();
		var visited = new HashSet<T>();
		var unvisited = new PriorityQueue<T>(createSorter(totalCosts));

		for (var node : startNodes) {
			totalCosts.put(node, 0);
			unvisited.add(node);
		}

		T current;
		while (!unvisited.isEmpty()) {
			current = unvisited.poll();
			if (visited.contains(current)) continue;
			visited.add(current);

			var currentTotalCost = totalCosts.get(current);
			var adjacentNodes = getAdjacent.apply(current).stream()
									.filter(n -> !visited.contains(n))
									.toList();

			for (var adjacentNode : adjacentNodes) {
				//unvisited.remove(node);
				var transitionCost = getTransitionCost.apply(current, adjacentNode);

				var totalCostToAdjacent = currentTotalCost + transitionCost;
				var prevTotalCostToAdjacent = totalCosts.getOrDefault(adjacentNode, Integer.MAX_VALUE);

				if (totalCostToAdjacent < prevTotalCostToAdjacent) {
					totalCosts.put(adjacentNode, totalCostToAdjacent);
					bestPaths.put(adjacentNode, current);
				}
			}

			unvisited.addAll(adjacentNodes);
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
		
		// No path
		if (bestEndpoint == null) {
			return new DijkstraPath(-1, new ArrayList<>(visited));
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
	
	/**
	 * Standard dijkstra's algorithm. Finds <b><em>all</em></b> best paths and returns
	 * a list of them. (All bests paths means all paths which reach an end node with
	 * the same total cost).
	 * @param startNodes
	 * @return
	 */
	public List<DijkstraPath> pathfindAllBest(Iterable<T> startNodes) {
		var totalCosts = new HashMap<T, Integer>();
		var bestPaths = new HashMap<T, Set<T>>();
		var visited = new HashSet<T>();
		var unvisited = new PriorityQueue<T>(createSorter(totalCosts));

		for (var node : startNodes) {
			totalCosts.put(node, 0);
			unvisited.add(node);
		}

		T current;
		while (!unvisited.isEmpty()) {
			current = unvisited.poll();
			if (visited.contains(current)) continue;
			visited.add(current);

			var currentTotalCost = totalCosts.get(current);
			var adjacentNodes = getAdjacent.apply(current).stream()
									.filter(n -> !visited.contains(n))
									.toList();

			for (var node : adjacentNodes) {
				var transitionCost = getTransitionCost.apply(current, node);

				var totalCostToAdjacent = currentTotalCost + transitionCost;
				var prevTotalCostToAdjacent = totalCosts.getOrDefault(node, Integer.MAX_VALUE);

				if (totalCostToAdjacent < prevTotalCostToAdjacent) {
					totalCosts.put(node, totalCostToAdjacent);
					
					var best = new HashSet<T>();
					best.add(current);
					bestPaths.put(node, best);
				}
				else if (totalCostToAdjacent == prevTotalCostToAdjacent) {
					bestPaths.computeIfAbsent(node, n -> new HashSet<T>()).add(current);
				}
			}

			unvisited.addAll(adjacentNodes);
		}

		// Collect the end nodes
		var endNodes = visited.parallelStream()
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

		var allPaths = new ArrayList<DijkstraPath>();
		var allNodePaths = allPaths(totalCosts, bestPaths, new ArrayList<>(), bestEndpoint);
		var bestCost = totalCosts.get(bestEndpoint);
		
		for (var path : allNodePaths) {
			allPaths.add(new DijkstraPath(bestCost, path));
		}

		return allPaths;
	}
	
	private List<List<T>> allPaths(HashMap<T, Integer> totalCosts, Map<T, Set<T>> bestPaths, List<T> currentPath, T currentNode) {
		// Add current node to path and then compute for all best paths
		currentPath.add(currentNode);
		
		// Base case, we're finished
		if (totalCosts.get(currentNode) == 0) {
			return List.of(currentPath);
		}
		
		var allLists = new ArrayList<List<T>>();
		for (var goodNode : bestPaths.get(currentNode)) {
			var splitPath = new ArrayList<>(currentPath);
			allLists.addAll(allPaths(totalCosts, bestPaths, splitPath, goodNode));
		}
		
		return allLists;
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
