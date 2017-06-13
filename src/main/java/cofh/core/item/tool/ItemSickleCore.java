package cofh.core.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSickleCore extends ItemToolCore {

	protected int radius = 3;

	public ItemSickleCore(ToolMaterial toolMaterial) {

		super(3.0F, -2.6F, toolMaterial);
		addToolClass("sickle");

		setMaxDamage(toolMaterial.getMaxUses() * 4);

		effectiveBlocks.add(Blocks.WEB);
		effectiveBlocks.add(Blocks.VINE);
		effectiveBlocks.add(Blocks.LEAVES);
		effectiveBlocks.add(Blocks.LEAVES2);

		effectiveMaterials.add(Material.LEAVES);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.WEB);
	}

	public ItemSickleCore setRadius(int radius) {

		this.radius = radius;
		return this;
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
		int x = pos.getX();
		// int y = pos.getY();
		int z = pos.getZ();

		int used = 0;
		world.playEvent(2001, pos, Block.getStateId(state));

		for (int i = x - radius; i <= x + radius; i++) {
			for (int k = z - radius; k <= z + radius; k++) {
				if (harvestBlock(world, new BlockPos(i, pos.getY(), k), player)) {
					used++;
				}
			}
		}
		if (used > 0 && !player.capabilities.isCreativeMode) {
			stack.damageItem(used, player);
		}
		return false;
	}

}
