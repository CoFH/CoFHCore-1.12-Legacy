package cofh.lib.util.helpers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Contains various helper functions to assist with {@link Fluid} and Fluid-related manipulation and interaction.
 *
 * @author King Lemming
 *
 */
public class FluidHelper {

	public static final int BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

	public static final Fluid WATER_FLUID = FluidRegistry.WATER;
	public static final Fluid LAVA_FLUID = FluidRegistry.LAVA;

	public static final FluidStack WATER = new FluidStack(WATER_FLUID, BUCKET_VOLUME);
	public static final FluidStack LAVA = new FluidStack(LAVA_FLUID, BUCKET_VOLUME);

	public static final FluidTankInfo[] NULL_TANK_INFO = new FluidTankInfo[] {};

	private FluidHelper() {

	}

	/* IFluidContainer Interaction */
	public static int fillFluidContainerItem(ItemStack container, FluidStack resource, boolean doFill) {

		return isFluidContainerItem(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) : 0;
	}

	public static FluidStack drainFluidContainerItem(ItemStack container, int maxDrain, boolean doDrain) {

		return isFluidContainerItem(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain)
				: null;
	}

	public static FluidStack extractFluidFromHeldContainer(EntityPlayer player, int maxDrain, boolean doDrain) {

		ItemStack container = player.getCurrentEquippedItem();

		return isFluidContainerItem(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain)
				: null;
	}

	public static int insertFluidIntoHeldContainer(EntityPlayer player, FluidStack resource, boolean doFill) {

		ItemStack container = player.getCurrentEquippedItem();

		return isFluidContainerItem(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) : 0;
	}

	public static boolean isPlayerHoldingFluidContainerItem(EntityPlayer player) {

		return isFluidContainerItem(player.getCurrentEquippedItem());
	}

	public static boolean isFluidContainerItem(ItemStack container) {

		return container != null && container.getItem() instanceof IFluidContainerItem;
	}

	public static FluidStack getFluidStackFromContainerItem(ItemStack container) {

		return ((IFluidContainerItem) container.getItem()).getFluid(container);
	}

	public static ItemStack setDefaultFluidTag(ItemStack container, FluidStack resource) {

		container.setTagCompound(new NBTTagCompound());
		NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
		container.stackTagCompound.setTag("Fluid", fluidTag);

		return container;
	}

