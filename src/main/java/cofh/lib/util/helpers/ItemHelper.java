package cofh.lib.util.helpers;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

import cofh.api.item.IEmpowerableItem;
import cofh.api.item.IInventoryContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.lib.util.OreDictionaryProxy;
import com.google.common.base.Strings;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Contains various helper functions to assist with {@link Item} and {@link ItemStack} manipulation and interaction.
 *
 * @author King Lemming
 *
 */
public final class ItemHelper {

	public static final String BLOCK = "block";
	public static final String ORE = "ore";
	public static final String DUST = "dust";
	public static final String INGOT = "ingot";
	public static final String NUGGET = "nugget";
	public static final String LOG = "log";

	public static OreDictionaryProxy oreProxy = new OreDictionaryProxy();

	private ItemHelper() {

	}

	public static ItemStack cloneStack(Item item, int stackSize) {

		if (item == null) {
			return null;
		}
		ItemStack stack = new ItemStack(item, stackSize);

		return stack;
	}

	public static ItemStack cloneStack(Block item, int stackSize) {

		if (item == null) {
			return null;
		}
		ItemStack stack = new ItemStack(item, stackSize);

		return stack;
	}

	public static ItemStack cloneStack(ItemStack stack, int stackSize) {

		if (stack == null) {
			return null;
		}
		ItemStack retStack = stack.copy();
		retStack.stackSize = stackSize;

		return retStack;
	}

	public static ItemStack cloneStack(ItemStack stack) {

		if (stack == null) {
			return null;
		}
		ItemStack retStack = stack.copy();

		return retStack;
	}

	public static ItemStack copyTag(ItemStack container, ItemStack other) {

		if (other != null && other.stackTagCompound != null) {
			container.stackTagCompound = (NBTTagCompound) other.stackTagCompound.copy();
		}
		return container;
	}

	public static NBTTagCompound setItemStackTagName(NBTTagCompound tag, String name) {

		if (Strings.isNullOrEmpty(name)) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		if (!tag.hasKey("display")) {
			tag.setTag("display", new NBTTagCompound());
		}
		tag.getCompoundTag("display").setString("Name", name);

		return tag;
	}

	public static ItemStack readItemStackFromNBT(NBTTagCompound nbt) {

		ItemStack stack = new ItemStack(Item.getItemById(nbt.getShort("id")));
		stack.stackSize = nbt.getInteger("Count");
		stack.setItemDamage(Math.max(0, nbt.getShort("Damage")));

		if (nbt.hasKey("tag", 10)) {
			stack.stackTagCompound = nbt.getCompoundTag("tag");
		}
		return stack;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", stack.stackSize);
		nbt.setShort("Damage", (short) getItemDamage(stack));

		if (stack.stackTagCompound != null) {
			nbt.setTag("tag", stack.stackTagCompound);
		}
		return nbt;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, int amount, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", amount);
		nbt.setShort("Damage", (short) getItemDamage(stack));

		if (stack.stackTagCompound != null) {
			nbt.setTag("tag", stack.stackTagCompound);
		}
		return nbt;
	}

