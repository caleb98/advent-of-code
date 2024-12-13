package net.calebscode.aoc;

public abstract class BasicSolution<T> implements Solution<T> {

	protected QuestionInput input;
	
	public BasicSolution(int day) {
		this(String.format("/inputs/day%d.txt", day));
	}
	
	public BasicSolution(String inputResourcePath) {
		input = new QuestionInput(inputResourcePath);
	}

}
