package net.calebscode.aoc.util;

import java.util.Objects;

public class Triple<A, B, C> {

	public A a;
	public B b;
	public C c;

	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Triple other))
			return false;

		return Objects.equals(a, other.a)
			&& Objects.equals(b, other.b)
			&& Objects.equals(c, other.c);
	}

	public static <X, Y, Z> Triple<X, Y, Z> of(X x, Y y, Z z) {
		return new Triple<>(x, y, z);
	}

}
