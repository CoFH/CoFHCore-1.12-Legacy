package cofh.core.util;

import cofh.core.util.core.IInitializer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class PluginCore implements IInitializer {

	protected final String modId;
	protected final String modName;
	protected boolean enable;
	protected boolean error;

	public PluginCore(String modId, String modName) {

		this.modId = modId;
		this.modName = modName;
	}

	/* IInitializer */
	@Override
	public abstract boolean initialize();

	@Override
	public abstract boolean register();

	/* HELPERS */
	protected Block getBlock(String id, String name) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id + ":" + name));
		return block == null ? Blocks.AIR : block;
	}

	protected Block getBlock(String name) {

		return getBlock(modId, name);
	}

	protected ItemStack getItemStack(String id, String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	protected ItemStack getItemStack(String name, int amount, int meta) {

		return getItemStack(modId, name, amount, meta);
	}

	protected ItemStack getItemStack(String name, int amount) {

		return getItemStack(modId, name, amount, 0);
	}

	protected ItemStack getItemStack(String name) {

		return getItemStack(modId, name, 1, 0);
	}

	public static final String NUGGET = "nugget";
	public static final String INGOT = "ingot";
	public static final String ORE = "ore";
	public static final String BLOCK = "block";
	public static final String DUST = "dust";
	public static final String PLATE = "plate";

}
