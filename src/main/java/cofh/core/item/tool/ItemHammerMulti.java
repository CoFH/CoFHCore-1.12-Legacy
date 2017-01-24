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

public class ItemHammerMulti extends ItemToolMulti {


	public ItemHammerMulti(String modName) {

		super(modName, 4.0F, -3.6F);
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
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		int used = 0;

		float refStrength = ForgeHooks.blockStrength(state, player, world, pos);
		if (refStrength != 0.0F) {
			RayTraceResult traceResult = BlockHelper.getCurrentMovingObjectPosition(player, true);
			BlockPos tracePos = traceResult.getBlockPos();
			IBlockState adjBlock;
			float strength;

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (x = tracePos.getX() - 1; x <= tracePos.getX() + 1; x++) {
						for (z = tracePos.getZ() - 1; z <= tracePos.getZ() + 1; z++) {
							BlockPos adjPos = new BlockPos(x, y, z);
							adjBlock = world.getBlockState(adjPos);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								if (harvestBlock(world, adjPos, player)) {
									used++;
								}
							}
						}
					}
					break;
				case NORTH:
				case SOUTH:
					for (x = tracePos.getX() - 1; x <= tracePos.getX() + 1; x++) {
						for (y = tracePos.getY() - 1; y <= tracePos.getY() + 1; y++) {
							BlockPos adjPos = new BlockPos(x, y, z);
							adjBlock = world.getBlockState(adjPos);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								if (harvestBlock(world, adjPos, player)) {
									used++;
								}
							}
						}
					}
					break;
				case WEST:
				case EAST:
					for (y = tracePos.getY() - 1; y <= tracePos.getY() + 1; y++) {
						for (z = tracePos.getZ() - 1; z <= tracePos.getZ() + 1; z++) {
							BlockPos adjPos = new BlockPos(x, y, z);
							adjBlock = world.getBlockState(adjPos);
							strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								if (harvestBlock(world, adjPos, player)) {
									used++;
								}
							}
						}
					}
					break;
			}
			if (used > 0 && !player.capabilities.isCreativeMode) {
				stack.damageItem(used, player);
			}
		}
		return false;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return getToolMaterial(stack).getMaxUses() * 2;
	}

}
