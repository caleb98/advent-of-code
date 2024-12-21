package net.calebscode.aoc.data;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import net.calebscode.aoc.functional.TriFunction;
import net.calebscode.aoc.functional.TriPredicate;
import net.calebscode.aoc.geometry.Point2D;

public interface Grid<T> {

	public T get(int x, int y);
	public T get(Point2D point);
	
	public void set(int x, int y, T value);
	public void set(Point2D point, T value);
	
	public boolean isInside(int x, int y);
	public boolean isInside(Point2D point);

	public int getWidth();
	public int getHeight();
	
	public Set<Point2D> getPointsWhere(Predicate<T> matcher);
	public Set<Point2D> getPointsWhere(TriPredicate<Integer, Integer, T> matcher);
	public <R> Grid<R> map(TriFunction<Integer, Integer, T, R> mapper);
	public Grid<T> filter(TriPredicate<Integer, Integer, T> filter);
	
	default Grid<T> filterByValue(Predicate<T> valueFilter) {
		return filter((x, y, v) -> valueFilter.test(v));
	}
	
	default Grid<T> filterByPosition(BiPredicate<Integer, Integer> positionFilter) {
		return filter((x, y, v) -> positionFilter.test(x, y));
	}
	
	default <R> Grid<R> mapByValue(Function<T, R> valueMapper) {
		return map((x, y, v) -> valueMapper.apply(v));
	}
	
	default <R> Grid<R> mapByPosition(BiFunction<Integer, Integer, R> positionMapper) {
		return map((x, y, v) -> positionMapper.apply(x, y));
	}
	
}
