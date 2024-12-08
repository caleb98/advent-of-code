package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2024_Day5 extends Solution<Long> {

	private QuestionInput input;
	private List<String> orderingRules;
	private List<String> updates;
	
	private Map<Integer, Set<Integer>> after;
	private Map<Integer, Set<Integer>> before;
	
	public AOC2024_Day5() {
		input = new QuestionInput("/inputs/day5.txt");
		
		var sections = input.getLinesSplitByBlank();
		orderingRules = sections.get(0);
		updates = sections.get(1);
		
		after = new HashMap<Integer, Set<Integer>>();
		before = new HashMap<Integer, Set<Integer>>();
		
		for (var rule : orderingRules) {
			var data = rule.split("\\|");
			int left = Integer.parseInt(data[0]);
			int right = Integer.parseInt(data[1]);
			
			after.computeIfAbsent(left, x -> new HashSet<>()).add(right);
			before.computeIfAbsent(right, x -> new HashSet<>()).add(left);
		}
	}
	
	@Override
	public Long solveFirst() {	
		return updates.stream()
			.map(this::lineToList)
			.filter(this::isOrderedCorrectly)
			.map(list -> list.get(list.size() / 2))
			.collect(Collectors.summarizingInt(x -> x))
			.getSum();
	}

	@Override
	public Long solveSecond() {		
		var fixedSum = updates.stream()
				.map(this::lineToList)
				.filter(arr -> !isOrderedCorrectly(arr))
				.map(list -> {
					// crikey...
					var fixed = new ArrayList<Integer>();
					while (list.size() > 0) {
						var addToFixed = list.removeFirst();
						boolean foundPosition = false;
						
						for (int i = 0; i <= fixed.size(); i++) {
							var fixedCopy = new ArrayList<>(fixed);
							fixedCopy.add(i, addToFixed);
							if (isOrderedCorrectly(fixedCopy)) {
								fixed = fixedCopy;
								foundPosition = true;
								break;
							}
						}
						
						if (!foundPosition) {
							System.out.println("Panic!");
						}
					}
					return fixed;
				})
				.map(list -> list.get(list.size() / 2))
				.collect(Collectors.summarizingInt(x -> x))
				.getSum();
		
		return fixedSum;
	}
	
	private List<Integer> lineToList(String line) {
		var data = line.split(",");
		var list = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			list.add(Integer.parseInt(data[i]));
		}
		return list;
	}

	private boolean isOrderedCorrectly(List<Integer> update) {
		for (int i = 0; i < update.size(); i++) {
			for (int j = 0; j < update.size(); j++) {
				if (j == i) continue;
				
				int first;
				int second;
				
				if (j < i) {
					first = update.get(j);
					second = update.get(i);
				}
				else { // j > i
					first = update.get(i);
					second = update.get(j);
				}
				
				if (after.computeIfAbsent(second, x -> new HashSet<>()).contains(first)) return false;
				if (before.computeIfAbsent(first, x -> new HashSet<>()).contains(second)) return false;
			}
		}
		
		return true;
	}
	
//	private List<Integer> fixPages(List<Integer> original) {
//		var fixed = new ArrayList<Integer>();
//		while (original.size() > 0) {
//			
//		}
//	}
	
	private boolean isBefore(Integer a, Integer b) {
		var current = new HashSet<>(before.computeIfAbsent(b, x -> new HashSet<>()));
		while (current.size() > 0) {
			if (current.contains(a)) return true;
			var next = new HashSet<Integer>();
			for (var num : current) {
				next.addAll(before.computeIfAbsent(num, x -> new HashSet<>()));
			}
			current = next;
		}
		return false;
	}
	
	private boolean isAfter(Integer a, Integer b) {
		var current = new HashSet<>(after.computeIfAbsent(b, x -> new HashSet<>()));
		while (current.size() > 0) {
			if (current.contains(a)) return true;
			var next = new HashSet<Integer>();
			for (var num : current) {
				next.addAll(after.computeIfAbsent(num, x -> new HashSet<>()));
			}
			current = next;
		}
		return false;
	}
	
	private int comparePages(Integer a, Integer b) {
		if (isBefore(a, b)) {
			return -1;
		}
		else if (isAfter(a, b)) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
}
