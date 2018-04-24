package cofh.core.item;

import cofh.api.item.IColorableItem;
import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class ItemMultiRF extends ItemMulti implements IColorableItem, IEnchantableItem, IEnergyContainerItem, IMultiModeItem {

	public ItemMultiRF(String modName) {

		super(modName);
	}

	public ItemStack setDefaultTag(ItemStack stack, int energy) {

		return EnergyHelper.setDefaultEnergyTag(stack, energy);
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || getEnergyStored(oldStack) > 0 != getEnergyStored(newStack) > 0);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return !isCreative(stack) && getEnergyStored(stack) > 0;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {

		return CoreProps.RGB_DURABILITY_FLUX;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack, 0);
		}
		return 1.0D - ((double) stack.getTagCompound().getInteger(CoreProps.ENERGY) / (double) getMaxEnergyStored(stack));
	}

	/* HELPERS */
	protected abstract int getCapacity(ItemStack stack);

	protected abstract int getReceive(ItemStack stack);

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return !isCreative(stack) && enchantment == CoreEnchantments.holding;
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container, 0);
		}
		int stored = Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
		int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, getReceive(container)));

		if (!simulate && !isCreative(container)) {
			stored += receive;
			container.getTagCompound().setInteger(CoreProps.ENERGY, stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container, 0);
		}
		if (isCreative(container)) {
			return maxExtract;
		}
		int stored = Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger(CoreProps.ENERGY, stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container, 0);
		}
		return Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return getCapacity(container);
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

	public static final int CAPACITY_MIN = 10000;
	public static final int CAPACITY_MAX = 10000000;

	public static final int XFER_MIN = 100;
	public static final int XFER_MAX = 1000000;

}
