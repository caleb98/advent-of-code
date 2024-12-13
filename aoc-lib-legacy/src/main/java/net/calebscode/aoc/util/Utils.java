package net.calebscode.aoc.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Utils {
	
	public static <T> void transposeInPlace(T[][] array) {
        if (array == null || array.length == 0 || array[0].length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }
        
        int rows = array.length;
        int cols = array[0].length;
        
        for (int i = 0; i < rows; i++) {
            for (int j = i + 1; j < cols; j++) {
            	T temp = array[i][j];
            	array[i][j] = array[j][i];
            	array[j][i] = temp;
            }
        }
	}
	
	/**
	 * Partitions a collection of elements into sets of contiguous
	 * like elements. The logic for determining if elements are adjacent
	 * to each other and for determining if elements are like can
	 * be provided via the getAdjacentElements and areElementsAlike
	 * parameters, respectively.
	 * 
	 * @param <T>
	 * @param allElements
	 * @param getAdjacentElements
	 * @param areElementsAlike
	 * @return
	 */
	public static <T> List<Set<T>> getAdjacentGroups(
		Collection<T> allElements,
		Function<T, Iterable<T>> getAdjacentElements,
		BiFunction<T, T, Boolean> areElementsAlike
	) {
		var groups = new LinkedList<Set<T>>();
		var unvisited = new HashSet<>(allElements);
		var toVisit = new HashSet<T>();
		
		while (unvisited.size() > 0) {
			toVisit.add(unvisited.iterator().next());
			var group = new HashSet<T>();
			
			while (toVisit.size() > 0) {
				var current = toVisit.iterator().next();
				toVisit.remove(current);
				unvisited.remove(current);
				group.add(current);
				
				for (var adjacent : getAdjacentElements.apply(current)) {
					if (unvisited.contains(adjacent) && areElementsAlike.apply(current, adjacent)) {
						toVisit.add(adjacent);
					}
				}
			}
			
			groups.add(group);
		}
		
		return groups;
	}
	
}
