package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public abstract class ItemToolAdv extends ItemTool {

	public String repairIngot = "";
	protected final TLinkedHashSet<String> toolClasses = new TLinkedHashSet<String>();
	private final Set<String> immutableClasses = java.util.Collections.unmodifiableSet(toolClasses);

	protected THashSet<Block> effectiveBlocks = new THashSet<Block>();
	protected THashSet<Material> effectiveMaterials = new THashSet<Material>();
	protected int harvestLevel = -1;
	protected boolean showInCreative = true;

	public ItemToolAdv(float baseDamage, float attackSpeed, Item.ToolMaterial toolMaterial) {

		super(baseDamage, attackSpeed, toolMaterial, null);
	}

	public ItemToolAdv(float baseDamage, float attackSpeed, Item.ToolMaterial toolMaterial, int harvestLevel) {

		this(baseDamage, attackSpeed, toolMaterial);
		this.harvestLevel = harvestLevel;
	}

	public ItemToolAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemToolAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
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

	protected boolean harvestBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		if (world.isAirBlock(pos)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		Block block = state.getBlock();
		// only effective materials
		if (!(toolClasses.contains(block.getHarvestTool(state)) || canHarvestBlock(state, player.getActiveItemStack()))) {
			return false;
		}

		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}
		// send the blockbreak event
		int exp = -1;
		if (playerMP != null) {
			exp = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
			if (exp == -1) {
				return false;
			}
		}
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				block.onBlockHarvested(world, pos, state, player);
			} else {
				world.playEvent(2001, pos, Block.getIdFromBlock(block));
			}
			if (block.removedByPlayer(state, world, pos, player, false)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			// send update to client
			if (!world.isRemote) {
				playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
			} else {
				Minecraft.getMinecraft().getConnection()
						.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}
		world.playEvent(player, 2001, pos, Block.getIdFromBlock(block));
		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, pos, state, player);
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
				block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getActiveItemStack());

				if (exp > 0) {
					block.dropXpOnBlockBreak(world, pos, exp);
				}
			}
			// always send block update to client
			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
		} else {
			// PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			Minecraft.getMinecraft().getConnection()
					.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	protected boolean isClassValid(String toolClass, ItemStack stack) {

		return true;
	}

	protected boolean isValidHarvestMaterial(ItemStack stack, World world, BlockPos pos) {

		return getEffectiveMaterials(stack).contains(world.getBlockState(pos).getMaterial());
	}

	protected int getHarvestLevel(ItemStack stack, int level) {

		return level;
	}

	protected float getEfficiency(ItemStack stack) {

		return efficiencyOnProperMaterial;
	}

	/* STANDARD METHODS */
	@Override
	public String getToolMaterialName() {

		return super.getToolMaterialName().contains(":") ? super.getToolMaterialName().split(":", 2)[1] : super.getToolMaterialName();
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {

		return getStrVsBlock(stack, state) > 1.0f;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
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
	public float getStrVsBlock(ItemStack stack, IBlockState state) {

		return (getEffectiveMaterials(stack).contains(state.getMaterial()) || getEffectiveBlocks(stack).contains(state.getBlock())) ? getEfficiency(stack) : 1.0F;
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack) {

		return toolClasses.isEmpty() ? super.getToolClasses(stack) : immutableClasses;
	}

}
