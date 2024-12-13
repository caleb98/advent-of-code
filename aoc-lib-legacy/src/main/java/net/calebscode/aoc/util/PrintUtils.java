package net.calebscode.aoc.util;

public class PrintUtils {

    public static <T> void print2DArray(T[][] array) {
        if (array == null || array.length == 0 || array[0].length == 0) {
            System.out.println("Array is empty or null");
            return;
        }

        int rows = array.length;
        int cols = array[0].length;

        // Determine the maximum width of each column
        int[] maxColWidths = new int[cols];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (array[i][j] != null) {
                    maxColWidths[j] = Math.max(maxColWidths[j], array[i][j].toString().length());
                }
            }
        }

        // Print the array with proper alignment
        for (T[] row : array) {
            for (int j = 0; j < row.length; j++) {
                String format = "%-" + maxColWidths[j] + "s "; // Left-align with padding
                System.out.printf(format, row[j] == null ? "null" : row[j].toString());
            }
            System.out.println();
        }
    }
    
    // Wrapper for char[][]
    public static void print2DArray(char[][] array) {
        Character[][] wrappedArray = new Character[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                wrappedArray[i][j] = array[i][j];
            }
        }
        print2DArray(wrappedArray);
    }

    // Wrapper for int[][]
    public static void print2DArray(int[][] array) {
        Integer[][] wrappedArray = new Integer[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                wrappedArray[i][j] = array[i][j];
            }
        }
        print2DArray(wrappedArray);
    }

    // Wrapper for double[][]
    public static void print2DArray(double[][] array) {
        Double[][] wrappedArray = new Double[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                wrappedArray[i][j] = array[i][j];
            }
        }
        print2DArray(wrappedArray);
    }

    // Wrapper for boolean[][]
    public static void print2DArray(boolean[][] array) {
        Boolean[][] wrappedArray = new Boolean[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                wrappedArray[i][j] = array[i][j];
            }
        }
        print2DArray(wrappedArray);
    }
	
}
