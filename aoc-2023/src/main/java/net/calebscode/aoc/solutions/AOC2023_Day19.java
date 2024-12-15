package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Pair;
import net.calebscode.aoc.numeric.Range;

public class AOC2023_Day19 extends BasicSolution<Long> {

	private static final Pattern WORKFLOW_PATTERN = Pattern.compile("(\\w+)\\{(.*)\\}");

	public AOC2023_Day19() {
		super(19);
	}

	@Override
	public Long solveFirst() {
		var sections = input.splitByBlankLine();
		var workflowsList = sections.get(0);
		var partsList = sections.get(1);

		var workflows = parseWorkflows(workflowsList.getLines());
		var parts = partsList.getLines().parallelStream().map(this::parsePart).toList();

		return parts.stream().filter(part -> {
				String current = "in";
				while (!isFinishState(current)) {
					current = workflows.get(current).apply(part);
				}

				return current.equals("A");
			})
			.mapToLong(part -> part.x + part.m + part.a + part.s)
			.sum();
	}

	@Override
	public Long solveSecond() {
		var sections = input.splitByBlankLine();
		var workflowsList = sections.get(0).getLines();

		var workflows = parseRangedWorkflows(workflowsList);
		var accepted = calculateRanges(workflows, "in", new PartRange());

		return accepted.stream().mapToLong(PartRange::count).sum();
	}

	private List<PartRange> calculateRanges(
		Map<String, Function<PartRange, List<Pair<PartRange, String>>>> workflows,
		String workflowName,
		PartRange parts
	) {
		var current = workflows.get(workflowName);
		var splits = current.apply(parts);
		return splits.stream().flatMap(pair -> {
			if (pair.second.equals("R")) {
				return Stream.of();
			}
			else if (pair.second.equals("A")) {
				return Stream.of(pair.first);
			}

			return calculateRanges(workflows, pair.second, pair.first).stream();
		}).toList();
	}

	private boolean isFinishState(String state) {
		return "A".equals(state) || "R".equals(state);
	}

	private Map<String, Function<Part, String>> parseWorkflows(List<String> data) {
		Map<String, Function<Part, String>> workflows = new HashMap<>();
		for (var workflowData : data) {
			var matcher = WORKFLOW_PATTERN.matcher(workflowData);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid workflow: " + workflowData);
			}

			var name = matcher.group(1);
			var stepData = matcher.group(2).split(",");

			final var steps = new ArrayList<WorkflowStep>();
			for (int i = 0; i < stepData.length - 1; i++) {
				var parts = stepData[i].split(":");
				var comparison = parts[0];
				var result = parts[1];

				var variable = comparison.charAt(0);
				var compare = comparison.charAt(1);
				var value = Integer.parseInt(comparison.substring(2));

				steps.add(new WorkflowStep(compare, variable, value, result));
			}

			workflows.put(name, (part) -> {
				for (var step : steps) {
					if (step.test(part)) return step.result;
				}
				return stepData[stepData.length - 1];
			});
		}

