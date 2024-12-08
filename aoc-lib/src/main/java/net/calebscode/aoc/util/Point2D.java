package net.calebscode.aoc.util;

import java.util.Objects;

public class Point2D {

	private int x;
	private int y;

	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double distance(Point2D other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}

	public int manhattanDistance(Point2D other) {
		return Math.abs(x - other.x) + Math.abs(y - other.y);
	}
	
	public Point2D translate(Point2D amount) {
		return translate(amount.x, amount.y);
	}

	public Point2D translate(int x, int y) {
		return new Point2D(this.x + x, this.y + y);
	}
	
	public Point2D negate() {
		return new Point2D(-this.x, -this.y);
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Point2D other))
			return false;

		return Objects.equals(x, other.x)
			&& Objects.equals(y, other.y);
	}

}
