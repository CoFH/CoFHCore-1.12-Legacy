package cofh.core.block;

import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTile;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.CoreUtils;
import cofh.core.util.RegistrySocial;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileCoFHBase extends TileEntity {

	private static final int METADATA_NOT_APPLICABLE = 0;
	protected boolean inWorld = false;

	public abstract String getName();

	public void blockBroken() {

	}

	public void blockDismantled() {

		blockBroken();
	}

	public void blockPlaced() {

	}

	public void markChunkDirty() {

		worldObj.markChunkDirty(this.pos, this);
	}

	public void callNeighborStateChange() {

		worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
	}

	public void callNeighborTileChange() {

		worldObj.updateComparatorOutputLevel(pos, getBlockType());
	}

	public void onNeighborBlockChange() {

	}

	public void onNeighborTileChange(BlockPos pos) {

	}

	public int getComparatorInputOverride() {

		return 0;
	}

	public int getLightValue() {

		return 0;
	}

	public boolean canPlayerAccess(EntityPlayer player) {

		if (!(this instanceof ISecurable)) {
			return true;
		}
		AccessMode access = ((ISecurable) this).getAccess();
		String name = player.getName();
		if (access.isPublic() || (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name))) {
			return true;
		}
		GameProfile profile = ((ISecurable) this).getOwner();
		UUID ownerID = profile.getId();
		if (SecurityHelper.isDefaultUUID(ownerID)) {
			return true;
		}
		UUID otherID = SecurityHelper.getID(player);
		if (ownerID.equals(otherID)) {
			return true;
		}
		return access.isRestricted() && RegistrySocial.playerHasAccess(name, profile);
	}

	public boolean canPlayerDismantle(EntityPlayer player) {

		return true;
	}

	public boolean isUsable(EntityPlayer player) {

		return player.getDistanceSq(pos) <= 64D && worldObj.getTileEntity(pos) == this;
	}

	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return false;
	}

	/* BLOCK STATE */
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return state;
	}

	/* COFH ASM */
	public void cofh_validate() {

		inWorld = true;
	}

	public void cofh_invalidate() {

		inWorld = false;
	}

	/* TIME CHECKS */
	protected final boolean timeCheck() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT == 0;
	}

	protected final boolean timeCheckHalf() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0;
	}

	protected final boolean timeCheckQuarter() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_QUARTER == 0;
	}

	protected final boolean timeCheckEighth() {

		return worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_EIGHTH == 0;
	}

	/* NETWORK METHODS */
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		return new SPacketUpdateTileEntity(this.pos, METADATA_NOT_APPLICABLE, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {

		//returning clean nbt from here instead of the one from TileEntity that's full of data we don't need
		return new NBTTagCompound();
	}

	public PacketCoFHBase getPacket() {

		return new PacketTile(this);
	}

	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = PacketTileInfo.newPacket(this);
		payload.addByte(TilePacketID.GUI.ordinal());
		return payload;
	}

	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = PacketTileInfo.newPacket(this);
		payload.addByte(TilePacketID.FLUID.ordinal());
		return payload;
	}

	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = PacketTileInfo.newPacket(this);
		payload.addByte(TilePacketID.MODE.ordinal());
		return payload;
	}

	protected void handleGuiPacket(PacketCoFHBase payload) {

	}

	protected void handleFluidPacket(PacketCoFHBase payload) {

	}

	protected void handleModePacket(PacketCoFHBase payload) {

		markChunkDirty();
	}

	public void sendDescPacket() {

		PacketHandler.sendToAllAround(getPacket(), this);
	}

	public void sendFluidPacket() {

		PacketHandler.sendToDimension(getFluidPacket(), worldObj.provider.getDimension());
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getModePacket());
		}
	}

	public void sendUpdatePacket(Side side) {

		if (worldObj == null) {
			return;
		}
		if (side == Side.CLIENT && ServerHelper.isServerWorld(worldObj)) {
			PacketHandler.sendToAllAround(getPacket(), this);
		} else if (side == Side.SERVER && ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getPacket());
		}
	}

	protected void updateLighting() {

		// TODO: Is this necessary now?
		//		int light2 = worldObj.getSavedLightValue(EnumSkyBlock.BLOCK, pos), light1 = getLightValue();
		//		if (light1 != light2 && worldObj.updateLightByType(EnumSkyBlock.BLOCK, pos)) {
		//			worldObj.markBlockForUpdate(pos);
		//		}
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

	public boolean hasGui() {

		return false;
	}

	public boolean openGui(EntityPlayer player) {

		return false;
	}

	public void receiveGuiNetworkData(int id, int data) {

	}

	public void sendGuiNetworkData(Container container, IContainerListener containerListener) {

	}

	/* PACKET ENUM */
	public static enum TilePacketID {
		GUI, FLUID, MODE
	}

}
