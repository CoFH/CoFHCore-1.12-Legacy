package cofh.item.tool;

import cofh.CoFHCore;
import cofh.entity.EntityCoFHFishHook;
import cofh.util.ItemHelper;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishingRodAdv extends ItemFishingRod {

	protected IIcon normalIcons[] = new IIcon[2];

	protected ToolMaterial toolMaterial;
	public String repairIngot = "";

	public ItemFishingRodAdv(ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		this.setMaxDamage(toolMaterial.getMaxUses());
	}

	public ItemFishingRodAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
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

		if (player.fishEntity != null) {
			int i = player.fishEntity.func_146034_e();
			stack.damageItem(i, player);
			player.swingItem();
		} else {
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				world.spawnEntityInWorld(new EntityCoFHFishHook(world, player));
			}
			player.swingItem();
		}
		return stack;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {

		return getIcon(stack, 0);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		EntityPlayer player = CoFHCore.proxy.getClientPlayer();

		if (player.inventory.getCurrentItem() == stack && player.fishEntity != null) {
			return this.normalIcons[1];
		}
		return this.normalIcons[0];
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		this.normalIcons[0] = ir.registerIcon(this.getIconString() + "_Uncast");
		this.normalIcons[1] = ir.registerIcon(this.getIconString() + "_Cast");
	}

}