		return workflows;
	}

	private Map<String, Function<PartRange, List<Pair<PartRange, String>>>> parseRangedWorkflows(List<String> data) {
		Map<String, Function<PartRange, List<Pair<PartRange, String>>>> workflows = new HashMap<>();
		for (var workflowData : data) {
			var matcher = WORKFLOW_PATTERN.matcher(workflowData);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid workflow: " + workflowData);
			}

			var name = matcher.group(1);
			var stepData = matcher.group(2).split(",");

			final var steps = new ArrayList<WorkflowStep>();
			for (int i = 0; i < stepData.length - 1; i++) {
				var parts = stepData[i].split(":");
				var comparison = parts[0];
				var result = parts[1];

				var variable = comparison.charAt(0);
				var compare = comparison.charAt(1);
				var value = Integer.parseInt(comparison.substring(2));

				steps.add(new WorkflowStep(compare, variable, value, result));
			}

			workflows.put(name, (part) -> {
				var splits = new ArrayList<Pair<PartRange, String>>();
				var current = part;
				for (var step : steps) {
					var split = step.split(current);

					// Add passing split
					if (split.first != null) splits.add(Pair.of(split.first, step.result));

					// Propogate failing split
					current = split.second;

					// Don't bother with the rest if nothing to propogate
					if (current == null) break;
				}

				// If we had any remaining part, pair it up with the default state
				if (current != null) {
					splits.add(Pair.of(current, stepData[stepData.length - 1]));
				}

				return splits;
			});
		}

		return workflows;
	}

	private Part parsePart(String line) {
		var data = line.replaceAll("[\\{\\}]", "").split(",");
		Part part = new Part(0, 0, 0, 0);
		for (var setting : data) {
			var settingData = setting.split("=");
			var name = settingData[0];
			var value = Integer.parseInt(settingData[1]);

			switch (name) {
				case "x" -> part.x = value;
				case "m" -> part.m = value;
				case "a" -> part.a = value;
				case "s" -> part.s = value;
			}
		}
		return part;
	}

	private static class WorkflowStep {
		char compareType;
		char compareVar;
		int compareValue;
		String result;

		public WorkflowStep(char compareType, char compareVar, int compareValue, String result) {
			this.compareType = compareType;
			this.compareVar = compareVar;
			this.compareValue = compareValue;
			this.result = result;
		}

		boolean test(Part part) {
			var value = switch (compareVar) {
				case 'x' -> part.x;
				case 'm' -> part.m;
				case 'a' -> part.a;
				case 's' -> part.s;

				default -> throw new IllegalArgumentException("Invalid WorkflowStep compareVar: " + compareVar);
			};

			return compareType == '>' ? value > compareValue : value < compareValue;
		}

		// Pair<Passes, Fails>
		Pair<PartRange, PartRange> split(PartRange part) {
			var range = switch (compareVar) {
				case 'x' -> part.x;
				case 'm' -> part.m;
				case 'a' -> part.a;
				case 's' -> part.s;

				default -> throw new IllegalArgumentException("Invalid WorkflowStep compareVar: " + compareVar);
			};

			var split = range.split(compareValue + (compareType == '>' ? 1 : 0));

			// The split didn't affect anything, so we just need to figure out whether the range
			// passes or fails this step.
			if (split.size() == 1) {
				boolean isRangeSmaller = range.getEnd() <= compareValue;
				return (isRangeSmaller ^ (compareType == '<')) ? Pair.of(null, part) : Pair.of(part, null);
			}

			var leftRange = split.get(0);
			var rightRange = split.get(1);

			PartRange left, right;

			switch (compareVar) {
				case 'x' -> {
					left = new PartRange(leftRange, part.m, part.a, part.s);
					right = new PartRange(rightRange, part.m, part.a, part.s);
				}
				case 'm' -> {
					left = new PartRange(part.x, leftRange, part.a, part.s);
					right = new PartRange(part.x, rightRange, part.a, part.s);
				}
				case 'a' -> {
					left = new PartRange(part.x, part.m, leftRange, part.s);
					right = new PartRange(part.x, part.m, rightRange, part.s);
				}
				case 's' -> {
					left = new PartRange(part.x, part.m, part.a, leftRange);
					right = new PartRange(part.x, part.m, part.a, rightRange);
				}

				default -> throw new IllegalArgumentException("Invalid WorkflowStep compareVar: " + compareVar);
			};

			return compareType == '<' ? Pair.of(left, right) : Pair.of(right, left);
		}
	}

	private static class PartRange {
		Range x, m, a, s;

		PartRange() {
			this(
				new Range(1, 4001),
				new Range(1, 4001),
				new Range(1, 4001),
				new Range(1, 4001)
			);
		}

		PartRange(Range x, Range m, Range a, Range s) {
			this.x = x;
			this.m = m;
			this.a = a;
			this.s = s;
		}

		long count() {
			return x.length() * m.length() * a.length() * s.length();
		}

		@Override
		public String toString() {
			return String.format("Part(x=%s m=%s a=%s s=%s)", x, m, a, s);
		}
	}

	private static class Part {
		int x, m, a, s;

		Part(int x, int m, int a, int s) {
			this.x = x;
			this.m = m;
			this.a = a;
			this.s = s;
		}
	}

}
