package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Pair;

public class AOC2024_Day9 extends Solution<Long> {

	private QuestionInput input;
	
	public AOC2024_Day9() {
		input = new QuestionInput("/inputs/day9.txt");
	}
	
	@Override
	public Long solveFirst() {
		var diskMap = input.getLines().get(0).chars().map(c -> c - '0').toArray();
		
		var tape = IntStream.range(0, diskMap.length)
			.mapToObj(index -> Pair.of(index, diskMap[index]))
			.flatMap(entry -> repeated(entry.a % 2 == 0 ? entry.a / 2 : -1, entry.b).stream())
			.toList();
		
		tape = new ArrayList<>(tape);
		
		int target = 0;
		int source = tape.size() - 1;
		while (target != source) {
			// Find empty target
			while (tape.get(target) != -1 && target < source) target++;
			
			// Find next source
			while (tape.get(source) == -1 && source >= target) source--;
			
			if (target >= source) break;
			
			// Swap
			tape.set(target, tape.get(source));
			tape.set(source, -1);
			
			target++;
			source--;
			
		}
		
		long checksum = 0;
		for (int i = 0; i < tape.size(); i++) {
			if (tape.get(i) == -1) continue;
			checksum += i * tape.get(i);
		}
		
		return checksum;
	}

	@Override
	public Long solveSecond() {
		var diskMap = input.getLines().get(0).chars().map(c -> c - '0').toArray();

		var tape = IntStream.range(0, diskMap.length)
			.mapToObj(index -> Pair.of(index, diskMap[index]))
			.flatMap(entry -> repeated(entry.a % 2 == 0 ? entry.a / 2 : -1, entry.b).stream())
			.toList();
		
		tape = new ArrayList<>(tape);

		var spaceMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < tape.size(); i++) {
			if (tape.get(i) != -1) continue;
			
			int j = i + 1;
			while (j < tape.size() && tape.get(j) == -1) j++;
			
			int length = j - i;
			spaceMap.put(i, length);
			i = j;
		}
		
		int source = tape.size() - 1;
		int lastValue = Integer.MAX_VALUE;
		while (source >= 0) {
			// Find next source
			while (source >= 0 && tape.get(source) == -1) source--;
			
			if (source <= 0) break;
			
			// Get source length
			int sourceValue = tape.get(source);
			int sourceEnd = source;
			while (source >= 0 && tape.get(source) == sourceValue) source--;
			int sourceLength = sourceEnd - source;
			
			// Verify we aren't moving the same number again
			if (sourceValue >= lastValue) continue;
			lastValue = sourceValue;
			
			// Find swap
			boolean foundSwap = false;
			var indexes = new ArrayList<>(spaceMap.keySet());
			Collections.sort(indexes);
			for (var index : indexes) {
				if (index > source) break; // Skip if we'd move the number backwards
				
				var availableLength = spaceMap.get(index);
				
				if (availableLength >= sourceLength) {
					int target = index;	
					int from = sourceEnd;
					
					for (; target < index + sourceLength; target++) {
						tape.set(target, sourceValue);
						tape.set(from--, -1);
					}
					
					// Update empty space entries
					spaceMap.remove(index);
					spaceMap.put(target, availableLength - sourceLength);
					
					foundSwap = true;
					break;
				}
			}
			
			if (foundSwap) {
				for (int i = source + 1; i <= sourceEnd; i++) {
					tape.set(i, -1);
				}
			}
		}
		
		long checksum = 0;
		for (int i = 0; i < tape.size(); i++) {
			if (tape.get(i) == -1) continue;
			checksum += i * tape.get(i);
		}
		
		return checksum;
	}
	
	private List<Integer> repeated(int value, int times) {
		return Collections.nCopies(times, value);
	}

}
