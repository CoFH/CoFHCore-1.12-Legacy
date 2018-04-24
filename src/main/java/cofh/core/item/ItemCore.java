package cofh.core.item;

import cofh.core.init.CoreProps;
import cofh.core.render.FontRendererCore;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.SecurityHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCore extends Item {

	protected String name;
	protected String modName;

	public ItemCore() {

		this("cofh");
	}

	public ItemCore(String modName) {

		this.modName = modName;
	}

	/* STANDARD METHODS */
	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return false;
	}

	@Override
	public String getHighlightTip(ItemStack stack, String displayName) {

		if (isActive(stack)) {
			return "";
		}
		return displayName;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {

		if (SecurityHelper.isSecure(stack)) {
			location.invulnerable = true;
			location.isImmuneToFire = true;
			((EntityItem) location).lifespan = Integer.MAX_VALUE;
		}
		return null;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return FontRendererCore.loadFontRendererStack(stack);
	}

	/* HELPERS */
	public boolean isActive(ItemStack stack) {

		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean(CoreProps.ACTIVE);
	}

	protected int getTintIndex(ItemStack stack) {

		return 2;
	}

	/* IItemColor */
	public int colorMultiplier(ItemStack stack, int tintIndex) {

		if (ColorHelper.hasColor0(stack) && tintIndex == getTintIndex(stack)) {
			return ColorHelper.getColor0(stack);
		}
		return 0xFFFFFF;
	}

	/* IColorableItem */
	public void applyColor(ItemStack stack, int color, int colorIndex) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		colorIndex = Math.min(colorIndex, getMaxColorIndex(stack));

		if (colorIndex == 0) {
			stack.getTagCompound().setInteger(CoreProps.COLOR_0, color);
		} else {
			stack.getTagCompound().setInteger(CoreProps.COLOR_1, color);
		}
	}

	public void removeColor(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().removeTag(CoreProps.COLOR_0);
		stack.getTagCompound().removeTag(CoreProps.COLOR_1);
	}

	public int getMaxColorIndex(ItemStack stack) {

		return 0;
	}

}
