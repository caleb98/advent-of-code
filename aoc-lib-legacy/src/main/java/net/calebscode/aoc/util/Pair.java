package net.calebscode.aoc.util;

import java.util.Objects;

public class Pair<A, B> {

	public A a;
	public B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public Pair<A, B> copy() {
		return new Pair<>(a, b);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Pair other))
			return false;

		return Objects.equals(a, other.a) && Objects.equals(b, other.b);
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", Objects.toString(a), Objects.toString(b));
	}

	public static <X, Y> Pair<X, Y> of(X x, Y y) {
		return new Pair<>(x, y);
	}

}
