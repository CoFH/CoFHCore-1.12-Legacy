package cofh.core.render.model;

import cofh.core.block.PartitionedBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.util.ResourceLocation;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * An IStateMapper implementation that also registers all partition models to be loaded.
 *
 * @author amadornes
 */
public class PartitionedStateMapper extends DefaultStateMapper {

	private final PartitionedBlockState state;

	public PartitionedStateMapper(PartitionedBlockState state) {

		this.state = state;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {

		if (block != state.getBlock()) {
			return super.putStateModelLocations(block); // If we somehow end up here, let's just play it safe
		}

		Map<IBlockState, ModelResourceLocation> map = new IdentityHashMap<>(super.putStateModelLocations(block));
		for (PartitionedBlockState.Partition partition : state.getPartitions()) {
			ResourceLocation name = partition.getName();
			if (name == null) {
				name = block.getRegistryName();
			}
			for (IBlockState state : partition.getPartitionContainer().getValidStates()) {
				String propertyString = getPropertyString(state.getProperties());
				map.put(state, new ModelResourceLocation(name, propertyString));
			}
		}
		return map;
	}

}
