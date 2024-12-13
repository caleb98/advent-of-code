package net.calebscode.aoc.numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

public class Range {

	private long start;
	private long end; // non-inclusive

	public Range(long start, long end) {
		this.start = start;
		this.end = end;
	}

	public boolean contains(long value) {
		return start <= value && value < end;
	}

	public long length() {
		return end - start;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	/**
	 * @return the sum of all values in this range
	 */
	public long sum() {
		return (long) (length() * (start + (end - 1))) / 2;
	}

	public LongStream stream() {
		return LongStream.range(start, end);
	}

	public boolean overlaps(Range other) {
		return start < other.end && other.start < end;
	}

	public boolean isAdjacent(Range other) {
		return start <= other.end && other.start <= end;
	}

	/**
	 * Splits the range based on the provided split position.
	 * If the split is outside of this range, the result will
	 * be a list containing only the range itself. Otherwise,
	 * this method will return a list of two ranges: one from
	 * [start, split) and another [split, end)
	 *
	 * @param split the value to split at
	 * @return
	 */
	public List<Range> split(long split) {
		if (split <= start || split >= end) return List.of(this);
		return List.of(
			new Range(start, split),
			new Range(split, end)
		);
	}

	public List<Range> intersection(Range other) {
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

	public List<Range> difference(Range other) {
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

	public Range union(Range other) {
		if (!isAdjacent(other)) {
			throw new IllegalArgumentException("Ranges must be adjacent to call Range.union()");
		}

		return new Range(Math.min(start, other.start), Math.max(end, other.end));
	}

    @Override
    public String toString() {
        return String.format("[%d, %d]", start, end - 1);
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
