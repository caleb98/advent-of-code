package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day7 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day7() {
		input = new QuestionInput("/inputs/day7.txt");
	}

	@Override
	public Long solveFirst() {
		var sortedHands = input.getLines().parallelStream()
			.map(this::parseHand)
			.sorted()
			.toList();

		long solution = 0;
		for (int i = 0; i < sortedHands.size(); i++) {
			solution += sortedHands.get(i).bid * (i + 1);
		}

		return solution;
	}

	@Override
	public Long solveSecond() {
		var sortedHands = input.getLines().stream()
			.map(this::parseHandWithJoker)
			.sorted(Hand::compareToWithJokerRule)
			.toList();

		long solution = 0;
		for (int i = 0; i < sortedHands.size(); i++) {
			solution += sortedHands.get(i).bid * (i + 1);
		}

		return solution;
	}

	private Hand parseHand(String hand) {
		var handData = hand.split(" ");
		var handCards = handData[0];
		var handBid = Long.parseLong(handData[1]);

		return new Hand(handCards, handBid);
	}

	private Hand parseHandWithJoker(String hand) {
		List<String> possibleHands = new ArrayList<>();
		possibleHands.add(hand);

		int prevHands;
		do {
			prevHands = possibleHands.size();

			// I've written some sketchy code for this event, but this little
			// maneuver makes me feel like I joined the dark side.
			possibleHands = possibleHands.stream().flatMap(baseHand -> {
				if (!baseHand.contains("J")) {
					return Stream.of(baseHand);
				}

				var possibilities = new ArrayList<String>();
				for (char c : Hand.cardValues.keySet()) {
					if (c != 'J') {
						possibilities.add(baseHand.replaceFirst("J", "" + c));
					}
				}
				return possibilities.stream();
			}).toList();
		} while (possibleHands.size() != prevHands);

		var bestHandType = possibleHands.parallelStream()
			.map(this::parseHand)
			.map(h -> h.type)
			.distinct()
			.sorted()
			.toList()
			.getLast();

		var realHand = parseHand(hand);
		realHand.type = bestHandType;
		return realHand;
	}

	static class Hand implements Comparable<Hand> {

		static final Map<Character, Integer> cardValues = new HashMap<>();
		static final Map<Character, Integer> cardValuesJoker = new HashMap<>();

		static {
			cardValues.put('2', 0);
			cardValues.put('3', 1);
			cardValues.put('4', 2);
			cardValues.put('5', 3);
			cardValues.put('6', 4);
			cardValues.put('7', 5);
			cardValues.put('8', 6);
			cardValues.put('9', 7);
			cardValues.put('T', 8);
			cardValues.put('J', 9);
			cardValues.put('Q', 10);
			cardValues.put('K', 11);
			cardValues.put('A', 12);

			cardValuesJoker.put('J', 0);
			cardValuesJoker.put('2', 1);
			cardValuesJoker.put('3', 2);
			cardValuesJoker.put('4', 3);
			cardValuesJoker.put('5', 4);
			cardValuesJoker.put('6', 5);
			cardValuesJoker.put('7', 6);
			cardValuesJoker.put('8', 7);
			cardValuesJoker.put('9', 8);
			cardValuesJoker.put('T', 9);
			cardValuesJoker.put('Q', 10);
			cardValuesJoker.put('K', 11);
			cardValuesJoker.put('A', 12);
		}

		String hand;
		HandType type;
		long bid;

		Hand(String hand, long bid) {
			this.hand = hand;
			this.bid = bid;

			HashMap<Character, Integer> cardCounts = new HashMap<>();
			for (char card : hand.toCharArray()) {
				int currentCount = cardCounts.computeIfAbsent(card, (c) -> 0);
				cardCounts.put(card, currentCount + 1);
			}

			// Check five of a kind

			switch (cardCounts.size()) {
				// One card can only be five of a kind
				case 1 -> {
					type = HandType.FIVE_OF_A_KIND;
				}

				// Could be four of a kind or full house
				case 2 -> {
					var cardEntries = cardCounts.entrySet().toArray(new Entry[]{});
					int firstCount = (int) cardEntries[0].getValue();
					int secondCount = (int) cardEntries[1].getValue();

					// Check four of a kind
					if (firstCount == 4 || secondCount == 4) {
						type = HandType.FOUR_OF_A_KIND;
					}
					// Must be full house: two cards and neither have count 4 implies 2/3 or 3/2
					else {
						type = HandType.FULL_HOUSE;
					}
				}

				// Could be three of a kind or a two pair
				case 3 -> {
					var maxCount = cardCounts.values().stream().mapToInt(c -> c).max();
					if (maxCount.getAsInt() == 3) {
						type = HandType.THREE_OF_A_KIND;
					}
					else {
						type = HandType.TWO_PAIR;
					}
				}

				// Must be one pair
				case 4 -> {
					type = HandType.ONE_PAIR;
				}

				// Five unique cards, so can only be high card
				case 5 -> {
					type = HandType.HIGH_CARD;
				}
			}
		}

		@Override
		public int compareTo(Hand other) {
			int typeCompare = type.compareTo(other.type);
			if (typeCompare != 0) {
				return typeCompare;
			}

			var thisCards = hand.toCharArray();
			var otherCards = other.hand.toCharArray();
			for (int i = 0; i < 5; i++) {
				var thisCardValue = cardValues.get(thisCards[i]);
				var otherCardValue = cardValues.get(otherCards[i]);
				var compare = thisCardValue.compareTo(otherCardValue);
				if (compare != 0) return compare;
			}

			return 0;
		}

		public int compareToWithJokerRule(Hand other) {
			int typeCompare = type.compareTo(other.type);
			if (typeCompare != 0) {
				return typeCompare;
			}

			var thisCards = hand.toCharArray();
			var otherCards = other.hand.toCharArray();
			for (int i = 0; i < 5; i++) {
				var thisCardValue = cardValuesJoker.get(thisCards[i]);
				var otherCardValue = cardValuesJoker.get(otherCards[i]);
				var compare = thisCardValue.compareTo(otherCardValue);
				if (compare != 0) return compare;
			}

			return 0;
		}

	}

	static enum HandType {
		HIGH_CARD,
		ONE_PAIR,
		TWO_PAIR,
		THREE_OF_A_KIND,
		FULL_HOUSE,
		FOUR_OF_A_KIND,
		FIVE_OF_A_KIND,
	}

}
