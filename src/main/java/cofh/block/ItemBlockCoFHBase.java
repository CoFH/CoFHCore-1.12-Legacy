package cofh.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockCoFHBase extends ItemBlock {

	public ItemBlockCoFHBase(Block block) {

		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
			int metadata) {

		if (world.getBlock(x, y, z).isAir(world, x, y, z) && !world.setBlockToAir(x, y, z)) {
			return false; // TODO: review if this is needed
		}
		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}

}
