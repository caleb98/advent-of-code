package net.calebscode.aoc.solutions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

import net.calebscode.aoc.BasicSolution;
import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.data.Pair;
import net.calebscode.aoc.data.Quad;

public class AOC2024_Day13 extends BasicSolution<Long> {

	private List<ClawMachine> machines;
	
	public AOC2024_Day13() {
		super(13);
		machines = input.splitByBlankLine().parallelStream()
				.map(QuestionInput::getLines)
				.map(this::parseClawMachine)
				.toList();
	}
	
	@Override
	public Long solveFirst() {
		return machines.parallelStream()
			.mapToLong(machine -> {
				var realPrize = Pair.of(machine.prize.first, machine.prize.second);
				
				var newBasis = vecsToMatrix(machine.buttonA, machine.buttonB);
				var change = inverse(newBasis);
				var prizeInNewBasis = mult(change, realPrize);
				
				// Can't reach the prize
				if (!isWhole(prizeInNewBasis.first) || !isWhole(prizeInNewBasis.second)) {
					return 0L;
				}
				
				long aPresses = round(prizeInNewBasis.first).longValue();
				long bPresses = round(prizeInNewBasis.second).longValue();
				
				return aPresses * 3 + bPresses * 1;
			})
			.sum();
	}

	@Override
	public Long solveSecond() {
		return machines.parallelStream()
				.mapToLong(machine -> {
					var offset = new BigDecimal(10000000000000.0);
					var realPrize = Pair.of(machine.prize.first.add(offset), machine.prize.second.add(offset));
					
					var newBasis = vecsToMatrix(machine.buttonA, machine.buttonB);
					var change = inverse(newBasis);
					var prizeInNewBasis = mult(change, realPrize);
					
					// Can't reach the prize
					if (!isWhole(prizeInNewBasis.first) || !isWhole(prizeInNewBasis.second)) {
						return 0L;
					}
					
					long aPresses = round(prizeInNewBasis.first).longValue();
					long bPresses = round(prizeInNewBasis.second).longValue();
					
					return aPresses * 3 + bPresses * 1;
				})
				.sum();
	}
	
	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_UP).round(MathContext.DECIMAL128);
	}

	private static final BigDecimal epsilon = new BigDecimal(1e-8);
	private boolean isWhole(BigDecimal value) {
		return (value.subtract(round(value))).abs().compareTo(epsilon) < 0;
	}
	
	private Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> vecsToMatrix(Pair<BigDecimal, BigDecimal> col1, Pair<BigDecimal, BigDecimal> col2) {
		return Quad.of(col1.first, col2.first, col1.second, col2.second);
	}
	
	private Pair<BigDecimal, BigDecimal> mult(Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> mat, Pair<BigDecimal, BigDecimal> vec) {
		return Pair.of(
			mat.first.multiply(vec.first).add(mat.second.multiply(vec.second)),
			mat.third.multiply(vec.first).add(mat.fourth.multiply(vec.second))
		);
	}
	
	private Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> mult(Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> left, Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> right) {
		return Quad.of(
			left.first.multiply(right.first).add(left.second.multiply(right.third)),
			left.first.multiply(right.second).add(left.second.multiply(right.fourth)),
			left.third.multiply(right.first).add(left.fourth.multiply(right.third)),
			left.third.multiply(right.second).add(left.fourth.multiply(right.fourth))
		);
	}
	
	private Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> inverse(Quad<BigDecimal, BigDecimal, BigDecimal, BigDecimal> matrix) {
		BigDecimal a = matrix.first;
		BigDecimal b = matrix.second;
		BigDecimal c = matrix.third;
		BigDecimal d = matrix.fourth;
		
		BigDecimal det = (a.multiply(d)).subtract(b.multiply(c));
		BigDecimal detInverse = BigDecimal.ONE.divide(det, MathContext.DECIMAL128);
		
		return Quad.of(
			detInverse.multiply(d),
			detInverse.multiply(b.negate()),
			detInverse.multiply(c.negate()),
			detInverse.multiply(a)
		);
	}
	
	private static final Pattern buttonPattern = Pattern.compile("Button [AB]: X\\+(\\d+), Y\\+(\\d+)");
	private static final Pattern prizePattern = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
	private ClawMachine parseClawMachine(List<String> info) {
		var buttonAMatcher = buttonPattern.matcher(info.get(0));
		var buttonBMatcher = buttonPattern.matcher(info.get(1));
		var prizeMatcher = prizePattern.matcher(info.get(2));
		
		buttonAMatcher.matches();
		buttonBMatcher.matches();
		prizeMatcher.matches();
		
		var buttonA = Pair.of(new BigDecimal(buttonAMatcher.group(1)), new BigDecimal(buttonAMatcher.group(2)));
		var buttonB = Pair.of(new BigDecimal(buttonBMatcher.group(1)), new BigDecimal(buttonBMatcher.group(2)));
		var prize = Pair.of(new BigDecimal(prizeMatcher.group(1)), new BigDecimal(prizeMatcher.group(2)));
		
		return new ClawMachine(buttonA, buttonB, prize);
	}
	
	private record ClawMachine(Pair<BigDecimal, BigDecimal> buttonA, Pair<BigDecimal, BigDecimal> buttonB, Pair<BigDecimal, BigDecimal> prize) {}

}
