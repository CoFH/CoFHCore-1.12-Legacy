package cofh.core.util.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.io.*;

/**
 * Contains various helper functions to assist with {@link Fluid} and Fluid-related manipulation and interaction.
 *
 * @author King Lemming
 */
public class FluidHelper {

	public static final int BUCKET_VOLUME = Fluid.BUCKET_VOLUME;

	public static final Fluid WATER_FLUID = FluidRegistry.WATER;
	public static final Fluid LAVA_FLUID = FluidRegistry.LAVA;

	public static final FluidStack WATER = new FluidStack(WATER_FLUID, BUCKET_VOLUME);
	public static final FluidStack LAVA = new FluidStack(LAVA_FLUID, BUCKET_VOLUME);

	@CapabilityInject (IFluidHandler.class)
	public static final Capability<IFluidHandler> FLUID_HANDLER = null;

	@CapabilityInject (IFluidHandlerItem.class)
	public static final Capability<IFluidHandler> FLUID_HANDLER_ITEM = null;

	public static final FluidTankInfo[] NULL_TANK_INFO = new FluidTankInfo[] {};

	private FluidHelper() {

	}

	public static boolean isPlayerHoldingFluidHandler(EntityPlayer player) {

		return isFluidHandler(player.getHeldItemMainhand());
	}

	public static FluidStack getFluidStackFromHandler(ItemStack container) {

		if (isFluidHandler(container)) {
			IFluidTankProperties[] tank = container.getCapability(FLUID_HANDLER_ITEM, null).getTankProperties();
			return tank.length <= 0 ? null : tank[0].getContents();
		}
		return null;
	}

	/**
	 * Checks if an item has the FluidHandlerItem capability.
	 *
	 * @param stack The ItemStack to check.
	 * @return If the ItemStack has the fluid cap.
	 */
	public static boolean isFluidHandler(ItemStack stack) {

		return !stack.isEmpty() && stack.hasCapability(FLUID_HANDLER_ITEM, null);
	}

