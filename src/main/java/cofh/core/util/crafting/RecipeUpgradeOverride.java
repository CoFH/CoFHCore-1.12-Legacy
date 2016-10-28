package cofh.core.util.crafting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class RecipeUpgradeOverride extends RecipeUpgrade {

	HashMap<String, NBTBase[]> overrides = new HashMap<String, NBTBase[]>();

	public RecipeUpgradeOverride(ItemStack result, Object[] recipe) {

		super(result, recipe);
	}

	public RecipeUpgradeOverride(int slot, ItemStack result, Object[] recipe) {

		super(slot, result, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		ItemStack craftingResult = super.getCraftingResult(craftMatrix);

		if (craftingResult.getTagCompound() != null) {
			for (Map.Entry<String, NBTBase[]> override : overrides.entrySet()) {
				if (override.getValue()[0] == null || override.getValue()[0].equals(craftingResult.getTagCompound().getTag(override.getKey()))) {
					craftingResult.getTagCompound().setTag(override.getKey(), override.getValue()[1].copy());
				}
			}
		}
		return craftingResult;
	}

	public RecipeUpgradeOverride addOverride(String key, NBTBase prevDefault, NBTBase newDefault) {

		overrides.put(key, new NBTBase[] { prevDefault, newDefault });
		return this;
	}

	public RecipeUpgradeOverride addByte(String key, byte prevDefault, byte newDefault) {

		return addOverride(key, new NBTTagByte(prevDefault), new NBTTagByte(newDefault));
	}

	public RecipeUpgradeOverride addShort(String key, short prevDefault, short newDefault) {

		return addOverride(key, new NBTTagShort(prevDefault), new NBTTagShort(newDefault));
	}

	public RecipeUpgradeOverride addInteger(String key, int prevDefault, int newDefault) {

		return addOverride(key, new NBTTagInt(prevDefault), new NBTTagInt(newDefault));
	}

	public RecipeUpgradeOverride addOverrideLong(String key, long prevDefault, long newDefault) {

		return addOverride(key, new NBTTagLong(prevDefault), new NBTTagLong(newDefault));
	}

	public RecipeUpgradeOverride addFloat(String key, float prevDefault, float newDefault) {

		return addOverride(key, new NBTTagFloat(prevDefault), new NBTTagFloat(newDefault));
	}

	public RecipeUpgradeOverride addDouble(String key, double prevDefault, double newDefault) {

		return addOverride(key, new NBTTagDouble(prevDefault), new NBTTagDouble(newDefault));
	}

	public RecipeUpgradeOverride addString(String key, String prevDefault, String newDefault) {

		return addOverride(key, new NBTTagString(prevDefault), new NBTTagString(newDefault));
	}

	public RecipeUpgradeOverride addByteArray(String key, byte[] prevDefault, byte[] newDefault) {

		return addOverride(key, new NBTTagByteArray(prevDefault), new NBTTagByteArray(newDefault));
	}

	public RecipeUpgradeOverride addIntArray(String key, int[] prevDefault, int[] newDefault) {

		return addOverride(key, new NBTTagIntArray(prevDefault), new NBTTagIntArray(newDefault));
	}

	public RecipeUpgradeOverride addBoolean(String key, boolean prevDefault, boolean newDefault) {

		return addByte(key, (byte) (prevDefault ? 1 : 0), (byte) (newDefault ? 1 : 0));
	}

}
