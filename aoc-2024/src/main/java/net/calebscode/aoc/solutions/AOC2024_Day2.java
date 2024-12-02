package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2024_Day2 extends Solution<Long> {

	private QuestionInput input;
	private List<List<Long>> reports;
	
	public AOC2024_Day2() {
		input = new QuestionInput("/inputs/day2.txt");
		
		reports = input.getLines().stream()
				.map(line -> line.split("\s+"))
				.map(values -> Stream.of(values).map(Long::parseLong).toList())
				.map(values -> Collections.unmodifiableList(values))
				.toList();
	}
	
	private boolean isSafe(List<Long> report) {
		if (report.size() <= 1) return true;
		
		double sign = report.get(1) - report.get(0);
		
		// Already violates the rule of needing to differ by at least one.
		if (sign == 0) return false;
		
		for (int i = 1; i < report.size(); i++) {
			double difference = report.get(i) - report.get(i - 1);
			
			// If the difference between any two levels divided by the sign
			// (the difference between the first two levels) is negative, then
			// it means that we swapped between increasing/decreasing.
			if (difference / sign < 0) return false;
			
			if (Math.abs(difference) < 1 || Math.abs(difference) > 3) return false;
		}
		
		return true;
	}
	
	private boolean isSafeWithout(List<Long> report, int withoutIndex) {
		var copy = new ArrayList<Long>(report);
		copy.remove(withoutIndex);
		return isSafe(copy);
	}
	
	// Day two and we're already doing some crazy brute force stuff...
	// bodes well for this year's advent of code
	private boolean isSafeWithDampener(List<Long> report) {
		if (report.size() <= 1) return true;
		
		double sign = report.get(1) - report.get(0);
		
		// See if we're already violating a rule.
		if (sign == 0) {
			return isSafeWithout(report, 0);
		};
		
		
		for (int i = 1; i < report.size(); i++) {
			double difference = report.get(i) - report.get(i - 1);
			
			// If the difference between any two levels divided by the sign
			// (the difference between the first two levels) is negative, then
			// it means that we swapped between increasing/decreasing.
			if (difference / sign < 0) {
				if (isSafeWithout(report, i) || isSafeWithout(report, i - 1)) {
					return true;
				}
				else {
					break;
				}
			};
			
			if (Math.abs(difference) < 1 || Math.abs(difference) > 3) {
				if (isSafeWithout(report, i) || isSafeWithout(report, i - 1)) {
					return true;
				}
				else {
					break;
				}
			}
		}
		
		// Handle edge case where first number can be removed but doesn't
		// trigger a failure immediately.
		return isSafeWithout(report, 0);
	}
	
	@Override
	public Long solveFirst() {
		long totalSafe = 0;
		
		for (var report : reports) {
			if (isSafe(report)) totalSafe++;
		}
		
		return totalSafe;
	}

	@Override
	public Long solveSecond() {
		long totalSafe = 0;
		
		for (var report : reports) {
			if (isSafeWithDampener(report)) totalSafe++;
		}
		
		var groups = reports.stream().collect(Collectors.groupingBy(this::isSafeWithDampener));

		System.out.println("Safe:");
		for (var safe : groups.get(true)) {
			System.out.println("\t" + safe);
		}
		
		System.out.println("\nUnsafe:");
		for (var safe : groups.get(false)) {
			System.out.println("\t" + safe);
		}
		
		return totalSafe;
	}

}
