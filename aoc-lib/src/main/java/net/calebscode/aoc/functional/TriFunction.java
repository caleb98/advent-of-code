package net.calebscode.aoc.functional;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {

	R apply(A a, B b, C c);
	
}
