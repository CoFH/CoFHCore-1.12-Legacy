package cofh.core.block;

import cofh.core.render.FontRendererCore;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockCore extends ItemBlock {

	public ItemBlockCore(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return false;
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

}
