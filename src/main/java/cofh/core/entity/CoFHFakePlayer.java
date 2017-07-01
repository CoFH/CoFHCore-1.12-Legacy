package cofh.core.entity;

import cofh.core.util.helpers.ItemHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class CoFHFakePlayer extends FakePlayer {

	private static GameProfile NAME = new GameProfile(UUID.fromString("5ae51d0b-e8bc-5a02-09f4-b5dbb05963da"), "[CoFH]");

	public boolean isSneaking = false;
	public ItemStack previousItem = ItemStack.EMPTY;
	public String myName = "[CoFH]";

	public CoFHFakePlayer(WorldServer world) {

		super(world, NAME);
		connection = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		this.addedToChunk = false;
	}

	public static boolean isBlockBreakable(CoFHFakePlayer myFakePlayer, World worldObj, BlockPos pos) {

		IBlockState state = worldObj.getBlockState(pos);

		if (state.getBlock().isAir(state, worldObj, pos)) {
			return false;
		}
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
		ItemStack itemstack1 = getHeldItem(EnumHand.MAIN_HAND);

		if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
			if (!itemstack.isEmpty()) {
				getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			}
			if (!itemstack1.isEmpty()) {
				getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			}
			myName = "[CoFH]" + (!itemstack1.isEmpty() ? " using " + itemstack1.getDisplayName() : "");
		}
		previousItem = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
		interactionManager.updateBlockRemoving();

		//This was commented out beforehand fyi.
		//if (itemInUse != null) {
		// tickItemInUse(itemstack);
		//}
	}

	public void tickItemInUse(ItemStack updateItem) {

		if (!updateItem.isEmpty() && ItemHelper.itemsEqualWithMetadata(previousItem, activeItemStack)) {

			activeItemStackUseCount = ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
			if (activeItemStackUseCount <= 0) {
				onItemUseFinish();
			} else {
				activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
				if (activeItemStackUseCount <= 25 && activeItemStackUseCount % 4 == 0) {
					updateItemUse(updateItem, 5);
				}
				if (--activeItemStackUseCount == 0 && !world.isRemote) {
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
			this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
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

		return getDefaultEyeHeight() + eyeHeight;
	}

	@Override
	public float getDefaultEyeHeight() {

		return 1.1F;
	}

	//@Override TODO
	public ItemStack getCurrentArmor(int par1) {

		return new ItemStack(Items.DIAMOND_CHESTPLATE);
	}

}
