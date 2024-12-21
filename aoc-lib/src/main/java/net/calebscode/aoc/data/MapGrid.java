package net.calebscode.aoc.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.calebscode.aoc.functional.TriFunction;
import net.calebscode.aoc.functional.TriPredicate;
import net.calebscode.aoc.geometry.Point2D;

public class MapGrid<T> implements Grid<T> {
	
	private Map<Point2D, T> elements;

	private boolean wrap;
	private int width;
	private int height;

	private BiFunction<Integer, Integer, T> outOfBoundsSupplier;
	
	private MapGrid(Map<Point2D, T> elements, boolean wrap, int width, int height, BiFunction<Integer, Integer, T> outOfBoundsSupplier) {
		this.elements = elements;
		this.wrap = wrap;
		this.width = width;
		this.height = height;
		this.outOfBoundsSupplier = outOfBoundsSupplier;
	}
	
	public MapGrid(T[][] data, boolean wrap) {
		this(data, wrap, throwingOutOfBounds());
	}
	
	public MapGrid(T[][] data, boolean wrap, BiFunction<Integer, Integer, T> outOfBoundsSupplier) {
		Objects.requireNonNull(data);
		Objects.requireNonNull(outOfBoundsSupplier);
		
		elements = new HashMap<>();
		
		width = data.length;
		height = data.length == 0 ? 0 : data[0].length;
		
		this.outOfBoundsSupplier = outOfBoundsSupplier;
		
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				elements.put(new Point2D(x, y), data[x][y]);
			}
		}
	}

	@Override
	public T get(int x, int y) {
		if (wrap) {
			x = Math.floorMod(x, width);
			y = Math.floorMod(y, height);
		}
		
		return isInside(x, y) ? elements.get(new Point2D(x, y)) : outOfBoundsSupplier.apply(x, y);
	}

	@Override
	public T get(Point2D point) {
		return get(point.getX(), point.getY());
	}
	
	@Override
	public void set(int x, int y, T value) {
		set(new Point2D(x, y), value);
	}
	
	@Override
	public void set(Point2D point, T value) {
		if (!isInside(point)) {
			throw new IndexOutOfBoundsException("For index x=" + point.getX() + ", y=" + point.getY());
		}
		
		elements.put(point, value);
	}
	
	@Override
	public boolean isInside(int x, int y) {
		if (wrap) return true;
		return x < width && x >= 0
				&& y < height && y >= 0;
	}

	@Override
	public boolean isInside(Point2D point) {
		return isInside(point.getX(), point.getY());
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public Set<Point2D> getPointsWhere(Predicate<T> matcher) {
		return getPointsWhere((x, y, v) -> matcher.test(v));
	}
	
	@Override
	public Set<Point2D> getPointsWhere(TriPredicate<Integer, Integer, T> matcher) {
		return elements.entrySet().parallelStream()
				.filter(entry -> matcher.test(entry.getKey().getX(), entry.getKey().getY(), entry.getValue()))
				.map(entry -> entry.getKey())
				.collect(Collectors.toSet());
	}
	
	@Override
	public <R> MapGrid<R> map(TriFunction<Integer, Integer, T, R> mapper) {
		var mappedElements = elements.entrySet().parallelStream()
			.collect(Collectors.toMap(
				entry -> entry.getKey(),
				entry -> mapper.apply(entry.getKey().getX(), entry.getKey().getY(), entry.getValue())
			));
		
		BiFunction<Integer, Integer, R> mappedOutOfBoundsSupplier = (x, y) -> mapper.apply(x, y, outOfBoundsSupplier.apply(x, y));
		
		return new MapGrid<>(mappedElements, wrap, width, height, mappedOutOfBoundsSupplier);
	}
	
	@Override
	public MapGrid<T> filter(TriPredicate<Integer, Integer, T> filter) {
		var filteredElements = elements.entrySet().parallelStream()
				.filter(entry -> filter.test(entry.getKey().getX(), entry.getKey().getY(), entry.getValue()))
				.collect(Collectors.toMap(
					entry -> entry.getKey(),
					entry -> entry.getValue()
				));
		
		return new MapGrid<>(filteredElements, wrap, width, height, outOfBoundsSupplier);
	}
	
	private static <T> BiFunction<Integer, Integer, T> throwingOutOfBounds() {
		return (x, y) -> { throw new IndexOutOfBoundsException("For index x=" + x + ", y=" + y); };
	}
	
}
