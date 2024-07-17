package cofh.lib.util;

import net.minecraft.world.World;

/**
 * A basic time tracker class. Nothing surprising here.
 *
 * @author King Lemming
 *
 */
public class TimeTracker {

	private long lastMark = Long.MIN_VALUE;

	public boolean hasDelayPassed(World world, int delay) {

		long currentTime = world.getTotalWorldTime();

		if (currentTime < lastMark) {
			lastMark = currentTime;
			return false;
		} else if (lastMark + delay <= currentTime) {
			lastMark = currentTime;
			return true;
		}
		return false;
	}

	public void markTime(World world) {

		lastMark = world.getTotalWorldTime();
	}

}
