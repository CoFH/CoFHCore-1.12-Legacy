package cofh.lib.util.helpers;

import cofh.api.item.IInventoryContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.lib.util.OreDictionaryProxy;
import com.google.common.base.Strings;
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
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

/**
 * Contains various helper functions to assist with {@link Item} and {@link ItemStack} manipulation and interaction.
 *
 * @author King Lemming
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

	public static boolean isPlayerHoldingSomething(EntityPlayer player) {

		return !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
	}

	public static ItemStack getMainhandStack(EntityPlayer player) {

		return player.getHeldItemMainhand();
	}

	public static ItemStack getOffhandStack(EntityPlayer player) {

		return player.getHeldItemOffhand();
	}

	public static ItemStack getHeldStack(EntityPlayer player) {

		ItemStack stack = player.getHeldItemMainhand();
		if (stack.isEmpty()) {
			stack = player.getHeldItemOffhand();
		}
		return stack;
	}

	public static ItemStack cloneStack(Item item, int stackSize) {

		if (item == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(item, stackSize);
	}

	public static ItemStack cloneStack(Block block, int stackSize) {

		if (block == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(block, stackSize);
	}

	public static ItemStack cloneStack(ItemStack stack, int stackSize) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack retStack = stack.copy();
		retStack.setCount(stackSize);

		return retStack;
	}

	public static ItemStack cloneStack(ItemStack stack) {

		return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
	}

	public static ItemStack copyTag(ItemStack container, ItemStack other) {

		if (!other.isEmpty() && other.hasTagCompound()) {
			container.setTagCompound(other.getTagCompound().copy());
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
		stack.setCount(nbt.getInteger("Count"));
		stack.setItemDamage(Math.max(0, nbt.getShort("Damage")));

		if (nbt.hasKey("tag", 10)) {
			stack.setTagCompound(nbt.getCompoundTag("tag"));
		}
		return stack;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", stack.getCount());
		nbt.setShort("Damage", (short) getItemDamage(stack));

		if (stack.hasTagCompound()) {
			nbt.setTag("tag", stack.getTagCompound());
		}
		return nbt;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, int amount, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", amount);
		nbt.setShort("Damage", (short) getItemDamage(stack));

		if (stack.hasTagCompound()) {
			nbt.setTag("tag", stack.getTagCompound());
		}
		return nbt;
	}

	public static String getNameFromItemStack(ItemStack stack) {

		if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("display")) {
			return "";
		}
		return stack.getTagCompound().getCompoundTag("display").getString("Name");
	}

	public static ItemStack consumeItem(ItemStack stack) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		Item item = stack.getItem();
		boolean largerStack = stack.getCount() > 1;
		// vanilla only alters the stack passed to hasContainerItem/etc. when the size is >1

		if (largerStack) {
			stack.shrink(1);
		}
		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);

			if (ret.isEmpty()) {
				return ItemStack.EMPTY;
			}
			if (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage()) {
				ret = ItemStack.EMPTY;
			}
			return ret;
		}
		return largerStack ? stack : ItemStack.EMPTY;
	}

	public static ItemStack consumeItem(ItemStack stack, EntityPlayer player) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		Item item = stack.getItem();
		boolean largerStack = stack.getCount() > 1;
		// vanilla only alters the stack passed to hasContainerItem/etc. when the size is >1

		if (largerStack) {
			stack.shrink(1);
		}
		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);

			if (ret.isEmpty() || (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage())) {
				ret = ItemStack.EMPTY;
			}
			if (stack.getCount() < 1) {
				return ret;
			}
			if (!ret.isEmpty() && !player.inventory.addItemStackToInventory(ret)) {
				player.dropItem(ret, false, true);
			}
		}
		return largerStack ? stack : ItemStack.EMPTY;
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop) {

		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace) {

		if (entityplayer == null || entityplayer.capabilities.isCreativeMode) {
			return true;
		}
		if (allowReplace && stack.getCount() <= 1) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
			entityplayer.inventory.addItemStackToInventory(dropStack);
			return true;
		} else if (allowDrop) {
			stack.shrink(1);
			if (!dropStack.isEmpty() && !entityplayer.inventory.addItemStackToInventory(dropStack)) {
				entityplayer.dropItem(dropStack, false, true);
			}
			return true;
		}
		return false;
	}

	/**
	 * This prevents an overridden getDamage() call from messing up metadata acquisition.
	 */
	public static int getItemDamage(ItemStack stack) {

		return Items.DIAMOND.getDamage(stack);
	}

	/**
	 * Gets a vanilla CraftingManager recipe.
	 */
	@Deprecated
	public static IRecipe getCraftingRecipe(InventoryCrafting inv, World world) {

		return CraftingManager.findMatchingRecipe(inv, world);
	}

	/**
	 * Gets a vanilla CraftingManager result.
	 */
	@Deprecated
	public static ItemStack getCraftingResult(InventoryCrafting inv, World world) {

		return CraftingManager.findMatchingResult(inv, world);
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
	public static ItemStack stack(Item t) {

		return new ItemStack(t);
	}

	public static ItemStack stack(Item t, int s) {

		return new ItemStack(t, s);
	}

	public static ItemStack stack(Item t, int s, int m) {

		return new ItemStack(t, s, m);
	}

	public static ItemStack stack(Block t) {

		return new ItemStack(t);
	}

	public static ItemStack stack(Block t, int s) {

		return new ItemStack(t, s);
	}

	public static ItemStack stack(Block t, int s, int m) {

		return new ItemStack(t, s, m);
	}

	public static ItemStack stack2(Item t) {

		return new ItemStack(t, 1, WILDCARD_VALUE);
	}

	public static ItemStack stack2(Item t, int s) {

		return new ItemStack(t, s, WILDCARD_VALUE);
	}

	public static ItemStack stack2(Block t) {

		return new ItemStack(t, 1, WILDCARD_VALUE);
	}

	public static ItemStack stack2(Block t, int s) {

		return new ItemStack(t, s, WILDCARD_VALUE);
	}

	/* CREATING OreRecipes */
	public static IRecipe ShapedRecipe(Block result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static IRecipe ShapedRecipe(Item result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static IRecipe ShapedRecipe(ItemStack result, Object... recipe) {

		return new ShapedOreRecipe(result, recipe);
	}

	public static IRecipe ShapedRecipe(Block result, int s, Object... recipe) {

		return new ShapedOreRecipe(stack(result, s), recipe);
	}

	public static IRecipe ShapedRecipe(Item result, int s, Object... recipe) {

		return new ShapedOreRecipe(stack(result, s), recipe);
	}

	public static IRecipe ShapedRecipe(ItemStack result, int s, Object... recipe) {

		return new ShapedOreRecipe(cloneStack(result, s), recipe);
	}

	public static IRecipe ShapelessRecipe(Block result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static IRecipe ShapelessRecipe(Item result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static IRecipe ShapelessRecipe(ItemStack result, Object... recipe) {

		return new ShapelessOreRecipe(result, recipe);
	}

	public static IRecipe ShapelessRecipe(Block result, int s, Object... recipe) {

		return new ShapelessOreRecipe(stack(result, s), recipe);
	}

	public static IRecipe ShapelessRecipe(Item result, int s, Object... recipe) {

		return new ShapelessOreRecipe(stack(result, s), recipe);
	}

	public static IRecipe ShapelessRecipe(ItemStack result, int s, Object... recipe) {

		return new ShapelessOreRecipe(cloneStack(result, s), recipe);
	}

	/* CRAFTING HELPER FUNCTIONS */
	// GEARS{
	public static boolean addGearRecipe(ItemStack gear, String ingot) {

		if (gear.isEmpty() || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', "ingotIron"));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, String ingot, String center) {

		if (gear.isEmpty() || !oreNameExists(ingot) || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		if (gear.isEmpty() | center.isEmpty() || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, ItemStack ingot, String center) {

		if (gear.isEmpty() | ingot.isEmpty() || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		if (gear.isEmpty() | ingot.isEmpty() | center.isEmpty()) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(gear), " X ", "XIX", " X ", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
		return true;
	}

	// rotated
	public static boolean addRotatedGearRecipe(ItemStack gear, String ingot, String center) {

		if (gear.isEmpty() || !oreNameExists(ingot) || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		if (gear.isEmpty() | center.isEmpty() || !oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, ItemStack ingot, String center) {

		if (gear.isEmpty() | ingot.isEmpty() || !oreNameExists(center)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center));
		return true;
	}

	public static boolean addRotatedGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		if (gear.isEmpty() | ingot.isEmpty() | center.isEmpty()) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(gear), "X X", " I ", "X X", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
		return true;
	}

	// }

	// SURROUND{
	public static boolean addSurroundRecipe(ItemStack out, ItemStack one, ItemStack eight) {

		if (out.isEmpty() | one.isEmpty() | eight.isEmpty()) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(out), "XXX", "XIX", "XXX", 'X', cloneStack(eight, 1), 'I', cloneStack(one, 1));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, String one, ItemStack eight) {

		if (out.isEmpty() | eight.isEmpty() || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, ItemStack one, String eight) {

		if (out.isEmpty() | one.isEmpty() || !oreNameExists(eight)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	public static boolean addSurroundRecipe(ItemStack out, String one, String eight) {

		if (out.isEmpty() || !oreNameExists(one) || !oreNameExists(eight)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one));
		return true;
	}

	// }

	// FENCES{
	public static boolean addFenceRecipe(ItemStack out, ItemStack in) {

		if (out.isEmpty() | in.isEmpty()) {
			return false;
		}
		GameRegistry.addRecipe(cloneStack(out), "XXX", "XXX", 'X', cloneStack(in, 1));
		return true;
	}

	public static boolean addFenceRecipe(ItemStack out, String in) {

		if (out.isEmpty() || !oreNameExists(in)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(out, "XXX", "XXX", 'X', in));
		return true;
	}

	// }

	// REVERSE STORAGE{
	public static boolean addReverseStorageRecipe(ItemStack nine, String one) {

		if (nine.isEmpty() || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(cloneStack(nine, 9), one));
		return true;
	}

	public static boolean addReverseStorageRecipe(ItemStack nine, ItemStack one) {

		if (nine.isEmpty() | one.isEmpty()) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(nine, 9), cloneStack(one, 1));
		return true;
	}

	public static boolean addSmallReverseStorageRecipe(ItemStack four, String one) {

		if (four.isEmpty() || !oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(cloneStack(four, 4), one));
		return true;
	}

	public static boolean addSmallReverseStorageRecipe(ItemStack four, ItemStack one) {

		if (four.isEmpty() | one.isEmpty()) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(four, 4), cloneStack(one, 1));
		return true;
	}

	// }

	// STORAGE{
	public static boolean addStorageRecipe(ItemStack one, String nine) {

		if (one.isEmpty() || !oreNameExists(nine)) {
			return false;
		}
		GameRegistry.addRecipe(ShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine));
		return true;
	}

	public static boolean addStorageRecipe(ItemStack one, ItemStack nine) {

		if (one.isEmpty() | nine.isEmpty()) {
			return false;
		}
		nine = cloneStack(nine, 1);
		GameRegistry.addShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine);
		return true;
	}

	public static boolean addSmallStorageRecipe(ItemStack one, String four) {

		if (one.isEmpty() || !oreNameExists(four)) {
			return false;
		}
		GameRegistry.addRecipe(ShapedRecipe(one, "XX", "XX", 'X', four));
		return true;
	}

	public static boolean addSmallStorageRecipe(ItemStack one, ItemStack four) {

		if (one.isEmpty() | four.isEmpty()) {
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

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Block in) {

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, ItemStack in) {

		if (out.isEmpty() | in.isEmpty()) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Item in, float XP) {

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addSmelting(ItemStack out, Block in, float XP) {

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addSmelting(ItemStack out, ItemStack in, float XP) {

		if (out.isEmpty() | in.isEmpty()) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), XP);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, Item in) {

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, Block in) {

		if (out.isEmpty() | in == null) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	public static boolean addWeakSmelting(ItemStack out, ItemStack in) {

		if (out.isEmpty() | in.isEmpty()) {
			return false;
		}
		FurnaceRecipes.instance().addSmeltingRecipe(cloneStack(in, 1), cloneStack(out), 0.1f);
		return true;
	}

	// }

	public static boolean addTwoWayConversionRecipe(ItemStack a, ItemStack b) {

		if (a.isEmpty() | b.isEmpty()) {
			return false;
		}
		GameRegistry.addShapelessRecipe(cloneStack(a, 1), cloneStack(b, 1));
		GameRegistry.addShapelessRecipe(cloneStack(b, 1), cloneStack(a, 1));
		return true;
	}

	public static void registerWithHandlers(String oreName, ItemStack stack) {

		OreDictionary.registerOre(oreName, stack);
		//GameRegistry.registerCustomItemStack(oreName, stack);
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

	/* MULTIMODE ITEM HELPERS */
	public static boolean isPlayerHoldingMultiModeItem(EntityPlayer player) {

		if (!isPlayerHoldingSomething(player)) {
			return false;
		}
		ItemStack heldItem = getHeldStack(player);
		Item equipped = heldItem.getItem();
		return equipped instanceof IMultiModeItem;
	}

	public static boolean incrHeldMultiModeItemState(EntityPlayer player) {

		if (!isPlayerHoldingSomething(player)) {
			return false;
		}
		ItemStack heldItem = getHeldStack(player);
		Item equipped = heldItem.getItem();
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped;

		return multiModeItem.incrMode(heldItem);
	}

	public static boolean decrHeldMultiModeItemState(EntityPlayer player) {

		if (!isPlayerHoldingSomething(player)) {
			return false;
		}
		ItemStack equipped = getHeldStack(player);
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped.getItem();

		return multiModeItem.incrMode(equipped);
	}

	public static boolean setHeldMultiModeItemState(EntityPlayer player, int mode) {

		if (!isPlayerHoldingSomething(player)) {
			return false;
		}
		ItemStack equipped = getHeldStack(player);
		IMultiModeItem multiModeItem = (IMultiModeItem) equipped.getItem();

		return multiModeItem.setMode(equipped, mode);
	}

	/**
	 * Determine if a player is holding a Fluid Handler.
	 */
	public static boolean isPlayerHoldingFluidHandler(EntityPlayer player) {

		return FluidHelper.isPlayerHoldingFluidHandler(player);
	}

	public static boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		return EnergyHelper.isPlayerHoldingEnergyContainerItem(player);
	}

	public static boolean isPlayerHoldingNothing(EntityPlayer player) {

		return getHeldStack(player).isEmpty();
	}

	public static Item getItemFromStack(ItemStack theStack) {

		return theStack.isEmpty() ? null : theStack.getItem();
	}

	public static boolean areItemsEqual(Item itemA, Item itemB) {

		if (itemA == null | itemB == null) {
			return false;
		}
		return itemA == itemB || itemA.equals(itemB);
	}

	public static boolean isPlayerHoldingItem(Class<?> item, EntityPlayer player) {

		return item.isInstance(getItemFromStack(getHeldStack(player)));
	}

	/**
	 * Determine if a player is holding an ItemStack of a specific Item type.
	 */
	public static boolean isPlayerHoldingItem(Item item, EntityPlayer player) {

		return areItemsEqual(item, getItemFromStack(getHeldStack(player)));
	}

	public static boolean isPlayerHoldingMainhand(Item item, EntityPlayer player) {

		return areItemsEqual(item, getItemFromStack(getMainhandStack(player)));
	}

	public static boolean isPlayerHoldingOffhand(Item item, EntityPlayer player) {

		return areItemsEqual(item, getItemFromStack(getOffhandStack(player)));
	}

	/**
	 * Determine if a player is holding an ItemStack with a specific Item ID and Metadata.
	 */
	public static boolean isPlayerHoldingItemStack(ItemStack stack, EntityPlayer player) {

		return itemsEqualWithMetadata(stack, getHeldStack(player));
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

		if (stackA.isEmpty() || stackB.isEmpty()) {
			return false;
		}
		return areItemsEqual(stackA.getItem(), stackB.getItem());
	}

	/**
	 * Determine if two ItemStacks have the same Item and NBT.
	 */
	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		return itemsEqualWithoutMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.getTagCompound(), stackB.getTagCompound()));
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

		return itemsEqualWithMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.getTagCompound(), stackB.getTagCompound()));
	}

	/**
	 * Determine if two ItemStacks have the same Item, identical damage, and NBT.
	 */
	public static boolean itemsIdentical(ItemStack stackA, ItemStack stackB) {

		return itemsEqualWithoutMetadata(stackA, stackB) && getItemDamage(stackA) == getItemDamage(stackB) && doNBTsMatch(stackA.getTagCompound(), stackB.getTagCompound());
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

		return itemsEqualWithoutMetadata(stackA, stackB) && (!stackA.getHasSubtypes() || ((getItemDamage(stackA) == OreDictionary.WILDCARD_VALUE || getItemDamage(stackB) == OreDictionary.WILDCARD_VALUE) || getItemDamage(stackB) == getItemDamage(stackA)));
	}

	public static boolean craftingEquivalent(ItemStack checked, ItemStack source, String oreDict, ItemStack output) {

		if (itemsEqualForCrafting(checked, source)) {
			return true;
		} else if (!output.isEmpty() && isBlacklist(output)) {
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
		return Item.getItemFromBlock(Blocks.BIRCH_STAIRS) == item || Item.getItemFromBlock(Blocks.JUNGLE_STAIRS) == item || Item.getItemFromBlock(Blocks.OAK_STAIRS) == item || Item.getItemFromBlock(Blocks.SPRUCE_STAIRS) == item || Item.getItemFromBlock(Blocks.PLANKS) == item || Item.getItemFromBlock(Blocks.WOODEN_SLAB) == item;
	}

	public static String getItemNBTString(ItemStack theItem, String nbtKey, String invalidReturn) {

		return theItem.getTagCompound() != null && theItem.getTagCompound().hasKey(nbtKey) ? theItem.getTagCompound().getString(nbtKey) : invalidReturn;
	}

	/**
	 * Adds Inventory information to ItemStacks which themselves hold things. Called in addInformation().
	 */
	public static void addInventoryInformation(ItemStack stack, List<String> list) {

		addInventoryInformation(stack, list, 0, Integer.MAX_VALUE);
	}

	public static void addInventoryInformation(ItemStack stack, List<String> list, int minSlot, int maxSlot) {

		if (stack.getTagCompound() == null) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		if (stack.getItem() instanceof IInventoryContainerItem && stack.getTagCompound().hasKey("Accessible")) {
			addAccessibleInventoryInformation(stack, list, minSlot, maxSlot);
			return;
		}
		if (!stack.getTagCompound().hasKey("Inventory", Constants.NBT.TAG_LIST) || stack.getTagCompound().getTagList("Inventory", stack.getTagCompound().getId()).tagCount() <= 0) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		NBTTagList nbtList = stack.getTagCompound().getTagList("Inventory", stack.getTagCompound().getId());
		ItemStack curStack;
		ItemStack curStack2;

		ArrayList<ItemStack> containedItems = new ArrayList<>();

		boolean[] visited = new boolean[nbtList.tagCount()];

		for (int i = 0; i < nbtList.tagCount(); i++) {
			NBTTagCompound tag = nbtList.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (visited[i] || slot < minSlot || slot > maxSlot) {
				continue;
			}
			visited[i] = true;
			curStack = new ItemStack(tag);

			if (curStack.isEmpty()) {
				continue;
			}
			containedItems.add(curStack);
			for (int j = 0; j < nbtList.tagCount(); j++) {
				NBTTagCompound tag2 = nbtList.getCompoundTagAt(j);
				int slot2 = tag.getInteger("Slot");

				if (visited[j] || slot2 < minSlot || slot2 > maxSlot) {
					continue;
				}
				curStack2 = new ItemStack(tag2);

				if (curStack.isEmpty()) {
					continue;
				}
				if (itemsIdentical(curStack, curStack2)) {
					curStack.grow(curStack2.getCount());
					visited[j] = true;
				}
			}
		}
		if (containedItems.size() > 0) {
			list.add(StringHelper.localize("info.cofh.contents") + ":");
		}
		for (ItemStack item : containedItems) {
			int maxStackSize = item.getMaxStackSize();

			if (!StringHelper.displayStackCount || item.getCount() < maxStackSize || maxStackSize == 1) {
				list.add("    " + StringHelper.ORANGE + item.getCount() + " " + StringHelper.getItemName(item));
			} else {
				if (item.getCount() % maxStackSize != 0) {
					list.add("    " + StringHelper.ORANGE + maxStackSize + "x" + item.getCount() / maxStackSize + "+" + item.getCount() % maxStackSize + " " + StringHelper.getItemName(item));
				} else {
					list.add("    " + StringHelper.ORANGE + maxStackSize + "x" + item.getCount() / maxStackSize + " " + StringHelper.getItemName(item));
				}
			}
		}
	}

	public static void addAccessibleInventoryInformation(ItemStack stack, List<String> list, int minSlot, int maxSlot) {

		int invSize = ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack);
		ItemStack curStack;
		ItemStack curStack2;

		ArrayList<ItemStack> containedItems = new ArrayList<>();

		boolean[] visited = new boolean[invSize];

		NBTTagCompound tag = stack.getTagCompound();
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
			curStack = new ItemStack(tag.getCompoundTag("Slot" + i));
			visited[i] = true;

			if (curStack.isEmpty()) {
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
				curStack2 = new ItemStack(tag.getCompoundTag("Slot" + j));

				if (curStack2.isEmpty()) {
					continue;
				}
				if (itemsIdentical(curStack, curStack2)) {
					curStack.grow(curStack2.getCount());
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

			if (!StringHelper.displayStackCount || item.getCount() < maxStackSize || maxStackSize == 1) {
				list.add("    " + StringHelper.ORANGE + item.getCount() + " " + StringHelper.getItemName(item));
			} else {
				if (item.getCount() % maxStackSize != 0) {
					list.add("    " + StringHelper.ORANGE + maxStackSize + "x" + item.getCount() / maxStackSize + "+" + item.getCount() % maxStackSize + " " + StringHelper.getItemName(item));
				} else {
					list.add("    " + StringHelper.ORANGE + maxStackSize + "x" + item.getCount() / maxStackSize + " " + StringHelper.getItemName(item));
				}
			}
		}
	}

	/**
	 * Compares item, meta, size and nbt of two stacks while ignoring nbt tag keys provided.
	 * This is useful in shouldCauseReequipAnimation overrides.
	 *
	 * @param stackA          first stack to compare
	 * @param stackB          second stack to compare
	 * @param nbtTagsToIgnore tag keys to ignore when comparing the stacks
	 */
	public static boolean areItemStacksEqualIgnoreTags(ItemStack stackA, ItemStack stackB, String... nbtTagsToIgnore) {

		if (stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		}
		if (stackA.isEmpty() && !stackB.isEmpty()) {
			return false;
		}
		if (!stackA.isEmpty() && stackB.isEmpty()) {
			return false;
		}
		if (stackA.getItem() != stackB.getItem()) {
			return false;
		}
		if (stackA.getItemDamage() != stackB.getItemDamage()) {
			return false;
		}
		if (stackA.getCount() != stackB.getCount()) {
			return false;
		}
		if (stackA.getTagCompound() == null && stackB.getTagCompound() == null) {
			return true;
		}
		if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
			return false;
		}
		if (stackA.getTagCompound() != null && stackB.getTagCompound() == null) {
			return false;
		}
		int numberOfKeys = stackA.getTagCompound().getKeySet().size();
		if (numberOfKeys != stackB.getTagCompound().getKeySet().size()) {
			return false;
		}

		NBTTagCompound tagA = stackA.getTagCompound();
		NBTTagCompound tagB = stackB.getTagCompound();

		String[] keys = new String[numberOfKeys];
		keys = tagA.getKeySet().toArray(keys);

		a:
		for (int i = 0; i < numberOfKeys; i++) {
			for (int j = 0; j < nbtTagsToIgnore.length; j++) {
				if (nbtTagsToIgnore[j].equals(keys[i])) {
					continue a;
				}
			}
			if (!tagA.getTag(keys[i]).equals(tagB.getTag(keys[i]))) {
				return false;
			}
		}
		return true;
	}
}
