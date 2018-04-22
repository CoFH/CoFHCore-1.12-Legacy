package cofh.api.item;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on subclasses of Item to have that item work as an advanced dye for CoFH mods.
 */
public interface IToolDye {

	boolean hasColor(ItemStack item);

	int getColor(ItemStack item);

}
