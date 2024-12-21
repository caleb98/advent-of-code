package net.calebscode.aoc.solutions;

import java.util.HashSet;
import java.util.Set;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.MapGrid;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.util.Utils;

public class AOC2024_Day10 extends BasicSolution<Long> {

	private MapGrid<Integer> map;
	private Set<Point2D> trailheads;
	
	public AOC2024_Day10() {
		super(10);
		
		var chars = input.asCharacterArray();
		Utils.transpose(chars);
		var mapData = new Integer[chars.length][];
		
		trailheads = new HashSet<Point2D>(); 
		for (int x = 0; x < chars.length; x++) {
			mapData[x] = new Integer[chars[x].length];
			for (int y = 0; y < chars[x].length; y++) {
				mapData[x][y] = chars[x][y] - '0';
				if (mapData[x][y] == 0) {
					trailheads.add(new Point2D(x, y));
				}
			}
		}
		
		map = new MapGrid<>(mapData, false, (x, y) -> -1);
	}
	
	@Override
	public Long solveFirst() {
		System.out.println(trailheads);
		return trailheads.stream()
				.mapToLong(this::calculateScore)
				.sum();
	}

	@Override
	public Long solveSecond() {
		System.out.println(trailheads);
		return trailheads.stream()
				.mapToLong(this::calculateRating)
				.sum();
	}
	
	private Long calculateScore(Point2D currentPos) {
		return calculateScore(currentPos, new HashSet<Point2D>());
	}
	
	private Long calculateScore(Point2D currentPos, Set<Point2D> visited) {
		visited.add(currentPos);
		
		var currentValue = map.get(currentPos);
		if (currentValue == 9) return 1L;
		
		long next = currentValue + 1;
		var up = currentPos.translate(0, -1);
		var down = currentPos.translate(0, 1);
		var left = currentPos.translate(-1, 0);
		var right = currentPos.translate(1, 0);
		
		var sumScores = 0L;
		if (!visited.contains(up) && map.get(up) == next) sumScores += calculateScore(up, visited);
		if (!visited.contains(down) && map.get(down) == next) sumScores += calculateScore(down, visited);
		if (!visited.contains(left) && map.get(left) == next) sumScores += calculateScore(left, visited);
		if (!visited.contains(right) && map.get(right) == next) sumScores += calculateScore(right, visited);
		
		return sumScores;
	}
	
	private Long calculateRating(Point2D currentPos) {
		var currentValue = map.get(currentPos);
		if (currentValue == 9) return 1L;
		
		long next = currentValue + 1;
		var up = currentPos.translate(0, -1);
		var down = currentPos.translate(0, 1);
		var left = currentPos.translate(-1, 0);
		var right = currentPos.translate(1, 0);
		
		var sumScores = 0L;
		if (map.get(up) == next) sumScores += calculateRating(up);
		if (map.get(down) == next) sumScores += calculateRating(down);
		if (map.get(left) == next) sumScores += calculateRating(left);
		if (map.get(right) == next) sumScores += calculateRating(right);
		
		return sumScores;
	}
	
}
