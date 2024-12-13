package net.calebscode.aoc.data;

import java.util.Objects;

public class Quad<A, B, C, D> {

	public A first;
	public B second;
	public C third;
	public D fourth;

	public Quad(A first, B second, C third, D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}
	
	public Quad<A, B, C, D> copy() {
		return new Quad<>(first, second, third, fourth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Quad other))
			return false;

		return Objects.equals(first, other.first)
			&& Objects.equals(second, other.second)
			&& Objects.equals(third, other.third)
			&& Objects.equals(fourth, other.fourth);
	}

	@Override
	public String toString() {
		return String.format(
				"<%s, %s, %s, %s>", 
				Objects.toString(first), 
				Objects.toString(second),
				Objects.toString(third),
				Objects.toString(fourth));
	}
	
	public static <W, X, Y, Z> Quad<W, X, Y, Z> of(W w, X x, Y y, Z z) {
		return new Quad<>(w, x, y, z);
	}

}
