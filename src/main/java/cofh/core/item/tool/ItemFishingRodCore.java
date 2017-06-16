package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemFishingRodCore extends ItemFishingRod {

	protected String repairIngot = "";
	protected ToolMaterial toolMaterial;

	protected int luckModifier = 0;
	protected int speedModifier = 0;

	protected boolean showInCreative = true;

	public ItemFishingRodCore(ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		setMaxStackSize(1);
		setMaxDamage(toolMaterial.getMaxUses() + 5);

		addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {

			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				return entityIn == null ? 0.0F : (entityIn.getHeldItemMainhand() == stack && entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).fishEntity != null ? 1.0F : 0.0F);
			}
		});
	}

	public ItemFishingRodCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemFishingRodCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	public ItemFishingRodCore setLuckModifier(int luckModifier) {

		this.luckModifier = luckModifier;
		return this;
	}

	public ItemFishingRodCore setSpeedModifier(int speedModifier) {

		this.speedModifier = speedModifier;
		return this;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
	}

//	@Override
//	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
//
//		ItemStack stack = player.getHeldItem(hand);
//
//		if (player.fishEntity != null) {
//			int i = player.fishEntity.handleHookRetraction();
//			stack.damageItem(i, player);
//			player.swingArm(hand);
//		} else {
//			world.playSound( null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
//
//			if (ServerHelper.isServerWorld(world)) {
//				EntityFishHook hook = new EntityFishHook(world, player);
//
//				int enchantSpeed = EnchantmentHelper.func_191528_c(stack);
//				hook.func_191516_a(speedModifier + enchantSpeed);
//
//				int enchantLuck = EnchantmentHelper.func_191529_b(stack);
//				hook.func_191517_b(luckModifier + enchantLuck);
//
//				world.spawnEntity(hook);
//			}
//			player.swingArm(hand);
//		}
//		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
//	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if (playerIn.fishEntity != null)
		{
			int i = playerIn.fishEntity.handleHookRetraction();
			itemstack.damageItem(i, playerIn);
			playerIn.swingArm(handIn);
		}
		else
		{
			worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!worldIn.isRemote)
			{
				EntityFishHook entityfishhook = new EntityFishHook(worldIn, playerIn);
				int j = EnchantmentHelper.func_191528_c(itemstack);

				if (j > 0)
				{
					entityfishhook.func_191516_a(j);
				}

				int k = EnchantmentHelper.func_191529_b(itemstack);

				if (k > 0)
				{
					entityfishhook.func_191517_b(k);
				}

				worldIn.spawnEntity(entityfishhook);
			}

			playerIn.swingArm(handIn);
			playerIn.addStat(StatList.getObjectUseStats(this));
		}

		return new ActionResult(EnumActionResult.SUCCESS, itemstack);
	}

}
