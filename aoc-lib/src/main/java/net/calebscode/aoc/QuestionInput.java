package net.calebscode.aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public char[][] asCharArray() {
		char[][] chars = new char[lines.get(0).length()][lines.size()];

		for (int i = 0; i < lines.size(); i++) {
			chars[i] = lines.get(i).toCharArray();
		}

		return chars;
	}

}
