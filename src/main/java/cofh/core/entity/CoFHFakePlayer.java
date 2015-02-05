package cofh.core.entity;

import cofh.lib.util.helpers.ItemHelper;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

public class CoFHFakePlayer extends FakePlayer {

	private static GameProfile NAME = new GameProfile(UUID.fromString("5ae51d0b-e8bc-5a02-09f4-b5dbb05963da"), "[CoFH]");

	public boolean isSneaking = false;
	public ItemStack previousItem = null;
	public String myName = "[CoFH]";

	public CoFHFakePlayer(WorldServer world) {

		super(world, NAME);
		playerNetServerHandler = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		this.addedToChunk = false;
	}

	public static boolean isBlockBreakable(CoFHFakePlayer myFakePlayer, World worldObj, int x, int y, int z) {

		Block block = worldObj.getBlock(x, y, z);
		if (myFakePlayer == null) {
			return block.getBlockHardness(worldObj, x, y, z) > -1;
		} else {
			return block.getPlayerRelativeBlockHardness(myFakePlayer, worldObj, x, y, z) > -1;
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
		ItemStack itemstack1 = getHeldItem();

		if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
			if (itemstack != null) {
				getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
			}
			if (itemstack1 != null) {
				getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers());
			}
			myName = "[CoFH]" + (itemstack1 != null ? " using " + itemstack1.getDisplayName() : "");
		}
		previousItem = itemstack1 == null ? null : itemstack1.copy();
		theItemInWorldManager.updateBlockRemoving();

		if (itemInUse != null) {
			//tickItemInUse(itemstack);
		}
	}

	public void tickItemInUse(ItemStack updateItem) {

		if (updateItem != null && ItemHelper.itemsEqualWithMetadata(previousItem, itemInUse)) {

			itemInUseCount = ForgeEventFactory.onItemUseTick(this, itemInUse, itemInUseCount);
            if (itemInUseCount <= 0) {
                onItemUseFinish();
            } else {
				itemInUse.getItem().onUsingTick(itemInUse, this, itemInUseCount);
				if (itemInUseCount <= 25 && itemInUseCount % 4 == 0) {
					updateItemUse(updateItem, 5);
				}
				if (--itemInUseCount == 0 && !worldObj.isRemote) {
					onItemUseFinish();
				}
            }
		} else {
			clearItemInUse();
		}
	}

	@Override
	protected void updateItemUse(ItemStack par1ItemStack, int par2) {

		if (par1ItemStack.getItemUseAction() == EnumAction.drink) {
			this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (par1ItemStack.getItemUseAction() == EnumAction.eat) {
			this.playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Override
	public String getDisplayName() {

		return getCommandSenderName();
	}

	@Override
	public float getEyeHeight() {

		return 1.1F;
	}

	@Override
	public ItemStack getCurrentArmor(int par1) {

		return new ItemStack(Items.diamond_chestplate);
	}

	@Override
	public void addChatMessage(IChatComponent chatmessagecomponent) {

	}

	@Override
	public void addChatComponentMessage(IChatComponent chatmessagecomponent) {

	}

}
