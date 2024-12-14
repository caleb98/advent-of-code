package net.calebscode.aoc.solutions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.data.Pair;
import net.calebscode.aoc.data.Triple;

public class AOC2023_Day20 extends BasicSolution<Long> {

	public AOC2023_Day20() {
		super(20);
	}

	@Override
	public Long solveFirst() {
		var modules = input.getLines().stream()
						.map(this::parseModule)
						.collect(Collectors.toMap(module -> module.name, module -> module));

		linkConjunctions(modules);

		long lowPulses = 0;
		long highPulses = 0;
		for (int i = 0; i < 1000; i++) {
			var result = doRun(modules);
			lowPulses += result.first;
			highPulses += result.second;
		}

		System.out.printf("L: %d\nH: %d\n", lowPulses, highPulses);

		return lowPulses * highPulses;
	}

	@Override
	public Long solveSecond() {
		var modules = input.getLines().stream()
				.map(this::parseModule)
				.collect(Collectors.toMap(module -> module.name, module -> module));

		linkConjunctions(modules);

		var conjunctions = modules.values().stream()
			.filter(module -> module instanceof Conjunction)
			.collect(Collectors.toMap(module -> module, module -> 0L));

		for (int i = 1; i <= 100000; i++) {
			// Do the run
			var processes = new LinkedList<Triple<String, String, Boolean>>();
			processes.push(Triple.of("button", "broadcaster", false));
			while (!processes.isEmpty()) {
				var current = processes.poll();

				var source = current.first;
				var module = modules.get(current.second);
				var pulse = current.third;

				if (module == null) continue;

				var results = module.processInput(source, pulse);
				processes.addAll(results);

				if (conjunctions.containsKey(module) && results.get(0).third && conjunctions.get(module) == 0) {
					conjunctions.put(module, (long) i);
				}
			}
		}

		for (var entry : conjunctions.entrySet()) {
			System.out.printf("%s: %d\n", entry.getKey().name, entry.getValue());
		}

		// TODO: really, I should implement something here that will find the least
		// common multiple of the rx input conjunctions. But for now, the LCM calculator
		// I found on google will work :)
		// https://www.calculatorsoup.com/calculators/math/lcm.php?input=3863+3943+3989+4003&data=none&action=solve
		return -1L;
	}

	private void linkConjunctions(Map<String, Module> modules) {
		var conjunctions = modules.values().stream()
				.filter(m -> m instanceof Conjunction)
				.map(m -> m.name)
				.collect(Collectors.toSet());

		modules.values().forEach(module -> {
			for (var dest : module.destinations) {
				if (conjunctions.contains(dest)) {
					var conjunction = (Conjunction) modules.get(dest);
					conjunction.previousInputs.put(module.name, false);
				}
			}
		});
	}

	private List<String> getRxInputs(Map<String, Module> modules) {
		return modules.values().parallelStream()
			.filter(module -> Arrays.stream(module.destinations).anyMatch(dest -> dest.equals("rx")))
			.map(module -> module.name)
			.toList();
	}

	private Pair<Long, Long> doRun(Map<String, Module> modules) {
		var processes = new LinkedList<Triple<String, String, Boolean>>();
		processes.push(Triple.of("button", "broadcaster", false));
		long lowPulses = 0;
		long highPulses = 0;
		while (!processes.isEmpty()) {
			var current = processes.poll();

			var source = current.first;
			var module = modules.get(current.second);
			var pulse = current.third;

			if (pulse) highPulses++; else lowPulses++;

			if (module == null) continue;

			var results = module.processInput(source, pulse);
			processes.addAll(results);
		}

		return Pair.of(lowPulses, highPulses);
	}

	private Module parseModule(String line) {
		var parts = line.split("->");

		var identifier = parts[0].trim();
		var type = identifier.charAt(0);
		var destinations = parts[1].replaceAll("\s", "").split(",");

		return switch (type) {
			case '%' -> {
				var name = identifier.substring(1);
				yield new FlipFlop(name, destinations);
			}

			case '&' -> {
				var name = identifier.substring(1);
				yield new Conjunction(name, destinations);
			}

			default -> {
				var name = identifier;
				yield new Broadcast(name, destinations);
			}
		};
	}

	private static abstract class Module {

		String name;
		String[] destinations;

		Module(String name, String[] destinations) {
			this.name = name;
			this.destinations = destinations;
		}

		// Triple<Source, Target, Pulse>
		abstract List<Triple<String, String, Boolean>> processInput(String source, boolean pulse);

	}

	private static class Broadcast extends Module {

		Broadcast(String name, String[] destinations) {
			super(name, destinations);
		}

		@Override
		List<Triple<String, String, Boolean>> processInput(String source, boolean pulse) {
			return Arrays.stream(destinations).map(dest -> Triple.of(name, dest, pulse)).toList();
		}

	}

	private static class FlipFlop extends Module {

		boolean state;

		FlipFlop(String name, String[] destinations) {
			super(name, destinations);
		}

		@Override
		List<Triple<String, String, Boolean>> processInput(String source, boolean pulse) {
			// If a flip-flop module receives a high pulse, it is ignored and nothing happens
			if (pulse) return List.of();

			// However, if a flip-flop module receives a low pulse, it flips between on and off.
			state = !state;

			// If it was off, it turns on and sends a high pulse.
			// If it was on, it turns off and sends a low pulse.
			return Arrays.stream(destinations).map(dest -> Triple.of(name, dest, state)).toList();
		}

	}

	private static class Conjunction extends Module {

		Map<String, Boolean> previousInputs = new HashMap<>();

		Conjunction(String name, String[] destinations) {
			super(name, destinations);
		}

		@Override
		List<Triple<String, String, Boolean>> processInput(String source, boolean pulse) {
			// When a pulse is received, the conjunction module first updates its
			// memory for that input.
			previousInputs.put(source, pulse);

			// Then, if it remembers high pulses for all inputs, it sends a low pulse;
			// otherwise, it sends a high pulse.
			boolean send = false;
			for (var prev : previousInputs.values()) {
				if (!prev) {
					send = true;
					break;
				}
			}

			final boolean result = send;
			return Arrays.stream(destinations).map(dest -> Triple.of(name, dest, result)).toList();
		}

	}

}