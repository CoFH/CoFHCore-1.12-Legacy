package cofh.block;

import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.core.CoFHProps;
import cofh.network.PacketCoFHBase;
import cofh.network.PacketHandler;
import cofh.network.PacketTile;
import cofh.util.CoreUtils;
import cofh.util.ServerHelper;
import cofh.util.SocialRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public abstract class TileCoFHBase extends TileEntity {

	public abstract String getName();

	public abstract int getType();

	public void blockBroken() {

	}

	public void blockDismantled() {

		blockBroken();
	}

	public void markChunkDirty() {

		worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
	}

	public void callNeighborBlockChange() {

		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	public void callNeighborTileChange() {

		worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
	}

	public void onNeighborBlockChange() {

	}

	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

	}

	public int getComparatorInput(int side) {

		return 0;
	}

	public int getLightValue() {

		return 0;
	}

	public boolean canPlayerAccess(String name) {

		if (!(this instanceof ISecurable)) {
			return true;
		}
		AccessMode access = ((ISecurable) this).getAccess();
		String owner = ((ISecurable) this).getOwnerName();

		return access.isPublic() || (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name)) || owner.equals(CoFHProps.DEFAULT_OWNER) || owner.equals(name)
				|| access.isRestricted() && SocialRegistry.playerHasAccess(name, owner);
	}

	public boolean canPlayerDismantle(EntityPlayer player) {

		return true;
	}

	public boolean isUseable(EntityPlayer player) {

		return player.getDistanceSq(xCoord, yCoord, zCoord) <= 64D && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
	}

	public boolean onWrench(EntityPlayer player, int hitSide) {

		return false;
	}

	protected final boolean timeCheck() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT == 0;
	}

	protected final boolean timeCheckEighth() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_EIGHTH == 0;
	}

	/* NETWORK METHODS */
	@Override
	public Packet getDescriptionPacket() {

		return PacketHandler.toMCPacket(getPacket());
	}

	public PacketCoFHBase getPacket() {

		return new PacketTile(this);
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else if (side == Side.SERVER && ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getPacket());
		}
	}

	/* GUI METHODS */
	public Object getGuiClient(InventoryPlayer inventory) {

		return null;
	}

	public Object getGuiServer(InventoryPlayer inventory) {

		return null;
	}

	public int getInvSlotCount() {

		return 0;
	}

	public boolean openGui(EntityPlayer player) {

		return false;
	}

	public void receiveGuiNetworkData(int i, int j) {

	}

	public void sendGuiNetworkData(Container container, ICrafting player) {

	}

}
