package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Grid;
import net.calebscode.aoc.geometry.Point2D;

public class AOC2024_Day08 extends BasicSolution<Long> {

	private Grid<Character> nodeMap;
	
	public AOC2024_Day08() {
		super(8);
		nodeMap = input.asCharacterGrid(false, false);
	}
	
	@Override
	public Long solveFirst() {
		var antennaMap = new HashMap<Character, List<Point2D>>();
		for (int x = 0; x < nodeMap.getWidth(); x++) {
			for (int y = 0; y < nodeMap.getHeight(); y++) {
				var current = nodeMap.get(x, y);
				if (Character.isDigit(current) || Character.isAlphabetic(current)) {
					var antennas = antennaMap.computeIfAbsent(current, c -> new ArrayList<>());
					antennas.add(new Point2D(x, y));
				}
			}
		}
		
		var antinodes = new HashSet<Point2D>();
		for (var antennaClass : antennaMap.keySet()) {
			antinodes.addAll(getAntinodesForAntennas(antennaMap.get(antennaClass)));
		}
		
		return antinodes.stream().filter(nodeMap::isInside).count();
	}

	@Override
	public Long solveSecond() {
		var antennaMap = new HashMap<Character, List<Point2D>>();
		for (int x = 0; x < nodeMap.getWidth(); x++) {
			for (int y = 0; y < nodeMap.getHeight(); y++) {
				var current = nodeMap.get(x, y);
				if (Character.isDigit(current) || Character.isAlphabetic(current)) {
					var antennas = antennaMap.computeIfAbsent(current, c -> new ArrayList<>());
					antennas.add(new Point2D(x, y));
				}
			}
		}
		
		var antinodes = new HashSet<Point2D>();
		for (var antennaClass : antennaMap.keySet()) {
			antinodes.addAll(getAntinodesForAntennasExtended(antennaMap.get(antennaClass)));
		}
		
		for (int y = 0; y < nodeMap.getHeight(); y++) {
			for (int x = 0; x < nodeMap.getWidth(); x++) {
				var current = nodeMap.get(x, y);
				if (Character.isDigit(current) || Character.isAlphabetic(current)) {
					System.out.print(current);
				}
				else {
					System.out.print(antinodes.contains(new Point2D(x, y)) ? '#' : '.');
				}
			}
			System.out.println();
		}
		
		return antinodes.stream().filter(nodeMap::isInside).count();
	}

	private Set<Point2D> getAntinodesForAntennas(List<Point2D> antennas) {
		var antinodes = new HashSet<Point2D>();
		for (int i = 0; i < antennas.size(); i++) {
			for (int j = i + 1; j < antennas.size(); j++) {
				antinodes.addAll(getAntinodes(antennas.get(i), antennas.get(j)));
			}
		}
		return antinodes;
	}
	
	private Set<Point2D> getAntinodes(Point2D a, Point2D b) {
		Point2D aToB = new Point2D(b.getX() - a.getX(), b.getY() - a.getY());
		Point2D bToA = new Point2D(a.getX() - b.getX(), a.getY() - b.getY());
		
		var antinodes = new HashSet<Point2D>();
		antinodes.add(b.translate(aToB));
		antinodes.add(a.translate(bToA));
		
		return antinodes;
	}
	
	private Set<Point2D> getAntinodesForAntennasExtended(List<Point2D> antennas) {
		var antinodes = new HashSet<Point2D>();
		for (int i = 0; i < antennas.size(); i++) {
			for (int j = i + 1; j < antennas.size(); j++) {
				antinodes.addAll(getAntinodesExtended(antennas.get(i), antennas.get(j)));
			}
		}
		return antinodes;
	}
	
	private Set<Point2D> getAntinodesExtended(Point2D a, Point2D b) {
		Point2D aToB = new Point2D(b.getX() - a.getX(), b.getY() - a.getY());
		Point2D bToA = new Point2D(a.getX() - b.getX(), a.getY() - b.getY());
		
		var antinodes = new HashSet<Point2D>();
		antinodes.add(a);
		antinodes.add(b);
		
		var aExtension = a.translate(bToA);
		var bExtension = b.translate(aToB);
		
		while (nodeMap.isInside(aExtension)) {
			antinodes.add(aExtension);
			aExtension = aExtension.translate(bToA);
		}
		
		while (nodeMap.isInside(bExtension)) {
			antinodes.add(bExtension);
			bExtension = bExtension.translate(aToB);
		}
		
		return antinodes;
	}
	
}
