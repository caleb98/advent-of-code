package net.calebscode.aoc.util;

import java.util.Objects;

public class PointLong {

	private long x;
	private long y;

	public PointLong(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public void setX(long x) {
		this.x = x;
	}

	public void setY(long y) {
		this.y = y;
	}

	public double distance(PointLong other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}

	public long manhattanDistance(PointLong other) {
		return Math.abs(x - other.x) + Math.abs(y - other.y);
	}

	public PointLong translate(long x, long y) {
		return new PointLong(this.x + x, this.y + y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof PointLong other))
			return false;

		return Objects.equals(x, other.x)
			&& Objects.equals(y, other.y);
	}

}
