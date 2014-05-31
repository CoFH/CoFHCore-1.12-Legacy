package cofh.item.tool;

import cofh.CoFHCore;
import cofh.entity.EntityCoFHFishHook;
import cofh.util.ItemHelper;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishingRodAdv extends ItemFishingRod {

	public String repairIngot = "";
	public ToolMaterial toolMaterial;

	public ItemFishingRodAdv(ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		this.setMaxDamage(toolMaterial.getMaxUses());
	}

	public ItemFishingRodAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreName(stack, repairIngot);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		EntityPlayer player = CoFHCore.proxy.getClientPlayer();

		if (player.inventory.getCurrentItem() == stack && player.fishEntity != null) {
			return func_94597_g();
		}
		return getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
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
	public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
				new AttributeModifier(field_111210_e, "Tool modifier", toolMaterial.getDamageVsEntity(), 0));
		return multimap;
	}

}
