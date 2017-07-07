package cofh.core.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCore extends CreativeTabs {

	private final String modName;
	private final String label;

	public CreativeTabCore(String modName) {

		this(modName, "");
	}

	public CreativeTabCore(String modName, String label) {

		super(modName + label);
		this.modName = modName;
		this.label = label;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getIconItemStack() {

		return new ItemStack(Blocks.STONE);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getTabIconItem() {

		return getIconItemStack();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public String getTabLabel() {

		return modName + ".creativeTab" + label;
	}

}
