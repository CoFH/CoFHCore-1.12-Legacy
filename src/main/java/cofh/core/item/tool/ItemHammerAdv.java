package cofh.core.item.tool;

import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemHammerAdv extends ItemToolAdv {

	public ItemHammerAdv(ToolMaterial toolMaterial) {

		super(4.0F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");

		effectiveBlocks.addAll(ItemPickaxe.EFFECTIVE_ON);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos hitPos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState state = world.getBlockState(hitPos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		float refStrength = ForgeHooks.blockStrength(state, player, world, hitPos);
		if (refStrength != 0.0D && canHarvestBlock(state, stack)) {
			RayTraceResult traceResult = BlockHelper.getCurrentMovingObjectPosition(player, true);
			BlockPos tracePos = traceResult.getBlockPos();
			IBlockState adjBlock;
			float strength;

			int x2 = hitPos.getX();
			int y2 = hitPos.getY();
			int z2 = hitPos.getZ();

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (x2 = tracePos.getX() - 1; x2 <= tracePos.getX() + 1; x2++) {
						for (z2 = tracePos.getZ() - 1; z2 <= tracePos.getZ() + 1; z2++) {
							BlockPos pos2 = new BlockPos(x2, y2, z2);
							adjBlock = world.getBlockState(pos2);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, pos2);
							if (strength > 0f && refStrength / strength <= 10f) {
								used |= harvestBlock(world, pos2, player);
							}
						}
					}
					break;
				case NORTH:
				case SOUTH:
					for (x2 = tracePos.getX() - 1; x2 <= tracePos.getX() + 1; x2++) {
						for (y2 = tracePos.getY() - 1; y2 <= tracePos.getY() + 1; y2++) {
							BlockPos pos2 = new BlockPos(x2, y2, z2);
							adjBlock = world.getBlockState(pos2);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, pos2);
							if (strength > 0f && refStrength / strength <= 10f) {
								used |= harvestBlock(world, pos2, player);
							}
						}
					}
					break;
				case WEST:
				case EAST:
					for (y2 = tracePos.getY() - 1; y2 <= tracePos.getY() + 1; y2++) {
						for (z2 = tracePos.getZ() - 1; z2 <= tracePos.getZ() + 1; z2++) {
							BlockPos pos2 = new BlockPos(x2, y2, z2);
							adjBlock = world.getBlockState(pos2);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, pos2);
							if (strength > 0f && refStrength / strength <= 10f) {
								used |= harvestBlock(world, pos2, player);
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
