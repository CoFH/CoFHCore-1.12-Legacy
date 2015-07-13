package cofh.core.item;

import cofh.core.util.fluid.BucketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemBucket extends ItemBase {

	Item container = Items.bucket;

	public ItemBucket() {

		super();
		setMaxStackSize(1);
		setContainerItem(container);
	}

	public ItemBucket(String modName) {

		super(modName);
		setMaxStackSize(1);
		setContainerItem(container);
	}

	public ItemBucket(String modName, Item container) {

		super(modName);
		setMaxStackSize(1);
		this.container = container;
		setContainerItem(container);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		MovingObjectPosition pos = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (pos == null || pos.typeOfHit != MovingObjectType.BLOCK) {
			return stack;
		}
		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;

		switch (pos.sideHit) {
		case 0:
			--y;
			break;
		case 1:
			++y;
			break;
		case 2:
			--z;
			break;
		case 3:
			++z;
			break;
		case 4:
			--x;
			break;
		case 5:
			++x;
			break;
		}
		if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack) || !world.isAirBlock(x, y, z) && world.getBlock(x, y, z).getMaterial().isSolid()) {
			return stack;
		}
		if (BucketHandler.emptyBucket(world, x, y, z, stack)) {
			if (!player.capabilities.isCreativeMode) {
				return new ItemStack(container);
			}
		}
		return stack;
	}

}
