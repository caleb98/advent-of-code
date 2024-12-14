package net.calebscode.aoc.solutions;

import net.calebscode.aoc.BasicSolution;

public class AOC2023_Day01 extends BasicSolution<Integer> {

	public AOC2023_Day01() {
		super(1);
	}

	@Override
	public Integer solveFirst() {
		return input.getLines().parallelStream()
			.map(line -> line.replaceAll("[a-z]", ""))
			.map(digits -> {
				char first = digits.charAt(0);
				char last = digits.charAt(digits.length() - 1);
				return first + "" + last;
			})
			.map(Integer::valueOf)
			.reduce(0, Integer::sum);
	}

	@Override
	public Integer solveSecond() {
		return input.getLines().parallelStream()
			.map(line -> {
				int index = 0;
				while (index < line.length()) {
					line = line.substring(0, index) + swapNumberWord(line.substring(index));
					index++;
				}
				return line;
			})
			.map(line -> line.replaceAll("[a-z]", ""))
			.map(digits -> {
				char first = digits.charAt(0);
				char last = digits.charAt(digits.length() - 1);
				return first + "" + last;
			})
			.map(Integer::valueOf)
			.reduce(0, Integer::sum);
	}

	String swapNumberWord(String text) {
		if (text.startsWith("one")) return "1" + text.substring(1);
		if (text.startsWith("two")) return "2" + text.substring(1);
		if (text.startsWith("three")) return "3" + text.substring(1);
		if (text.startsWith("four")) return "4" + text.substring(1);
		if (text.startsWith("five")) return "5" + text.substring(1);
		if (text.startsWith("six")) return "6" + text.substring(1);
		if (text.startsWith("seven")) return "7" + text.substring(1);
		if (text.startsWith("eight")) return "8" + text.substring(1);
		if (text.startsWith("nine")) return "9" + text.substring(1);
		return text;
	}

}
