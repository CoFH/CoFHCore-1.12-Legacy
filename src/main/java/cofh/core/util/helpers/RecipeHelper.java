package cofh.core.util.helpers;

import cofh.core.util.crafting.ShapelessFluidRecipeFactory.ShapelessFluidRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;

import static cofh.core.util.helpers.ItemHelper.cloneStack;

public class RecipeHelper {

	/* GENERAL */
	public static void addShapedOreRecipe(ItemStack output, Object... input) {

		ResourceLocation location = getNameForRecipe(output);
		ShapedOreRecipe recipe = new ShapedOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	public static void addShapedRecipe(ItemStack output, Object... input) {

		ResourceLocation location = getNameForRecipe(output);
		CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(input);
		ShapedRecipes recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), primer.width, primer.height, primer.input, output);
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	public static void addShapelessFluidRecipe(ItemStack output, Object... input) {

		ResourceLocation location = getNameForRecipe(output);
		ShapelessFluidRecipe recipe = new ShapelessFluidRecipe(location, output, input);
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	public static void addShapelessOreRecipe(ItemStack output, Object... input) {

		ResourceLocation location = getNameForRecipe(output);
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	public static void addShapelessRecipe(ItemStack output, Object... input) {

		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(location.getResourceDomain(), output, buildInput(input));
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	/* GEARS */
	public static void addGearRecipe(ItemStack gear, String ingot) {

		addShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', "ingotIron");
	}

	public static void addGearRecipe(ItemStack gear, String ingot, String center) {

		addShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center);
	}

	public static void addGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		addShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center);
	}

	public static void addGearRecipe(ItemStack gear, ItemStack ingot, String center) {

		addShapedRecipe(gear, " X ", "XIX", " X ", 'X', ingot, 'I', center);
	}

	public static void addGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		addShapedRecipe(cloneStack(gear), " X ", "XIX", " X ", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
	}

	/* STORAGE */
	public static void addStorageRecipe(ItemStack one, String nine) {

		addShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine);
	}

	public static void addStorageRecipe(ItemStack one, ItemStack nine) {

		nine = cloneStack(nine, 1);
		addShapelessRecipe(one, nine, nine, nine, nine, nine, nine, nine, nine, nine);
	}

	public static void addSmallStorageRecipe(ItemStack one, String four) {

		addShapedRecipe(one, "XX", "XX", 'X', four);
	}

	public static void addSmallStorageRecipe(ItemStack one, ItemStack four) {

		addShapedRecipe(cloneStack(one), "XX", "XX", 'X', cloneStack(four, 1));
	}

	public static void addReverseStorageRecipe(ItemStack nine, String one) {

		addShapelessRecipe(cloneStack(nine, 9), one);
	}

	public static void addReverseStorageRecipe(ItemStack nine, ItemStack one) {

		addShapelessRecipe(cloneStack(nine, 9), cloneStack(one, 1));
	}

	public static void addSmallReverseStorageRecipe(ItemStack four, String one) {

		addShapelessRecipe(cloneStack(four, 4), one);
	}

	public static void addSmallReverseStorageRecipe(ItemStack four, ItemStack one) {

		addShapelessRecipe(cloneStack(four, 4), cloneStack(one, 1));
	}

	public static void addTwoWayStorageRecipe(ItemStack one, ItemStack nine) {

		addStorageRecipe(one, nine);
		addReverseStorageRecipe(nine, one);
	}

	public static void addTwoWayStorageRecipe(ItemStack one, String one_ore, ItemStack nine, String nine_ore) {

		addStorageRecipe(one, nine_ore);
		addReverseStorageRecipe(nine, one_ore);
	}

	public static void addSmallTwoWayStorageRecipe(ItemStack one, ItemStack four) {

		addSmallStorageRecipe(one, four);
		addSmallReverseStorageRecipe(four, one);
	}

	public static void addSmallTwoWayStorageRecipe(ItemStack one, String one_ore, ItemStack four, String four_ore) {

		addSmallStorageRecipe(one, four_ore);
		addSmallReverseStorageRecipe(four, one_ore);
	}

	/* HELPERS */
	public static ResourceLocation getNameForRecipe(ItemStack output) {

		ModContainer activeContainer = Loader.instance().activeModContainer();
		ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		int index = 0;
		while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
			index++;
			recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getResourcePath() + "_" + index);
		}
		return recipeLoc;
	}

	public static NonNullList<Ingredient> buildInput(Object[] input) {

		NonNullList<Ingredient> list = NonNullList.create();

		for (Object obj : input) {
			if (obj instanceof Ingredient) {
				list.add((Ingredient) obj);
			} else {
				Ingredient ingredient = CraftingHelper.getIngredient(obj);

				if (ingredient == null) {
					ingredient = Ingredient.EMPTY;
				}
				list.add(ingredient);
			}
		}
		return list;
	}

	/* SMELTING */
	public static void addSmelting(Block input, ItemStack output, float xp) {

		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(Item input, ItemStack output, float xp) {

		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {

		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output) {

		addSmelting(input, output, 0F);
	}

	public static void addSmelting(Item input, ItemStack output) {

		addSmelting(input, output, 0F);
	}

	public static void addSmelting(Block input, ItemStack output) {

		addSmelting(input, output, 0F);
	}

}
