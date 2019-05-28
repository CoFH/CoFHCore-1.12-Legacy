package cofh.core.proxy;

import cofh.core.block.PartitionedBlockState;
import cofh.core.render.model.PartitionedBakedModel;
import cofh.core.render.model.PartitionedStateMapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

/**
 * A handler for partitioned block model lifecycle events.
 *
 * @author amadornes
 */
@SideOnly(Side.CLIENT)
public final class EventHandlerPartitionedModel {

	public static final EventHandlerPartitionedModel INSTANCE = new EventHandlerPartitionedModel();

	/**
	 * Finds all blocks with partitioned state and replaces their state mapper with one that also loads partition models.
	 */
	@SubscribeEvent
	public void onModelRegistryReady(ModelRegistryEvent event) {

		for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
			if (block.getBlockState() instanceof PartitionedBlockState) {
				PartitionedBlockState pbs = (PartitionedBlockState) block.getBlockState();
				ModelLoader.setCustomStateMapper(block, new PartitionedStateMapper(pbs));
			}
		}
	}

	/**
	 * Finds all the blocks with partitioned state and wraps all their models to add those partitions.
	 */
	@SubscribeEvent
	public void onModelBaked(ModelBakeEvent event) {

		// Dummy state mapper just so we can use the unified getPropertyString method
		DefaultStateMapper stateMapper = new DefaultStateMapper();

		for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
			if (block.getBlockState() instanceof PartitionedBlockState) {
				PartitionedBlockState pbs = (PartitionedBlockState) block.getBlockState();
				for (IBlockState state : pbs.getValidStates()) {
					String propertyString = stateMapper.getPropertyString(state.getProperties());
					ModelResourceLocation location = new ModelResourceLocation(block.getRegistryName(), propertyString);
					IBakedModel model = event.getModelRegistry().getObject(location);
					event.getModelRegistry().putObject(location, new PartitionedBakedModel(model, pbs.getPartitions()));
				}
			}
		}
	}

}
