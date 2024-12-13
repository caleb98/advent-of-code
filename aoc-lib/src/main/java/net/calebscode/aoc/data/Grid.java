package net.calebscode.aoc.data;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import net.calebscode.aoc.geometry.Point2D;

public class Grid<T> {
	
	private boolean wrap;
	private T[][] data;
	private BiFunction<Integer, Integer, T> outOfBoundsSupplier;
	
	public Grid(T[][] data, boolean wrap) {
		this(data, wrap, (x, y) -> { throw new IndexOutOfBoundsException(); });
	}
	
	public Grid(T[][] data, boolean wrap, BiFunction<Integer, Integer, T> outOfBoundsSupplier) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }
		
		this.data = data;
		this.wrap = wrap;
		this.outOfBoundsSupplier = outOfBoundsSupplier;
	}
	
	public int getWidth() {
		return data.length;
	}
	
	public int getHeight() {
		return data[0].length;
	}
	
	public boolean isInside(Point2D point) {
		return isInside(point.getX(), point.getY());
	}
	
	public boolean isInside(int x, int y) {
		if (wrap) return true;
		return x < data.length    && x >= 0
			&& y < data[0].length && y >= 0;
	}
	
	public T get(Point2D point) {
		return get(point.getX(), point.getY());
	}
	
	public T get(int x, int y) {
		if (wrap) {
			x = Math.floorMod(x, data.length);
			y = Math.floorMod(y, data[0].length);
		}
		
		return isInside(x, y) ? data[x][y] : outOfBoundsSupplier.apply(x, y);
	}
	
	public Stream<T> stream() {
		return Arrays.stream(data).flatMap(row -> Arrays.stream(row));
	}
	
}
