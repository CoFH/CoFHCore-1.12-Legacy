package cofh.core.render.model;

import cofh.core.block.AdaptedProperty;
import cofh.core.block.PartitionedBlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * A model extending another that merges property partitions into its quads on request.
 *
 * @author amadornes
 */
@SideOnly(Side.CLIENT)
public class PartitionedBakedModel implements IBakedModel {

	private final IBakedModel parent;
	private final PartitionedBlockState.Partition[] partitions;

	public PartitionedBakedModel(IBakedModel parent, PartitionedBlockState.Partition[] partitions) {

		this.parent = parent;
		this.partitions = partitions;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

		List<BakedQuad> quads = new ArrayList<>(parent.getQuads(state, side, rand));
		BlockModelShapes bms = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
		for (PartitionedBlockState.Partition partition : partitions) {
			IBlockState partitionState = partition.getPartitionContainer().getBaseState();
			if (state instanceof IExtendedBlockState) {
				IExtendedBlockState extendedState = (IExtendedBlockState) state;
				for (AdaptedProperty<?> property : partition.getProperties()) {
					partitionState = with(partitionState, extendedState, property);
				}
			}
			IBakedModel partitionModel = bms.getModelForState(partitionState);
			quads.addAll(partitionModel.getQuads(partitionState, side, rand));
		}
		return quads;
	}

	// Dirty little hack to avoid quite a few ugly raw casts
	private <V extends Comparable<V>> IBlockState with(IBlockState dst, IExtendedBlockState src, AdaptedProperty<V> prop) {

		return dst.withProperty(prop.getActualProperty(), src.getValue(prop));
	}

	@Override
	public boolean isAmbientOcclusion() {

		return parent.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {

		return parent.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {

		return parent.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {

		return parent.getParticleTexture();
	}

	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {

		return parent.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {

		return parent.getOverrides();
	}

	@Override
	public boolean isAmbientOcclusion(IBlockState state) {

		return parent.isAmbientOcclusion(state);
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		Pair<? extends IBakedModel, Matrix4f> pair = parent.handlePerspective(cameraTransformType);
		if (pair.getLeft() != parent) {
			return pair;
		}
		return Pair.of(this, pair.getRight());
	}

}
