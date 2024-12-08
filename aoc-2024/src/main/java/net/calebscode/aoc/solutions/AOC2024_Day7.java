package net.calebscode.aoc.solutions;

import java.util.List;
import java.util.stream.Stream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Pair;

public class AOC2024_Day7 extends Solution<Long> {

	private QuestionInput input;
	private List<Pair<Long, List<Long>>> equations;
	
	public AOC2024_Day7() {
		input = new QuestionInput("/inputs/day7.txt");
		equations = input.getLines().stream()
			.map(line -> line.split(":"))
			.map(data -> {
				var goal = Long.parseLong(data[0]);
				var numbers = Stream.of(data[1].trim().split(" ")).map(Long::parseLong).toList();
				return Pair.of(goal, numbers);
			})
			.toList();
		
//		System.out.println("Equations:");
//		equations.forEach(eq -> System.out.printf("\t%d: %s\t\t%s\n", eq.a, eq.b, canEqual(eq.a, eq.b)));
	}
	
	@Override
	public Long solveFirst() {
		return equations.parallelStream()
				.filter(eq -> canEqual(eq.a, eq.b))
				.map(eq -> eq.a)
				.mapToLong(x -> x)
				.sum();
	}

	@Override
	public Long solveSecond() {
		return equations.parallelStream()
				.filter(eq -> canEqualWithConcat(eq.a, eq.b))
				.map(eq -> eq.a)
				.mapToLong(x -> x)
				.sum();
	}
	
	private static boolean canEqual(Long goal, List<Long> numbers) {
		return canEqual(goal, numbers.getFirst(), numbers.subList(1, numbers.size()));
	}
	
	private static boolean canEqual(Long goal, Long current, List<Long> remaining) {		
		// Base Case
		if (remaining.size() == 0) {
			return goal.equals(current);
		}
		
		return canEqual(goal, current + remaining.getFirst(), remaining.subList(1, remaining.size()))
			|| canEqual(goal, current * remaining.getFirst(), remaining.subList(1, remaining.size()));
	}
	
	private static boolean canEqualWithConcat(Long goal, List<Long> numbers) {
		return canEqualWithConcat(goal, numbers.getFirst(), numbers.subList(1, numbers.size()));
	}
	
	private static boolean canEqualWithConcat(Long goal, Long current, List<Long> remaining) {		
		// Base Case
		if (remaining.size() == 0) {
			return goal.equals(current);
		}
		
		return canEqualWithConcat(goal, current + remaining.getFirst(), remaining.subList(1, remaining.size()))
			|| canEqualWithConcat(goal, current * remaining.getFirst(), remaining.subList(1, remaining.size()))
			|| canEqualWithConcat(goal, Long.parseLong(String.format("%d%d", current, remaining.getFirst())), remaining.subList(1, remaining.size()));
	}

}
