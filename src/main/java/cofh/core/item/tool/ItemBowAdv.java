package cofh.core.item.tool;

import cofh.core.enchantment.CoFHEnchantment;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemBowAdv extends ItemBow {

	protected ToolMaterial toolMaterial;

	public String repairIngot = "";
	protected boolean showInCreative = true;
	public float arrowSpeedMultiplier = 2.0F;
	public float arrowDamageMultiplier = 1.25F;

	public ItemBowAdv(Item.ToolMaterial toolMaterial) {

		super();
		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());
	}

	public int cofh_canEnchantApply(ItemStack stack, Enchantment ench) {

		if (ench.effectId == Enchantment.looting.effectId) {
			return 1;
		}
		if (ench.type == EnumEnchantmentType.BOW) {
			return 1;
		}
		return -1;
	}

	public ItemBowAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemBowAdv setArrowSpeed(float multiplier) {

		this.arrowSpeedMultiplier = multiplier;
		return this;
	}

	public ItemBowAdv setArrowDamage(float multiplier) {

		arrowDamageMultiplier = multiplier;
		return this;
	}

	public ItemBowAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		ArrowNockEvent event = new ArrowNockEvent(player, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.result;
		}
		if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow)
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0) {
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}
		return stack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemUse) {

		int draw = this.getMaxItemUseDuration(stack) - itemUse;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, draw);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		draw = event.charge;

		boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (flag || player.inventory.hasItem(Items.arrow)) {
			float drawStrength = draw / 20.0F;
			drawStrength = (drawStrength * drawStrength + drawStrength * 2.0F) / 3.0F;

			if (drawStrength > 1.0F) {
				drawStrength = 1.0F;
			} else if (drawStrength < 0.1F) {
				return;
			}
			int enchantPower = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
			int enchantKnockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
			int enchantFire = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
			int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.multishot.effectId, stack);

			EntityArrow arrow = new EntityArrow(world, player, drawStrength * arrowSpeedMultiplier);
			double damage = arrow.getDamage() * arrowDamageMultiplier;
			arrow.setDamage(damage);

			if (drawStrength == 1.0F) {
				arrow.setIsCritical(true);
			}
			if (enchantPower > 0) {
				arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
			}
			if (enchantKnockback > 0) {
				arrow.setKnockbackStrength(enchantKnockback);
			}
			if (enchantFire > 0) {
				arrow.setFire(100);
			}
			if (flag) {
				arrow.canBePickedUp = 2;
			} else {
				player.inventory.consumeInventoryItem(Items.arrow);
			}
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawStrength * 0.5F);

			if (ServerHelper.isServerWorld(world)) {
				world.spawnEntityInWorld(arrow);
			}
			for (int i = 0; i < enchantMultishot; i++) {
				arrow = new EntityArrow(world, player, drawStrength * arrowSpeedMultiplier);
				arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, 1.5f * drawStrength * arrowSpeedMultiplier, 3.0F);

				arrow.setDamage(damage);

				if (drawStrength == 1.0F) {
					arrow.setIsCritical(true);
				}
				if (enchantPower > 0) {
					arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
				}
				if (enchantKnockback > 0) {
					arrow.setKnockbackStrength(enchantKnockback);
				}
				if (enchantFire > 0) {
					arrow.setFire(100);
				}
				arrow.canBePickedUp = 2;

				if (ServerHelper.isServerWorld(world)) {
					world.spawnEntityInWorld(arrow);
				}
			}
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
		}
	}

}
