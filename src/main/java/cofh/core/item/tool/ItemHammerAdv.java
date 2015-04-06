package cofh.core.item.tool;

import cofh.lib.util.helpers.BlockHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemHammerAdv extends ItemToolAdv {

	public ItemHammerAdv(ToolMaterial toolMaterial) {

		super(4.0F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");

		effectiveBlocks.addAll(ItemPickaxe.field_150915_c);
		effectiveMaterials.add(Material.iron);
		effectiveMaterials.add(Material.anvil);
		effectiveMaterials.add(Material.rock);
		effectiveMaterials.add(Material.ice);
		effectiveMaterials.add(Material.packedIce);
		effectiveMaterials.add(Material.glass);
		effectiveMaterials.add(Material.redstoneLight);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);

		if (!canHarvestBlock(block, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		float refStrength = ForgeHooks.blockStrength(block, player, world, x, y, z);
		if (refStrength != 0.0D && canHarvestBlock(block, stack)) {
			MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, true);
			List<ItemStack> drops = new ArrayList<ItemStack>();
			Block adjBlock;
			float strength;

			int x2 = x;
			int y2 = y;
			int z2 = z;

			switch (pos.sideHit) {
			case 0:
			case 1:
				for (x2 = pos.blockX - 1; x2 <= pos.blockX + 1; x2++) {
					for (z2 = pos.blockZ - 1; z2 <= pos.blockZ + 1; z2++) {
						adjBlock = world.getBlock(x2, y2, z2);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, x2, y2, z2);
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			case 2:
			case 3:
				for (x2 = pos.blockX - 1; x2 <= pos.blockX + 1; x2++) {
					for (y2 = pos.blockY - 1; y2 <= pos.blockY + 1; y2++) {
						adjBlock = world.getBlock(x2, y2, z2);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, x2, y2, z2);
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			default:
				for (y2 = pos.blockY - 1; y2 <= pos.blockY + 1; y2++) {
					for (z2 = pos.blockZ - 1; z2 <= pos.blockZ + 1; z2++) {
						adjBlock = world.getBlock(x2, y2, z2);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, x2, y2, z2);
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			}
			if (used && !player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
		}
		return true;
	}

}
