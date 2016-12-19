package cofh.core.block;

import codechicken.lib.util.BlockUtils;
import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.core.CoFHProps;
import cofh.core.RegistrySocial;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTile;
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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class TileCoFHBase extends TileEntity {

    @Override
    public void onChunkUnload() {

        if (!tileEntityInvalid) {
            invalidate(); // this isn't called when a tile unloads. guard incase it is in the future
        }
    }

    public abstract String getName();

    public abstract int getType();

    public void blockBroken() {

    }

    public void blockDismantled() {

        blockBroken();
    }

    public void blockPlaced() {

    }

    public void markChunkDirty() {

        worldObj.markChunkDirty(this.getPos(), this);
    }

    public void callNeighborBlockChange() {
        worldObj.notifyNeighborsOfStateChange(getPos(), getBlockType());
    }

    public void callNeighborTileChange() {
        worldObj.updateComparatorOutputLevel(this.getPos(), this.getBlockType());
    }

    public void onNeighborBlockChange() {

    }

    public void onNeighborTileChange(BlockPos pos) {

    }

    //TODO Side
    public int getComparatorInput(/*int side*/) {

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

    public boolean isUseable(EntityPlayer player) {

        return BlockUtils.isEntityInRange(getPos(), player, 64) && worldObj.getTileEntity(pos) == this;
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
    //@Override//TODO

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

    public void sendDescPacket() {

        PacketHandler.sendToAllAround(getPacket(), this);
    }

    protected void updateLighting() {
        int light2 = worldObj.getLightFor(EnumSkyBlock.BLOCK, getPos()), light1 = getLightValue();
        if (light1 != light2 && worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos())) {
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(pos, state, state, 3);//TODO might me markBlocksForUpdate.
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

    public void sendGuiNetworkData(Container container, IContainerListener player) {

    }

}
