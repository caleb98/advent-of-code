package net.calebscode.aoc.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import net.calebscode.aoc.BasicSolution;

public class AOC2024_Day17 extends BasicSolution<String> {

	public AOC2024_Day17() {
		super(17);
	}
	
	@Override
	public String solveFirst() {
		var data = input.splitByBlankLine();
		var registers = data.get(0).getLines().stream()
				.map(line -> line.replaceAll("Register [ABC]:\\s+", ""))
				.mapToLong(Long::parseLong)
				.toArray();
		var program = Arrays.stream(data.get(1).getLines().get(0).replaceAll("Program:\\s*", "").split(",")).mapToInt(Integer::parseInt).toArray();
		
		return runProgram(program, registers);
	}

	@Override
	public String solveSecond() {
		var data = input.splitByBlankLine();
		
		var programSource = data.get(1).getLines().get(0).replaceAll("Program:\\s*", "");
		var expectedOutput = programSource.split(",");
				
		var program = Arrays.stream(data.get(1).getLines().get(0).replaceAll("Program:\\s*", "").split(",")).mapToInt(Integer::parseInt).toArray();
		
		var registers = data.get(0).getLines().stream()
				.map(line -> line.replaceAll("Register [ABC]:\\s+", ""))
				.mapToLong(Long::parseLong)
				.toArray();

		searchForValidARegister(expectedOutput, program, 0, 0);
		
		return String.valueOf("");
	}
	
	private void searchForValidARegister(String[] programSource, int[] program, long currentA, int validated) {
		if (validated == programSource.length) {
			System.out.println("Found match! a = " + currentA);
			return;
		}
		
		var shiftedA = currentA << 3;
		var nextValidate = validated + 1;
		var expecting = Arrays.copyOfRange(programSource, programSource.length - nextValidate, programSource.length);
		
		for (int newA = 0; newA <= 7; newA++) {
			if (runProgramExpecting(program, new long[] { shiftedA + newA, 0, 0 }, expecting)) {
				
				var nextSearchA = (shiftedA + newA);
				searchForValidARegister(programSource, program, nextSearchA, nextValidate);
				
			}
		}
	}
	
	private String runProgram(int[] program, long[] registers) {
		var outputs = new ArrayList<String>();
		
		int ip = 0;
		while (ip < program.length) {
			ip = step(program, registers, outputs, ip);
		}
		
		return outputs.stream().collect(Collectors.joining(","));
	}
	
	private boolean doLoop(long[] registers) {
		long a = registers[0];
		long b = registers[1];
		long c = registers[2];
		
		//System.out.printf("  %16s %16s %16s\n", "A", "B", "C");
		//System.out.printf("> %16s %16s %16s\tBST 4 | Set B to the last three digits of A\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		b = a % 8; // BST 4							Set B to the last three digits of A
		//System.out.printf("> %16s %16s %16s\tBXL 3 | B = B XOR 011\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		b = b ^ 3; // BXL 3                         B = B XOR 010
		//System.out.printf("> %16s %16s %16s\tCDV 5 | Set C to A with the last B digits removed\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		c = a / (long) Math.pow(2, b); // CDV 5     Set C to A with the last B digits removed
		//System.out.printf("> %16s %16s %16s\tADV 3 | Remove the last 3 digits from A\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		a = a / (long) Math.pow(2, 3); // ADV 3     Remove the last 3 digits from A
		//System.out.printf("> %16s %16s %16s\tBXL 5 | B = B XOR 101\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		b = b ^ 5; // BXL 5                         B = B XOR 101
		//System.out.printf("> %16s %16s %16s\tBXC 4 | B = B XOR C\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		b = b ^ c; // BXC 4                         B = B XOR C
		//System.out.printf("> %16s %16s %16s\n", Long.toBinaryString(a), Long.toBinaryString(b), Long.toBinaryString(c));
		System.out.print(" " + (b % 8) + ""); // OUT 5[0] = a;
		
		registers[0] = a;
		registers[1] = b;
		registers[2] = c;
		
		return a != 0;
	}
	
	private boolean runProgramExpecting(int[] program, long[] registers, String[] expectedOutputs) {
		var outputs = new ArrayList<String>();
		
		var expectedIndex = 0;
		int ip = 0;
		while (ip < program.length) {
			var oldOutputSize = outputs.size();
			ip = step(program, registers, outputs, ip);
			if (outputs.size() > oldOutputSize) {
				var newOutput = outputs.getLast();
				var expectedOutput = expectedOutputs[expectedIndex];
				
				if (!newOutput.equals(expectedOutput)) {
					return false;
				}
				
				expectedIndex++;
			}
		}
		
		return true;
	}

	private int step(int[] program, long[] registers, ArrayList<String> outputs, int ip) {
		int op = program[ip];
		int literalOperand = program[ip + 1];
		long comboOperand = combo(literalOperand, registers);
		
		switch (op) {
			case 0: { // adv
				long numerator = registers[0];
				long denominator = (long) Math.pow(2, comboOperand);
				registers[0] = numerator / denominator;
				ip += 2;
				break;
			}
			
			case 1: { // bxl
				registers[1] ^= literalOperand;
				ip += 2;
				break;
			}
			
			case 2: { // bst
				registers[1] = comboOperand % 8;
				ip += 2;
				break;
			}
			
			case 3: { // jnz 
				if (registers[0] == 0) {
					ip += 2;
				}
				else {
					ip = literalOperand;
				}
				break;
			}
			
			case 4: { // bxc 
				registers[1] ^= registers[2];
				ip += 2;
				break;
			}
			
			case 5: { // out
				outputs.add(String.valueOf(comboOperand % 8));
				ip += 2;
				break;
			}
			
			case 6: { // bdv
				long numerator = registers[0];
				long denominator = (long) Math.pow(2, comboOperand);
				registers[1] = numerator / denominator;
				ip += 2;
				break;
			}
			
			case 7: { // cdv
				long numerator = registers[0];
				long denominator = (long) Math.pow(2, comboOperand);
				registers[2] = numerator / denominator;
				ip += 2;
				break;
			}
			
			default: {
				break;
			}
		}
		return ip;
	}
	
	private long combo(int operand, long[] registers) {
		if (operand <= 3) {
			return operand;			
		}
		else if (operand < 7) {
			return registers[operand - 4];
		}
		else {
			throw new IllegalArgumentException("invalid operand " + operand);
		}
	}

}
