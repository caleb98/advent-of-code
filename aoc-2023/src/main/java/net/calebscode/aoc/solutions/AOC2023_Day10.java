package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day10 extends Solution<Integer> {

	private QuestionInput input;

	public AOC2023_Day10() {
		input = new QuestionInput("/inputs/day10.txt");
	}

	@Override
	public Integer solveFirst() {
		var layout = getPipeLayout();
		var start = getStart(layout);
		HashSet<Node> visited = getVisitedNodes(layout, start[0], start[1]);

		return visited.parallelStream()
					.mapToInt(node -> node.distance)
					.max().getAsInt();
	}

	@Override
	public Integer solveSecond() {
		var layout = getPipeLayout();
		var start = getStart(layout);
		HashSet<Node> visited = getVisitedNodes(layout, start[0], start[1]);

		// Actually insert the character that the start would have
		layout[start[1]][start[0]] = getStartCharacter(layout, new Node(start[0], start[1], 0));

		int area = 0;

		char[][] test = new char[layout.length][];

		for (int y = 0; y < layout.length; y++) {
			boolean isInside = false;
			test[y] = new char[layout[y].length];

			for (int x = 0; x < layout[y].length; x++) {
				test[y][x] = '.';
				var current = layout[y][x];
				var currentNode = new Node(x, y, -1);
				if (visited.contains(currentNode) && isNorthFacing(current)) {
					isInside = !isInside;
				}
				else if (isInside && !visited.contains(currentNode)) {
					area += 1;
					test[y][x] = 'I';
				}
			}
		}

		return area;
	}

	private boolean isNorthFacing(char c) {
		return c == '|' || c == 'J' || c == 'L';
	}

	private int[] getStart(char[][] layout) {
		int startX = -1;
		int startY = -1;
		for (int y = 0; y < layout.length; y++) {
			boolean foundStart = false;
			for (int x = 0; x < layout[y].length; x++) {
				if (layout[y][x] == 'S') {
					startX = x;
					startY = y;
					foundStart = true;
					break;
				}
			}

			if (foundStart) {
				break;
			}
		}

		return new int[] { startX, startY };
	}

	private HashSet<Node> getVisitedNodes(char[][] layout, int startX, int startY) {
		HashSet<Node> visited = new HashSet<>();
		List<Node> toVisit = new ArrayList<>();

		Node start = new Node(startX, startY, 0);
		toVisit.add(start);

		while(!toVisit.isEmpty()) {
			var visiting = toVisit.removeFirst();

			setupConnections(layout, visiting);
			for (var conn : visiting.connections) {
				if (!visited.contains(conn)) {
					toVisit.add(conn);
				}
			}

			visited.add(visiting);
		}
		return visited;
	}

	private void setupConnections(char[][] layout, Node node) {
		int newDist = node.distance + 1;
		node.connections = switch (layout[node.y][node.x]) {

			case '|' -> List.of(new Node(node.x, node.y - 1, newDist), new Node(node.x, node.y + 1, newDist));
			case '-' -> List.of(new Node(node.x - 1, node.y, newDist), new Node(node.x + 1, node.y, newDist));
			case 'F' -> List.of(new Node(node.x + 1, node.y, newDist), new Node(node.x, node.y + 1, newDist));
			case '7' -> List.of(new Node(node.x - 1, node.y, newDist), new Node(node.x, node.y + 1, newDist));
			case 'L' -> List.of(new Node(node.x, node.y - 1, newDist), new Node(node.x + 1, node.y, newDist));
			case 'J' -> List.of(new Node(node.x, node.y - 1, newDist), new Node(node.x - 1, node.y, newDist));
			case 'S' -> getStartConnections(layout, node);

			default -> Collections.emptyList();

		};
	}

	private char getStartCharacter(char[][] layout, Node start) {
		int north = start.y - 1;
		int south = start.y + 1;
		int east = start.x + 1;
		int west = start.x - 1;

		if (connectsSouth(layout, start.x, north) && connectsNorth(layout, start.x, south)) return '|';
		if (connectsSouth(layout, start.x, north) && connectsEast(layout, west, start.y)) return 'J';
		if (connectsSouth(layout, start.x, north) && connectsWest(layout, east, start.y)) return 'L';

		if (connectsNorth(layout, start.x, south) && connectsEast(layout, west, start.y)) return '7';
		if (connectsNorth(layout, start.x, south) && connectsWest(layout, east, start.y)) return 'F';

		if (connectsEast(layout, west, start.y) && connectsWest(layout, east, start.y)) return '-';

		return '!';
	}

	private List<Node> getStartConnections(char[][] layout, Node start) {
		var connections = new ArrayList<Node>();

		int north = start.y - 1;
		int south = start.y + 1;
		int east = start.x + 1;
		int west = start.x - 1;

		if (connectsSouth(layout, start.x, north)) connections.add(new Node(start.x, north, 1));
		if (connectsNorth(layout, start.x, south)) connections.add(new Node(start.x, south, 1));
		if (connectsEast(layout, west, start.y)) connections.add(new Node(west, start.y, 1));
		if (connectsWest(layout, east, start.y)) connections.add(new Node(east, start.y, 1));

		return connections;
	}

	private boolean connectsNorth(char[][] layout, int x, int y) {
		if (y < 0 || y > layout.length || x < 0 || x > layout[y].length) {
			return false;
		}

		var type = layout[y][x];
		return type == '|' || type =='J' || type == 'L';
	}

	private boolean connectsSouth(char[][] layout, int x, int y) {
		if (y < 0 || y > layout.length || x < 0 || x > layout[y].length) {
			return false;
		}

		var type = layout[y][x];
		return type == '|' || type =='7' || type == 'F';
	}

	private boolean connectsEast(char[][] layout, int x, int y) {
		if (y < 0 || y > layout.length || x < 0 || x > layout[y].length) {
			return false;
		}

		var type = layout[y][x];
		return type == '-' || type =='F' || type == 'L';
	}

	private boolean connectsWest(char[][] layout, int x, int y) {
		if (y < 0 || y > layout.length || x < 0 || x > layout[y].length) {
			return false;
		}

		var type = layout[y][x];
		return type == '-' || type =='J' || type == '7';
	}

	private char[][] getPipeLayout() {
		var lines = input.getLines();
		char[][] schematic = new char[lines.get(0).length()][lines.size()];

		for (int i = 0; i < lines.size(); i++) {
			schematic[i] = lines.get(i).toCharArray();
		}

		return schematic;
	}

	static class Node {

		List<Node> connections;
		int x;
		int y;
		int distance;

		Node(int x, int y, int distance) {
			this.x = x;
			this.y = y;
			this.distance = distance;
		}

		@Override
		public int hashCode() {
			// I don't like this, but it makes the
			// Node behave the way I want in a Set.
			return Objects.hash(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || !(obj instanceof Node other)) return false;
			return x == other.x && y == other.y;
		}

	}

}
