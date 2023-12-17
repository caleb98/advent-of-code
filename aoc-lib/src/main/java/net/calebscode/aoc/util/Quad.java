package net.calebscode.aoc.util;

import java.util.Objects;

public class Quad<A, B, C, D> {

	public A a;
	public B b;
	public C c;
	public D d;

	public Quad(A a, B b, C c, D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Quad other))
			return false;

		return Objects.equals(a, other.a)
			&& Objects.equals(b, other.b)
			&& Objects.equals(c, other.c)
			&& Objects.equals(d, other.d);
	}

	public static <W, X, Y, Z> Quad<W, X, Y, Z> of(W w, X x, Y y, Z z) {
		return new Quad<>(w, x, y, z);
	}

}
