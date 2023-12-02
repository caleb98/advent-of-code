package net.calebscode.aoc;

public abstract class Solution<T> {

	public void printQuestionInfo() {
		System.out.println("Question information not provided.");
	}

	public abstract T solveFirst();
	public abstract T solveSecond();

}
