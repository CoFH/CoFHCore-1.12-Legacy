package cofh.core.block;

import cofh.api.tileentity.ISecurable;
import cofh.core.init.CoreProps;
import cofh.core.util.RegistrySocial;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTile;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class TileCore extends TileEntity {

	public abstract String getTileName();

	public abstract int getType();

	public void blockBroken() {

	}

	public void blockDismantled() {

		blockBroken();
	}

	public void blockPlaced() {

	}

	public void callBlockUpdate() {

		IBlockState state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	public void callNeighborStateChange() {

		worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
	}

	public void callNeighborTileChange() {

		worldObj.updateComparatorOutputLevel(pos, getBlockType());
	}

	public void markChunkDirty() {

		worldObj.markChunkDirty(this.pos, this);
	}

	@Override
	public void onChunkUnload() {

		if (!tileEntityInvalid) {
			invalidate();
		}
	}

	public void onNeighborBlockChange() {

	}

	public void onNeighborTileChange(BlockPos pos) {

	}

	public boolean canPlayerAccess(EntityPlayer player) {

		if (!(this instanceof ISecurable)) {
			return true;
		}
		ISecurable.AccessMode access = ((ISecurable) this).getAccess();
		String name = player.getName();
		if (access.isPublic() || (CoreProps.enableOpSecureAccess && CoreUtils.isOp(name))) {
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
		return access.isFriendsOnly() && RegistrySocial.playerHasAccess(name, profile);
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

	public int getComparatorInputOverride() {

		return 0;
	}

	public int getLightValue() {

		return 0;
	}

	/* BLOCK STATE */
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return state;
	}

	/* TIME CHECKS */
	protected final boolean timeCheck() {

		return worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT == 0;
	}

	protected final boolean timeCheckHalf() {

		return worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF == 0;
	}

	protected final boolean timeCheckQuarter() {

		return worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT_QUARTER == 0;
	}

	protected final boolean timeCheckEighth() {

		return worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT_EIGHTH == 0;
	}

	/* NETWORK METHODS */
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		return PacketHandler.toTilePacket(getPacket(), getPos());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		PacketHandler.handleNBTPacket(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {

		return PacketHandler.toNBTTag(getPacket(), super.getUpdateTag());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {

		PacketHandler.handleNBTPacket(tag);
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

		// TODO: Still necessary?
		int light2 = worldObj.getLightFor(EnumSkyBlock.BLOCK, getPos()), light1 = getLightValue();
		if (light1 != light2 && worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos())) {
			IBlockState state = worldObj.getBlockState(getPos());
			worldObj.notifyBlockUpdate(pos, state, state, 3);
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

	public boolean hasGui() {

		return false;
	}

	public boolean openGui(EntityPlayer player) {

		return false;
	}

	public void receiveGuiNetworkData(int id, int data) {

	}

	public void sendGuiNetworkData(Container container, IContainerListener player) {

	}

	/* PACKET ENUM */
	public enum TilePacketID {
		GUI, FLUID, MODE
	}

}
