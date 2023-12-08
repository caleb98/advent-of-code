package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day6 extends Solution<Long> {

	private QuestionInput input;

	private List<Long> times;
	private List<Long> distances;

	public AOC2023_Day6() {
		input = new QuestionInput("/inputs/day6.txt");
	}

	@Override
	public Long solveFirst() {
		parseTimeAndDistances();
		var winningOptionCounts = new ArrayList<Long>();
		for (int i = 0; i < times.size(); i++) {
			long raceTime = times.get(i);
			long recordDist = distances.get(i);
			var winningOptions = getHoldTimesToBeatDistance(raceTime, recordDist);
			winningOptionCounts.add((long) winningOptions.size());
		}

		return winningOptionCounts.stream().reduce(1L, (a, b) -> a * b);
	}

	@Override
	public Long solveSecond() {
		parseTimeAndDistancesSquished();
		var winningOptionCounts = new ArrayList<Long>();
		for (int i = 0; i < times.size(); i++) {
			long raceTime = times.get(i);
			long recordDist = distances.get(i);
			var winningOptions = getHoldTimesToBeatDistance(raceTime, recordDist);
			winningOptionCounts.add((long) winningOptions.size());
		}

		return winningOptionCounts.stream().reduce(1L, (a, b) -> a * b);
	}

	private List<Long> getHoldTimesToBeatDistance(long raceDuration, long recordDistance) {
		var winning = new ArrayList<Long>();
		for (long i = 1; i < raceDuration; i++) {
			if (getDistance(raceDuration, i) > recordDistance) {
				winning.add(i);
			}
		}
		return winning;
	}

	private long getDistance(long raceDuration, long holdDuration) {
		long velocity = holdDuration;
		long movingTime = raceDuration - holdDuration;
		return velocity * movingTime;
	}

	private void parseTimeAndDistances() {
		var timeLine = input.getLines().get(0);
		var distLine = input.getLines().get(1);

		var timeStrings = timeLine.split(":")[1].trim().replaceAll("\s+", " ").split("\\s");
		var distStrings = distLine.split(":")[1].trim().replaceAll("\s+", " ").split("\\s");

		times = Arrays.stream(timeStrings)
					.map(Long::parseLong)
					.toList();

		distances = Arrays.stream(distStrings)
						.map(Long::parseLong)
						.toList();
	}

	private void parseTimeAndDistancesSquished() {
			var timeLine = input.getLines().get(0);
		var distLine = input.getLines().get(1);

		var timeString = timeLine.split(":")[1].trim().replaceAll("\s", "");
		var distString = distLine.split(":")[1].trim().replaceAll("\s", "");

		times = List.of(Long.parseLong(timeString));
		distances = List.of(Long.parseLong(distString));
	}

}
