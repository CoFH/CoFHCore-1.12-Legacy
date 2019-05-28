package cofh.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A modified version of ExtendedBlockState with support for property partitions.<br/>
 * These allow for the declaration of properties that are baked independently from the rest of the model and composed in real time.
 *
 * @author amadornes
 */
public class PartitionedBlockState extends ExtendedBlockState {

	private final Partition[] partitions;

	private PartitionedBlockState(Block block, IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties, Partition[] partitions) {

		super(block, properties, unlistedProperties);
		this.partitions = partitions;
	}

	public Partition[] getPartitions() {

		return partitions;
	}

	public static class Partition {

		private final AdaptedProperty<?>[] properties;

		private final ResourceLocation name;
		private final BlockStateContainer partitionContainer;

		private Partition(Block block, ResourceLocation name, AdaptedProperty<?>[] properties) {

			this.properties = properties;
			this.name = name;

			IProperty<?>[] blockProperties = new IProperty[properties.length];
			for (int i = 0; i < properties.length; i++) {
				blockProperties[i] = properties[i].getActualProperty();
			}
			this.partitionContainer = new BlockStateContainer(block, blockProperties);
		}

		public AdaptedProperty[] getProperties() {

			return properties;
		}

		public ResourceLocation getName() {

			return name;
		}

		public BlockStateContainer getPartitionContainer() {

			return partitionContainer;
		}

	}

	public static class Builder {

		private final Block block;
		private final Set<String> propertyNames = new HashSet<>();
		// Only really using a list here because Forge does. I don't think ordering matters at all, but just in case...
		private final List<IProperty<?>> listed = new ArrayList<>();
		private final Set<IUnlistedProperty<?>> unlisted = new HashSet<>();
		private final List<Partition> partitions = new ArrayList<>();

		public Builder(Block block) {

			this.block = block;
		}

		public Builder add(IProperty<?>... props) {

			for (IProperty<?> prop : props) {
				if (propertyNames.add(prop.getName())) {
					listed.add(prop);
				} else {
					throw new IllegalArgumentException("Attempted to add an already existing block property: " + prop.getName());
				}
			}
			return this;
		}

		public Builder add(IUnlistedProperty<?>... props) {

			unlisted.addAll(Arrays.asList(props));
			return this;
		}

		public Builder addPartition(String domain, String name, AdaptedProperty<?>... props) {

			return addPartition(new ResourceLocation(domain, name), props);
		}

		public Builder addPartition(ResourceLocation name, AdaptedProperty<?>... props) {

			for (AdaptedProperty<?> prop : props) {
				if (propertyNames.contains(prop.getName())) {
					throw new IllegalArgumentException("Attempted to add an already existing block property to a partition: " + prop.getName());
				}
			}
			for (AdaptedProperty<?> prop : props) {
				propertyNames.add(prop.getName());
				unlisted.add(prop);
			}
			partitions.add(new Partition(block, name, props));
			return this;
		}

		public BlockStateContainer build() {

			IProperty<?>[] listed = this.listed.toArray(new IProperty[0]);
			if (partitions.isEmpty()) {
				IUnlistedProperty<?>[] unlisted = this.unlisted.toArray(new IUnlistedProperty[0]);
				return new BlockStateContainer.Builder(block).add(listed).add(unlisted).build();
			}
			IUnlistedProperty<?>[] unlisted = this.unlisted.toArray(new IUnlistedProperty[0]);
			Partition[] partitions = this.partitions.toArray(new Partition[0]);
			return new PartitionedBlockState(block, listed, unlisted, partitions);
		}

	}

}
