package cofh.core.block;

import cofh.core.init.CoreProps;
import cofh.core.render.FontRendererCore;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockCore extends ItemBlock {

	protected BlockCore blockCore;

	// TODO: Remove in 4.6.0.
	public ItemBlockCore(Block block) {

		this((BlockCore) block);
	}

	public ItemBlockCore(BlockCore block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();

		blockCore = block;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return blockCore.getUnlocalizedName(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return blockCore.getRarity(stack);
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
	public boolean hasEffect(ItemStack stack) {

		return CoreProps.enableEnchantEffects && stack.isItemEnchanted();
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

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
