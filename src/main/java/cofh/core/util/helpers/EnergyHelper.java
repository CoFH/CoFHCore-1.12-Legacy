package cofh.core.util.helpers;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class contains helper functions related to Redstone Flux, the basis of the CoFH Energy System.
 *
 * Compatibility is also provide for the Forge Energy system.
 *
 * @author King Lemming
 */
public class EnergyHelper {

	@CapabilityInject (IEnergyStorage.class)
	public static final Capability<IEnergyStorage> ENERGY_HANDLER = null;

	private EnergyHelper() {

	}

	/* NBT TAG HELPER */
	public static void addEnergyInformation(ItemStack stack, List<String> list) {

		if (stack.getItem() instanceof IEnergyContainerItem) {
			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(stack.getTagCompound().getInteger("Energy")) + " / " + StringHelper.getScaledNumber(((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack)) + " RF");
		}
	}

	/* IEnergyContainer Interaction */
	public static int extractEnergyFromContainer(ItemStack container, int maxExtract, boolean simulate) {

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
	}

	public static int insertEnergyIntoContainer(ItemStack container, int maxReceive, boolean simulate) {

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
	}

	public static int extractEnergyFromHeldContainer(EntityPlayer player, int maxExtract, boolean simulate) {

		ItemStack container = player.getHeldItemMainhand();

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
	}

	public static int insertEnergyIntoHeldContainer(EntityPlayer player, int maxReceive, boolean simulate) {

		ItemStack container = player.getHeldItemMainhand();

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
	}

	public static boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		return isEnergyContainerItem(player.getHeldItemMainhand());
	}

	public static boolean isEnergyContainerItem(ItemStack container) {

		return !container.isEmpty() && container.getItem() instanceof IEnergyContainerItem;
	}

	/**
	 * Checks if an item has the EnergyHandler capability.
	 *
	 * @param stack The ItemStack to check.
	 * @return If the ItemStack has the Energy cap.
	 */
	public static boolean isEnergyHandler(@Nullable ItemStack stack) {

		return !stack.isEmpty() && stack.hasCapability(ENERGY_HANDLER, null);
	}

	public static IEnergyStorage getEnergyHandler(ItemStack stack) {

		return stack.getCapability(ENERGY_HANDLER, null);
	}

	public static ItemStack setDefaultEnergyTag(ItemStack container, int energy) {

		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.getTagCompound().setInteger("Energy", energy);

		return container;
	}

	/* IEnergyHandler Interaction */
	public static int extractEnergyFromAdjacentEnergyProvider(TileEntity tile, EnumFacing side, int energy, boolean simulate) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		if (handler instanceof IEnergyProvider) {
			return ((IEnergyProvider) handler).extractEnergy(side.getOpposite(), energy, simulate);
		} else if (handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite())) {
			return handler.getCapability(ENERGY_HANDLER, side.getOpposite()).extractEnergy(energy, simulate);
		}
		return 0;
	}

	public static int insertEnergyIntoAdjacentEnergyReceiver(TileEntity tile, EnumFacing side, int energy, boolean simulate) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		if (handler instanceof IEnergyReceiver) {
			return ((IEnergyReceiver) handler).receiveEnergy(side.getOpposite(), energy, simulate);
		} else if (handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite())) {
			return handler.getCapability(ENERGY_HANDLER, side.getOpposite()).receiveEnergy(energy, simulate);
		}
		return 0;
	}

	public static boolean isAdjacentEnergyConnectableFromSide(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return isEnergyConnectableFromSide(handler, side.getOpposite());
	}

	public static boolean isEnergyConnectableFromSide(TileEntity tile, EnumFacing from) {

		return tile instanceof IEnergyConnection && ((IEnergyConnection) tile).canConnectEnergy(from);
	}

	public static boolean isAdjacentEnergyReceiverFromSide(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return isEnergyReceiverFromSide(handler, side.getOpposite());
	}

	public static boolean isEnergyReceiverFromSide(TileEntity tile, EnumFacing from) {

		return tile instanceof IEnergyReceiver && ((IEnergyReceiver) tile).canConnectEnergy(from);
	}

	public static boolean isAdjacentEnergyProviderFromSide(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return isEnergyProviderFromSide(handler, side.getOpposite());
	}

	public static boolean isEnergyProviderFromSide(TileEntity tile, EnumFacing from) {

		return tile instanceof IEnergyProvider && ((IEnergyProvider) tile).canConnectEnergy(from);
	}

	public static boolean isAdjacentEnergyHandler(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		return handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite());
	}

	public static boolean canAdjacentEnergyHandlerExtract(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		if (handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite())) {
			IEnergyStorage storage = handler.getCapability(ENERGY_HANDLER, side.getOpposite());

			if (storage != null) {
				return storage.canExtract();
			}
		}
		return false;
	}

	public static boolean canAdjacentEnergyHandlerReceive(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		if (handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite())) {
			IEnergyStorage storage = handler.getCapability(ENERGY_HANDLER, side.getOpposite());

			if (storage != null) {
				return storage.canReceive();
			}
		}
		return false;
	}

	/**
	 * Checks if the tile has the energy capability on a specific face.
	 *
	 * @param tile The tile to check.
	 * @param face The face of the block to check.
	 * @return If the face has the cap.
	 */
	public static boolean isEnergyHandler(TileEntity tile, EnumFacing face) {

		return tile != null && tile.hasCapability(ENERGY_HANDLER, face);
	}

}
