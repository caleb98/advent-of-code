package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Pair;

public class AOC2023_Day15 extends BasicSolution<Long> {

	public AOC2023_Day15() {
		super(15);
	}

	@Override
	public Long solveFirst() {
		return input.getLines().parallelStream()
			.flatMap(line -> Arrays.stream(line.split(",")))
			.mapToLong(string -> (long) hash(string))
			.sum();
	}

	@Override
	public Long solveSecond() {
		var specs = input.getLines().parallelStream()
						.flatMap(line -> Arrays.stream(line.split(",")))
						.toList();

		Map<Integer, List<Pair<String, Integer>>> lenses = new HashMap<>();
		for (var spec : specs) {
			boolean adding = spec.contains("=");
			var specData = spec.split(adding ? "=" : "-");

			var label = specData[0];
			var box = lenses.computeIfAbsent(hash(label), i -> new ArrayList<>());

			if (adding) {
				var focalLength = Integer.valueOf(specData[1]);

				// Search for matching lens label
				int replace = -1;
				for (int i = 0; i < box.size(); i++) {
					if (box.get(i).first.equals(label)) {
						replace = i;
						break;
					}
				}

				// If present, replace
				if (replace != -1) {
					box.set(replace, Pair.of(label, focalLength));
				}
				// Otherwise, add at end
				else {
					box.add(Pair.of(label, focalLength));
				}
			}
			else {
				// Search for matching lens label
				int remove = -1;
				for (int i = 0; i < box.size(); i++) {
					if (box.get(i).first.equals(label)) {
						remove = i;
						break;
					}
				}

				// Remove if present
				if (remove != -1) {
					box.remove(remove);
				}
			}

			// Print boxes
			// System.out.println("====================");
			// for (int boxNum = 0; boxNum < 256; boxNum++) {
			// 	if (!lenses.containsKey(boxNum)) continue;

			// 	var boxLenses = lenses.get(boxNum);
			// 	var output = boxLenses.stream()
			// 					.map(pair -> String.format("[%s %d]", pair.a, pair.b))
			// 					.collect(Collectors.joining(" "));

			// 	System.out.printf("Box %3d: %s\n", boxNum, output);
			// }
		}

		long totalPower = 0;
		for (int boxNum = 0; boxNum < 256; boxNum++) {
			if (!lenses.containsKey(boxNum)) continue;
			var boxLenses = lenses.get(boxNum);

			for (int lensNum = 0; lensNum < boxLenses.size(); lensNum++) {
				var lens = boxLenses.get(lensNum);
				totalPower += (boxNum + 1) * (lensNum + 1) * lens.second;
			}
		}

		return totalPower;
	}

	private int hash(String string) {
		int current = 0;
		for (char c : string.toCharArray()) {
			current += c;
			current *= 17;
			current %= 256;
		}
		return current;
	}

}
