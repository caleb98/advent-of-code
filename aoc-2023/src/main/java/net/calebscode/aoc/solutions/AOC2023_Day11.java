package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.geometry.PointLong;

public class AOC2023_Day11 extends BasicSolution<Long> {

	public AOC2023_Day11() {
		super(11);
	}

	@Override
	public Long solveFirst() {
		var universe = input.asCharArray();
		universe = calculateExpansion(universe);

		var galaxies = findGalaxies(universe);

		long pairDistances = 0;
		while (galaxies.size() > 1) {
			var currentGalaxy = galaxies.removeFirst();
			for (int i = 0; i < galaxies.size(); i++) {
				pairDistances += currentGalaxy.manhattanDistance(galaxies.get(i));
			}
		}

		return pairDistances;
	}

	@Override
	public Long solveSecond() {
		var universe = input.asCharArray();

		// Find galaxies first
		var galaxies = findGalaxies(universe);

		var emptyRows = IntStream.range(0, universe.length)
						.filter(row -> !galaxies.stream().anyMatch(
							point -> point.getY() == row
						))
						.toArray();

		var emptyCols = IntStream.range(0, universe[0].length)
						.filter(col -> !galaxies.stream().anyMatch(
							point -> point.getX() == col
						))
						.toArray();

		var expandedGalaxies = galaxies.stream()
			.map(start -> {
				long shiftX = 0;
				long shiftY = 0;

				for (int i = 0; i < emptyRows.length; i++) {
					if (emptyRows[i] < start.getY()) {
						shiftY += 999_999L;
					} else {
						break;
					}
				}

				for (int i = 0; i < emptyCols.length; i++) {
					if (emptyCols[i] < start.getX()) {
						shiftX += 999_999L;
					} else {
						break;
					}
				}

				return start.translate(shiftX, shiftY);
			}).toList();

		expandedGalaxies = new ArrayList<>(expandedGalaxies);

		long pairDistances = 0;
		while (expandedGalaxies.size() > 1) {
			var currentGalaxy = expandedGalaxies.removeFirst();
			for (int i = 0; i < expandedGalaxies.size(); i++) {
				pairDistances += currentGalaxy.manhattanDistance(expandedGalaxies.get(i));
			}
		}

		return pairDistances;
	}

	private List<PointLong> findGalaxies(char[][] universe) {
		var galaxies = new ArrayList<PointLong>();
		for (int row = 0; row < universe.length; row++) {
			for (int col = 0; col < universe[row].length; col++) {
				if (universe[row][col] == '#') {
					galaxies.add(new PointLong(col, row));
				}
			}
		}

		return galaxies;
	}

	private char[][] calculateExpansion(char[][] universe) {
		for (int col = 0; col < universe[0].length; col++) {
			if (isColEmpty(universe, col)) {
				col++;
				universe = expandCol(universe, col);
			}
		}

		for (int row = 0; row < universe.length; row++) {
			if (isRowEmpty(universe, row)) {
				row++;
				universe = expandRow(universe, row);
			}
		}

		return universe;
	}

	private void printUniverse(char[][] universe) {
		System.out.println("=".repeat(universe[0].length + 1));
		for (var row : universe) {
			System.out.println(row);
		}
	}

	private boolean isRowEmpty(char[][] input, int row) {
		return new String(input[row]).matches("^\\.*$");
	}

	private boolean isColEmpty(char[][] input, int col) {
		for (int row = 0; row < input.length; row++) {
			if (input[row][col] != '.') return false;
		}
		return true;
	}

	private char[][] expandRow(char[][] input, int row) {
		char[] newRow = ".".repeat(input[0].length).toCharArray();
		char[][] output = new char[input.length + 1][];

		for (int i = 0; i < row; i++) {
			output[i] = input[i];
		}

		output[row] = newRow;

		for (int i = row + 1; i < output.length; i++) {
			output[i] = input[i - 1];
		}

		return output;
	}

	private char[][] expandCol(char[][] input, int col) {
		char[][] output = new char[input.length][];

		for (int row = 0; row < input.length; row++) {
			var rowString = new String(input[row]);
			var front = rowString.substring(0, col);
			var rear = rowString.substring(col);
			var newRow = (front + "." + rear).toCharArray();
			output[row] = newRow;
		}

		return output;
	}

}
