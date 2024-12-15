package net.calebscode.aoc.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import net.calebscode.aoc.geometry.Point2D;

public class Grid<T> {
	
	private boolean wrap;
	private List<List<T>> data;
	private BiFunction<Integer, Integer, T> outOfBoundsSupplier;
	
	public Grid(T[][] data, boolean wrap) {
		this(arrayToList(data), wrap);
	}
	
	public Grid(T[][] data, boolean wrap, BiFunction<Integer, Integer, T> outOfBoundsSupplier) {
		this(arrayToList(data), wrap, outOfBoundsSupplier);
	}
	
	public Grid(List<List<T>> data, boolean wrap) {
		this(data, wrap, (x, y) -> { throw new IndexOutOfBoundsException(); });
	}
	
	public Grid(List<List<T>> data, boolean wrap, BiFunction<Integer, Integer, T> outOfBoundsSupplier) {
        if (data == null || data.size() == 0 || data.get(0).size() == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }
		
		this.data = data;
		this.wrap = wrap;
		this.outOfBoundsSupplier = outOfBoundsSupplier;
	}
	
	public int getWidth() {
		return data.size();
	}
	
	public int getHeight() {
		return data.get(0).size();
	}
	
	public boolean isInside(Point2D point) {
		return isInside(point.getX(), point.getY());
	}
	
	public boolean isInside(int x, int y) {
		if (wrap) return true;
		return x < data.size()        && x >= 0
			&& y < data.get(0).size() && y >= 0;
	}
	
	public T get(Point2D point) {
		return get(point.getX(), point.getY());
	}
	
	public T get(int x, int y) {
		if (wrap) {
			x = Math.floorMod(x, data.size());
			y = Math.floorMod(y, data.get(0).size());
		}
		
		return isInside(x, y) ? data.get(x).get(y) : outOfBoundsSupplier.apply(x, y);
	}
	
	public Set<Point2D> getAllPointsWhere(Predicate<T> matcher) {
		return getAllPointsWhere(0, 0, getWidth(), getHeight(), matcher);
	}
	
	public Set<Point2D> getAllPointsWhere(int startXInclusive, int startYInclusive, int endXExclusive, int endYExclusive, Predicate<T> matcher) {
		var matching = new HashSet<Point2D>();
		for (int x = startXInclusive; x < endXExclusive; x++) {
			for (int y = startYInclusive; y < endYExclusive; y++) {
				if (matcher.test(get(x, y))) {
					matching.add(new Point2D(x, y));
				}
			}
		}
		return matching;
	}
	
	public <R> Grid<R> mapElements(Function<T, R> converter) {
		var convertedData = data.stream().map(row -> row.stream().map(converter).toList()).toList();
		BiFunction<Integer, Integer, R> convertedOutOfBoundsSupplier = (x, y) -> converter.apply(outOfBoundsSupplier.apply(x, y));
		
		return new Grid<R>(convertedData, wrap, convertedOutOfBoundsSupplier);
	}
	
	private static <T> ArrayList<List<T>> arrayToList(T[][] data) {
		var dataList = new ArrayList<List<T>>(data.length);
        for (int x = 0; x < data.length; x++) {
        	var columnList = new ArrayList<T>(data[x].length);
        	for (int y = 0; y < data[x].length; y++) {
        		columnList.add(data[x][y]);
        	}
        	dataList.add(columnList);
        }
		return dataList;
	}
	
}
