package cofh.core.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StateMapper extends StateMapperBase implements ItemMeshDefinition {

	private final ModelResourceLocation modelLocation;

	public StateMapper(String modName, String path, String name) {
		this.modelLocation = new ModelResourceLocation(new ResourceLocation(modName.toLowerCase(), path), name);
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		return modelLocation;
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		return modelLocation;
	}
}
