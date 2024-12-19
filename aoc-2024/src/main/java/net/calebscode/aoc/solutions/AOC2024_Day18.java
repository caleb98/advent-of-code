package net.calebscode.aoc.solutions;

import java.util.List;
import java.util.stream.Stream;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.pathfinding.DijkstraPathfinder;

public class AOC2024_Day18 extends BasicSolution<Long> {

	// Test values
//	private int memorySize = 7;
//	private int numBytesFallen = 12;
	
	// Real values
	private int memorySize = 71;
	private int numBytesFallen = 1024;
	
	public AOC2024_Day18() {
		super(18);
	}
	
	@Override
	public Long solveFirst() {
		var memory = createMemory();
		
		var points = input.getLines().parallelStream()
				.map(line -> line.split(","))
				.map(nums -> new Point2D(Integer.parseInt(nums[0]), Integer.parseInt(nums[1])))
				.limit(numBytesFallen)
				.toList();
		
		for (var point : points) {
			memory[point.getX()][point.getY()] = '#';
		}
		
		var pathfinder = new DijkstraPathfinder<Point2D>(
			position -> Stream.of(position.up(), position.down(), position.left(), position.right()).filter(next ->
				next.getX() >= 0 && next.getX() < memorySize && next.getY() >= 0 && next.getY() < memorySize && memory[next.getX()][next.getY()] != '#').toList(),
			(from, to) -> 1,
			position -> position.getX() == memorySize - 1 && position.getY() == memorySize - 1 
		);
		
		var path = pathfinder.pathfind(List.of(new Point2D(0, 0)));
		
		for (var point : path.getPath()) {
			memory[point.getX()][point.getY()] = 'O';
		}
		
		printMemory(memory);
		
		// weird off by 2 error here for some reason?? only in real data though, test gets it fine...
		return (long) path.getPath().size() - 1;
	}

	@Override
	public Long solveSecond() {
		var memory = createMemory();
		
		var points = input.getLines().parallelStream()
				.map(line -> line.split(","))
				.map(nums -> new Point2D(Integer.parseInt(nums[0]), Integer.parseInt(nums[1])))
				.toList();
		
		int startValue = 2950;
		
		for (int i = 0; i < startValue; i++) {
			var currentPoint = points.get(i);
			memory[currentPoint.getX()][currentPoint.getY()] = '#';
		}
		
		for (int i = startValue; i < points.size(); i++) {
			var currentPoint = points.get(i);
			
			memory[currentPoint.getX()][currentPoint.getY()] = '#';
			
			var pathfinder = new DijkstraPathfinder<Point2D>(
				position -> Stream.of(position.up(), position.down(), position.left(), position.right()).filter(next ->
					next.getX() >= 0 && next.getX() < memorySize && next.getY() >= 0 && next.getY() < memorySize && memory[next.getX()][next.getY()] != '#').toList(),
				(from, to) -> 1,
				position -> position.getX() == memorySize - 1 && position.getY() == memorySize - 1 
			);
			
			var path = pathfinder.pathfind(List.of(new Point2D(0, 0)));
			
			if (path.getTotalCost() == -1) {
				System.out.println("Can't pathfind after " + currentPoint);
				break;
			}
			else {
				System.out.printf("%4d:\t%d\n", i, path.getPath().size());
			}
		}
		
		return -1L;
	}
	
	private void printMemory(Character[][] memory) {
		for (int i = 0; i < memorySize; i++) {
			for (int j = 0; j < memorySize; j++) {
				System.out.print(memory[j][i]);
			}
			System.out.println();
		}
	}
	
	private Character[][] createMemory() {
		Character[][] data = new Character[memorySize][];
		for (int i = 0; i < memorySize; i++) {
			Character[] row = new Character[memorySize];
			for (int j = 0; j < memorySize; j++) {
				row[j] = '.';
			}
			data[i] = row;
		}
		
		return data;
	}

}
