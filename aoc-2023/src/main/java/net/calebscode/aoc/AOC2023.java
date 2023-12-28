package net.calebscode.aoc;

import net.calebscode.aoc.solutions.AOC2023_Day22;

public class AOC2023 {
    public static void main(String[] args) {

		var solution = new AOC2023_Day22();
		System.out.printf(
			"First Solution: %d\n",
			solution.timeFirst()
		);

		System.out.printf(
			"Second Solution: %d\n",
			solution.timeSecond()
		);

    }
}
