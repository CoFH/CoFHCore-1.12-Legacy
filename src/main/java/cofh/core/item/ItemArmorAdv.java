package cofh.core.item;

import cofh.lib.util.helpers.ItemHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

public class ItemArmorAdv extends ItemArmor {

    public String repairIngot = "";
    public String[] textures = new String[2];
    protected Multimap<String, AttributeModifier> properties = HashMultimap.create();
    protected boolean showInCreative = true;

    public ItemArmorAdv(ArmorMaterial material, EntityEquipmentSlot type) {
        super(material, 0, type);
    }

    public ItemArmorAdv setRepairIngot(String repairIngot) {
        this.repairIngot = repairIngot;
        return this;
    }

    public ItemArmorAdv setArmorTextures(String[] textures) {
        this.textures = textures;
        return this;
    }

    public ItemArmorAdv setShowInCreative(boolean showInCreative) {
        this.showInCreative = showInCreative;
        return this;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

        if (showInCreative) {
            list.add(new ItemStack(item, 1, 0));
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

        return ItemHelper.isOreNameEqual(stack, repairIngot);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (slot == EntityEquipmentSlot.LEGS) {
            return textures[1];
        }
        return textures[0];
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        map.putAll(properties);
        return map;
    }

    public ItemArmorAdv putAttribute(String attribute, AttributeModifier modifier) {

        properties.put(attribute, modifier);
        return this;
    }

    public Collection<AttributeModifier> removeAttribute(String attribute) {

        return properties.removeAll(attribute);
    }

}
