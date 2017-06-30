package cofh.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;

/**
 * Wrapper for a Block/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 *
 * @author King Lemming
 */
public final class BlockWrapper {

	public Block block;
	public int metadata;

	public BlockWrapper(IBlockState state) {

		this(state.getBlock(), state.getBlock().getMetaFromState(state));
	}

	public BlockWrapper(Block block, int metadata) {

		this.block = block;
		this.metadata = metadata;
	}

	public BlockWrapper set(Block block, int metadata) {

		if (block != null) {
			this.block = block;
			this.metadata = metadata;
		} else {
			this.block = null;
			this.metadata = 0;
		}
		return this;
	}

	public boolean isEqual(@Nonnull IBlockState state) {

		return block == state.getBlock() && metadata == block.getMetaFromState(state);
	}

	public boolean isEqual(BlockWrapper other) {

		if (other == null) {
			return false;
		}
		if (metadata == other.metadata) {
			if (block == other.block) {
				return true;
			}
			if (block != null && other.block != null) {
				return block.delegate.get() == other.block.delegate.get();
			}
		}
		return false;
	}

	final int getId() {

		return Block.getIdFromBlock(block);
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof BlockWrapper)) {
			return false;
		}
		return isEqual((BlockWrapper) o);
	}

	@Override
	public int hashCode() {

		return metadata | getId() << 16;
	}

	@Override
	public String toString() {

		String b = getClass().getName() + '@' + System.identityHashCode(this) + '{' + "m:" + metadata + ", i:" + (block == null ? null : block.getClass().getName()) + '@' + System.identityHashCode(block) + ", v:" + getId() + '}';
		return b;
	}

}
