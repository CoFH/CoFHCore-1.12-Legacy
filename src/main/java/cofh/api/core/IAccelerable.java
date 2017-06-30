package cofh.api.core;

/**
 * Implemented on objects which perform a function that can be sped up.
 *
 * This is an elegant way of NOT calling another update(), preventing potential infinite-loop cases.
 *
 * @author King Lemming
 */
public interface IAccelerable {

	/**
	 * This method should be something small and low in CPU usage as it may be called multiple times per tick.
	 *
	 * The return is a measure of how much "work" was performed. In the case of an RF-using block, the amount of energy used.
	 */
	int updateAccelerable();

}
