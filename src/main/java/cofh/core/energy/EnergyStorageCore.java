package cofh.core.energy;

import cofh.redstoneflux.impl.EnergyStorage;

public class EnergyStorageCore extends EnergyStorage {

	public EnergyStorageCore(int capacity) {

		this(capacity, capacity, capacity);
	}

	public EnergyStorageCore(int capacity, int maxTransfer) {

		this(capacity, maxTransfer, maxTransfer);
	}

	public EnergyStorageCore(int capacity, int maxReceive, int maxExtract) {

		super(capacity, maxReceive, maxExtract);
	}

	public int getSpace() {

		return energy >= capacity ? 0 : capacity - energy;
	}

	public int receiveEnergyNoLimit(int maxReceive, boolean simulate) {

		int energyReceived = Math.min(capacity - energy, maxReceive);

		if (!simulate) {
			energy += energyReceived;
		}
		return energyReceived;
	}

	public int extractEnergyNoLimit(int maxExtract, boolean simulate) {

		int energyExtracted = Math.min(energy, maxExtract);

		if (!simulate) {
			energy -= energyExtracted;
		}
		return energyExtracted;
	}

}
