package cofh.pcc.util;

import buildcraft.api.transport.IPipeTile;

import cofh.api.transport.IItemDuct;
import cofh.pcc.inventory.IInventoryManager;
import cofh.pcc.inventory.InventoryManager;
import cofh.util.position.BlockPosition;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class UtilInventory {

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in all directions.
	 * 
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IItemDuct> findConduits(World world, int x, int y, int z) {

		return findConduits(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in each directiontocheck.
	 * 
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IItemDuct> findConduits(World world, int x, int y, int z, ForgeDirection[] directionstocheck) {

		Map<ForgeDirection, IItemDuct> pipes = new LinkedHashMap<ForgeDirection, IItemDuct>();
		for (ForgeDirection direction : directionstocheck) {
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = direction;
			bp.moveForwards(1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te instanceof IItemDuct) {
				pipes.put(direction, (IItemDuct) te);
			}
		}
		return pipes;
	}

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in all directions.
	 * 
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IPipeTile> findPipes(World world, int x, int y, int z) {

		return findPipes(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in each directiontocheck.
	 * 
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IPipeTile> findPipes(World world, int x, int y, int z, ForgeDirection[] directionstocheck) {

		Map<ForgeDirection, IPipeTile> pipes = new LinkedHashMap<ForgeDirection, IPipeTile>();
		for (ForgeDirection direction : directionstocheck) {
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = direction;
			bp.moveForwards(1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te instanceof IPipeTile) {
				pipes.put(direction, (IPipeTile) te);
			}
		}
		return pipes;
	}

	/**
	 * Searches from position x, y, z, checking for inventories in all directions.
	 * 
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z) {

		return findChests(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for inventories in each directiontocheck.
	 * 
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z, ForgeDirection[] directionstocheck) {

		Map<ForgeDirection, IInventory> chests = new LinkedHashMap<ForgeDirection, IInventory>();
		for (ForgeDirection direction : directionstocheck) {
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = direction;
			bp.moveForwards(1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te != null && te instanceof IInventory) {
				chests.put(direction, (IInventory) te);
			}
		}
		return chests;
	}

	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. DOESN'T drop items into the world. Example of this behavior: Cargo dropoff rail, item
	 * collector.
	 * 
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack) {

		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, ForgeDirection.VALID_DIRECTIONS,
				ForgeDirection.UNKNOWN);
	}

	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. Drops items into the world. Example of this behavior: Harvesters, sludge boilers, etc.
	 * 
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection airdropdirection) {

		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, ForgeDirection.VALID_DIRECTIONS, airdropdirection);
	}

	/**
	 * Drops an ItemStack, into chests > pipes > the world, but only in a single direction. Example of this behavior: Item Router, Ejector
	 * 
	 * @param dropdirection
	 *            a -single- direction in which to check for pipes/chests
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection dropdirection, ForgeDirection airdropdirection) {

		ForgeDirection[] dropdirections = { dropdirection };
		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, dropdirections, airdropdirection);
	}

	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order.
	 * 
	 * @param from
	 *            the TileEntity doing the dropping
	 * @param stack
	 *            the ItemStack being dropped
	 * @param dropdirections
	 *            directions in which stack may be dropped into chests or pipes
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air. ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be
	 *            dropped into the world.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection[] dropdirections, ForgeDirection airdropdirection) {

		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, dropdirections, airdropdirection);
	}

	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order. It generally shouldn't be necessary to call this explicitly.
	 * 
	 * @param world
	 *            the worldObj
	 * @param bp
	 *            the BlockPosition to drop from
	 * @param stack
	 *            the ItemStack being dropped
	 * @param dropdirections
	 *            directions in which stack may be dropped into chests or pipes
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air. ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be
	 *            dropped into the world.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(World world, BlockPosition bp, ItemStack stack, ForgeDirection[] dropdirections, ForgeDirection airdropdirection) {

		// (0) Sanity check. Don't bother dropping if there's nothing to drop, and never try to drop items on the client.
		if (world.isRemote | stack == null || stack.stackSize == 0 || stack.getItem() == null) {
			return stack;
		}
		stack = stack.copy();
		// (0.5) Try to put stack in conduits that are in valid directions
		for (Entry<ForgeDirection, IItemDuct> pipe : findConduits(world, bp.x, bp.y, bp.z, dropdirections).entrySet()) {
			ForgeDirection from = pipe.getKey().getOpposite();
			stack = pipe.getValue().insertItem(from, stack);
			if (stack == null || stack.stackSize <= 0) {
				return null;
			}
		}
		// (1) Try to put stack in pipes that are in valid directions
		for (Entry<ForgeDirection, IPipeTile> pipe : findPipes(world, bp.x, bp.y, bp.z, dropdirections).entrySet()) {
			ForgeDirection from = pipe.getKey().getOpposite();
			if (pipe.getValue().isPipeConnected(from)) {
				if (pipe.getValue().injectItem(stack.copy(), false, from) > 0) {
					stack.stackSize -= pipe.getValue().injectItem(stack.copy(), true, from);
					if (stack.stackSize <= 0) {
						return null;
					}
				}
			}
		}
		// (2) Try to put stack in chests that are in valid directions
		for (Entry<ForgeDirection, IInventory> chest : findChests(world, bp.x, bp.y, bp.z, dropdirections).entrySet()) {
			IInventoryManager manager = InventoryManager.create(chest.getValue(), chest.getKey().getOpposite());
			stack = manager.addItem(stack);
			if (stack == null || stack.stackSize == 0) {
				return null;
			}
		}
		// (3) Having failed to put it in a chest or a pipe, drop it in the air if airdropdirection is a valid direction.
		bp.orientation = airdropdirection;
		bp.moveForwards(1);
		if (Arrays.asList(ForgeDirection.VALID_DIRECTIONS).contains(airdropdirection) && world.isAirBlock(bp.x, bp.y, bp.z)) {
			bp.moveBackwards(1);
			dropStackInAir(stack, bp, world, airdropdirection);
			return null;
		}
		// (4) Is the stack still here? :( Better give it back.
		return stack;
	}

	private static void dropStackInAir(ItemStack stack, BlockPosition bp, World world, ForgeDirection towards) {

		float dropOffsetX = 0.0F;
		float dropOffsetY = 0.0F;
		float dropOffsetZ = 0.0F;

		switch (towards) {
		case UNKNOWN:
		case UP:
			dropOffsetX = 0.5F;
			dropOffsetY = 1.5F;
			dropOffsetZ = 0.5F;
			break;
		case DOWN:
			dropOffsetX = 0.5F;
			dropOffsetY = -0.75F;
			dropOffsetZ = 0.5F;
			break;
		case NORTH:
			dropOffsetX = 0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = -0.5F;
			break;
		case SOUTH:
			dropOffsetX = 0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 1.5F;
			break;
		case EAST:
			dropOffsetX = 1.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 0.5F;
			break;
		case WEST:
			dropOffsetX = -0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 0.5F;
			break;
		default:
			break;

		}

		EntityItem entityitem = new EntityItem(world, bp.x + dropOffsetX, bp.y + dropOffsetY, bp.z + dropOffsetZ, stack.copy());
		entityitem.motionX = 0.0D;
		if (towards != ForgeDirection.DOWN) {
			entityitem.motionY = 0.3D;
		}
		entityitem.motionZ = 0.0D;
		entityitem.delayBeforeCanPickup = 20;
		world.spawnEntityInWorld(entityitem);
	}

	public static ItemStack consumeItem(ItemStack stack, EntityPlayer player) {

		if (stack == null) {
			return null;
		}

		Item item = stack.getItem();

		if (item == null) {
			return null;
		}

		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);
			if (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage()) {
				ret = null;
			}
			if (stack.stackSize < 2) {
				return ret;
			}
			if (ret != null && !player.inventory.addItemStackToInventory(ret)) {
				player.func_146097_a(ret, false, true);
			}
		}

		stack.stackSize -= 1;
		if (stack.stackSize > 0) {
			return stack;
		}
		return null;
	}

	public static void mergeStacks(ItemStack to, ItemStack from) {

		if (to == null || from == null) {
			return;
		}

		if (!to.getItem().equals(from.getItem()) || to.getItemDamage() != from.getItemDamage()) {
			return;
		}
		if (to.getTagCompound() != null || from.getTagCompound() != null) {
			return;
		}

		int amountToCopy = Math.min(to.getMaxStackSize() - to.stackSize, from.stackSize);
		to.stackSize += amountToCopy;
		from.stackSize -= amountToCopy;
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2) {

		return stacksEqual(s1, s2, true);
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2, boolean nbtSensitive) {

		if (s1 == null || s2 == null) {
			return false;
		}
		if (!s1.isItemEqual(s2)) {
			return false;
		}

		if (nbtSensitive) {
			if (s1.getTagCompound() == null && s2.getTagCompound() == null) {
				return true;
			}
			if (s1.getTagCompound() == null || s2.getTagCompound() == null) {
				return false;
			}
			return s1.getTagCompound().equals(s2.getTagCompound());
		}

		return true;
	}
}
