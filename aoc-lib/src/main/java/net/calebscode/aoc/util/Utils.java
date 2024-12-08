package net.calebscode.aoc.util;

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
	
}
