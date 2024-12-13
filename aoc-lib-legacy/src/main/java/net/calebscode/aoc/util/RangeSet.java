package net.calebscode.aoc.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;

public class RangeSet {

	private Set<Range> ranges;

	public RangeSet() {
		ranges = new HashSet<>();
	}

	public long length() {
		return ranges.stream().mapToLong(Range::length).sum();
	}

	public LongStream stream() {
		return ranges.stream().flatMapToLong(Range::stream).sorted();
	}

	public void add(Range range) {
		// If the range overlaps any existing range,
		// replace them with their union.
		var iter = ranges.iterator();
		var current = range;
		while (iter.hasNext()) {
			var present = iter.next();

			if (current.isAdjacent(present)) {
				iter.remove();
				current = current.union(present);
			}
		}

		ranges.add(current);
	}

	public RangeSet union(RangeSet other) {
		RangeSet union = new RangeSet();
		var allRanges = new HashSet<Range>(ranges);
		allRanges.addAll(ranges);
		allRanges.addAll(other.ranges);
		allRanges.forEach(union::add);
		return union;
	}

}
