package cofh.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.function.Function;

public class RegistryHelper {

	public static void registerBlockAndItem(Block block, ResourceLocation name) {
		registerBlockAndItem(block, name, ItemBlock::new);
	}

	public static void registerBlockAndItem(Block block, ResourceLocation name, @Nullable Function<Block, ItemBlock> itemFactory) {

		GameRegistry.register(block.setRegistryName(name));
		GameRegistry.register(itemFactory.apply(block).setRegistryName(name));
	}
}
