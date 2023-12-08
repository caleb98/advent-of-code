package net.calebscode.aoc.solutions;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;

public class AOC2023_Day4 extends Solution<Integer> {

	private QuestionInput input;

	public AOC2023_Day4() {
		input = new QuestionInput("/inputs/day4.txt");
	}

	@Override
	public Integer solveFirst() {
		return input.getLines().parallelStream()
			.map(this::parseTicketFromLine)
			.map(LotteryTicket::getPoints)
			.reduce(0, Integer::sum);
	}

	@Override
	public Integer solveSecond() {
		var tickets = input.getLines().parallelStream()
			.map(this::parseTicketFromLine)
			.toList();

		var ticketCounts = createTicketCountArray(tickets.size());

		for (var ticket : tickets) {
			var copies = ticketCounts[ticket.cardNumber - 1];
			var wins = ticket.getWinningNumberCount();

			for (int i = ticket.cardNumber; i < ticket.cardNumber + wins; i++) {
				ticketCounts[i] += copies;
			}
		}

		return IntStream.of(ticketCounts).sum();
	}

	private int[] createTicketCountArray(int numTickets) {
		int[] ticketCounts = new int[numTickets];
		for (int i = 0; i < ticketCounts.length; i++) {
			ticketCounts[i] = 1;
		}
		return ticketCounts;
	}

	private LotteryTicket parseTicketFromLine(String line) {
		var cardParts = line.substring(5).split(":");
		int cardNum = Integer.parseInt(cardParts[0].trim());

		var numberParts = cardParts[1].replaceAll("\s+", " ").trim();
		var parts = numberParts.split("\\|");
		var winning = parts[0].trim();
		var ticket = parts[1].trim();

		var winningSet = new HashSet<Integer>();
		var ticketSet = new HashSet<Integer>();

		for (var winningNumber : winning.split(" ")) {
			winningSet.add(Integer.parseInt(winningNumber));
		}

		for (var ticketNumber : ticket.split(" ")) {
			ticketSet.add(Integer.parseInt(ticketNumber));
		}

		return new LotteryTicket(cardNum, winningSet, ticketSet);
	}

	static class LotteryTicket {

		int cardNumber;
		Set<Integer> winningNumbers;
		Set<Integer> ticketNumbers;

		LotteryTicket(int cardNumber, Set<Integer> winningNumbers, Set<Integer> ticketNumbers) {
			this.cardNumber = cardNumber;
			this.winningNumbers = winningNumbers;
			this.ticketNumbers = ticketNumbers;
		}

		int getPoints() {
			var count = getWinningNumberCount();
			if (count == 0) {
				return 0;
			}
			else {
				return (int) Math.pow(2, count - 1);
			}
		}

		int getWinningNumberCount() {
			Set<Integer> intersection = new HashSet<>(ticketNumbers);
			intersection.retainAll(winningNumbers);
			return intersection.size();
		}

	}

}
