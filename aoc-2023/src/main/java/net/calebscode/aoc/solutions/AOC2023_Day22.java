package net.calebscode.aoc.solutions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.calebscode.aoc.QuestionInput;
import net.calebscode.aoc.Solution;
import net.calebscode.aoc.util.Point2D;
import net.calebscode.aoc.util.Point3D;

public class AOC2023_Day22 extends Solution<Long> {

	private QuestionInput input;

	public AOC2023_Day22() {
		input = new QuestionInput("/inputs/day22.txt");
	}

	@Override
	public Long solveFirst() {
		var tops = new HashMap<Point2D, Integer>();
		var bricks = input.getLines().parallelStream()
			.map(Brick::new)
			.sorted((a, b) -> {
				var az = a.blocks.stream().mapToInt(Point3D::getZ).min().getAsInt();
				var bz = b.blocks.stream().mapToInt(Point3D::getZ).min().getAsInt();
				return Integer.compare(az, bz);
			})
			.toList();

		boolean falling = true;
		while (falling) {
			falling = false;

			for (var brick : bricks) {
				var blocksUnder = brick.getBelow();
				var canFall = false;

				for (var block : blocksUnder) {
					var pos = new Point2D(block.getX(), block.getY());
					var top = tops.getOrDefault(pos, 0);

					if (block.getZ() <= top) {
						canFall = false;
						break;
					}
				}

				// Falling, just move down
				if (canFall) {
					falling = true;
					brick.moveDown();
				}
				// When a brick can't fall, we need to update the tops
				else {
					// TODO: link up the stacked blocks
					for (var block : brick.blocks) {
						var pos = new Point2D(block.getX(), block.getY());
						var old = tops.getOrDefault(pos, 0);
						if (block.getZ() > old) {
							tops.put(pos, block.getZ());
						}
					}
				}
			}
		}

		var blocks = bricks.stream().flatMap(b -> b.blocks.stream()).collect(Collectors.toSet());

		var xs = bricks.stream().flatMapToInt(b -> b.blocks.stream().mapToInt(Point3D::getX));
		var ys = bricks.stream().flatMapToInt(b -> b.blocks.stream().mapToInt(Point3D::getY));
		var zs = bricks.stream().flatMapToInt(b -> b.blocks.stream().mapToInt(Point3D::getZ));

		var minX = xs.min().getAsInt();
		var maxX = xs.max().getAsInt();

		var minY = ys.min().getAsInt();
		var maxY = ys.max().getAsInt();

		var maxZ = zs.min().getAsInt();

		for (int z = 0; z <= maxZ; z++) {
			System.out.println("\nz=" + z);
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if (blocks.contains(new Point3D(x, y, z))) {
						System.out.println();
					}
				}
			}
		}

		return -1L;
	}

	@Override
	public Long solveSecond() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'solveSecond'");
	}

	private static class Brick {

		Set<Point3D> blocks = new HashSet<>();

		Brick(String data) {
			var parts = data.split("~");
			var start = parts[0];
			var end = parts[1];

			var startParts = start.split(",");
			var endParts = end.split(",");

			var sx = Integer.parseInt(startParts[0]);
			var sy = Integer.parseInt(startParts[1]);
			var sz = Integer.parseInt(startParts[2]);

			var ex = Integer.parseInt(endParts[0]);
			var ey = Integer.parseInt(endParts[1]);
			var ez = Integer.parseInt(endParts[2]);

			for (int x = sx; x <= ex; x++) {
				for (int y = sy; y <= ey; y++) {
					for (int z = sz; z <= ez; z++) {
						blocks.add(new Point3D(x, y, z));
					}
				}
			}
		}

		List<Point3D> getBelow() {
			var bottom =  blocks.stream().mapToInt(b -> b.getZ()).min().getAsInt();
			return blocks.stream()
				.map(b -> new Point2D(b.getX(), b.getY()))
				.distinct()
				.map(b -> new Point3D(b.getX(), b.getY(), bottom - 1))
				.toList();
		}

		void moveDown() {
			blocks = blocks.stream()
				.map(b -> b.translate(0, 0, -1))
				.collect(Collectors.toSet());
		}

	}

}
