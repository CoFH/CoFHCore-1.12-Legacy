package cofh.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cofh.util.CoreUtils;
import cofh.util.ServerHelper;

public class ItemSickleAdv extends ItemToolAdv {

	public int radius = 3;

	public ItemSickleAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("sickle");

		effectiveMaterials.add(Material.leaves);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
		effectiveMaterials.add(Material.web);
	}

	public ItemSickleAdv setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {

		return block == Blocks.web || block == Blocks.vine;
	}

	@Override
	protected void harvestBlock(World world, int x, int y, int z, EntityPlayer player) {

		Block block = world.getBlock(x, y, z);

		if (block.getBlockHardness(world, x, y, z) < 0 || block.equals(Blocks.waterlily)) {
			return;
		}
		int bMeta = world.getBlockMetadata(x, y, z);

		if (block.canHarvestBlock(player, bMeta)) {
			block.harvestBlock(world, player, x, y, z, bMeta);
		}
		if (ServerHelper.isServerWorld(world) && block.equals(Blocks.vine)) {
			CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.vine), world, x, y, z);
		}
		world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {

		if (!(entity instanceof EntityPlayer)) {
			return false;
		}
		EntityPlayer player = (EntityPlayer) entity;

		if (block.getBlockHardness(world, x, y, z) != 0.0D && !effectiveMaterials.contains(block.getMaterial())) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, entity);
			}
			return false;
		}
		boolean used = false;

		for (int i = x - radius; i <= x + radius; i++) {
			for (int k = z - radius; k <= z + radius; k++) {
				if (isValidHarvestMaterial(stack, world, i, y, k)) {
					harvestBlock(world, i, y, k, player);
					used = true;
				}
			}
		}
		if (used) {
			stack.damageItem(1, entity);
		}
		return used;
	}

}
