package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface IColorableItem extends INBTCopyIngredient {

	void applyColor(ItemStack item, int color, int colorIndex);

	void removeColor(ItemStack item);

}
