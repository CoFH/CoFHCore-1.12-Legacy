package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public abstract class ItemToolAdv extends ItemTool {

	public String repairIngot = "";
	private final TLinkedHashSet<String> toolClasses = new TLinkedHashSet<String>();
	private final Set<String> immutableClasses = java.util.Collections.unmodifiableSet(toolClasses);

	protected THashSet<Block> effectiveBlocks = new THashSet<Block>();
	protected THashSet<Material> effectiveMaterials = new THashSet<Material>();
	protected int harvestLevel = -1;
	protected boolean showInCreative = true;

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

	protected boolean harvestBlock(World world, int x, int y, int z, EntityPlayer player) {

		if (world.isAirBlock(x, y, z)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		// only effective materials
		if (!(toolClasses.contains(block.getHarvestTool(meta)) || canHarvestBlock(block, player.getCurrentEquippedItem()))) {
			return false;
		}

		if (!ForgeHooks.canHarvestBlock(block, player, meta)) {
			return false;
		}
		// send the blockbreak event
		BreakEvent event = null;
		if (playerMP != null) {
			event = ForgeHooks.onBlockBreakEvent(world, playerMP.theItemInWorldManager.getGameType(), playerMP, x, y, z);
			if (event.isCanceled()) {
				return false;
			}
		}
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				block.onBlockHarvested(world, x, y, z, meta, player);
			} else {
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) | (meta << 12));
			}

			if (block.removedByPlayer(world, player, x, y, z, false)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			}
			// send update to client
			if (!world.isRemote) {
				playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			} else {
				Minecraft.getMinecraft().getNetHandler()
						.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}
		world.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(block) | (meta << 12));
		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, x, y, z, meta, player);
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
				block.harvestBlock(world, player, x, y, z, meta);
				if (event != null) {
					block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
				}
			}
			// always send block update to client
			playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		} else {
			// PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			}
			Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	protected boolean isClassValid(String toolClass, ItemStack stack) {

		return true;
	}

	protected boolean isValidHarvestMaterial(ItemStack stack, World world, int x, int y, int z) {

		return getEffectiveMaterials(stack).contains(world.getBlock(x, y, z).getMaterial());
	}

	protected int getHarvestLevel(ItemStack stack, int level) {

		return level;
	}

	protected float getEfficiency(ItemStack stack) {

		return efficiencyOnProperMaterial;
	}

	@Override
	public String getToolMaterialName() {

		return super.getToolMaterialName().contains(":") ? super.getToolMaterialName().split(":", 2)[1] : super.getToolMaterialName();
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {

		return func_150893_a(stack, block) > 1.0f;
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
	public float func_150893_a(ItemStack stack, Block block) {

		return (getEffectiveMaterials(stack).contains(block.getMaterial()) || getEffectiveBlocks(stack).contains(block)) ? getEfficiency(stack) : 1.0F;
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

	@Override
	public Set<String> getToolClasses(ItemStack stack) {

		return toolClasses.isEmpty() ? super.getToolClasses(stack) : immutableClasses;
	}

}
