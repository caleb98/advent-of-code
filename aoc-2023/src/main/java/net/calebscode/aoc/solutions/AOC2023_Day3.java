package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day3 extends Solution<Integer> {

	private QuestionInput input;

	public AOC2023_Day3() {
		input = new QuestionInput("/inputs/day3.txt");
	}

	@Override
	public Integer solveFirst() {
		var schematic = getEngineSchematic();
		int partSum = 0;

		for (int line = 0; line < schematic.length; line++) {
			char[] lineChars = schematic[line];
			int start = -1;
			int end = -1;
			int partNum = 0;

			for (int col = 0; col < lineChars.length; col++) {
				if (Character.isDigit(lineChars[col])) {

					if (start == -1) {
						start = col;
						end = col;
						partNum = (lineChars[col] - '0');
					}
					else {
						partNum = (10 * partNum) + (lineChars[col] - '0');
						end = col;
					}

				}
				else if (start != -1) {

					if (checkSymbolAdjacent(schematic, line, start, end)) {
						System.out.printf("Found partNum: %d at row=%d, col=%d\n", partNum, line, col);
						partSum += partNum;
					}

					start = -1;
					end = -1;
					partNum = 0;

				}
			}

			// Final check for numbers at end of row
			if (start != -1) {
				if (checkSymbolAdjacent(schematic, line, start, end)) {
					System.out.printf("Found partNum: %d at row=%d, col=%d\n", partNum, line, lineChars.length - 1);
					partSum += partNum;
				}
			}
		}

		return partSum;
	}

	@Override
	public Integer solveSecond() {
		var schematic = getEngineSchematic();
		HashMap<Point, List<Integer>> gearParts = new HashMap<>();

		for (int line = 0; line < schematic.length; line++) {
			char[] lineChars = schematic[line];
			int start = -1;
			int end = -1;
			int partNum = 0;

			for (int col = 0; col < lineChars.length; col++) {
				if (Character.isDigit(lineChars[col])) {

					if (start == -1) {
						start = col;
						end = col;
						partNum = (lineChars[col] - '0');
					}
					else {
						partNum = (10 * partNum) + (lineChars[col] - '0');
						end = col;
					}

				}
				else if (start != -1) {

					var gear = getAdjacentGear(schematic, line, start, end);
					if (gear.isPresent()) {
						System.out.printf("Found part number with gear: %d at row=%d, col=%d\n", partNum, line, col);
						var gearPoint = gear.get();
						if (!gearParts.containsKey(gearPoint)) {
							gearParts.put(gearPoint, new ArrayList<>());
						}
						gearParts.get(gearPoint).add(partNum);
					}

					start = -1;
					end = -1;
					partNum = 0;

				}
			}

			// Final check for numbers at end of row
			if (start != -1) {
				var gear = getAdjacentGear(schematic, line, start, end);
				if (gear.isPresent()) {
					System.out.printf("Found partNum: %d at row=%d, col=%d\n", partNum, line, lineChars.length - 1);
					var gearPoint = gear.get();
					if (!gearParts.containsKey(gearPoint)) {
						gearParts.put(gearPoint, new ArrayList<>());
					}
					gearParts.get(gearPoint).add(partNum);
				}
			}
		}

		return gearParts.entrySet().parallelStream()
			.filter(entry -> entry.getValue().size() > 1)
			.map(entry -> {
				var partNums = entry.getValue();
				return partNums.get(0) * partNums.get(1);
			})
			.reduce(0, Integer::sum);
	}

	private Optional<Point> getAdjacentGear(char[][] schematic, int row, int start, int end) {
		int startRow = row - 1;
		int endRow = row + 1;
		int startCol = start - 1;
		int endCol = end + 1;

		for (int currentRow = startRow; currentRow <= endRow; currentRow++) {
			for (int currentCol = startCol; currentCol <= endCol; currentCol++) {
				char c = getChar(schematic, currentRow, currentCol);
				if (isGear(c)) {
					return Optional.of(new Point(currentRow, currentCol));
				}
			}
		}

		return Optional.empty();
	}

	private boolean checkSymbolAdjacent(char[][] schematic, int row, int start, int end) {
		int startRow = row - 1;
		int endRow = row + 1;
		int startCol = start - 1;
		int endCol = end + 1;

		for (int currentRow = startRow; currentRow <= endRow; currentRow++) {
			for (int currentCol = startCol; currentCol <= endCol; currentCol++) {
				char c = getChar(schematic, currentRow, currentCol);
				if (isSymbol(c)) {
					return true;
				}
			}
		}

		return false;
	}

	private char getChar(char[][] schematic, int row, int col) {
		if (row < 0 || row >= schematic.length || col < 0 || col >= schematic[0].length) {
			return '.';
		}
		else {
			return schematic[row][col];
		}
	}

	private char[][] getEngineSchematic() {
		var lines = input.getLines();
		char[][] schematic = new char[lines.get(0).length()][lines.size()];

		for (int i = 0; i < lines.size(); i++) {
			schematic[i] = lines.get(i).toCharArray();
		}

		return schematic;
	}

	private boolean isSymbol(char c) {
		return c != '.' && !Character.isDigit(c);
	}

	private boolean isGear(char c) {
		return c == '*';
	}

	static class Point {
		int row;
		int col;

		Point(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public int hashCode() {
			return Objects.hash(row, col);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Point other)) return false;
			return row == other.row && col == other.col;
		}
	}
}
