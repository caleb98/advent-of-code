package net.calebscode.aoc.solutions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Pair;

public class AOC2024_Day11 extends BasicSolution<Long> {
	
	public AOC2024_Day11() {
		super(11);
	}
	
	@Override
	public Long solveFirst() {
		var stones = new LinkedList<String>();
		for (var stone : input.getLines().get(0).split("\\s+")) {
			stones.add(stone);
		}
		
		return stones.stream().mapToLong(stone -> getNumStones(stone, 25)).sum();
	}

	@Override
	public Long solveSecond() {
		var stones = new LinkedList<String>();
		for (var stone : input.getLines().get(0).split("\\s+")) {
			stones.add(stone);
		}
		
		return stones.stream().mapToLong(stone -> getNumStones(stone, 75)).sum();
	}
	
	/*
	 * Cache rules everything around me,
	 * C.R.E.A.M. get the outputs,
	 * counting all the stones, y'all 
	 */
	static HashMap<Pair<String, Integer>, Long> cache = new HashMap<>();
	public static long cacheHits = 0;
	public static long cacheMisses = 0;
	private Long getNumStones(String stone, int remainingBlinks) {
		if (remainingBlinks == 0) {
			return 1L;
		}
		
		var inputs = Pair.of(stone, remainingBlinks);
		if (cache.containsKey(inputs)) {
			cacheHits++;
			return cache.get(inputs);
		}
		cacheMisses++;
		
		long result;
		if (stone.equals("0")) {
			result = getNumStones("1", remainingBlinks - 1);

		}
		else if (stone.length() % 2 == 0) {
			var split = stone.length() / 2;
			var left = String.valueOf(Long.parseLong(stone.substring(0, split)));
			var right = String.valueOf(Long.parseLong(stone.substring(split)));
			
			result = 0;
			result += getNumStones(left, remainingBlinks - 1);
			result += getNumStones(right, remainingBlinks - 1);
		}
		else {
			var newStone = String.valueOf(Long.parseLong(stone) * 2024L);
			result = getNumStones(newStone, remainingBlinks - 1);
		}		
		
		cache.put(inputs, result);
		return result;
	}
	
	/*
	 * Original approach, takes way too long (especially for 75 iterations)
	 */
	private void doBlink(List<String> stones) {
		for (int i = 0; i < stones.size(); i++) {
			var stone = stones.get(i);
			
			if (stone.equals("0")) {
				stones.set(i, "1");
			}
			else if (stone.length() % 2 == 0) {
				var split = stone.length() / 2;
				var left = String.valueOf(Long.parseLong(stone.substring(0, split)));
				var right = String.valueOf(Long.parseLong(stone.substring(split)));
				stones.remove(i);
				stones.add(i, left);
				stones.add(i + 1, right);
				i++;
			}
			else {
				var newStone = String.valueOf(Long.parseLong(stone) * 2024L);
				stones.set(i, newStone);
			}
		}
	}

}
