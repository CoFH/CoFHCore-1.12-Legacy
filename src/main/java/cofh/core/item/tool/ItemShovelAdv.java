package cofh.core.item.tool;

import net.minecraft.item.ItemSpade;

public class ItemShovelAdv extends ItemToolAdv {

	public ItemShovelAdv(ToolMaterial toolMaterial) {

		super(1.0F, toolMaterial);
		addToolClass("shovel");

		effectiveBlocks.addAll(ItemSpade.field_150916_c);
	}

}