	public static String getNameFromItemStack(ItemStack stack) {

		if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("display")) {
			return "";
		}
		return stack.stackTagCompound.getCompoundTag("display").getString("Name");
	}

	public static ItemStack damageItem(ItemStack stack, int amt, Random rand) {

		if (stack != null && stack.isItemStackDamageable() && stack.attemptDamageItem(amt, rand)) {
			if (--stack.stackSize <= 0) {
				stack = null;
			} else {
				stack.setItemDamage(0);
			}
		}

		return stack;
	}

	public static ItemStack consumeItem(ItemStack stack) {

		if (stack == null) {
			return null;
		}

		Item item = stack.getItem();
		boolean largerStack = stack.stackSize > 1;
		// vanilla only alters the stack passed to hasContainerItem/etc. when the size is >1

		if (largerStack) {
			stack.stackSize -= 1;
		}
		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);

			if (ret == null) {
				return null;
			}
			if (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage()) {
				ret = null;
			}
			return ret;
		}

		return largerStack ? stack : null;
	}

	public static ItemStack consumeItem(ItemStack stack, EntityPlayer player) {

		if (stack == null) {
			return null;
		}

		Item item = stack.getItem();
		boolean largerStack = stack.stackSize > 1;
		// vanilla only alters the stack passed to hasContainerItem/etc. when the size is >1

		if (largerStack) {
			stack.stackSize -= 1;
		}
		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);

			if (ret == null || (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage())) {
				ret = null;
			}
			if (stack.stackSize < 1) {
				return ret;
			}
			if (ret != null && !player.inventory.addItemStackToInventory(ret)) {
				player.func_146097_a(ret, false, true);
			}
		}

		return largerStack ? stack : null;
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop) {

		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace) {

		if (entityplayer == null || entityplayer.capabilities.isCreativeMode) {
			return true;
		}
		if (allowReplace && stack.stackSize <= 1) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			entityplayer.inventory.addItemStackToInventory(dropStack);
			return true;
		} else if (allowDrop) {
			stack.stackSize -= 1;
			if (dropStack != null && !entityplayer.inventory.addItemStackToInventory(dropStack)) {
				entityplayer.func_146097_a(dropStack, false, true);
			}
			return true;
		}
		return false;
	}

	/**
	 * This prevents an overridden getDamage() call from messing up metadata acquisition.
	 */
	public static int getItemDamage(ItemStack stack) {

		return Items.diamond.getDamage(stack);
	}

	/**
	 * Gets a vanilla CraftingManager result.
	 */
	public static ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {

		ItemStack[] dmgItems = new ItemStack[2];
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null) {
				if (dmgItems[0] == null) {
					dmgItems[0] = inv.getStackInSlot(i);
				} else {
					dmgItems[1] = inv.getStackInSlot(i);
					break;
				}
			}
		}
		if (dmgItems[0] == null || dmgItems[0].getItem() == null) {
			return null;
		} else if (dmgItems[1] != null && dmgItems[0].getItem() == dmgItems[1].getItem() && dmgItems[0].stackSize == 1 && dmgItems[1].stackSize == 1
				&& dmgItems[0].getItem().isRepairable()) {
			Item theItem = dmgItems[0].getItem();
			int var13 = theItem.getMaxDamage() - dmgItems[0].getItemDamageForDisplay();
			int var8 = theItem.getMaxDamage() - dmgItems[1].getItemDamageForDisplay();
			int var9 = var13 + var8 + theItem.getMaxDamage() * 5 / 100;
			int var10 = Math.max(0, theItem.getMaxDamage() - var9);

			return new ItemStack(dmgItems[0].getItem(), 1, var10);
		} else {
			IRecipe recipe;
			for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
				recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

				if (recipe.matches(inv, world)) {
					return recipe.getCraftingResult(inv);
				}
			}
			return null;
		}
	}

	/* ORE DICTIONARY FUNCTIONS */
	public static ItemStack getOre(String oreName) {

		return oreProxy.getOre(oreName);
	}

	public static String getOreName(ItemStack stack) {

		return oreProxy.getOreName(stack);
	}

	public static boolean isOreIDEqual(ItemStack stack, int oreID) {

		return oreProxy.isOreIDEqual(stack, oreID);
	}

	public static boolean isOreNameEqual(ItemStack stack, String oreName) {

		return oreProxy.isOreNameEqual(stack, oreName);
	}

	public static boolean oreNameExists(String oreName) {

		return oreProxy.oreNameExists(oreName);
	}

	public static boolean hasOreName(ItemStack stack) {

		return !getOreName(stack).equals("Unknown");
	}

	public static boolean isBlock(ItemStack stack) {

		return getOreName(stack).startsWith(BLOCK);
	}

	public static boolean isOre(ItemStack stack) {

		return getOreName(stack).startsWith(ORE);
	}

	public static boolean isDust(ItemStack stack) {

		return getOreName(stack).startsWith(DUST);
	}

	public static boolean isIngot(ItemStack stack) {

		return getOreName(stack).startsWith(INGOT);
	}

	public static boolean isNugget(ItemStack stack) {

		return getOreName(stack).startsWith(NUGGET);
	}

	public static boolean isLog(ItemStack stack) {

		return getOreName(stack).startsWith(LOG);
	}

	/* CREATING ItemStacks */
	public static final ItemStack stack(Item t) {

		return new ItemStack(t);
	}

	public static final ItemStack stack(Item t, int s) {

		return new ItemStack(t, s);
	}

	public static final ItemStack stack(Item t, int s, int m) {

		return new ItemStack(t, s, m);
	}

	public static final ItemStack stack(Block t) {

		return new ItemStack(t);
	}

	public static final ItemStack stack(Block t, int s) {

		return new ItemStack(t, s);
	}

	public static final ItemStack stack(Block t, int s, int m) {

		return new ItemStack(t, s, m);
	}

	public static final ItemStack stack2(Item t) {

		return new ItemStack(t, 1, WILDCARD_VALUE);
	}

	public static final ItemStack stack2(Item t, int s) {

		return new ItemStack(t, s, WILDCARD_VALUE);
	}

	public static final ItemStack stack2(Block t) {

		return new ItemStack(t, 1, WILDCARD_VALUE);
	}

	public static final ItemStack stack2(Block t, int s) {

		return new ItemStack(t, s, WILDCARD_VALUE);
	}

	/* CREATING OreRecipes */
	public static final IRecipe ShapedRecipe(Block result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static final IRecipe ShapedRecipe(Item result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static final IRecipe ShapedRecipe(ItemStack result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static final IRecipe ShapedRecipe(Block result, int s, Object... recipe) {

		return new ShapedOreRecipe(stack(result, s), recipe);
	}

	public static final IRecipe ShapedRecipe(Item result, int s, Object... recipe) {

		return new ShapedOreRecipe(stack(result, s), recipe);
	}

	public static final IRecipe ShapedRecipe(ItemStack result, int s, Object... recipe) {

		return new ShapedOreRecipe(cloneStack(result, s), recipe);
	}

	public static final IRecipe ShapelessRecipe(Block result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static final IRecipe ShapelessRecipe(Item result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static final IRecipe ShapelessRecipe(ItemStack result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static final IRecipe ShapelessRecipe(Block result, int s, Object... recipe) {

		return new ShapelessOreRecipe(stack(result, s), recipe);
	}

	public static final IRecipe ShapelessRecipe(Item result, int s, Object... recipe) {

		return new ShapelessOreRecipe(stack(result, s), recipe);
	}

	public static final IRecipe ShapelessRecipe(ItemStack result, int s, Object... recipe) {

		return new ShapelessOreRecipe(cloneStack(result, s), recipe);
	}

	/* CRAFTING HELPER FUNCTIONS */
	// GEARS{
	public static boolean addGearRecipe(ItemStack gear, String ingot) {

		if (gear == null || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', "ingotIron"));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, String ingot, String center) {

		if (gear == null || !oreNameExists(ingot) || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		if (gear == null | center == null || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, ItemStack ingot, String center) {

		if (gear == null | ingot == null || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		if (gear == null | ingot == null | center == null) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(gear), " X ", "XIX", " X ", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
		return true;
	}

	// rotated
	public static boolean addRotatedGearRecipe(ItemStack gear, String ingot, String center) {

		if (gear == null || !oreNameExists(ingot) || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		if (gear == null | center == null || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, ItemStack ingot, String center) {

		if (gear == null | ingot == null || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		if (gear == null | ingot == null | center == null) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(gear), "X X", " I ", "X X", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
		return true;
	}

	// }

	// SURROUND{
	public static boolean addSurroundRecipe(ItemStack out, ItemStack one, ItemStack eight) {

		if (out == null | one == null | eight == null) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(out), "XXX", "XIX", "XXX", 'X', cloneStack(eight, 1), 'I', cloneStack(one, 1));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, String one, ItemStack eight) {

		if (out == null | eight == null || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, ItemStack one, String eight) {

		if (out == null | one == null || !oreNameExists(eight)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, String one, String eight) {

		if (out == null || !oreNameExists(one) || !oreNameExists(eight)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	// }

	// FENCES{
	public static boolean addFenceRecipe(ItemStack out, ItemStack in) {

		if (out == null | in == null) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(out), "XXX", "XXX", 'X', cloneStack(in, 1));
		return true;
	}

	public static boolean addFenceRecipe(ItemStack out, String in) {

		if (out == null || !oreNameExists(in)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XXX", 'X', in));
		return true;
	}

	// }

	// REVERSE STORAGE{
	public static boolean addReverseStorageRecipe(ItemStack nine, String one) {

		if (nine == null || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(cloneStack(nine, 9), one));
		return true;
	}

	public static boolean addReverseStorageRecipe(ItemStack nine, ItemStack one) {

		if (nine == null | one == null) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(nine, 9), cloneStack(one, 1));
		return true;
	}

	public static boolean addSmallReverseStorageRecipe(ItemStack four, String one) {

		if (four == null || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(cloneStack(four, 4), one));
		return true;
	}

	public static boolean addSmallReverseStorageRecipe(ItemStack four, ItemStack one) {

		if (four == null | one == null) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(four, 4), cloneStack(one, 1));
		return true;
	}

	// }

	// STORAGE{
	public static boolean addStorageRecipe(ItemStack one, String nine) {

		if (one == null || !oreNameExists(nine)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine));
		return true;
	}

	public static boolean addStorageRecipe(ItemStack one, ItemStack nine) {

		if (one == null | nine == null) {
			return false;
		}
		nine = cloneStack(nine, 1);
		GameRegistry.addShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine);
		return true;
	}

	public static boolean addSmallStorageRecipe(ItemStack one, String four) {

		if (one == null || !oreNameExists(four)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(one, "XX", "XX", 'X', four));
		return true;
	}

	public static boolean addSmallStorageRecipe(ItemStack one, ItemStack four) {

		if (one == null | four == null) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(one), "XX", "XX", 'X', cloneStack(four, 1));
		return true;
	}

	public static boolean addTwoWayStorageRecipe(ItemStack one, ItemStack nine) {

		return addStorageRecipe(one, nine) && addReverseStorageRecipe(nine, one);
	}

	public static boolean addTwoWayStorageRecipe(ItemStack one, String one_ore, ItemStack nine, String nine_ore) {

		return addStorageRecipe(one, nine_ore) && addReverseStorageRecipe(nine, one_ore);
	}

	public static boolean addSmallTwoWayStorageRecipe(ItemStack one, ItemStack four) {

		return addSmallStorageRecipe(one, four) && addSmallReverseStorageRecipe(four, one);
	}

	public static boolean addSmallTwoWayStorageRecipe(ItemStack one, String one_ore, ItemStack four, String four_ore) {

		return addSmallStorageRecipe(one, four_ore) && addSmallReverseStorageRecipe(four, one_ore);
	}

	// }

	// SMELTING{
	public static boolean addSmelting(ItemStack out, Item in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Block in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, ItemStack in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Item in, float XP) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Block in, float XP) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addSmelting(ItemStack out, ItemStack in, float XP) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, Item in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, Block in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, ItemStack in) {

		if (out == null | in == null) {
			return false;
		}
		FurnaceRecipes.smelting().func_151394_a(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	// }

	public static boolean addTwoWayConversionRecipe(ItemStack a, ItemStack b) {

		if (a == null | b == null) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(a, 1), cloneStack(b, 1));
		GameRegistry.addShapelessRecipe(cloneStack(b, 1), cloneStack(a, 1));
		return true;
	}

	public static void registerWithHandlers(String oreName, ItemStack stack) {

		OreDictionary.registerOre(oreName, stack);
		GameRegistry.registerCustomItemStack(oreName, stack);
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", stack);
	}

	// RECIPE{

	public static void addRecipe(IRecipe recipe) {

		GameRegistry.addRecipe(recipe);
	}

	public static void addRecipe(ItemStack out, Object... recipe) {

		GameRegistry.addRecipe(out, recipe);
	}

	public static void addShapedRecipe(ItemStack out, Object... recipe) {

		GameRegistry.addRecipe(out, recipe);
	}

	public static void addShapedRecipe(Item out, Object... recipe) {

		addRecipe(new ItemStack(out), recipe);
	}

	public static void addShapedRecipe(Block out, Object... recipe) {

		addRecipe(new ItemStack(out), recipe);
	}

	public static void addShapelessRecipe(ItemStack out, Object... recipe) {

		GameRegistry.addShapelessRecipe(out, recipe);
	}

	public static void addShapelessRecipe(Item out, Object... recipe) {

		addShapelessRecipe(new ItemStack(out), recipe);
	}

	public static void addShapelessRecipe(Block out, Object... recipe) {

		addShapelessRecipe(new ItemStack(out), recipe);
	}

	public static void addShapedOreRecipe(ItemStack out, Object... recipe) {

		GameRegistry.addRecipe(ShapedRecipe(out, recipe));
	}

	public static void addShapedOreRecipe(Item out, Object... recipe) {

		GameRegistry.addRecipe(ShapedRecipe(out, recipe));
	}

	public static void addShapedOreRecipe(Block out, Object... recipe) {

		GameRegistry.addRecipe(ShapedRecipe(out, recipe));
	}

	public static void addShapelessOreRecipe(ItemStack out, Object... recipe) {

		GameRegistry.addRecipe(ShapelessRecipe(out, recipe));
	}

	public static void addShapelessOreRecipe(Item out, Object... recipe) {

		GameRegistry.addRecipe(ShapelessRecipe(out, recipe));
	}

	public static void addShapelessOreRecipe(Block out, Object... recipe) {

		GameRegistry.addRecipe(ShapelessRecipe(out, recipe));
	}

	// }

	/* EMPOWERED ITEM HELPERS */
	public static boolean isPlayerHoldingEmpowerableItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem;
	}

	public static boolean isPlayerHoldingEmpoweredItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem && ((IEmpowerableItem) equipped).isEmpowered(player.getCurrentEquippedItem());
	}

	public static boolean toggleHeldEmpowerableItemState(EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IEmpowerableItem empowerableItem = (IEmpowerableItem) equipped.getItem();

		return empowerableItem.setEmpoweredState(equipped, !empowerableItem.isEmpowered(equipped));
	}

	/* MULTIMODE ITEM HELPERS */
	public static boolean isPlayerHoldingMultiModeItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IMultiModeItem;
	}

	public static boolean incrHeldMultiModeItemState(EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped.getItem();

		return multiModeItem.incrMode(equipped);
	}

	public static boolean decrHeldMultiModeItemState(EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped.getItem();

		return multiModeItem.incrMode(equipped);
	}

	public static boolean setHeldMultiModeItemState(EntityPlayer player, int mode) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped.getItem();

		return multiModeItem.setMode(equipped, mode);
	}

	/**
	 * Determine if a player is holding a registered Fluid Container.
	 */
	public static final boolean isPlayerHoldingFluidContainer(EntityPlayer player) {

		return FluidContainerRegistry.isContainer(player.getCurrentEquippedItem());
	}

	public static final boolean isPlayerHoldingFluidContainerItem(EntityPlayer player) {

		return FluidHelper.isPlayerHoldingFluidContainerItem(player);
	}

	public static final boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		return EnergyHelper.isPlayerHoldingEnergyContainerItem(player);
	}

	public static final boolean isPlayerHoldingNothing(EntityPlayer player) {

		return player.getCurrentEquippedItem() == null;
	}

	public static Item getItemFromStack(ItemStack theStack) {

		return theStack == null ? null : theStack.getItem();
	}

	public static boolean areItemsEqual(Item itemA, Item itemB) {

		if (itemA == null | itemB == null) {
			return false;
		}
		return itemA == itemB || itemA.equals(itemB);
	}

	public static final boolean isPlayerHoldingItem(Class<?> item, EntityPlayer player) {

		return item.isInstance(getItemFromStack(player.getCurrentEquippedItem()));
	}

	/**
	 * Determine if a player is holding an ItemStack of a specific Item type.
	 */
	public static final boolean isPlayerHoldingItem(Item item, EntityPlayer player) {

		return areItemsEqual(item, getItemFromStack(player.getCurrentEquippedItem()));
	}

	/**
	 * Determine if a player is holding an ItemStack with a specific Item ID, Metadata, and NBT.
	 */
	public static final boolean isPlayerHoldingItemStack(ItemStack stack, EntityPlayer player) {

		return itemsEqualWithMetadata(stack, player.getCurrentEquippedItem());
	}

	/**
	 * Determine if the damage of two ItemStacks is equal. Assumes both itemstacks are of type A.
	 */
	public static boolean itemsDamageEqual(ItemStack stackA, ItemStack stackB) {

		return (!stackA.getHasSubtypes() && stackA.getMaxDamage() == 0) || (getItemDamage(stackA) == getItemDamage(stackB));
	}

	/**
	 * Determine if two ItemStacks have the same Item.
	 */
	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB) {

		if (stackA == null || stackB == null) {
			return false;
		}
		return areItemsEqual(stackA.getItem(), stackB.getItem());
	}

	/**
	 * Determine if two ItemStacks have the same Item and NBT.
	 */
	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		return itemsEqualWithoutMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	/**
	 * Determine if two ItemStacks have the same Item and damage.
	 */
	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB) {

		return itemsEqualWithoutMetadata(stackA, stackB) && itemsDamageEqual(stackA, stackB);
	}

	/**
	 * Determine if two ItemStacks have the same Item, damage, and NBT.
	 */
	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		return itemsEqualWithMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	/**
	 * Determine if two ItemStacks have the same Item, identical damage, and NBT.
	 */
	public static boolean itemsIdentical(ItemStack stackA, ItemStack stackB) {

		return itemsEqualWithoutMetadata(stackA, stackB) && getItemDamage(stackA) == getItemDamage(stackB)
				&& doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound);
	}

	/**
	 * Determine if two NBTTagCompounds are equal.
	 */
	public static boolean doNBTsMatch(NBTTagCompound nbtA, NBTTagCompound nbtB) {

		if (nbtA == null & nbtB == null) {
			return true;
		}
		if (nbtA != null & nbtB != null) {
			return nbtA.equals(nbtB);
		}
		return false;
	}

	public static boolean itemsEqualForCrafting(ItemStack stackA, ItemStack stackB) {

		return itemsEqualWithoutMetadata(stackA, stackB)
				&& (!stackA.getHasSubtypes() || ((getItemDamage(stackA) == OreDictionary.WILDCARD_VALUE || getItemDamage(stackB) == OreDictionary.WILDCARD_VALUE) || getItemDamage(stackB) == getItemDamage(stackA)));
	}

	public static boolean craftingEquivalent(ItemStack checked, ItemStack source, String oreDict, ItemStack output) {

		if (itemsEqualForCrafting(checked, source)) {
			return true;
		} else if (output != null && isBlacklist(output)) {
			return false;
		} else if (oreDict == null || oreDict.equals("Unknown")) {
			return false;
		} else {
			return getOreName(checked).equalsIgnoreCase(oreDict);
		}
	}

	public static boolean doOreIDsMatch(ItemStack stackA, ItemStack stackB) {

		int id = oreProxy.getOreID(stackA);
		return id >= 0 && id == oreProxy.getOreID(stackB);
	}

	public static boolean isBlacklist(ItemStack output) {

		Item item = output.getItem();
		return Item.getItemFromBlock(Blocks.birch_stairs) == item || Item.getItemFromBlock(Blocks.jungle_stairs) == item
				|| Item.getItemFromBlock(Blocks.oak_stairs) == item || Item.getItemFromBlock(Blocks.spruce_stairs) == item
				|| Item.getItemFromBlock(Blocks.planks) == item || Item.getItemFromBlock(Blocks.wooden_slab) == item;
	}

	public static String getItemNBTString(ItemStack theItem, String nbtKey, String invalidReturn) {

		return theItem.stackTagCompound != null && theItem.stackTagCompound.hasKey(nbtKey) ? theItem.stackTagCompound.getString(nbtKey) : invalidReturn;
	}

	/**
	 * Adds Inventory information to ItemStacks which themselves hold things. Called in addInformation().
	 */
	public static void addInventoryInformation(ItemStack stack, List<String> list) {

		addInventoryInformation(stack, list, 0, Integer.MAX_VALUE);
	}

	public static void addInventoryInformation(ItemStack stack, List<String> list, int minSlot, int maxSlot) {

		if (stack.stackTagCompound == null) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		if (stack.getItem() instanceof IInventoryContainerItem && stack.stackTagCompound.hasKey("Accessible")) {
			addAccessibleInventoryInformation(stack, list, minSlot, maxSlot);
			return;
		}
		if (!stack.stackTagCompound.hasKey("Inventory", Constants.NBT.TAG_LIST)
				|| stack.stackTagCompound.getTagList("Inventory", stack.stackTagCompound.getId()).tagCount() <= 0) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		NBTTagList nbtList = stack.stackTagCompound.getTagList("Inventory", stack.stackTagCompound.getId());
		ItemStack curStack;
		ItemStack curStack2;

		ArrayList<ItemStack> containedItems = new ArrayList<ItemStack>();

		boolean[] visited = new boolean[nbtList.tagCount()];

		for (int i = 0; i < nbtList.tagCount(); i++) {
			NBTTagCompound tag = nbtList.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (visited[i] || slot < minSlot || slot > maxSlot) {
				continue;
			}
			visited[i] = true;
			curStack = ItemStack.loadItemStackFromNBT(tag);

			if (curStack == null) {
				continue;
			}
			containedItems.add(curStack);
			for (int j = 0; j < nbtList.tagCount(); j++) {
				NBTTagCompound tag2 = nbtList.getCompoundTagAt(j);
				int slot2 = tag.getInteger("Slot");

				if (visited[j] || slot2 < minSlot || slot2 > maxSlot) {
					continue;
				}
				curStack2 = ItemStack.loadItemStackFromNBT(tag2);

				if (curStack2 == null) {
					continue;
				}
				if (itemsIdentical(curStack, curStack2)) {
					curStack.stackSize += curStack2.stackSize;
					visited[j] = true;
				}
			}
		}
		if (containedItems.size() > 0) {
			list.add(StringHelper.localize("info.cofh.contents") + ":");
		}
		for (ItemStack item : containedItems) {
			int maxStackSize = item.getMaxStackSize();

			if (!StringHelper.displayStackCount || item.stackSize < maxStackSize || maxStackSize == 1) {
				list.add("    " + StringHelper.BRIGHT_GREEN + item.stackSize + " " + StringHelper.getItemName(item));
			} else {
				if (item.stackSize % maxStackSize != 0) {
					list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + "+" + item.stackSize % maxStackSize
							+ " " + StringHelper.getItemName(item));
				} else {
					list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + " " + StringHelper.getItemName(item));
				}
			}
		}
	}

	public static void addAccessibleInventoryInformation(ItemStack stack, List<String> list, int minSlot, int maxSlot) {

		int invSize = ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack);
		ItemStack curStack;
		ItemStack curStack2;

		ArrayList<ItemStack> containedItems = new ArrayList<ItemStack>();

		boolean[] visited = new boolean[invSize];

		NBTTagCompound tag = stack.stackTagCompound;
		if (tag.hasKey("Inventory")) {
			tag = tag.getCompoundTag("Inventory");
		}

		for (int i = minSlot; i < Math.min(invSize, maxSlot); i++) {
			if (visited[i]) {
				continue;
			}
			if (!tag.hasKey("Slot" + i)) {
				continue;
			}
			curStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Slot" + i));
			visited[i] = true;

			if (curStack == null) {
				continue;
			}
			containedItems.add(curStack);
			for (int j = minSlot; j < Math.min(invSize, maxSlot); j++) {
				if (visited[j]) {
					continue;
				}
				if (!tag.hasKey("Slot" + j)) {
					continue;
				}
				curStack2 = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Slot" + j));

				if (curStack2 == null) {
					continue;
				}
				if (itemsIdentical(curStack, curStack2)) {
					curStack.stackSize += curStack2.stackSize;
					visited[j] = true;
				}
			}
		}
		if (containedItems.size() > 0) {
			list.add(StringHelper.localize("info.cofh.contents") + ":");
		} else {
			list.add(StringHelper.localize("info.cofh.empty"));
		}
		for (ItemStack item : containedItems) {
			int maxStackSize = item.getMaxStackSize();

			if (!StringHelper.displayStackCount || item.stackSize < maxStackSize || maxStackSize == 1) {
				list.add("    " + StringHelper.BRIGHT_GREEN + item.stackSize + " " + StringHelper.getItemName(item));
			} else {
				if (item.stackSize % maxStackSize != 0) {
					list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + "+" + item.stackSize % maxStackSize
							+ " " + StringHelper.getItemName(item));
				} else {
					list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + " " + StringHelper.getItemName(item));
				}
			}
		}
	}

}
