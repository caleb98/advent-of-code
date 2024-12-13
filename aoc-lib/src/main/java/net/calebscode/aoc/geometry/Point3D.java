package net.calebscode.aoc.geometry;

import java.util.Objects;

public class Point3D {

	private int x;
	private int y;
	private int z;

	public Point3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public double distance(Point3D other) {
		return Math.sqrt(
			Math.pow(x - other.x, 2)
			+ Math.pow(y - other.y, 2)
			+ Math.pow(z - other.z, 2));
	}

	public Point3D translate(int x, int y, int z) {
		return new Point3D(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d)", x, y, z);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Point3D other))
			return false;

		return Objects.equals(x, other.x)
			&& Objects.equals(y, other.y)
			&& Objects.equals(z, other.z);
	}

}
