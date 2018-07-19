package cofh.core.block;

import cofh.api.core.IPortableData;
import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.GuiHandler;
import cofh.core.network.ITilePacketHandler;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileNameable extends TileCore implements ITilePacketHandler, IPortableData {

	public String customName = "";

	public void setCustomName(String name) {

		if (!name.isEmpty()) {
			customName = name;
		}
	}

	/* BASE METHODS */
	protected abstract Object getMod();

	protected abstract String getModVersion();

	protected abstract String getTileName();

	public int getType() {

		return 0;
	}

	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	/* GUI METHODS */
	public int getScaledProgress(int scale) {

		return 0;
	}

	public int getScaledSpeed(int scale) {

		return 0;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (hasGui()) {
			player.openGui(getMod(), GuiHandler.TILE_ID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return hasGui();
	}

	@Override
	public boolean openConfigGui(EntityPlayer player) {

		if (hasConfigGui()) {
			player.openGui(getMod(), GuiHandler.TILE_CONFIG_ID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return hasConfigGui();
	}

	@Override
	public void sendGuiNetworkData(Container container, IContainerListener listener) {

		if (listener instanceof EntityPlayer) {
			PacketBase guiPacket = getGuiPacket();
			if (guiPacket != null) {
				PacketHandler.sendTo(guiPacket, (EntityPlayer) listener);
			}
		}
	}

	public FluidTankCore getTank() {

		return null;
	}

	public FluidStack getTankFluid() {

		return null;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		if (nbt.hasKey("Name")) {
			customName = nbt.getString("Name");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", getModVersion());

		if (!customName.isEmpty()) {
			nbt.setString("Name", customName);
		}
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addString(customName);

		return payload;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		customName = payload.getString();
		world.checkLight(pos);
	}

	/* IPortableData */
	@Override
	public String getDataType() {

		return getTileName();
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}
		if (readPortableTagInternal(player, tag)) {
			markChunkDirty();
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}
		writePortableTagInternal(player, tag);
	}

	//	/* PLUGIN METHODS */
	//	public void provideInfo(ProbeMode mode, IProbeInfo info, EnumFacing facing, EntityPlayer player) {
	//
	//	}

	/* SLOT CONFIG */

}
