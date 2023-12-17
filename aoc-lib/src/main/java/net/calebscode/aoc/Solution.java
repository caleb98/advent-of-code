package net.calebscode.aoc;

public abstract class Solution<T> {

	public void printQuestionInfo() {
		System.out.println("Question information not provided.");
	}

	public abstract T solveFirst();
	public abstract T solveSecond();

	public final T timeFirst() {
		long start = System.currentTimeMillis();
		T result = solveFirst();
		long end = System.currentTimeMillis();
		System.out.printf("Solved first in %dms\n", end - start);
		return result;
	}

	public final T timeSecond() {
		long start = System.currentTimeMillis();
		T result = solveSecond();
		long end = System.currentTimeMillis();
		System.out.printf("Solved second in %dms\n", end - start);
		return result;
	}


}
