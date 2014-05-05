package cofh.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ItemPickaxeAdv extends ItemToolAdv {

	public static Set<Block> effectiveBlocks = new HashSet<Block>();
	public static Set<Material> effectiveMaterials = new HashSet<Material>();

	static {
		effectiveBlocks.addAll(ItemPickaxe.field_150915_c);
		effectiveMaterials.add(Material.iron);
		effectiveMaterials.add(Material.anvil);
		effectiveMaterials.add(Material.rock);
	}

	public ItemPickaxeAdv(ToolMaterial toolMaterial) {

		super(2.0F, toolMaterial);
		addToolClass("pickaxe");
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
