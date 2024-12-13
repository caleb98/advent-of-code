package net.calebscode.aoc.data;

import java.util.Objects;

public class Triple<A, B, C> {

	public A first;
	public B second;
	public C third;

	public Triple(A a, B b, C c) {
		this.first = a;
		this.second = b;
		this.third = c;
	}
	
	public Triple<A, B, C> copy() {
		return new Triple<>(first, second, third);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Triple other))
			return false;

		return Objects.equals(first, other.first)
			&& Objects.equals(second, other.second)
			&& Objects.equals(third, other.third);
	}

	@Override
	public String toString() {
		return String.format(
				"<%s, %s, %s>", 
				Objects.toString(first), 
				Objects.toString(second),
				Objects.toString(third));
	}
	
	public static <X, Y, Z> Triple<X, Y, Z> of(X x, Y y, Z z) {
		return new Triple<>(x, y, z);
	}

}
