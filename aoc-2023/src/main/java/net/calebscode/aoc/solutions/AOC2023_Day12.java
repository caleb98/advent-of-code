package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day12 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day12() {
		input = new QuestionInput("/inputs/day12.txt");
	}

	@Override
	public Long solveFirst() {
		long start = System.currentTimeMillis();
		var layouts = input.getLines().parallelStream()
							.map(this::parseLayoutData)
							.mapToLong(data -> {
								return countPossible(data.layout, data.groups, false);
							})
							.sum();
		long end = System.currentTimeMillis();
		System.out.printf("Computation time: %dms\n", end - start);
		return layouts;
	}

	@Override
	public Long solveSecond() {
		long start = System.currentTimeMillis();
		var layouts = input.getLines().parallelStream()
							.map(this::parseLayoutData)
							.map(this::unfoldLayout)
							.mapToLong(data -> {
								return countPossible(data.layout, data.groups, false);
							})
							.sum();
		long end = System.currentTimeMillis();
		System.out.printf("Computation time: %dms\n", end - start);
		return layouts;
	}

	private Map<CountPossibleInput, Long> cache = new ConcurrentHashMap<>();
	private long countPossible(String layout, List<Integer> broken, boolean inGroup) {
		var methodInput = new CountPossibleInput(layout, broken, inGroup);
		if (cache.containsKey(methodInput)) {
			return cache.get(methodInput);
		}

		// No more broken springs
		if (sum(broken) == 0) {
			return layout.contains("#") ? 0 : 1;
		}
		// No more layout left to check
		else if (layout.isBlank()) {
			return (sum(broken) == 0) ? 1 : 0;
		}

		// Some quick checks to prune unnecessary branches
		// (holy cow this actually speeds things up a TON!)
		int brokenSum = sum(broken);
		int brokenInLayout = layout.replaceAll("[\\.\\?]", "").length();

		if (brokenInLayout > brokenSum) {
			return 0;
		}

		var current = layout.charAt(0);
		long result;

		// Inside a group
		if (inGroup) {
			// If no more springs, must be ? or .
			if (broken.getFirst() == 0) {
				result =  (current == '?' || current == '.') ?
					countPossible(layout.substring(1), tail(broken), false) :
					0;
			}
			// Otherwise, must be ? or #
			else {
				result = (current == '?' || current == '#') ?
					countPossible(layout.substring(1), reduceFirst(broken), true) :
					0;
			}
		}
		// Outside a group with ., just continue on unchanged
		else if (current == '.') {
			result = countPossible(layout.substring(1), broken, false);
		}
		// Outside a group with spring #, start new group
		else if (current == '#') {
			result = countPossible(layout.substring(1), reduceFirst(broken), true);
		}
		// Outside a group with either
		else if (current == '?') {
			// Check both result =paths
			result = countPossible(layout.substring(1), broken, false)
					+ countPossible(layout.substring(1), reduceFirst(broken), true);
		}
		else {
			// This is probably unnecessary, but I'm putting it here in case
			// this explodes somehow.
			throw new IllegalArgumentException("Invalid character in layout: " + current);
		}

		cache.put(methodInput, result);
		return result;
	}

	private LayoutData parseLayoutData(String line) {
		var parts = line.split(" ");
		var layout = parts[0];
		var groups = Arrays.stream(parts[1].split(",")).map(Integer::parseInt).toList();

		return new LayoutData(layout, groups);
	}

	private LayoutData unfoldLayout(LayoutData data) {
		var springs = String.join("?", Collections.nCopies(5, data.layout));
		var groups = Collections.nCopies(5, data.groups).stream().flatMap(list -> list.stream()).toList();
		return new LayoutData(springs, groups);
	}

	private List<Integer> reduceFirst(List<Integer> list) {
		var reduced = new ArrayList<Integer>(list);
		reduced.set(0, reduced.get(0) - 1);
		return reduced;
	}

	private <T> List<T> tail(List<T> list) {
		return list.subList(1, list.size());
	}

	private int sum(List<Integer> list) {
		return list.stream().mapToInt(i -> i).sum();
	}

	static record LayoutData(String layout, List<Integer> groups) {}
	static record CountPossibleInput(String layout, List<Integer> broken, boolean inGroup) {}

}
