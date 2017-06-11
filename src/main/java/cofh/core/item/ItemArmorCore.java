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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

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
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

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

		if (slot == armorType) {
			Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
			map.putAll(properties);
			return map;
		}
		return super.getAttributeModifiers(slot, stack);
	}

}
