package cofh.item.tool;

import cofh.util.ItemHelper;
import cofh.util.ServerHelper;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemBowAdv extends ItemBow {

	protected IIcon normalIcons[] = new IIcon[4];
	protected ToolMaterial toolMaterial;

	public String repairIngot = "";
	public float arrowSpeedMultiplier = 2.0F;
	public float arrowDamageMultiplier = 1.0F;

	public ItemBowAdv(Item.ToolMaterial toolMaterial) {

		super();
		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());
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
		if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow)) {
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
			float f = draw / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f > 1.0F) {
				f = 1.0F;
			} else if (f < 0.1F) {
				return;
			}
			EntityArrow arrow = new EntityArrow(world, player, f * arrowSpeedMultiplier);

			double damage = arrow.getDamage() * arrowDamageMultiplier;
			arrow.setDamage(damage);

			if (f == 1.0F) {
				arrow.setIsCritical(true);
			}
			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (k > 0) {
				arrow.setDamage(damage + k * 0.5D + 0.5D);
			}
			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (l > 0) {
				arrow.setKnockbackStrength(l);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
				arrow.setFire(100);
			}
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (flag) {
				arrow.canBePickedUp = 2;
			} else {
				player.inventory.consumeInventoryItem(Items.arrow);
			}
			if (ServerHelper.isServerWorld(world)) {
				world.spawnEntityInWorld(arrow);
			}
		}
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {

		return getIcon(stack, 0);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		return this.normalIcons[0];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {

		if (useRemaining > 0) {
			int draw = stack.getMaxItemUseDuration() - useRemaining;

			if (draw > 17) {
				return this.normalIcons[3];
			} else if (draw > 13) {
				return this.normalIcons[2];
			} else if (draw > 0) {
				return this.normalIcons[1];
			}
		}
		return this.normalIcons[0];
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		this.normalIcons[0] = ir.registerIcon(this.getIconString());

		for (int i = 1; i < 4; i++) {
			this.normalIcons[i] = ir.registerIcon(this.getIconString() + "_" + (i - 1));
		}
	}

}
