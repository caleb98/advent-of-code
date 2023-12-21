package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Pair;
import net.calebscode.aoc.util.Point;
import net.calebscode.aoc.util.PointLong;

public class AOC2023_Day18 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day18() {
		input = new QuestionInput("/inputs/day18.txt");
	}

	@Override
	public Long solveFirst() {
		var instructions = input.getLines().stream()
			.map(this::parseInstruction)
			.toList();

		var points = createPointsForInstructions(instructions);

		// Find volume of rectangles
		return calculateArea(points);
	}

	@Override
	public Long solveSecond() {
		var instructions = input.getLines().stream()
			.map(this::parseTrueInstruction)
			.toList();

		var points = createPointsForInstructions(instructions);

		// Find volume of rectangles
		return calculateArea(points);
	}

	private long calculateArea(ArrayList<PointLong> points) {
		long area = 0;
		for (int i = 0; i < points.size(); i++) {
			var a = points.get(i);
			var b = points.get(i != points.size() - 1 ? i + 1 : 0);

			// If x coordinate is the same, we can ignore since it will have zero area.
			if (a.getX() == b.getX()) continue;

			// Our instructions only produce straight lines, so we now know that our
			// points are horizontally aligned.
			long height = a.getY(); // == b.getY()
			long width = b.getX() - a.getX();

			area += width * height;
		}

		return Math.abs(area);
	}

	private ArrayList<PointLong> createPointsForInstructions(List<Pair<Character, Integer>> instructions) {
		var points = new ArrayList<PointLong>();
		var pos = new PointLong(0, 0);
		char prevDir = ' ';
		boolean wasInside = false;
		for (var instruction : instructions) {
			var dir = instruction.a;
			var len = instruction.b;

			PointLong next = switch (dir) {
				case 'U' -> {
					if (prevDir == 'R') {
						if (wasInside) pos = pos.translate(-1, 0);
						wasInside = true;
					}
					else if (prevDir == 'L') {
						if (!wasInside) pos = pos.translate(-1, 0);
						wasInside = false;
					}

					yield pos.translate(0, -len);
				}

				case 'D' -> {
					if (prevDir == 'L') {
						if (wasInside) pos = pos.translate(1, 0);
						wasInside = true;
					}
					else if (prevDir == 'R') {
						if (!wasInside) pos = pos.translate(1, 0);
						wasInside = false;
					}

					yield pos.translate(0, len);
				}

				case 'L' -> {
					if (prevDir == 'U') {
						if (wasInside) pos = pos.translate(0, 1);
						wasInside = true;
					}
					else if (prevDir == 'D') {
						if (!wasInside) pos = pos.translate(0, 1);
						wasInside = false;
					}

					yield pos.translate(-len, 0);
				}

				case 'R' -> {
					if (prevDir == 'D') {
						if (wasInside) pos = pos.translate(0, -1);
						wasInside = true;
					}
					else if (prevDir == 'U') {
						if (!wasInside) pos = pos.translate(0, -1);
						wasInside = false;
					}

					yield pos.translate(len, 0);
				}
				default -> throw new IllegalArgumentException("Unexpected value: " + dir);
			};

			points.add(pos);
			prevDir = dir;
			pos = next;
		}

		points.add(pos);
		return points;
	}

	private Pair<Character, Integer> parseInstruction(String line) {
		var code = line.split(" ");
		var dir = code[0].charAt(0);
		var len = Integer.parseInt(code[1]);

		return Pair.of(dir, len);
	}

	private Pair<Character, Integer> parseTrueInstruction(String line) {
		var code = line.split(" ")[2].replaceAll("[\\(\\)]", "").substring(1);
		var dirCode = code.charAt(code.length() - 1);
		var lenCode = code.substring(0, code.length() - 1);

		var dir = switch (dirCode) {
			case '0' -> 'R';
			case '1' -> 'D';
			case '2' -> 'L';
			case '3' -> 'U';
			default -> throw new IllegalArgumentException("Instruction contains invalid dirCode: " + line);
		};

		var len = Integer.parseInt(lenCode, 16);

		return Pair.of(dir, len);
	}

}
