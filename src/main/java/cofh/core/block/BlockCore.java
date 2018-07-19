package cofh.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class BlockCore extends Block {

	protected String modName;
	protected String name;

	public BlockCore(Material material, String modName) {

		super(material);
		this.modName = modName;
	}

	public BlockCore(Material material, MapColor blockMapColor, String modName) {

		super(material, blockMapColor);
		this.modName = modName;
	}

	@Override
	public Block setUnlocalizedName(String name) {

		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public String getUnlocalizedName(ItemStack stack) {

		return getUnlocalizedName();
	}

	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.COMMON;
	}

}
