package cofh.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemAxeAdv extends ItemToolAdv {

	public static Set<Block> effectiveBlocks = new HashSet<Block>();
	public static Set<Material> effectiveMaterials = new HashSet<Material>();

	static {
		effectiveBlocks.addAll(ItemAxe.field_150917_c);
		effectiveMaterials.add(Material.wood);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
	}

	public ItemAxeAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("axe");
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
