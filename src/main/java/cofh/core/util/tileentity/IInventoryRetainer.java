package cofh.core.util.tileentity;

/**
 * Marks a Tile Entity which can optionally retain its inventory when broken.
 *
 * @author King Lemming
 */
public interface IInventoryRetainer {

	/**
	 * Simple boolean check to see if the Tile is going to keep its inventory at the time of query.
	 */
	boolean retainInventory();

}
