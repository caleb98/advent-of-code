package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.calebscode.aoc.BasicSolution;

public class AOC2024_Day01 extends BasicSolution<Integer> {

	private List<Integer> leftList;
	private List<Integer> rightList;
	
	public AOC2024_Day01() {
		super(1);
		
		var lines = input.getLines();
		leftList = new ArrayList<Integer>();
		rightList = new ArrayList<Integer>();
		
		for (var line : lines) {
			var values = line.split("\s+");
			
			var leftValue = Integer.parseInt(values[0]);
			var rightValue = Integer.parseInt(values[1]);
			
			leftList.add(leftValue);
			rightList.add(rightValue);
		}
		
		Collections.sort(leftList);
		Collections.sort(rightList);
		
		leftList = Collections.unmodifiableList(leftList);
		rightList = Collections.unmodifiableList(rightList);
	}
	
	@Override
	public Integer solveFirst() {		
		int[] differences = new int[leftList.size()];
		
		for (int i = 0; i < leftList.size(); i++) {
			differences[i] = Math.abs(leftList.get(i) - rightList.get(i));
		}
		
		return IntStream.of(differences).sum();
	}

	@Override
	public Integer solveSecond() {
		var occurrenceMap = rightList.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting()));
		int similarity = 0;
		
		for (var value : leftList) {
			similarity += value * occurrenceMap.getOrDefault(value, 0L);
		}
		
		return similarity;
	}

}
