package cofh.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * The purpose of this class is to show how to use and provide an interface for Thermal Expansion's IMC Recipe manipulation.
 *
 * It is not the only way to add recipes to TE, but it is BY FAR the safest. Please use it.
 *
 * @author King Lemming
 */
public class ThermalExpansionHelper {

	private ThermalExpansionHelper() {

	}

	/**
	 * MACHINES
	 */

	/* FURNACE */
	public static void addFurnaceRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));
		FMLInterModComms.sendMessage("thermalexpansion", "AddFurnaceRecipe", toSend);
	}

	public static void removeFurnaceRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveFurnaceRecipe", toSend);
	}

	/* PULVERIZER */
	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		addPulverizerRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		addPulverizerRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", secondaryChance);
		}
		FMLInterModComms.sendMessage("thermalexpansion", "AddPulverizerRecipe", toSend);
	}

	public static void removePulverizerRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemovePulverizerRecipe", toSend);
	}

	/* SAWMILL */
	public static void addSawmillRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		addSawmillRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	public static void addSawmillRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		addSawmillRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static void addSawmillRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", secondaryChance);
		}

		FMLInterModComms.sendMessage("thermalexpansion", "AddSawmillRecipe", toSend);
	}

	public static void removeSawmillRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveSawmillRecipe", toSend);
	}

	/* SMELTER */
	public static void addSmelterRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		addSmelterRecipe(energy, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0);
	}

	public static void addSmelterRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		addSmelterRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static void addSmelterRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || primaryOutput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("primaryInput", new NBTTagCompound());
		toSend.setTag("secondaryInput", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag("primaryInput"));
		secondaryInput.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", secondaryChance);
		}
		FMLInterModComms.sendMessage("thermalexpansion", "AddSmelterRecipe", toSend);
	}

	public static void removeSmelterRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("primaryInput", new NBTTagCompound());
		toSend.setTag("secondaryInput", new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag("primaryInput"));
		secondaryInput.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveSmelterRecipe", toSend);
	}

	/* INSOLATOR */
	public static void addInsolatorRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		addInsolatorRecipe(energy, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0);
	}

	public static void addInsolatorRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		addInsolatorRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static void addInsolatorRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || primaryOutput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("primaryInput", new NBTTagCompound());
		toSend.setTag("secondaryInput", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag("primaryInput"));
		secondaryInput.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", secondaryChance);
		}
		FMLInterModComms.sendMessage("thermalexpansion", "AddInsolatorRecipe", toSend);
	}

	public static void removeInsolatorRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("primaryInput", new NBTTagCompound());
		toSend.setTag("secondaryInput", new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag("primaryInput"));
		secondaryInput.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveInsolatorRecipe", toSend);
	}

	/* CRUCIBLE */
	public static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output) {

		if (input.isEmpty() || output == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));

		FMLInterModComms.sendMessage("thermalexpansion", "AddCrucibleRecipe", toSend);
	}

	public static void removeCrucibleRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveCrucibleRecipe", toSend);
	}

	/* REFINERY */
	public static void addRefineryRecipe(int energy, FluidStack input, FluidStack output, ItemStack outputItem) {

		if (input == null || output == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());

		if (!outputItem.isEmpty()) {
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			outputItem.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
		}
		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));

		FMLInterModComms.sendMessage("thermalexpansion", "AddRefineryRecipe", toSend);
	}

	public static void removeRefineryRecipe(FluidStack input) {

		if (input == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveRefineryRecipe", toSend);
	}

	/* TRANSPOSER */
	public static void addTransposerFill(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", reversible);
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));

		FMLInterModComms.sendMessage("thermalexpansion", "AddTransposerFillRecipe", toSend);
	}

	public static void addTransposerExtract(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", reversible);
		toSend.setInteger("chance", chance);
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));

		FMLInterModComms.sendMessage("thermalexpansion", "AddTransposerExtractRecipe", toSend);
	}

	public static void removeTransposerFill(ItemStack input, FluidStack fluid) {

		if (input.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveTransposerFillRecipe", toSend);
	}

	public static void removeTransposerExtract(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveTransposerExtractRecipe", toSend);
	}

	/* CHARGER */
	public static void addChargerRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));
		FMLInterModComms.sendMessage("thermalexpansion", "AddChargerRecipe", toSend);
	}

	public static void removeChargerRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("input", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		FMLInterModComms.sendMessage("thermalexpansion", "RemoveChargerRecipe", toSend);
	}

	/**
	 * DYNAMOS
	 */

	/* MAGMATIC */
	public static void addMagmaticFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString("fluidName", fluidName);
		toSend.setInteger("energy", energy);

		FMLInterModComms.sendMessage("thermalexpansion", "MagmaticFuel", toSend);
	}

	/* COMPRESSION */
	public static void addCompressionFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString("fluidName", fluidName);
		toSend.setInteger("energy", energy);

		FMLInterModComms.sendMessage("thermalexpansion", "CompressionFuel", toSend);
	}

	/* REACTANT */
	public static void addReactantFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString("fluidName", fluidName);
		toSend.setInteger("energy", energy);

		FMLInterModComms.sendMessage("thermalexpansion", "ReactantFuel", toSend);
	}

	/* COOLANT */
	public static void addCoolant(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString("fluidName", fluidName);
		toSend.setInteger("energy", energy);

		FMLInterModComms.sendMessage("thermalexpansion", "Coolant", toSend);
	}

}
