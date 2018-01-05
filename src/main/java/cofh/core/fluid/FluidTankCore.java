package cofh.core.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Reference implementation of {@link IFluidTank}. Use/extend this or implement your own.
 *
 * @author King Lemming, cpw (LiquidTank)
 */
public class FluidTankCore implements IFluidTank {

	protected FluidStack fluid;
	protected int capacity;
	protected boolean locked;

	public FluidTankCore(int capacity) {

		this(null, capacity);
	}

	public FluidTankCore(FluidStack stack, int capacity) {

		this.fluid = stack;
		this.capacity = capacity;
	}

	public FluidTankCore(Fluid fluid, int amount, int capacity) {

		this(new FluidStack(fluid, amount), capacity);
	}

	public FluidTankCore readFromNBT(NBTTagCompound nbt) {

		FluidStack fluid = null;
		locked = false;
		if (!nbt.hasKey("Empty")) {
			fluid = FluidStack.loadFluidStackFromNBT(nbt);
			locked = nbt.getBoolean("Lock") && fluid != null;
		}
		setFluid(fluid);
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		if (fluid != null) {
			fluid.writeToNBT(nbt);
			nbt.setBoolean("Lock", locked);
		} else {
			nbt.setString("Empty", "");
		}
		return nbt;
	}

	public FluidTankCore setLock(Fluid fluid) {

		locked = fluid != null;
		if (locked) {
			if (this.fluid == null || !this.fluid.getFluid().equals(fluid)) {
				this.fluid = new FluidStack(fluid, 0);
			}
		}
		return this;
	}

	public void setLocked(boolean lock) {

		if (lock) {
			setLocked();
		} else {
			clearLocked();
		}
	}

	public void setLocked() {

		if (locked || this.fluid == null) {
			return;
		}
		locked = true;
	}

	public void clearLocked() {

		locked = false;
		if (this.getFluidAmount() <= 0) {
			this.fluid = null;
		}
	}

	public void setFluid(FluidStack fluid) {

		this.fluid = fluid;
	}

	public void setCapacity(int capacity) {

		this.capacity = capacity;
	}

	/**
	 * Only ever call this on a LOCKED tank. Be really sure you know WTF you are doing.
	 */
	public void modifyFluidStored(int amount) {

		if (!locked) {
			return;
		}
		this.fluid.amount += amount;

		if (this.fluid.amount > capacity) {
			this.fluid.amount = capacity;
		} else if (this.fluid.amount < 0) {
			this.fluid.amount = 0;
		}
	}

	public boolean isLocked() {

		return locked;
	}

	public int getSpace() {

		if (fluid == null) {
			return capacity;
		}
		return fluid.amount >= capacity ? 0 : capacity - fluid.amount;
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {

		if (resource == null || !resource.isFluidEqual(fluid)) {
			return null;
		}
		return drain(resource.amount, doDrain);
	}

	/* IFluidTank */
	@Override
	public FluidStack getFluid() {

		return fluid;
	}

	@Override
	public int getFluidAmount() {

		if (fluid == null) {
			return 0;
		}
		return fluid.amount;
	}

	@Override
	public int getCapacity() {

		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {

		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (resource == null) {
			return 0;
		}
		if (!doFill) {
			if (fluid == null) {
				return Math.min(capacity, resource.amount);
			}
			if (!fluid.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - fluid.amount, resource.amount);
		}
		if (fluid == null) {
			fluid = new FluidStack(resource, Math.min(capacity, resource.amount));
			return fluid.amount;
		}
		if (!fluid.isFluidEqual(resource)) {
			return 0;
		}
		int filled = capacity - fluid.amount;

		if (resource.amount < filled) {
			fluid.amount += resource.amount;
			filled = resource.amount;
		} else {
			fluid.amount = capacity;
		}
		return filled;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {

		if (fluid == null || locked && fluid.amount <= 0) {
			return null;
		}
		int drained = maxDrain;
		if (fluid.amount < drained) {
			drained = fluid.amount;
		}
		FluidStack stack = new FluidStack(fluid, drained);
		if (doDrain) {
			fluid.amount -= drained;
			if (fluid.amount <= 0) {
				if (locked) {
					fluid.amount = 0;
				} else {
					fluid = null;
				}
			}
		}
		return stack;
	}

}
