package cofh.core.item.tool;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemHorseArmorCore extends Item {

	protected final HorseArmorType type;
	protected boolean showInCreative = true;

	public ItemHorseArmorCore(HorseArmorType type) {

		super();
		this.type = type;
	}

	public ItemHorseArmorCore setShowInCreative(boolean showInCreative) {

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
	public HorseArmorType getHorseArmorType(ItemStack stack) {

		return type;
	}

	@Override
	public String getHorseArmorTexture(EntityLiving wearer, ItemStack stack) {

		return type.getTextureName();
	}

	@Override
	public void onHorseArmorTick(World world, EntityLiving wearer, ItemStack itemStack) {

	}

}
