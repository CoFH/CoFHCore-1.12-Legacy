package cofh.core.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class UniformRandomProvider extends ConstantProvider {

	protected Number max;

	public UniformRandomProvider(Number min, Number max) {

		super(min);
		this.max = max;
		boolean longBad = min.longValue() >= max.longValue();
		boolean doubleBad = min.doubleValue() >= max.doubleValue();
		if (longBad && doubleBad) {
			throw new IllegalArgumentException("min >= max");
		}
	}

	@Override
	public long longValue(World world, Random rand, BlockPos pos) {

		return getRandomLong(max.longValue() - min.longValue(), rand) + min.longValue();
	}

	@Override
	public double doubleValue(World world, Random rand, BlockPos pos) {

		return getRandomDouble(max.doubleValue() - min.doubleValue(), rand) + min.doubleValue();
	}

	public static long getRandomLong(long val, Random rand) {

		if (val == 0) {
			return 0;
		}
		int low = (int) (val & Integer.MAX_VALUE);
		int mid = (int) ((val >>> 31) & Integer.MAX_VALUE);
		int high = (int) ((val >>> 62) & Integer.MAX_VALUE);

		boolean mh = (mid | high) > 0;
		long r = mh ? rand.nextInt() & Integer.MAX_VALUE : rand.nextInt(low);
		if (mh) {
			r |= (high > 0 ? rand.nextInt() & Integer.MAX_VALUE : rand.nextInt(mid)) << 31;
		}
		if (high > 0) {
			r |= rand.nextInt(high) << 62;
		}

		return r;
	}

	public static double getRandomDouble(double val, Random rand) {

		return rand.nextDouble() * val;
	}

}
