package cofh.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ItemShovelAdv extends ItemToolAdv {

	public static Set<Block> effectiveBlocks = new HashSet<Block>();
	public static Set<Material> effectiveMaterials = new HashSet<Material>();

	static {
		effectiveBlocks.addAll(ItemSpade.field_150916_c);
	}

	public ItemShovelAdv(ToolMaterial toolMaterial) {

		super(1.0F, toolMaterial);
		addToolClass("shovel");
	}

	@Override
	protected Set<Block> getEffectiveBlocks(ItemStack stack) {

		return effectiveBlocks;
	}

	@Override
	protected Set<Material> getEffectiveMaterials(ItemStack stack) {

		return effectiveMaterials;
	}
}
