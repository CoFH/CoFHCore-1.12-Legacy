package cofh.item.tool;

import cofh.util.ItemHelper;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public abstract class ItemToolAdv extends ItemTool {

	public String repairIngot = "";
	private final TLinkedHashSet<String> toolClasses = new TLinkedHashSet<String>();
	private final Set<String> immutableClasses = java.util.Collections.unmodifiableSet(toolClasses);

	protected THashSet<Block> effectiveBlocks = new THashSet<Block>();
	protected THashSet<Material> effectiveMaterials = new THashSet<Material>();
	protected int harvestLevel = -1;

	public ItemToolAdv(float baseDamage, Item.ToolMaterial toolMaterial) {

		super(baseDamage, toolMaterial, null);
	}

	public ItemToolAdv(float baseDamage, Item.ToolMaterial toolMaterial, int harvestLevel) {

		this(baseDamage, toolMaterial);
		this.harvestLevel = harvestLevel;
	}

	public ItemToolAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	protected void addToolClass(String string) {

		toolClasses.add(string);
	}

	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {

		return effectiveBlocks;
	}

	protected THashSet<Material> getEffectiveMaterials(ItemStack stack) {

		return effectiveMaterials;
	}

	protected boolean isClassValid(String toolClass, ItemStack stack) {

		return true;
	}

	protected float getEfficiency(ItemStack stack) {

		return efficiencyOnProperMaterial;
	}

	protected int getHarvestLevel(ItemStack stack, int level) {

		return level;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public float func_150893_a(ItemStack stack, Block block) {

		return (getEffectiveMaterials(stack).contains(block.getMaterial()) || getEffectiveBlocks(stack).contains(block)) ? getEfficiency(stack) : 1.0F;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {

		return func_150893_a(stack, block) > 1.0f;
	}

	protected void harvestBlock(World world, int x, int y, int z, EntityPlayer player) {

		Block block = world.getBlock(x, y, z);

		if (block.getBlockHardness(world, x, y, z) < 0) {
			return;
		}
		int bMeta = world.getBlockMetadata(x, y, z);

		if (block.canHarvestBlock(player, bMeta)) {
			block.harvestBlock(world, player, x, y, z, bMeta);
		}
		world.setBlockToAir(x, y, z);
	}

	protected boolean isValidHarvestMaterial(ItemStack stack, World world, int x, int y, int z) {

		return getEffectiveMaterials(stack).contains(world.getBlock(x, y, z).getMaterial());
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {

		if (harvestLevel != -1) {
			return harvestLevel;
		}
		int level = super.getHarvestLevel(stack, toolClass);
		if (level == -1 && isClassValid(toolClass, stack) && toolClasses.contains(toolClass)) {
			level = toolMaterial.getHarvestLevel();
		}
		return getHarvestLevel(stack, level);
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack) {

		return toolClasses.isEmpty() ? super.getToolClasses(stack) : immutableClasses;
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {

		for (String type : getToolClasses(stack)) {
			int level = getHarvestLevel(stack, type);

			if (type.equals(block.getHarvestTool(meta))) {
				if (block.getHarvestLevel(meta) < level) {
					return getEfficiency(stack);
				}
			}
		}
		return super.getDigSpeed(stack, block, meta);
	}

}
