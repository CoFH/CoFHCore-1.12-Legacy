package cofh.api.tileentity;

/**
 * Implement this interface on objects which can report information about their steam usage.
 *
 * This is used for reporting purposes - Steam transactions are handled via Fluid capabilities!
 *
 * @author King Lemming
 */
public interface ISteamInfo {

	/**
	 * Returns steam usage/generation per tick (mB/t).
	 */
	int getInfoSteamPerTick();

	/**
	 * Returns maximum steam usage/generation per tick (mB/t).
	 */
	int getInfoMaxSteamPerTick();

}
