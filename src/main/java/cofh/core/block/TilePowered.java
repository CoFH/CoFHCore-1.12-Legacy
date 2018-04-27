package cofh.core.block;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TilePowered extends TileReconfigurable implements IEnergyInfo, IEnergyReceiver {

	protected EnergyStorage energyStorage = new EnergyStorage(0);

	public boolean smallStorage() {

		return false;
	}

	protected boolean hasEnergy(int energy) {

		return energyStorage.getEnergyStored() >= energy;
	}

	protected int getEnergySpace() {

		return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
	}

	protected boolean hasChargeSlot() {

		return true;
	}

	protected void chargeEnergy() {

		if (!hasChargeSlot()) {
			return;
		}
		int chargeSlot = getChargeSlot();

		if (EnergyHelper.isEnergyContainerItem(inventory[chargeSlot])) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(((IEnergyContainerItem) inventory[chargeSlot].getItem()).extractEnergy(inventory[chargeSlot], energyRequest, false), false);
			if (inventory[chargeSlot].getCount() <= 0) {
				inventory[chargeSlot] = ItemStack.EMPTY;
			}
		} else if (EnergyHelper.isEnergyHandler(inventory[chargeSlot])) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(inventory[chargeSlot].getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if (inventory[chargeSlot].getCount() <= 0) {
				inventory[chargeSlot] = ItemStack.EMPTY;
			}
		}
	}

	public int getChargeSlot() {

		return inventory.length - 1;
	}

	public final void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	/* GUI METHODS */
	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		energyStorage.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(isActive);
		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		isActive = payload.getBool();
		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		energyStorage.setEnergyStored(payload.getInt());
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return energyStorage.getMaxEnergyStored() > 0;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return TilePowered.this.receiveEnergy(from, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return 0;
				}

				@Override
				public int getEnergyStored() {

					return TilePowered.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return TilePowered.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {

					return false;
				}

				@Override
				public boolean canReceive() {

					return true;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
