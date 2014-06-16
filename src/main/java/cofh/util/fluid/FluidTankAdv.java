package cofh.util.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Reference implementation of {@link IFluidTank}. Use/extend this or implement your own.
 * 
 * @author King Lemming, cpw (LiquidTank)
 * 
 */
public class FluidTankAdv implements IFluidTank {

	protected FluidStack fluid;
	protected int capacity;
	protected boolean locked;

	public FluidTankAdv(int capacity) {

		this(null, capacity);
	}

	public FluidTankAdv(FluidStack stack, int capacity) {

		this.fluid = stack;
		this.capacity = capacity;
	}

	public FluidTankAdv(Fluid fluid, int amount, int capacity) {

		this(new FluidStack(fluid, amount), capacity);
	}

	public FluidTankAdv readFromNBT(NBTTagCompound nbt) {

		if (!nbt.hasKey("Empty")) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
			locked = nbt.getBoolean("Lock") && fluid != null;

			if (fluid != null) {
				setFluid(fluid);
			}
		}
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

	public void setLock(Fluid fluid) {

		locked = fluid != null;
		if (locked) {
			this.fluid = new FluidStack(fluid, 0);
		}
	}

	public void setFluid(FluidStack fluid) {

		this.fluid = fluid;
	}

	public void setCapacity(int capacity) {

		this.capacity = capacity;
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

	public int getSpace() {

		if (fluid == null) {
			return capacity;
		}
		return fluid.amount >= capacity ? 0 : capacity - fluid.amount;
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

		if (fluid == null) {
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