	public static boolean isFillableEmptyContainer(ItemStack empty) {

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return false;
		}
		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canFill()) {
				return false;
			}
			FluidStack contents = properties.getContents();
			if (contents != null && contents.amount > 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDrainableFilledContainer(ItemStack container) {

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return false;
		}
		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canDrain()) {
				return false;
			}
			FluidStack contents = properties.getContents();
			if (contents == null || contents.amount < properties.getCapacity()) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack setDefaultFluidTag(ItemStack container, FluidStack resource) {

		container.setTagCompound(new NBTTagCompound());
		NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
		container.getTagCompound().setTag("Fluid", fluidTag);

		return container;
	}

	/* IFluidHandler Interaction */
	public static FluidStack extractFluidFromAdjacentFluidHandler(TileEntity tile, EnumFacing side, int maxDrain, boolean doDrain) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).drain(maxDrain, doDrain) : null;
	}

	public static int insertFluidIntoAdjacentFluidHandler(TileEntity tile, EnumFacing side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).fill(fluid, doFill) : 0;
	}

	public static int insertFluidIntoAdjacentFluidHandler(World world, BlockPos pos, EnumFacing side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(world, pos, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).fill(fluid, doFill) : 0;
	}

	public static boolean isAdjacentFluidHandler(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		return handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
	}

	/**
	 * Checks if the tile has the fluid capability on a specific face.
	 *
	 * @param tile The tile to check.
	 * @param face The face of the block to check.
	 * @return If the face has the cap.
	 */
	public static boolean isFluidHandler(TileEntity tile, EnumFacing face) {

		return tile != null && tile.hasCapability(FLUID_HANDLER, face);
	}

	/**
	 * Checks if the tile has the fluid capability on the "General" face.
	 *
	 * @param tile The tile to check.
	 * @return If the tile has the cap.
	 */
	public static boolean isFluidHandler(TileEntity tile) {

		return tile != null && tile.hasCapability(FLUID_HANDLER, null);
	}

	/**
	 * Attempts to drain the item to an IFluidHandler.
	 *
	 * @param stack   The stack to drain from.
	 * @param handler The IFluidHandler to fill.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If the interaction was successful.
	 */
	public static boolean drainItemToHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		if (stack.isEmpty() || handler == null || player == null) {
			return false;
		}
		IItemHandler playerInv = new InvWrapper(player.inventory);
		FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player);
		if (result.isSuccess()) {
			player.setHeldItem(hand, result.getResult());
			return true;
		}
		return false;
	}

	/**
	 * Attempts to fill the item from an IFluidHandler.
	 *
	 * @param stack   The stack to fill.
	 * @param handler The IFluidHandler to drain from.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If the interaction was successful.
	 */
	public static boolean fillItemFromHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		if (stack.isEmpty() || handler == null || player == null) {
			return false;
		}
		IItemHandler playerInv = new InvWrapper(player.inventory);
		FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player);
		if (result.isSuccess()) {
			player.setHeldItem(hand, result.getResult());
			return true;
		}
		return false;
	}

	/**
	 * Attempts to interact the item with an IFluidHandler.
	 * Interaction will always try and fill the item first, if this fails it will attempt to drain the item.
	 *
	 * @param stack   The stack to interact with.
	 * @param handler The Handler to fill / drain.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If any interaction with the handler was successful.
	 */
	public static boolean interactWithHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		return fillItemFromHandler(stack, handler, player, hand) || drainItemToHandler(stack, handler, player, hand);
	}

	/* PACKETS */
	public static void writeFluidStackToPacket(FluidStack fluid, DataOutput data) throws IOException {

		if (!isValidFluidStack(fluid)) {
			data.writeShort(-1);
		} else {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(fluid.writeToNBT(new NBTTagCompound()), byteStream);
			byte[] abyte = byteStream.toByteArray();
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
			ByteArrayInputStream byteStream = new ByteArrayInputStream(abyte);
			return FluidStack.loadFluidStackFromNBT(CompressedStreamTools.readCompressed(byteStream));
		}
	}

	/* HELPERS */
	public static boolean isValidFluidStack(FluidStack fluid) {

		return fluid != null && FluidRegistry.getFluidName(fluid) != null;
	}

	public static int getFluidLuminosity(FluidStack fluid) {

		return fluid == null ? 0 : getFluidLuminosity(fluid.getFluid());
	}

	public static int getFluidLuminosity(Fluid fluid) {

		return fluid == null ? 0 : fluid.getLuminosity();
	}

	public static FluidStack getFluidFromWorld(World world, BlockPos pos, boolean doDrain) {

		IBlockState state = world.getBlockState(pos);
		Block bId = state.getBlock();
		int bMeta = bId.getMetaFromState(state);

		if (Block.isEqualTo(bId, Blocks.WATER)) {
			if (bMeta == 0) {
				return WATER.copy();
			} else {
				return null;
			}
		} else if (Block.isEqualTo(bId, Blocks.LAVA) || Block.isEqualTo(bId, Blocks.FLOWING_LAVA)) {
			if (bMeta == 0) {
				return LAVA.copy();
			} else {
				return null;
			}
		} else if (bId instanceof IFluidBlock) {
			IFluidBlock block = (IFluidBlock) bId;
			return block.drain(world, pos, doDrain);
		}
		return null;
	}

	public static FluidStack getFluidFromWorld(World world, BlockPos pos) {

		return getFluidFromWorld(world, pos, false);
	}

	public static Fluid lookupFluidForBlock(Block block) {

		if (block == Blocks.FLOWING_WATER) {
			return WATER_FLUID;
		}
		if (block == Blocks.FLOWING_LAVA) {
			return LAVA_FLUID;
		}
		return FluidRegistry.lookupFluidForBlock(block);
	}

	public static FluidStack getFluidForFilledItem(ItemStack container) {

		if (container != null && isFluidHandler(container)) {
			return getFluidStackFromHandler(container);
		}
		return null;
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

	public static int getFluidHash(FluidStack stack) {

		return stack.tag != null ? stack.getFluid().getName().hashCode() + 31 * stack.tag.toString().hashCode() : stack.getFluid().getName().hashCode();
	}

}
