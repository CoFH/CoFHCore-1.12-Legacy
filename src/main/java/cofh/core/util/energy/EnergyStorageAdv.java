package cofh.core.util.energy;

import cofh.api.energy.EnergyStorage;

public class EnergyStorageAdv extends EnergyStorage {

	public EnergyStorageAdv(int capacity) {

		this(capacity, capacity, capacity);
	}

	public EnergyStorageAdv(int capacity, int maxTransfer) {

		this(capacity, maxTransfer, maxTransfer);
	}

	public EnergyStorageAdv(int capacity, int maxReceive, int maxExtract) {

		super(capacity, maxReceive, maxExtract);
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
