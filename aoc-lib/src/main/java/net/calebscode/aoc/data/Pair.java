package net.calebscode.aoc.data;

import java.util.Objects;

public class Pair<A, B> {

	public A first;
	public B second;

	public Pair(A a, B b) {
		this.first = a;
		this.second = b;
	}

	public Pair<A, B> copy() {
		return new Pair<>(first, second);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Pair other))
			return false;

		return Objects.equals(first, other.first) 
			&& Objects.equals(second, other.second);
	}

	@Override
	public String toString() {
		return String.format("<%s, %s>", Objects.toString(first), Objects.toString(second));
	}

	public static <X, Y> Pair<X, Y> of(X x, Y y) {
		return new Pair<>(x, y);
	}
	
}
