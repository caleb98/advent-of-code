package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day8 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day8() {
		input = new QuestionInput("/inputs/day8.txt");
	}

	@Override
	public Long solveFirst() {
		var lines = new ArrayList<>(input.getLines());
		var instructions = lines.removeFirst();
		lines.removeFirst();

		var statesList = lines.parallelStream()
			.filter(data -> !data.isBlank())
			.map(this::parseState)
			.toList();

		var states = new HashMap<String, State>();
		statesList.forEach(state -> states.put(state.name, state));

		long totalSteps = runInstructions(states, instructions, "AAA", name -> name.equals("ZZZ"));

		return totalSteps;
	}

	@Override
	public Long solveSecond() {
		var lines = new ArrayList<>(input.getLines());
		var instructions = lines.removeFirst();
		lines.removeFirst();

		var statesList = lines.parallelStream()
			.filter(data -> !data.isBlank())
			.map(this::parseState)
			.toList();

		var states = new HashMap<String, State>();
		statesList.forEach(state -> states.put(state.name, state));

		var steps = states.keySet().stream()
								.filter(name -> name.endsWith("A"))
								.map(name -> states.get(name))
								.map(state -> runInstructions(
									states,
									instructions,
									state.name,
									stateName -> stateName.endsWith("Z")
								))
								.mapToLong(Long::valueOf)
								.toArray();

		return lcm(steps);
	}

	// shamelessly yoinked from https://www.baeldung.com/java-least-common-multiple
	private Map<Long, Long> getPrimeFactors(long number) {
		long absNumber = Math.abs(number);

		Map<Long, Long> primeFactorsMap = new HashMap<>();

		for (long factor = 2; factor <= absNumber; factor++) {
			while (absNumber % factor == 0) {
				Long power = primeFactorsMap.get(factor);
				if (power == null) {
					power = 0L;
				}
				primeFactorsMap.put(factor, power + 1);
				absNumber /= factor;
			}
		}

		return primeFactorsMap;
	}

	// shamelessly yoinked from https://www.baeldung.com/java-least-common-multiple
	// but with modifications for many numbers
	private long lcm(long... nums) {
		List<Map<Long, Long>> primeFactors = Arrays.stream(nums)
													.mapToObj(this::getPrimeFactors)
													.toList();

		Set<Long> primeFactorsUnionSet = new HashSet<>();
		for (var factors : primeFactors) {
			primeFactorsUnionSet.addAll(factors.keySet());
		}

		long lcm = 1;

		for (Long primeFactor : primeFactorsUnionSet) {
			lcm *= Math.pow(
				primeFactor,
				primeFactors.stream()
					.mapToLong(factors -> factors.getOrDefault(primeFactor, 0L))
					.max()
					.orElse(0)
			);
		}

		return lcm;
	}

	private long runInstructions(Map<String, State> states, String instructions, String start, Function<String, Boolean> isEndState) {
		State currentState = states.get(start);

		long steps = 0;
		while (!isEndState.apply(currentState.name)) {
			for (char dir : instructions.toCharArray()) {
				steps++;
				if (dir == 'L') {
					currentState = states.get(currentState.left);
				}
				else {
					currentState = states.get(currentState.right);
				}
			}
		}

		return steps;
	}

	private State parseState(String line) {
		var stateData = line.split("=");
		var name = stateData[0].trim();
		var dirs = stateData[1].replaceAll("[\\(\\) ]", "").split(",");
		var left = dirs[0];
		var right = dirs[1];

		return new State(name, left, right);
	}

	static class State {

		String name;
		String left;
		String right;

		State(String name, String left, String right) {
			this.name = name;
			this.left = left;
			this.right = right;
		}

	}

}
