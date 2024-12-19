package net.calebscode.aoc.geometry;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import net.calebscode.aoc.data.OrthogonalDirection;

public class Point2D {

	private int x;
	private int y;

	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point2D(Point2D other) {
		x = other.x;
		y = other.y;
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
	
	public Point2D moveForward(OrthogonalDirection facing) {
		return switch (facing) {
			case UP -> up();
			case DOWN -> down();
			case LEFT -> left();
			case RIGHT -> right();
		};
	}
	
	public Point2D moveBackward(OrthogonalDirection facing) {
		return switch (facing) {
			case UP -> down();
			case DOWN -> up();
			case LEFT -> right();
			case RIGHT -> left();
		};
	}
	
	public Point2D up() {
		return this.translate(0, -1);
	}
	
	public Point2D down() {
		return this.translate(0, 1);
	}
	
	public Point2D left() {
		return this.translate(-1, 0);
	}
	
	public Point2D right() {
		return this.translate(1, 0);
	}

	public Collection<Point2D> orthogonallyAdjacent() {
		return Set.of(up(), down(), left(), right());
	}
	
	public Collection<Point2D> adjacent() {
		return Set.of(
			up(), up().left(), up().right(),
			left(), right(),
			down(), down().left(), down().right()
		);
	}
	
	public Collection<Point2D> diagonallyAdjacent() {
		return Set.of(up().left(), up().right(), down().left(), down().right());
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
