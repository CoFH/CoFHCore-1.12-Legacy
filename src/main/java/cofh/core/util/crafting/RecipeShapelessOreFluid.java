package cofh.core.util.crafting;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeShapelessOreFluid extends ShapelessOreRecipe {

	// TODO: FIXME. Obviously do NOT PASS NULL.
	public RecipeShapelessOreFluid(Block result, Object... recipe) {

		super(null, result, replaceFluidWithUniversalBucket(recipe));
	}

	public RecipeShapelessOreFluid(Item result, Object... recipe) {

		super(null, result, replaceFluidWithUniversalBucket(recipe));
	}

	public RecipeShapelessOreFluid(ItemStack result, Object... recipe) {

		super(null, result, replaceFluidWithUniversalBucket(recipe));
	}

	public static Object[] replaceFluidWithUniversalBucket(Object[] array) {

		for (int i = 0; i < array.length; i++) {
			Object obj = array[i];
			if (obj instanceof FluidStack) {
				ItemStack filledBucket = FluidUtil.getFilledBucket((FluidStack) obj);
				array[i] = filledBucket;
			} else if (obj instanceof FluidStack) {
				ItemStack bucket = ForgeModContainer.getInstance().universalBucket.getEmpty().copy();
				IFluidHandlerItem handler = FluidUtil.getFluidHandler(bucket);
				handler.fill((FluidStack) obj, true);
				array[i] = bucket;
			}
		}
		return array;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {

		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			ItemStack stackInSlot = inv.getStackInSlot(i);
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stackInSlot);
			if (fluidHandler == null) {
				ret.set(i, ForgeHooks.getContainerItem(stackInSlot).copy());
			} else {
				fluidHandler.drain(Fluid.BUCKET_VOLUME, true);
				ret.set(i, fluidHandler.getContainer().copy());
			}
		}
		return ret;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {

		ArrayList<Object> required = new ArrayList<>(input);

		for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
			ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);

			if (!stackInSlot.isEmpty()) {
				boolean inRecipe = false;

				for (Object aRequired : required) {
					boolean match = false;

					if (aRequired instanceof ItemStack) {
						ItemStack requiredStack = (ItemStack) aRequired;
						if (requiredStack.getItem() == ForgeModContainer.getInstance().universalBucket) {
							FluidStack fluidStack = FluidUtil.getFluidContained(requiredStack);
							IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stackInSlot);
							if (fluidHandler != null) {
								if (fluidStack.isFluidStackIdentical(fluidHandler.drain(Fluid.BUCKET_VOLUME, false))) {
									match = true;
								}
							}
						} else {
							IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stackInSlot);
							if (fluidHandler == null) {
								match = OreDictionary.itemMatches(requiredStack, stackInSlot, false);
							}
						}
					} else if (aRequired instanceof List) {
						for (ItemStack stack : ((List<ItemStack>) aRequired)) {
							if (OreDictionary.itemMatches(stack, stackInSlot, false)) {
								match = true;
								break;
							}
						}
					}
					if (match) {
						inRecipe = true;
						required.remove(aRequired);
						break;
					}
				}
				if (!inRecipe) {
					return false;
				}
			}
		}
		return required.isEmpty();
	}

}
