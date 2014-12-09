package cofh.core.util.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.Map;

public class RecipeUpgradeOveride extends RecipeUpgrade {
    HashMap<String, NBTBase[]> overrides = new HashMap<String, NBTBase[]>();

    public RecipeUpgradeOveride(ItemStack result, Object[] recipe) {
        super(result, recipe);
    }

    public RecipeUpgradeOveride(int slot, ItemStack result, Object[] recipe) {
        super(slot, result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
        ItemStack craftingResult = super.getCraftingResult(craftMatrix);

        if (craftingResult.stackTagCompound != null) {
            for (Map.Entry<String, NBTBase[]> override : overrides.entrySet()) {
                if (override.getValue()[0] == null || override.getValue()[0].equals(craftingResult.stackTagCompound.getTag(override.getKey())))
                    craftingResult.stackTagCompound.setTag(override.getKey(), override.getValue()[1].copy());
            }
        }
        return craftingResult;
    }

    public RecipeUpgradeOveride addOveride(String key, NBTBase prevDefault, NBTBase newDefault) {
        overrides.put(key, new NBTBase[]{prevDefault, newDefault});
        return this;
    }

    public RecipeUpgradeOveride addOverideByte(String key, byte prevDefault, byte newDefault) {
        return addOveride(key, new NBTTagByte(prevDefault), new NBTTagByte(newDefault));
    }

    public RecipeUpgradeOveride addOverideShort(String key, short prevDefault, short newDefault) {
        return addOveride(key, new NBTTagShort(prevDefault), new NBTTagShort(newDefault));
    }

    public RecipeUpgradeOveride addOverideInteger(String key, int prevDefault, int newDefault) {
        return addOveride(key, new NBTTagInt(prevDefault), new NBTTagInt(newDefault));
    }

    public RecipeUpgradeOveride addOverideLong(String key, long prevDefault, long newDefault) {
        return addOveride(key, new NBTTagLong(prevDefault), new NBTTagLong(newDefault));
    }

    public RecipeUpgradeOveride addOverideFloat(String key, float prevDefault, float newDefault) {
        return addOveride(key, new NBTTagFloat(prevDefault), new NBTTagFloat(newDefault));
    }

    public RecipeUpgradeOveride addOverideDouble(String key, double prevDefault, double newDefault) {
        return addOveride(key, new NBTTagDouble(prevDefault), new NBTTagDouble(newDefault));
    }

    public RecipeUpgradeOveride addOverideString(String key, String prevDefault, String newDefault) {
        return addOveride(key, new NBTTagString(prevDefault), new NBTTagString(newDefault));
    }

    public RecipeUpgradeOveride addOverideByteArray(String key, byte[] prevDefault, byte[] newDefault) {
        return addOveride(key, new NBTTagByteArray(prevDefault), new NBTTagByteArray(newDefault));
    }

    public RecipeUpgradeOveride addOverideIntArray(String key, int[] prevDefault, int[] newDefault) {
        return addOveride(key, new NBTTagIntArray(prevDefault), new NBTTagIntArray(newDefault));
    }

    public RecipeUpgradeOveride addOverideBoolean(String key, boolean prevDefault, boolean newDefault) {
        return addOverideByte(key, (byte) (prevDefault ? 1 : 0), (byte) (newDefault ? 1 : 0));
    }


}
