package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.List;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Pair;

public class AOC2023_Day13 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day13() {
		input = new QuestionInput("/inputs/day13.txt");
	}

	@Override
	public Long solveFirst() {
		var reflections = getLayouts().parallelStream()
							.map(this::getReflection)
							.mapToLong(this::getReflectionValue)
							.sum();

		return reflections;
	}

	@Override
	public Long solveSecond() {
		var reflections = getLayouts().parallelStream()
							.map(this::getReflectionSmudged)
							.mapToLong(this::getReflectionValue)
							.sum();

		return reflections;
	}

	private long getReflectionValue(Pair<ReflectionType, Integer> reflection) {
		return switch (reflection.a) {
			case HORIZONTAL -> 100 * reflection.b;
			case VERTICAL -> reflection.b;
		};
	}

	private Pair<ReflectionType, Integer> getReflection(Layout layout) {
		// Check horizontal
		for (int i = 1; i < layout.rows.size(); i++) {
			int spread = Math.min(i, layout.rows.size() - i);
			boolean reflects = true;

			for (int offset = 0; offset < spread; offset++) {
				int top = i - 1 - offset;
				int bottom = i + offset;
				if (!layout.rows.get(top).equals(layout.rows.get(bottom))) {
					reflects = false;
					break;
				}
			}

			if (reflects) {
				return Pair.of(ReflectionType.HORIZONTAL, i);
			}
		}

		// Check vertical
		for (int i = 1; i < layout.cols.size(); i++) {
			int spread = Math.min(i, layout.cols.size() - i);
			boolean reflects = true;

			for (int offset = 0; offset < spread; offset++) {
				int left = i - 1 - offset;
				int right = i + offset;
				if (!layout.cols.get(left).equals(layout.cols.get(right))) {
					reflects = false;
					break;
				}
			}

			if (reflects) {
				return Pair.of(ReflectionType.VERTICAL, i);
			}
		}

		throw new IllegalArgumentException("No reflection.");
	}

	private Pair<ReflectionType, Integer> getReflectionSmudged(Layout layout) {
		Pair<ReflectionType, Integer> bestReflection = null;

		// Check horizontal
		for (int i = 1; i < layout.rows.size(); i++) {
			int spread = Math.min(i, layout.rows.size() - i);
			boolean reflects = true;
			boolean smudge = false;

			for (int offset = 0; offset < spread; offset++) {
				var top = layout.rows.get(i - 1 - offset);
				var bottom = layout.rows.get(i + offset);

				for (int charPos = 0; charPos < top.length(); charPos++) {
					boolean charsMatch = top.charAt(charPos) == bottom.charAt(charPos);
					if (!charsMatch && !smudge) {
						smudge = true;
					}
					else if (!charsMatch) {
						reflects = false;
						break;
					}
				}

				if (!reflects) {
					break;
				}
			}

			// Now we only count the reflection if there was also a smudge
			if (reflects && smudge && (bestReflection == null || bestReflection.b < i)) {
				bestReflection = Pair.of(ReflectionType.HORIZONTAL, i);
			}
		}

		// Check vertical
		for (int i = 1; i < layout.cols.size(); i++) {
			int spread = Math.min(i, layout.cols.size() - i);
			boolean reflects = true;
			boolean smudge = false;

			for (int offset = 0; offset < spread; offset++) {
				var left = layout.cols.get(i - 1 - offset);
				var right = layout.cols.get(i + offset);

				for (int charPos = 0; charPos < left.length(); charPos++) {
					boolean charsMatch = left.charAt(charPos) == right.charAt(charPos);
					if (!charsMatch && !smudge) {
						smudge = true;
					}
					else if (!charsMatch) {
						reflects = false;
						break;
					}
				}

				if (!reflects) {
					break;
				}
			}

			if (reflects && smudge && (bestReflection == null || bestReflection.b < i)) {
				bestReflection = Pair.of(ReflectionType.VERTICAL, i);
			}
		}

		if (bestReflection == null)
			throw new IllegalArgumentException("No reflection with smudge.");

		return bestReflection;
	}

	private List<Layout> getLayouts() {
		var maps = new ArrayList<List<String>>();
		var current = new ArrayList<String>();

		for (var line : input.getLines()) {
			if (line.isBlank()) {
				maps.add(current);
				current = new ArrayList<>();
			}
			else {
				current.add(line);
			}
		}

		if (!current.isEmpty())
			maps.add(current);

		return maps.stream().map(Layout::new).toList();
	}

	static enum ReflectionType {
		VERTICAL,
		HORIZONTAL,
	}

	static class Layout {

		List<String> rows;
		List<String> cols;

		Layout(List<String> rows) {
			this.rows = rows;

			cols = new ArrayList<String>();
			for (int i = 0; i < rows.get(0).length(); i++) {
				var sb = new StringBuilder();
				for (var row : rows) {
					sb.append(row.charAt(i));
				}
				cols.add(sb.toString());
			}
		}

	}

}
