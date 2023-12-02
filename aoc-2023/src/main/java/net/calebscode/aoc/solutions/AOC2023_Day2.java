package net.calebscode.aoc.solutions;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day2 extends Solution<Integer> {

	private final QuestionInput input;

	private static final int MAX_RED_CUBES = 12;
	private static final int MAX_GREEN_CUBES = 13;
	private static final int MAX_BLUE_CUBES = 14;

	public AOC2023_Day2() {
		input = new QuestionInput("/inputs/day2.txt");
	}

	@Override
	public Integer solveFirst() {
		return input.getLines().stream()
			.map(line -> line.substring(5))
			.map(line -> line.split(":"))
			.filter(parts -> verifyGameIsPossible(parts[1]))
			.map(parts -> parts[0])
			.map(Integer::valueOf)
			.reduce(0, Integer::sum);
	}

	@Override
	public Integer solveSecond() {
		return input.getLines().stream()
			.map(line -> line.split(":")[1])
			.map(this::findFewestCubes)
			.map(CubeSet::power)
			.reduce(0, Integer::sum);
	}

	private CubeSet findFewestCubes(String gameData) {
		String[] pulls = gameData.split(";");
		int maxRed = 0;
		int maxGreen = 0;
		int maxBlue = 0;

		for (String pull : pulls) {
			int red = 0;
			int green = 0;
			int blue = 0;

			String[] counts = pull.split(",");
			for (String count : counts) {
				String[] data = count.trim().split(" ");
				int number = Integer.parseInt(data[0]);
				String color = data[1];

				switch (color) {
					case "red" -> red += number;
					case "blue" -> blue += number;
					case "green" -> green += number;
					default -> throw new IllegalArgumentException("Invalid cube color: " + color);
				}
			}

			if (red > maxRed) maxRed = red;
			if (green > maxGreen) maxGreen = green;
			if (blue > maxBlue) maxBlue = blue;
		}

		return new CubeSet(maxRed, maxGreen, maxBlue);
	}


	private boolean verifyGameIsPossible(String gameData) {
		String[] pulls = gameData.split(";");

		for (String pull : pulls) {
			int red = 0;
			int green = 0;
			int blue = 0;

			String[] counts = pull.split(",");
			for (String count : counts) {
				String[] data = count.trim().split(" ");
				int number = Integer.parseInt(data[0]);
				String color = data[1];

				switch (color) {
					case "red" -> red += number;
					case "blue" -> blue += number;
					case "green" -> green += number;
					default -> throw new IllegalArgumentException("Invalid cube color: " + color);
				}
			}

			if (red > MAX_RED_CUBES || green > MAX_GREEN_CUBES || blue > MAX_BLUE_CUBES) {
				return false;
			}
		}

		return true;
	}

	private static class CubeSet {

		int r;
		int g;
		int b;

		CubeSet(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		int power() {
			return r * g * b;
		}

	}

}
