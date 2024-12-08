package net.calebscode.aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import net.calebscode.aoc.util.Grid;
import net.calebscode.aoc.util.Utils;

public class QuestionInput {

	private ArrayList<String> lines = new ArrayList<>();

	public QuestionInput(String resourcePath) {
		try (
			var inputStream = getClass().getResourceAsStream(resourcePath);
			var reader = new BufferedReader(new InputStreamReader(inputStream));
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public String getAllInput() {
		return lines.stream().reduce("", (acc, val) -> acc + val);
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public List<List<String>> getLinesSplitByBlank() {
		List<List<String>> sections = new ArrayList<>();
		List<String> current = new ArrayList<>();
		for (var line : lines) {
			if (line.isBlank()) {
				if (!current.isEmpty()) {
					sections.add(current);
					current = new ArrayList<>();
				}
			} else {
				current.add(line);
			}
		}

		if (!current.isEmpty()) {
			sections.add(current);
		}

		return sections;
	}
	
	public Grid<Character> asGrid(boolean wrap) {
		var charArr = asCharacterArray();
		Utils.transposeInPlace(charArr);
		return new Grid<Character>(charArr, wrap);
	}
	
	public Grid<Character> asGrid(boolean wrap, BiFunction<Integer, Integer, Character> outOfBoundsSupplier) {
		var charArr = asCharacterArray();
		Utils.transposeInPlace(charArr);
		return new Grid<Character>(charArr, wrap, outOfBoundsSupplier);
	}

	/**
	 * When accessing using x,y position, remember
	 * that y comes first:
	 * arr[row][col] = arr[y][x]
	 * @return
	 */
	public char[][] asCharArray() {
		char[][] chars = new char[lines.size()][];

		for (int i = 0; i < lines.size(); i++) {
			chars[i] = lines.get(i).toCharArray();
		}

		return chars;
	}
	
	public Character[][] asCharacterArray() {
		Character[][] chars = new Character[lines.size()][];
		
		for (int i = 0; i < lines.size(); i++) {
			var line = lines.get(i);
			chars[i] = line.chars().mapToObj(c -> (char) c).toList().toArray(new Character[line.length()]);
		}

		return chars;
	}

	public int[][] asIntArray() {
		int[][] ints = new int[lines.size()][];

		for (int row = 0; row < lines.size(); row++) {
			var line = lines.get(row);
			ints[row] = new int[line.length()];

			for (int col = 0; col < line.length(); col++) {
				ints[row][col] = line.charAt(col) - '0';
			}
		}

		return ints;
	}

}
