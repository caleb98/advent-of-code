package net.calebscode.aoc.solutions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Triple;
import net.calebscode.aoc.geometry.Point2D;
import net.calebscode.aoc.util.ArrayUtils;

public class AOC2023_Day16 extends BasicSolution<Long> {

	public AOC2023_Day16() {
		super(16);
	}

	@Override
	public Long solveFirst() {
		var layout = input.asCharArray();
		ArrayUtils.transpose(layout);
		return getTotalEnergized(layout, Triple.of(new Point2D(-1, 0), 1, 0));
	}

	@Override
	public Long solveSecond() {
		var layout = input.asCharArray();
		ArrayUtils.transpose(layout);
		long bestEnergy = 0;

		// Check vertical entry from top or bottom
		for (int col = 0; col < layout.length; col++) {
			var colBest = Math.max(
				getTotalEnergized(layout, Triple.of(new Point2D(col, -1), 0, 1)),
				getTotalEnergized(layout, Triple.of(new Point2D(col, layout.length), 0, -1))
			);

			if (colBest > bestEnergy) {
				bestEnergy = colBest;
			}
		}

		// Check horizontal entry from left or right
		for (int row = 0; row < layout[0].length; row++) {
			var rowBest = Math.max(
				getTotalEnergized(layout, Triple.of(new Point2D(-1, row), 1, 0)),
				getTotalEnergized(layout, Triple.of(new Point2D(layout[0].length, row), -1, 0))
			);

			if (rowBest > bestEnergy) {
				bestEnergy = rowBest;
			}
		}

		return bestEnergy;
	}

	private long getTotalEnergized(char[][] layout, Triple<Point2D, Integer, Integer> initial) {
		// The Triple here is a point containing the beam's current
		// position, and then two integers dx and dy that store the
		// beam's motion.
		HashSet<Point2D> energized = new HashSet<>();
		HashSet<Triple<Point2D, Integer, Integer>> allBeams = new HashSet<>();
		Queue<Triple<Point2D, Integer, Integer>> currentBeams = new LinkedList<>();

		// First beam
		currentBeams.add(initial);

		while (!currentBeams.isEmpty()) {
			var beam = currentBeams.poll();
			var nextPos = beam.first.translate(beam.second, beam.third);

			// Skip any beams that are going out of bounds.
			if (!isInBounds(nextPos, layout)) {
				continue;
			}

			var space = layout[nextPos.getY()][nextPos.getX()];

			List<Triple<Point2D, Integer, Integer>> nextBeams;

			// Mirror forward
			if (space == '/') {
				int nextDX;
				int nextDY;
				if (beam.second != 0) { // Horizontal motion, flip and make vertical
					nextDY = -beam.second;
					nextDX = 0;
				}
				else { // Vertical motion, flip and make horizontal
					nextDY = 0;
					nextDX = -beam.third;
				}
				var nextBeam = Triple.of(nextPos, nextDX, nextDY);
				nextBeams = List.of(nextBeam);
			}
			// Mirror backward
			else if (space == '\\') {
				int nextDX;
				int nextDY;
				if (beam.second != 0) { // Horizontal motion, make vertical
					nextDY = beam.second;
					nextDX = 0;
				}
				else { // Vertical motion, make horizontal
					nextDY = 0;
					nextDX = beam.third;
				}
				var nextBeam = Triple.of(nextPos, nextDX, nextDY);
				nextBeams = List.of(nextBeam);
			}
			// Vertical splitter
			else if (space == '|' && beam.second != 0) {
				var upBeam = Triple.of(nextPos, 0, -1);
				var downBeam = Triple.of(nextPos, 0, 1);
				nextBeams = List.of(upBeam, downBeam);
			}
			// Horizontal splitter
			else if (space == '-' && beam.third != 0) {
				var leftBeam = Triple.of(nextPos, -1, 0);
				var rightBeam = Triple.of(nextPos, 1, 0);
				nextBeams = List.of(leftBeam, rightBeam);
			}
			// Must be empty or effectively empty
			else {
				var nextBeam = Triple.of(nextPos, beam.second, beam.third);
				nextBeams = List.of(nextBeam);
			}

			// Filter the next beams to remove anything we've encountered
			nextBeams = nextBeams.stream()
				.filter(nextBeam -> !allBeams.contains(nextBeam))
				.toList();

			// Add next beams to processing queue
			currentBeams.addAll(nextBeams);

			// Keep track of the new beams for overall data
			allBeams.addAll(nextBeams);
			energized.addAll(nextBeams.stream().map(nextBeam -> nextBeam.first).toList());
		}

		// energized.forEach(point -> layout[point.getY()][point.getX()] = '#');

		// for (var row : layout) {
		// 	System.out.println(row);
		// }

		return (long) energized.size();
	}

	private boolean isInBounds(Point2D position, char[][] layout) {
		return position.getY() >= 0
			&& position.getX() >= 0
			&& position.getY() < layout.length
			&& position.getX() < layout[position.getY()].length;
	}

}
