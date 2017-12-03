package cofh.core.item.tool;

import cofh.core.item.IAOEBreakItem;
import cofh.core.util.RayTracer;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ItemHammerCore extends ItemToolCore implements IAOEBreakItem {

	public ItemHammerCore(ToolMaterial toolMaterial) {

		super(4.0F, -3.4F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");

		setMaxDamage(toolMaterial.getMaxUses() * 2);

		effectiveBlocks.addAll(ItemPickaxe.EFFECTIVE_ON);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);

		if (harvestLevel > 0) {
			attackDamage = 10.0F;
			attackSpeed = -3.5F + (0.1F * harvestLevel);
		} else {
			attackDamage = 7.0F;
			attackSpeed = -3.4F + (0.1F * (int) (efficiency / 5));
		}
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
		world.playEvent(2001, pos, Block.getStateId(state));

		float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);
		if (refStrength != 0.0F) {
			RayTraceResult traceResult = RayTracer.retrace(player);

			if (traceResult == null) {
				return false;
			}
			BlockPos adjPos;
			IBlockState adjState;
			float strength;
			int used = 0;

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			int radius = 1;

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (int i = x - radius; i <= x + radius; i++) {
						for (int k = z - radius; k <= z + radius; k++) {
							adjPos = new BlockPos(i, y, k);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
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
					for (int i = x - radius; i <= x + radius; i++) {
						for (int j = y - radius; j <= y + radius; j++) {
							adjPos = new BlockPos(i, j, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
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
					for (int j = y - radius; j <= y + radius; j++) {
						for (int k = z - radius; k <= z + radius; k++) {
							adjPos = new BlockPos(x, j, k);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
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
		return true;
	}

	/* IAOEBreakItem */
	@Override
	public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, BlockPos pos, EntityPlayer player) {

		ArrayList<BlockPos> area = new ArrayList<>();
		World world = player.getEntityWorld();

		if (!canHarvestBlock(world.getBlockState(pos), stack)) {
			return ImmutableList.copyOf(area);
		}
		RayTraceResult traceResult = RayTracer.retrace(player);
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
