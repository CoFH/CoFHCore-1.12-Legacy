package cofh.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import cofh.api.tileentity.ISecureTile;
import cofh.api.tileentity.ISecureTile.AccessMode;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTilePacket;
import cofh.network.PacketHandler;
import cofh.social.RegistryFriends;
import cofh.util.CoreUtils;
import cofh.util.ServerHelper;
import cpw.mods.fml.relauncher.Side;

public abstract class TileCoFHBase extends TileEntity {

	public void blockBroken() {

	}

	public void blockDismantled() {

		blockBroken();
	}

	public boolean canPlayerAccess(String name) {

		if (!(this instanceof ISecureTile)) {
			return false;
		}
		AccessMode access = ((ISecureTile) this).getAccess();
		String owner = ((ISecureTile) this).getOwnerName();

		return access.isPublic()
				|| (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name))
				|| owner.equals(CoFHProps.DEFAULT_OWNER) || owner.equals(name)
				|| access.isRestricted()
				&& RegistryFriends.playerHasAccess(name, owner);
	}

	public boolean canPlayerDismantle(EntityPlayer player) {

		return true;
	}

	public int getComparatorInput(int side) {

		return 0;
	}

	public int getLightValue() {

		return 0;
	}

	public abstract String getName();

	public abstract int getType();

	public void callNeighborTileChange() {

		if (getBlockType() != null) {
			worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord,
					this.getBlockType());
		}
	}

	public boolean isUseable(EntityPlayer player) {

		return player.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
	}

	public void onNeighborBlockChange() {

	}

	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

	}

	public boolean onWrench(EntityPlayer player, int hitSide) {

		return false;
	}

	public boolean openGui(EntityPlayer player) {

		return false;
	}

	protected boolean timeCheck() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT == 0;
	}

	protected boolean timeCheckEighth() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_EIGHTH == 0;
	}

	/* NETWORK METHODS */
	@Override
	public Packet getDescriptionPacket() {

		return PacketHandler.toMcPacket(getPacket());
	}

	public CoFHPacket getPacket() {

		return new CoFHTilePacket(this);
	}

	public void sendDescPacket() {

		PacketHandler.sendToAllAround(getPacket(), this);
	}

	public void sendUpdatePacket(Side side) {

		if (worldObj == null) {
			return;
		}
		if (side == Side.CLIENT && ServerHelper.isServerWorld(worldObj)) {
			PacketHandler.sendToAllAround(getPacket(), this);
			worldObj.func_147451_t(xCoord, yCoord, zCoord); // ???
		} else if (side == Side.SERVER && ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getPacket());
		}
	}

	/* GUI METHODS */
	public int getInvSlotCount() {

		return 0;
	}

	public void receiveGuiNetworkData(int i, int j) {

	}

	public void sendGuiNetworkData(Container container, ICrafting player) {

	}

}
