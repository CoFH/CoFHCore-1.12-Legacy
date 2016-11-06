package cofh.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CoFHEnchantment {

    public static Enchantment holding;
    public static Enchantment multishot;

    private CoFHEnchantment() {

    }

    public static void postInit() {

        holding = new EnchantmentHolding("cofh:holding");
        multishot = new EnchantmentMultishot("cofh:multishot");

        GameRegistry.register(holding);
        GameRegistry.register(multishot);
    }

    public static NBTTagList getEnchantmentTagList(NBTTagCompound nbt) {

        return nbt == null ? null : nbt.getTagList("ench", 10);
    }

    public static void addEnchantment(ItemStack stack, Enchantment ench, int level) {

        addEnchantment(stack.getTagCompound(), Enchantment.getEnchantmentID(ench), level);
    }

    public static void addEnchantment(NBTTagCompound nbt, Enchantment ench, int level){
        addEnchantment(nbt, Enchantment.getEnchantmentID(ench), level);
    }

    public static void addEnchantment(ItemStack stack, int id, int level) {

        addEnchantment(stack.getTagCompound(), id, level);
    }

    public static void addEnchantment(NBTTagCompound nbt, int id, int level) {

        if (nbt == null) {
            nbt = new NBTTagCompound();
        }
        NBTTagList list = getEnchantmentTagList(nbt);

        if (list == null) {
            list = new NBTTagList();
        }
        boolean found = false;
        for (int i = 0; i < list.tagCount() && !found; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if (tag.getShort("id") == id) {
                tag.setShort("id", (short) id);
                tag.setShort("lvl", (short) level);
                found = true;
            }
        }
        if (!found) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setShort("id", (short) id);
            tag.setShort("lvl", (short) level);
            list.appendTag(tag);
        }
        nbt.setTag("ench", list);
    }

}
