package net.calebscode.aoc.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.calebscode.aoc.BasicSolution;

public class AOC2024_Day19 extends BasicSolution<Long> {

	private List<String> towelPatterns;
	private List<String> desiredDesigns;
	
	public AOC2024_Day19() {
		super(19);
		
		var data = input.splitByBlankLine();
		towelPatterns = Arrays.asList(data.get(0).asOneLine().split("\\s*,\\s*"));
		desiredDesigns = data.get(1).getLines();
	}
	
	@Override
	public Long solveFirst() {
		return desiredDesigns.stream()
				.filter(design -> { 
					var result = canMakeDesign(design, towelPatterns); 
					System.out.printf("%s -> %s\n", design, result);
					return result;
				})
				.count();
	}

	@Override
	public Long solveSecond() {
		return desiredDesigns.stream()
				.mapToLong(design -> numPossibleWays(design, towelPatterns))
				.sum();
	}
	
	private Map<String, Boolean> canMakeCache = new ConcurrentHashMap<>();
	private boolean canMakeDesign(String design, List<String> patterns) {
		if (design.equals("")) {
			return true;
		}
		
		if (canMakeCache.containsKey(design)) {
			return canMakeCache.get(design);
		}
		
		for (var pattern : patterns) {
			if (design.startsWith(pattern)) {
				var next = design.substring(pattern.length());
				if (canMakeDesign(next, patterns)) {
					canMakeCache.put(design, true);
					return true;
				}
			}
		}
		
		canMakeCache.put(design, false);
		return false;
	}

	private Map<String, Long> waysCache = new ConcurrentHashMap<>();
	private long numPossibleWays(String design, List<String> patterns) {
		if (design.equals("")) {
			return 1;
		}
		
		if (waysCache.containsKey(design)) {
			return waysCache.get(design);
		}
		
		long totalWays = 0;
		for (var pattern : patterns) {
			if (design.startsWith(pattern)) {
				var next = design.substring(pattern.length());
				totalWays += numPossibleWays(next, patterns);
			}
		}
		
		waysCache.put(design, totalWays);
		return totalWays;
	}

}
