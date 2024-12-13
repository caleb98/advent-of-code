package net.calebscode.aoc.solutions;

import net.calebscode.aoc.BasicSolution;

public class AOC2024_Day04 extends BasicSolution<Long> {

	private char[][] search;
	
	public AOC2024_Day04() {
		super(4);
		search = input.asCharArray();
	}
	
	@Override
	public Long solveFirst() {
		long matches = 0;
		
		for (int x = 0; x < search.length; x++) {
			for (int y = 0; y < search[x].length; y++) {
				matches += countXmasMatchesStartingFrom(x, y);
			}
		}
		
		return matches;
	}

	@Override
	public Long solveSecond() {
		long matches = 0;
		
		for (int x = 0; x < search.length; x++) {
			for (int y = 0; y < search[x].length; y++) {
				if (isXmasCrossAt(x, y)) matches++;
			}
		}
		
		return matches;
	}

	private char charAt(int x, int y) {
		if (x < 0 || x >= search.length) return ' ';
		if (y < 0 || y >= search[0].length) return ' ';
		return search[x][y];
	}
	
	private int countXmasMatchesStartingFrom(int x, int y) {
		if (charAt(x, y) != 'X') return 0;
		
		int matches = 0;
		
		// Forward
		if (charAt(x + 1, y) == 'M' && charAt(x + 2, y) == 'A' && charAt(x + 3, y) == 'S') matches++;
		
		// Backward
		if (charAt(x - 1, y) == 'M' && charAt(x - 2, y) == 'A' && charAt(x - 3, y) == 'S') matches++;
		
		// Down
		if (charAt(x, y + 1) == 'M' && charAt(x, y + 2) == 'A' && charAt(x, y + 3) == 'S') matches++;
		
		// Up
		if (charAt(x, y - 1) == 'M' && charAt(x, y - 2) == 'A' && charAt(x, y - 3) == 'S') matches++;
		
		// Forward Up
		if (charAt(x + 1, y - 1) == 'M' && charAt(x + 2, y - 2) == 'A' && charAt(x + 3, y - 3) == 'S') matches++;
		
		// Forward Down
		if (charAt(x + 1, y + 1) == 'M' && charAt(x + 2, y + 2) == 'A' && charAt(x + 3, y + 3) == 'S') matches++;
		
		// Backward Up
		if (charAt(x - 1, y - 1) == 'M' && charAt(x - 2, y - 2) == 'A' && charAt(x - 3, y - 3) == 'S') matches++;
		
		// Backward Down
		if (charAt(x - 1, y + 1) == 'M' && charAt(x - 2, y + 2) == 'A' && charAt(x - 3, y + 3) == 'S') matches++;
		
		return matches;
	}
	
	private boolean isXmasCrossAt(int x, int y) {
		if (charAt(x, y) != 'A') return false;
		
		boolean downDiagonalMatch = ((charAt(x - 1, y - 1) == 'M' && charAt(x + 1, y + 1) == 'S'))
									|| ((charAt(x - 1, y - 1) == 'S' && charAt(x + 1, y + 1) == 'M'));
		
		boolean upDiagonalMatch = ((charAt(x - 1, y + 1) == 'M' && charAt(x + 1, y - 1) == 'S'))
									|| ((charAt(x - 1, y + 1) == 'S' && charAt(x + 1, y - 1) == 'M'));
		
		return downDiagonalMatch && upDiagonalMatch;
	}
	
}
