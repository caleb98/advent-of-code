package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day5 extends Solution<Long> {

	private QuestionInput input;

	private List<Long> seeds = new ArrayList<>();
	private List<Range> seedRanges = new ArrayList<>();
	private Map<Range, Range> seedToSoil;
	private Map<Range, Range> soilToFert;
	private Map<Range, Range> fertToWater;
	private Map<Range, Range> waterToLight;
	private Map<Range, Range> lightToTemp;
	private Map<Range, Range> tempToHumid;
	private Map<Range, Range> humidToLoc;

	public AOC2023_Day5() {
		input = new QuestionInput("/inputs/day5.txt");
		parseInput();
	}

	@Override
	public Long solveFirst() {
		var minLoc = seeds.parallelStream()
			.map(this::getLocationFromSeed)
			.reduce(Long.MAX_VALUE, Long::min);

		return minLoc;
	}

	@Override
	public Long solveSecond() {
		var minLoc = seedRanges.parallelStream()
			.flatMap(range -> getLocationFromSeedRange(range).stream())
			.map(range -> range.start)
			.reduce(Long.MAX_VALUE, Long::min);

		return minLoc;
	}

	private void parseInput() {
		var lines = input.getLines();
		var seedString = lines.getFirst().split(":")[1].trim();
		var seedData = seedString.split(" ");

		for (var seed : seedData) {
			seeds.add(Long.parseLong(seed));
		}

		for (int i = 0; i < seedData.length; i += 2) {
			var start = Long.parseLong(seedData[i]);
			var end = start + Long.parseLong(seedData[i + 1]);
			seedRanges.add(new Range(start, end));
		}

		int start;
		int end;

		for (int i = 1; i < lines.size(); i++) {
			var line = lines.get(i);

			switch (line) {

				case "seed-to-soil map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					seedToSoil = parseMapData(lines, start, end);
					i = end;
				}

				case "soil-to-fertilizer map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					soilToFert = parseMapData(lines, start, end);
					i = end;
				}

				case "fertilizer-to-water map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					fertToWater = parseMapData(lines, start, end);
					i = end;
				}

				case "water-to-light map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					waterToLight = parseMapData(lines, start, end);
					i = end;
				}

				case "light-to-temperature map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					lightToTemp = parseMapData(lines, start, end);
					i = end;
				}

				case "temperature-to-humidity map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					tempToHumid = parseMapData(lines, start, end);
					i = end;
				}

				case "humidity-to-location map:" -> {
					start = i + 1;
					end = findEndOfData(lines, start);
					humidToLoc = parseMapData(lines, start, end);
					i = end;
				}

			}
		}
	}

	private int findEndOfData(List<String> lines, int start) {
		for (int i = start; i < lines.size(); i++) {
			if (lines.get(i).isBlank()) {
				return i;
			}
		}
		return lines.size();
	}

	private Map<Range, Range> parseMapData(List<String> lines, int start, int end) {
		var map = new HashMap<Range, Range>();

		for (int i = start; i < end; i++) {
			var data = lines.get(i).split(" ");
			var range = Long.parseLong(data[2]);
			var sourceStart = Long.parseLong(data[1]);
			var destStart = Long.parseLong(data[0]);

			var sourceRange = new Range(sourceStart, sourceStart + range);
			var destRange = new Range(destStart, destStart + range);

			map.put(sourceRange, destRange);
		}

		return map;
	}

	private long getLocationFromSeed(long seed) {
		var soil = getMapping(seedToSoil, seed);
		var fert = getMapping(soilToFert, soil);
		var water = getMapping(fertToWater, fert);
		var light = getMapping(waterToLight, water);
		var temp = getMapping(lightToTemp, light);
		var humid = getMapping(tempToHumid, temp);
		var loc = getMapping(humidToLoc, humid);

		return loc;
	}

	private List<Range> getLocationFromSeedRange(Range seed) {
		var soil = getMapping(seedToSoil, List.of(seed));
		var fert = getMapping(soilToFert, soil);
		var water = getMapping(fertToWater, fert);
		var light = getMapping(waterToLight, water);
		var temp = getMapping(lightToTemp, light);
		var humid = getMapping(tempToHumid, temp);
		var loc = getMapping(humidToLoc, humid);

		return loc;
	}

	private List<Range> getMapping(Map<Range, Range> map, List<Range> from) {
		var splits = new ArrayList<Range>(from);

		for (var sourceRange : map.keySet()) {
			var newSplits = new ArrayList<Range>();
			for (var split : splits) {
				newSplits.addAll(split.intersection(sourceRange));
				newSplits.addAll(split.difference(sourceRange));
			}
			splits = newSplits;
		}

		return splits.stream()
			.map(range -> getRangeMapping(map, range))
			.toList();
	}

	// Assumes range is completely inside the mapping that will map it.
	private Range getRangeMapping(Map<Range, Range> map, Range from) {
	for (var sourceRange : map.keySet()) {
			if (sourceRange.contains(from.start)) {
				var destRange = map.get(sourceRange);
				var offset = from.start - sourceRange.start;

				var start = destRange.start + offset;
				var end = start + from.length();
				return new Range(start, end);
			}
		}

		return from;
	}

	private long getMapping(Map<Range, Range> map, long from) {
		for (var sourceRange : map.keySet()) {
			if (sourceRange.contains(from)) {
				var destRange = map.get(sourceRange);
				var offset = from - sourceRange.start;
				return destRange.start + offset;
			}
		}

		return from;
	}

	private static class Range {

		long start;
		long end; // non-inclusive

		Range(long start, long end) {
			this.start = start;
			this.end = end;
		}

		boolean contains(long value) {
			return start <= value && value < end;
		}

		long length() {
			return end - start;
		}

		List<Range> intersection(Range other) {
			// No overlap
			if (other.start >= end || start >= other.end) {
				return List.of();
			}
			// This range completely inside of other range
			else if (start >= other.start && end <= other.end) {
				return List.of(this);
			}
			// Other range completely inside this range
			else if (other.start >= start && other.end <= end) {
				return List.of(other);
			}

			// Partial overlap
			return List.of(new Range(Long.max(start, other.start), Long.min(end, other.end)));
		}

		List<Range> difference(Range other) {
			// If other range completely includes this one
			if (other.start <= start && other.end >= end) {
				return List.of();
			}

			var differences = new ArrayList<Range>();

			// This range completely includes other (two ranges result)
			if (start < other.start && end > other.end) {
				differences.add(new Range(start, other.start));
				differences.add(new Range(other.end, end));
			}
			// No overlap
			else if (end <= other.start || start >= other.end) {
				differences.add(this);
			}
			// This range is shifted forward from the other
			else if (start < other.start) {
				differences.add(new Range(start, other.start));
			}
			// This range is shifted backward from the other
			else if (end > other.end) {
				differences.add(new Range(other.end, end));
			}

			return differences;
		}

		@Override
		public int hashCode() {
			return Objects.hash(start, end);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Range other = (Range) obj;
			if (start != other.start)
				return false;
			if (end != other.end)
				return false;
			return true;
		}

	}

}
