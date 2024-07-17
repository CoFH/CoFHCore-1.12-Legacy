package cofh.lib.util;

import cofh.api.fluid.ITankContainerBucketable;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.position.BlockPosition;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 *
 * @deprecated Use {@link cofh.lib.util.helpers.FluidHelper}
 */
@Deprecated
public class UtilLiquidMover {

	/**
	 * Attempts to fill tank with the player's current item.
	 *
	 * @param itcb
	 *            the tank the liquid is going into
	 * @param player
	 *            the player trying to fill the tank
	 * @return True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EntityPlayer player) {

		ItemStack ci = player.inventory.getCurrentItem();
		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(ci);
		if (liquid != null) {
			Item item = ci.getItem();
			if (itcb.fill(ForgeDirection.UNKNOWN, liquid, false) == liquid.amount) {
				itcb.fill(ForgeDirection.UNKNOWN, liquid, true);

				if (!player.capabilities.isCreativeMode) {
					if (item.hasContainerItem(ci)) {
						ItemStack drop = item.getContainerItem(ci);
						if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage()) {
							drop = null;
						}
						ItemHelper.disposePlayerItem(ci, drop, player, true);
					} else {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(ci, player));
					}

					if (!player.worldObj.isRemote) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
					}
				}
				return true;
			}
		} else if (ci != null && ci.getItem() instanceof IFluidContainerItem) {
			Item item = ci.getItem();
			IFluidContainerItem fluidContainer = (IFluidContainerItem) item;
			liquid = fluidContainer.getFluid(ci);
			if (itcb.fill(ForgeDirection.UNKNOWN, liquid, false) > 0) {
				int amount = itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				ItemStack drop = ci.splitStack(1);
				ci.stackSize++;
				fluidContainer.drain(drop, amount, true);

				if (!player.capabilities.isCreativeMode) {
					if (item.hasContainerItem(drop)) {
						drop = item.getContainerItem(drop);
						if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage()) {
							drop = null;
						}
					}
					ItemHelper.disposePlayerItem(ci, drop, player, true);

					if (!player.worldObj.isRemote) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Attempts to drain tank into the player's current item.
	 *
	 * @param itcb
	 *            the tank the liquid is coming from
	 * @param player
	 *            the player trying to take liquid from the tank
	 * @return True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EntityPlayer player) {

		ItemStack ci = player.inventory.getCurrentItem();
		boolean isSmartContainer = false;
		IFluidContainerItem fluidContainer;
		if (ci != null && (FluidContainerRegistry.isEmptyContainer(ci) || (isSmartContainer = ci.getItem() instanceof IFluidContainerItem))) {
			for (FluidTankInfo tank : itcb.getTankInfo(ForgeDirection.UNKNOWN)) {
				FluidStack tankLiquid = tank.fluid;
				if (tankLiquid == null || tankLiquid.amount == 0) {
					continue;
				}
				ItemStack filledBucket = null;
				FluidStack bucketLiquid = null;
				if (isSmartContainer) {
					fluidContainer = (IFluidContainerItem) ci.getItem();
					filledBucket = ci.copy();
					filledBucket.stackSize = 1;
					if (fluidContainer.fill(filledBucket, tankLiquid, false) > 0) {
						int amount = fluidContainer.fill(filledBucket, tankLiquid, true);
						bucketLiquid = new FluidStack(tankLiquid, amount);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false);
						if (l == null || l.amount < amount) {
							filledBucket = null;
						}
					} else {
						filledBucket = null;
					}
				} else {
					filledBucket = FluidContainerRegistry.fillFluidContainer(tankLiquid, ci);
					if (FluidContainerRegistry.isFilledContainer(filledBucket)) {
						bucketLiquid = FluidContainerRegistry.getFluidForFilledItem(filledBucket);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false);
						if (l == null || l.amount < bucketLiquid.amount) {
							filledBucket = null;
						}
					} else {
						filledBucket = null;
					}
				}
				if (filledBucket != null
						&& ItemHelper.disposePlayerItem(ci, filledBucket, player, true)) {
					if (!player.worldObj.isRemote) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
					}
					itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
					return true;
				}
			}
		}
		return false;
	}

	public static void pumpLiquid(IFluidTank iFluidTank, TileEntity from) {

		if (iFluidTank != null && iFluidTank.getFluidAmount() > 0) {
			FluidStack l = iFluidTank.getFluid().copy();
			int amount = Math.min(l.amount, FluidContainerRegistry.BUCKET_VOLUME);
			l.amount = amount;
			for (BlockPosition adj : new BlockPosition(from).getAdjacent(true)) {

				IFluidHandler tile = adj.getTileEntity(from.getWorldObj(), IFluidHandler.class);
				if (tile != null) {
					if (!tile.canFill(adj.orientation.getOpposite(), l.getFluid())) {
						continue;
					}

					int filled = tile.fill(adj.orientation.getOpposite(), l, true);
					iFluidTank.drain(filled, true);
					amount -= filled;
					if (amount <= 0) {
						break;
					}
				}
			}
		}
	}

}
