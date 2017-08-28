package cofh.core.item.tool;

import cofh.core.util.RayTracer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemHammerCore extends ItemToolCore { // implements IAOEBreakItem {

	public ItemHammerCore(ToolMaterial toolMaterial) {

		this(-3.6F, toolMaterial);
	}

	public ItemHammerCore(float attackSpeed, ToolMaterial toolMaterial) {

		super(4.0F, attackSpeed, toolMaterial);
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

		damageVsEntity = damageVsEntity + 2;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		int used = 0;
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

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
						for (z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
							if (x == pos.getX() && z == pos.getZ()) {
								continue;
							}
							adjPos = new BlockPos(x, y, z);
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
					for (x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
						for (y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
							if (x == pos.getX() && y == pos.getY()) {
								continue;
							}
							adjPos = new BlockPos(x, y, z);
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
					for (y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
						for (z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
							if (y == pos.getY() && z == pos.getZ()) {
								continue;
							}
							adjPos = new BlockPos(x, y, z);
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
		return false;
	}

	//	/* IAOEBreakItem */
	//	@Override
	//	public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, BlockPos pos, EntityPlayer player) {
	//
	//		Iterable<BlockPos> area;
	//		RayTraceResult traceResult = RayTracer.retrace(player);
	//
	//		switch (traceResult.sideHit) {
	//			case DOWN:
	//				area = BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, -1, 1));
	//				break;
	//			case UP:
	//				area = BlockPos.getAllInBox(pos.add(-1, 1, -1), pos.add(1, 1, 1));
	//				break;
	//			case NORTH:
	//				area = BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, -1));
	//				break;
	//			case SOUTH:
	//				area = BlockPos.getAllInBox(pos.add(-1, -1, 1), pos.add(1, 1, 1));
	//				break;
	//			case WEST:
	//				area = BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(-1, 1, 1));
	//				break;
	//			default:
	//				area = BlockPos.getAllInBox(pos.add(1, -1, -1), pos.add(1, 1, 1));
	//				break;
	//		}
	//		return ImmutableList.copyOf(area);
	//	}

}
