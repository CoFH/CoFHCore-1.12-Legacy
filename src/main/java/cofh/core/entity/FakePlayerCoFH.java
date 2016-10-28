package cofh.core.entity;

import cofh.lib.util.helpers.ItemHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class FakePlayerCoFH extends FakePlayer {
	private static GameProfile NAME = new GameProfile(UUID.fromString("5ae51d0b-e8bc-5a02-09f4-b5dbb05963da"), "[CoFH]");

	public boolean isSneaking = false;
	public ItemStack previousItem = null;
	public String myName = "[CoFH]";

	public FakePlayerCoFH(WorldServer world) {

		super(world, NAME);
		connection = new FakeNetServerHandler(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		this.addedToChunk = false;
	}

	public static boolean isBlockBreakable(FakePlayerCoFH myFakePlayer, World worldObj, BlockPos pos) {

		if (worldObj.isAirBlock(pos)) {
			return false;
		}

		IBlockState state = worldObj.getBlockState(pos);

		if (myFakePlayer == null) {
			return state.getBlockHardness(worldObj, pos) > -1;
		} else {
			return state.getPlayerRelativeBlockHardness(myFakePlayer, worldObj, pos) > -1;
		}
	}

	public void setItemInHand(ItemStack m_item) {

		this.inventory.currentItem = 0;
		this.inventory.setInventorySlotContents(0, m_item);
	}

	public void setItemInHand(int slot) {

		this.inventory.currentItem = slot;
	}

	@Override
	public double getDistanceSq(double x, double y, double z) {

		return 0F;
	}

	@Override
	public double getDistance(double x, double y, double z) {

		return 0F;
	}

	@Override
	public boolean isSneaking() {

		return isSneaking;
	}

	@Override
	public void onUpdate() {

		ItemStack itemstack = previousItem;
		//TODO verify it's ok to use just main hand for held item consideration here
		ItemStack itemstack1 = getHeldItemMainhand();

		if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
			if (itemstack != null) {
				getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			}
			if (itemstack1 != null) {
				getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			}
			myName = "[CoFH]" + (itemstack1 != null ? " using " + itemstack1.getDisplayName() : "");
		}
		previousItem = itemstack1 == null ? null : itemstack1.copy();
		interactionManager.updateBlockRemoving();

		if (activeItemStack != null) {
			// tickItemInUse(itemstack);
		}
	}

	public void tickItemInUse(ItemStack updateItem) {

		if (updateItem != null && ItemHelper.itemsIdentical(previousItem, activeItemStack)) {

			activeItemStackUseCount = ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
			if (activeItemStackUseCount <= 0) {
				onItemUseFinish();
			} else {
				activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
				if (activeItemStackUseCount <= 25 && activeItemStackUseCount % 4 == 0) {
					updateItemUse(updateItem, 5);
				}
				if (--activeItemStackUseCount == 0 && !worldObj.isRemote) {
					onItemUseFinish();
				}
			}
		} else {
			resetActiveHand();
		}
	}

	@Override
	protected void updateItemUse(ItemStack par1ItemStack, int par2) {

		if (par1ItemStack.getItemUseAction() == EnumAction.DRINK) {
			this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (par1ItemStack.getItemUseAction() == EnumAction.EAT) {
			this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(getName());
	}

	@Override
	public float getEyeHeight() {

		return 1.1F;
	}

	@Nullable
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {

		return new ItemStack(Items.DIAMOND_CHESTPLATE);
	}

	@Override
	public void addChatMessage(ITextComponent chatmessagecomponent) {

	}

	@Override
	public void addChatComponentMessage(ITextComponent chatmessagecomponent) {

	}

}
