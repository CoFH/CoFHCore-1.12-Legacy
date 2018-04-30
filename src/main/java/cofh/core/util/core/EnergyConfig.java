package cofh.core.util.core;

public class EnergyConfig {

	public int minPower = 2;
	public int maxPower = 20;
	public int maxEnergy = 20000;
	public int minPowerLevel = maxEnergy / 10;
	public int maxPowerLevel = 9 * maxEnergy / 10;
	public int energyRamp = maxPowerLevel / maxPower;

	public EnergyConfig() {

	}

	public EnergyConfig(EnergyConfig config) {

		this.minPower = config.minPower;
		this.maxPower = config.maxPower;
		this.maxEnergy = config.maxEnergy;
		this.minPowerLevel = config.minPowerLevel;
		this.maxPowerLevel = config.maxPowerLevel;
		this.energyRamp = config.energyRamp;
	}

	public EnergyConfig copy() {

		return new EnergyConfig(this);
	}

	public boolean setDefaultParams(int basePower) {

		maxPower = basePower;
		minPower = basePower / 10;
		maxEnergy = basePower * 1000;
		maxPowerLevel = 9 * maxEnergy / 10;
		minPowerLevel = maxEnergy / 10;
		energyRamp = maxPowerLevel / basePower;

		return true;
	}

	public boolean setDefaultParams(int basePower, boolean smallStorage) {

		if (!smallStorage) {
			return setDefaultParams(basePower);
		}
		maxPower = basePower;
		minPower = basePower;
		maxEnergy = basePower * 10;
		maxPowerLevel = maxPower;
		minPowerLevel = maxPower - 1;
		energyRamp = 1;

		return true;
	}

}
