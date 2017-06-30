package cofh.lib.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;
import java.util.Random;

public class OperationProvider implements INumberProvider {

	protected final INumberProvider valueA;
	protected final INumberProvider valueB;
	protected final Operation operation;
	protected final long min, max;

	public OperationProvider(INumberProvider valueA, INumberProvider valueB, String type, long min, long max) {

		this.valueA = valueA;
		this.valueB = valueB;
		this.operation = Operation.valueOf(type.toUpperCase(Locale.US));
		this.min = min;
		this.max = max;
	}

	public int intValue(World world, Random rand, BlockPos pos) {

		return (int) longValue(world, rand, pos);
	}

	public long longValue(World world, Random rand, BlockPos pos) {

		return Math.min(Math.max(operation.perform(valueA.longValue(world, rand, pos), valueB.longValue(world, rand, pos)), min), max);
	}

	public float floatValue(World world, Random rand, BlockPos pos) {

		return (float) doubleValue(world, rand, pos);
	}

	public double doubleValue(World world, Random rand, BlockPos pos) {

		return Math.min(Math.max(operation.perform(valueA.doubleValue(world, rand, pos), valueB.doubleValue(world, rand, pos)), min), max);
	}

	private static enum Operation {

		ADD {
			@Override
			public long perform(long a, long b) {

				return a + b;
			}

			@Override
			public double perform(double a, double b) {

				return a + b;
			}
		}, SUBTRACT {
			@Override
			public long perform(long a, long b) {

				return a - b;
			}

			@Override
			public double perform(double a, double b) {

				return a - b;
			}
		}, MULTIPLY {
			@Override
			public long perform(long a, long b) {

				return a * b;
			}

			@Override
			public double perform(double a, double b) {

				return a * b;
			}
		}, DIVIDE {
			@Override
			public long perform(long a, long b) {

				return a / b;
			}

			@Override
			public double perform(double a, double b) {

				return a / b;
			}
		}, MODULO {
			@Override
			public long perform(long a, long b) {

				return a % b;
			}

			@Override
			public double perform(double a, double b) {

				return a % b;
			}
		};

		public abstract long perform(long a, long b);

		public abstract double perform(double a, double b);
	}

}
