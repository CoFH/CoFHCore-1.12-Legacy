package cofh.core.item.tool;

import cofh.core.init.CoreProps;
import cofh.core.item.IAOEBreakItem;
import cofh.core.util.RayTracer;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ItemExcavatorCore extends ItemToolCore implements IAOEBreakItem {

	public ItemExcavatorCore(ToolMaterial toolMaterial) {

		super(1.0F, -3.0F, toolMaterial);
		addToolClass("shovel");
		addToolClass("excavator");

		setMaxDamage(toolMaterial.getMaxUses() * 2);

		effectiveBlocks.addAll(ItemSpade.EFFECTIVE_ON);

		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (state.getBlockHardness(world, pos) == 0.0F) {
			return false;
		}
		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		// world.playEvent(2001, pos, Block.getStateId(state));
		if (player.isSneaking()) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);
		float maxStrength = refStrength / CoreProps.AOE_BREAK_FACTOR;

		if (refStrength != 0.0F) {
			RayTraceResult traceResult = RayTracer.retrace(player, false);

			if (traceResult == null || traceResult.sideHit == null) {
				return false;
			}
			BlockPos adjPos;
			IBlockState adjState;
			float strength;
			int count = 0;

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			int radius = 1;

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (int i = x - radius; i <= x + radius; i++) {
						for (int k = z - radius; k <= z + radius; k++) {
							if (i == x && k == z) {
								continue;
							}
							adjPos = new BlockPos(i, y, k);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && strength >= maxStrength) {
								if (harvestBlock(world, adjPos, player)) {
									count++;
								}
							}
						}
					}
					break;
				case NORTH:
				case SOUTH:
					for (int i = x - radius; i <= x + radius; i++) {
						for (int j = y - radius; j <= y + radius; j++) {
							if (i == x && j == y) {
								continue;
							}
							adjPos = new BlockPos(i, j, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && strength >= maxStrength) {
								if (harvestBlock(world, adjPos, player)) {
									count++;
								}
							}
						}
					}
					break;
				case WEST:
				case EAST:
					for (int j = y - radius; j <= y + radius; j++) {
						for (int k = z - radius; k <= z + radius; k++) {
							if (j == y && k == z) {
								continue;
							}
							adjPos = new BlockPos(x, j, k);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && strength >= maxStrength) {
								if (harvestBlock(world, adjPos, player)) {
									count++;
								}
							}
						}
					}
					break;
			}
			if (count > 0 && !player.capabilities.isCreativeMode) {
				stack.damageItem(count, player);
			}
		}
		return false;
	}

	/* IAOEBreakItem */
	@Override
	public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, BlockPos pos, EntityPlayer player) {

		ArrayList<BlockPos> area = new ArrayList<>();
		World world = player.getEntityWorld();

		RayTraceResult traceResult = RayTracer.retrace(player, false);
		if (traceResult == null || traceResult.sideHit == null || !canHarvestBlock(world.getBlockState(pos), stack) || player.isSneaking()) {
			return ImmutableList.copyOf(area);
		}
		BlockPos harvestPos;

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		int radius = 1;

		switch (traceResult.sideHit) {
			case DOWN:
			case UP:
				for (int i = x - radius; i <= x + radius; i++) {
					for (int k = z - radius; k <= z + radius; k++) {
						if (i == x && k == z) {
							continue;
						}
						harvestPos = new BlockPos(i, y, k);
						if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
							area.add(harvestPos);
						}
					}
				}
				break;
			case NORTH:
			case SOUTH:
				for (int i = x - radius; i <= x + radius; i++) {
					for (int j = y - radius; j <= y + radius; j++) {
						if (i == x && j == y) {
							continue;
						}
						harvestPos = new BlockPos(i, j, z);
						if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
							area.add(harvestPos);
						}
					}
				}
				break;
			case WEST:
			case EAST:
				for (int j = y - radius; j <= y + radius; j++) {
					for (int k = z - radius; k <= z + radius; k++) {
						if (j == y && k == z) {
							continue;
						}
						harvestPos = new BlockPos(x, j, k);
						if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
							area.add(harvestPos);
						}
					}
				}
				break;
		}
		return ImmutableList.copyOf(area);
	}

}
