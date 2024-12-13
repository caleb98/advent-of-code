package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import net.calebscode.aoc.BasicSolution;

public class AOC2024_Day03 extends BasicSolution<Long> {

	private Pattern multPattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
	private Pattern doPattern = Pattern.compile("do\\(\\)");
	private Pattern dontPattern = Pattern.compile("don't\\(\\)");
	
	public AOC2024_Day03() {
		super(3);
	}
	
	@Override
	public Long solveFirst() {
		var instructions = input.getAllInput();
		var matcher = multPattern.matcher(instructions);
		
		long sum = 0;
		while (matcher.find()) {
			long first = Long.parseLong(matcher.group(1));
			long second = Long.parseLong(matcher.group(2));
			sum += first * second;
		}
		
		return sum;
	}

	@Override
	public Long solveSecond() {
		var instructions = input.getAllInput();
		var multMatcher = multPattern.matcher(instructions);
		var doMatcher = doPattern.matcher(instructions);
		var dontMatcher = dontPattern.matcher(instructions);
		
		var instructionIndices = new HashMap<Integer, Instruction>();
		
		while(multMatcher.find()) {
			long first = Long.parseLong(multMatcher.group(1));
			long second = Long.parseLong(multMatcher.group(2));
			
			instructionIndices.put(multMatcher.start(), mult(first, second));
		}
		
		while (doMatcher.find()) {
			instructionIndices.put(doMatcher.start(), DO);
		}
		
		while (dontMatcher.find()) {
			instructionIndices.put(dontMatcher.start(), DONT);
		}
		
		var sortedIndices = new ArrayList<Integer>(instructionIndices.keySet());
		Collections.sort(sortedIndices);
		
		boolean enabled = true;
		long sum = 0;
		for (int index : sortedIndices) {
			var instruction = instructionIndices.get(index);
			if (instruction.isDo) {
				enabled = true;
			}
			else if (instruction.isDont) {
				enabled = false;
			}
			else if (enabled) {
				sum += instruction.a * instruction.b;
			}
		}
		
		
		return sum;
	}
	
	private static Instruction mult(long a, long b) {
		return new Instruction(false, false, a, b);
	}

	private static Instruction DO = new Instruction(true, false, 0, 0);
	private static Instruction DONT = new Instruction(false, true, 0, 0);
	
	record Instruction(boolean isDo, boolean isDont, long a, long b) {}

}
