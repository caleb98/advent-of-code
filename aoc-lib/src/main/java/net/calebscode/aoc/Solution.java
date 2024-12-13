package net.calebscode.aoc;

public interface Solution<T> {

	public T solveFirst();
	public T solveSecond();

	public default void run() {
		System.out.println("=== Running First ===");
		var firstAnswer = timeFirst();
		System.out.println("=====================\n");

		System.out.println("=== Running Second ===");
		var secondAnswer = timeSecond();
		System.out.println("======================\n");
		
		System.out.println("=== Results ===");
		System.out.printf("First Solution:  %s\n", firstAnswer);
		System.out.printf("Second Solution: %s\n", secondAnswer);
		System.out.println("===============");
	}
	
	public default T timeFirst() {
		long start = System.currentTimeMillis();
		T result = solveFirst();
		long end = System.currentTimeMillis();
		System.out.printf("Solved first in %dms\n", end - start);
		return result;
	}

	public default T timeSecond() {
		long start = System.currentTimeMillis();
		T result = solveSecond();
		long end = System.currentTimeMillis();
		System.out.printf("Solved second in %dms\n", end - start);
		return result;
	}


}