	/* IFluidHandler Interaction */
	public static FluidStack extractFluidFromAdjacentFluidHandler(TileEntity tile, int side, int maxDrain, boolean doDrain) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IFluidHandler ? ((IFluidHandler) handler).drain(ForgeDirection.VALID_DIRECTIONS[side ^ 1], maxDrain, doDrain) : null;
	}

	public static int insertFluidIntoAdjacentFluidHandler(TileEntity tile, int side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IFluidHandler ? ((IFluidHandler) handler).fill(ForgeDirection.VALID_DIRECTIONS[side ^ 1], fluid, doFill) : 0;
	}

	// TODO: Replace with sided version post-1.8 Fluid revamp
	public static boolean isAdjacentFluidHandler(TileEntity tile, int side) {

		return BlockHelper.getAdjacentTileEntity(tile, side) instanceof IFluidHandler;
	}

	// TODO: Replace with sided version post-1.8 Fluid revamp
	public static boolean isFluidHandler(TileEntity tile) {

		return tile instanceof IFluidHandler;
	}

	/* Fluid Container Registry Interaction */
	public static boolean fillContainerFromHandler(World world, IFluidHandler handler, EntityPlayer player, FluidStack tankFluid) {

		ItemStack container = player.getCurrentEquippedItem();

		if (FluidContainerRegistry.isEmptyContainer(container)) {
			ItemStack returnStack = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(returnStack);

			if (fluid == null || returnStack == null) {
				return false;
			}
			if (ServerHelper.isClientWorld(world)) {
				return true;
			}
			if (!player.capabilities.isCreativeMode) {
				if (container.stackSize == 1) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
					container.stackSize--;
					if (container.stackSize <= 0) {
						container = null;
					}
				} else {
					if (ItemHelper.disposePlayerItem(player.getCurrentEquippedItem(), returnStack, player, true)) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
					}
				}
			}
			handler.drain(ForgeDirection.UNKNOWN, fluid.amount, true);
			return true;
		}
		return false;
	}

	public static boolean fillHandlerWithContainer(World world, IFluidHandler handler, EntityPlayer player) {

		ItemStack container = player.getCurrentEquippedItem();
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);

		if (fluid != null) {
			if (handler.fill(ForgeDirection.UNKNOWN, fluid, false) == fluid.amount || player.capabilities.isCreativeMode) {
				ItemStack returnStack = FluidContainerRegistry.drainFluidContainer(container);
				if (ServerHelper.isClientWorld(world)) {
					return true;
				}
				if (!player.capabilities.isCreativeMode) {
					if (ItemHelper.disposePlayerItem(player.getCurrentEquippedItem(), returnStack, player, true)) {
						if (ServerHelper.isServerWorld(world)) {
							player.openContainer.detectAndSendChanges();
							((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
						}
					}
				}
				handler.fill(ForgeDirection.UNKNOWN, fluid, true);
				return true;
			}
		}
		return false;
	}

	/* PACKETS */
	public static void writeFluidStackToPacket(FluidStack fluid, DataOutput data) throws IOException {

		if (!isValidFluidStack(fluid)) {
			data.writeShort(-1);
		} else {
			byte[] abyte = CompressedStreamTools.compress(fluid.writeToNBT(new NBTTagCompound()));
			data.writeShort((short) abyte.length);
			data.write(abyte);
		}
	}

	public static FluidStack readFluidStackFromPacket(DataInput data) throws IOException {

		short length = data.readShort();

		if (length < 0) {
			return null;
		} else {
			byte[] abyte = new byte[length];
			data.readFully(abyte);
			return FluidStack.loadFluidStackFromNBT(CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L)));
		}
	}

	/* HELPERS */
	public static boolean isValidFluidStack(FluidStack fluid) {

		return fluid == null ? false : FluidRegistry.getFluidName(fluid) != null;
	}

	public static int getFluidLuminosity(FluidStack fluid) {

		return fluid == null ? 0 : getFluidLuminosity(fluid.getFluid());
	}

	public static int getFluidLuminosity(Fluid fluid) {

		return fluid == null ? 0 : fluid.getLuminosity();
	}

	public static FluidStack getFluidFromWorld(World world, int x, int y, int z, boolean doDrain) {

		Block bId = world.getBlock(x, y, z);
		int bMeta = world.getBlockMetadata(x, y, z);

		if (Block.isEqualTo(bId, Blocks.water)) {
			if (bMeta == 0) {
				return WATER.copy();
			} else {
				return null;
			}
		} else if (Block.isEqualTo(bId, Blocks.lava) || Block.isEqualTo(bId, Blocks.flowing_lava)) {
			if (bMeta == 0) {
				return LAVA.copy();
			} else {
				return null;
			}
		} else if (bId instanceof IFluidBlock) {
			IFluidBlock block = (IFluidBlock) bId;
			return block.drain(world, x, y, z, doDrain);
		}
		return null;
	}

	public static FluidStack getFluidFromWorld(World world, int x, int y, int z) {

		return getFluidFromWorld(world, x, y, z, false);
	}

	public static Fluid lookupFluidForBlock(Block block) {

		if (block == Blocks.flowing_water) {
			return WATER_FLUID;
		}
		if (block == Blocks.flowing_lava) {
			return LAVA_FLUID;
		}
		return FluidRegistry.lookupFluidForBlock(block);
	}

	public static FluidStack getFluidForFilledItem(ItemStack container) {

		if (container != null && container.getItem() instanceof IFluidContainerItem) {
			return ((IFluidContainerItem) container.getItem()).getFluid(container);
		}
		return FluidContainerRegistry.getFluidForFilledItem(container);
	}

	public static boolean isFluidEqualOrNull(FluidStack resourceA, FluidStack resourceB) {

		return resourceA == null || resourceB == null || resourceA.isFluidEqual(resourceB);
	}

	public static boolean isFluidEqualOrNull(Fluid fluidA, FluidStack resourceB) {

		return fluidA == null || resourceB == null || fluidA == resourceB.getFluid();
	}

	public static boolean isFluidEqualOrNull(Fluid fluidA, Fluid fluidB) {

		return fluidA == null || fluidB == null || fluidA == fluidB;
	}

	public static boolean isFluidEqual(FluidStack resourceA, FluidStack resourceB) {

		return resourceA != null && resourceA.isFluidEqual(resourceB);
	}

	public static boolean isFluidEqual(Fluid fluidA, FluidStack resourceB) {

		return fluidA != null && resourceB != null && fluidA == resourceB.getFluid();
	}

	public static boolean isFluidEqual(Fluid fluidA, Fluid fluidB) {

		return fluidA != null && fluidB != null && fluidA.equals(fluidB);
	}

}
