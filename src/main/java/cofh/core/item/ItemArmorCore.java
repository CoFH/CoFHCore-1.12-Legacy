package cofh.core.item;

import cofh.lib.util.helpers.ItemHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemArmorCore extends ItemArmor {

	protected String repairIngot = "";
	protected String[] textures = new String[2];

	protected boolean showInCreative = true;

	protected Multimap<String, AttributeModifier> properties = HashMultimap.create();

	public ItemArmorCore(ArmorMaterial material, EntityEquipmentSlot type) {

		super(material, 0, type);
	}

	public ItemArmorCore putAttribute(String attribute, AttributeModifier modifier) {

		properties.put(attribute, modifier);
		return this;
	}

	public ItemArmorCore setArmorTextures(String[] textures) {

		this.textures = textures;
		return this;
	}

	public ItemArmorCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemArmorCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab) && showInCreative) {
			items.add(new ItemStack(this, 1, 0));
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

		if (slot == armorType) {
			Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
			map.putAll(properties);
			return map;
		}
		return super.getAttributeModifiers(slot, stack);
	}

}
