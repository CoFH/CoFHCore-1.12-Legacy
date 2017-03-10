package cofh.core.util.tileentity;

/**
 * Implement this interface on Tile Entities which have Transfer Control functionality. This means that a tile can be set to automatically transfer things into or out of it.
 *
 * This is a genericized interface and exactly WHAT is transferred into or out of the tile is up to the tile itself. :)
 *
 * @author King Lemming
 */
public interface ITransferControl {

	/**
	 * Returns whether or not a tile is capable of automatic input transfer.
	 */
	boolean hasTransferIn();

	/**
	 * Returns whether or not a tile is capable of automatic output transfer.
	 */
	boolean hasTransferOut();

	/**
	 * Returns current state of automatic input transfer.
	 */
	boolean getTransferIn();

	/**
	 * Returns current state of automatic output transfer.
	 */
	boolean getTransferOut();

	/**
	 * Attempt to enable/disable automatic input transfer. Returns TRUE on successful change.
	 */
	boolean setTransferIn(boolean input);

	/**
	 * Attempt to enable/disable automatic output transfer. Returns TRUE on successful change.
	 */
	boolean setTransferOut(boolean output);

}
