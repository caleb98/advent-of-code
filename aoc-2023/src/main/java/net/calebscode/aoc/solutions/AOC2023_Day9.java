package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day9 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day9() {
		input = new QuestionInput("/inputs/day9.txt");
	}

	@Override
	public Long solveFirst() {
		var sumPredictions = input.getLines().parallelStream()
						.map(this::parseData)
						.map(this::predict)
						.reduce(0L, Long::sum);

		return sumPredictions;
	}

	@Override
	public Long solveSecond() {
		var sumPredictions = input.getLines().parallelStream()
						.map(this::parseData)
						.map(this::reverse)
						.map(this::predict)
						.reduce(0L, Long::sum);

		return sumPredictions;
	}

	private <T> List<T> reverse(List<T> data) {
		var reversed = new ArrayList<T>();
		for (int i = data.size() - 1; i >= 0; i--) {
			reversed.add(data.get(i));
		}
		return reversed;
	}

	private List<Long> parseData(String line) {
		var data = line.trim().split(" ");
		var dataLongs = Stream.of(data).parallel()
							.map(Long::parseLong)
							.toList();

		return new ArrayList<>(dataLongs);
	}

	private Long predict(List<Long> values) {
		Stack<List<Long>> data = new Stack<>();
		data.push(values);
		data.push(getDiffList(values));

		while (!isAllZeros(data.peek())) {
			data.push(getDiffList(data.peek()));
		}

		while (data.size() > 1) {
			var lastChange = data.pop().getLast();
			var nextData = data.peek();
			nextData.add(nextData.getLast() + lastChange);
		}

		return data.pop().getLast();
	}

	private List<Long> getDiffList(List<Long> values) {
		var diffList = new ArrayList<Long>(values.size() - 1);
		for (int i = 1; i < values.size(); i++) {
			diffList.add(values.get(i) - values.get(i - 1));
		}
		return diffList;
	}

	private boolean isAllZeros(List<Long> values) {
		return values.parallelStream().allMatch(value -> value == 0);
	}

}
