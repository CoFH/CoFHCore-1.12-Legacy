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

	static final String MOD_ID = "thermalexpansion";

	static final String ENERGY = "energy";
	static final String FLUID_NAME = "fluidName";

	static final String INPUT = "input";
	static final String OUTPUT = "output";
	static final String PRIMARY_INPUT = "primaryInput";
	static final String SECONDARY_INPUT = "secondaryInput";
	static final String PRIMARY_OUTPUT = "primaryOutput";
	static final String SECONDARY_OUTPUT = "secondaryOutput";
	static final String SECONDARY_CHANCE = "secondaryChance";

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

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		FMLInterModComms.sendMessage(MOD_ID, "AddFurnaceRecipe", toSend);
	}

	public static void removeFurnaceRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveFurnaceRecipe", toSend);
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

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(PRIMARY_OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		primaryOutput.writeToNBT(toSend.getCompoundTag(PRIMARY_OUTPUT));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag(SECONDARY_OUTPUT, new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag(SECONDARY_OUTPUT));
			toSend.setInteger(SECONDARY_CHANCE, secondaryChance);
		}
		FMLInterModComms.sendMessage(MOD_ID, "AddPulverizerRecipe", toSend);
	}

	public static void removePulverizerRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemovePulverizerRecipe", toSend);
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

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(PRIMARY_OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		primaryOutput.writeToNBT(toSend.getCompoundTag(PRIMARY_OUTPUT));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag(SECONDARY_OUTPUT, new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag(SECONDARY_OUTPUT));
			toSend.setInteger(SECONDARY_CHANCE, secondaryChance);
		}

		FMLInterModComms.sendMessage(MOD_ID, "AddSawmillRecipe", toSend);
	}

	public static void removeSawmillRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveSawmillRecipe", toSend);
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

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(PRIMARY_INPUT, new NBTTagCompound());
		toSend.setTag(SECONDARY_INPUT, new NBTTagCompound());
		toSend.setTag(PRIMARY_OUTPUT, new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag(PRIMARY_INPUT));
		secondaryInput.writeToNBT(toSend.getCompoundTag(SECONDARY_INPUT));
		primaryOutput.writeToNBT(toSend.getCompoundTag(PRIMARY_OUTPUT));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag(SECONDARY_OUTPUT, new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag(SECONDARY_OUTPUT));
			toSend.setInteger(SECONDARY_CHANCE, secondaryChance);
		}
		FMLInterModComms.sendMessage(MOD_ID, "AddSmelterRecipe", toSend);
	}

	public static void removeSmelterRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(PRIMARY_INPUT, new NBTTagCompound());
		toSend.setTag(SECONDARY_INPUT, new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag(PRIMARY_INPUT));
		secondaryInput.writeToNBT(toSend.getCompoundTag(SECONDARY_INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveSmelterRecipe", toSend);
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

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(PRIMARY_INPUT, new NBTTagCompound());
		toSend.setTag(SECONDARY_INPUT, new NBTTagCompound());
		toSend.setTag(PRIMARY_OUTPUT, new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag(PRIMARY_INPUT));
		secondaryInput.writeToNBT(toSend.getCompoundTag(SECONDARY_INPUT));
		primaryOutput.writeToNBT(toSend.getCompoundTag(PRIMARY_OUTPUT));

		if (!secondaryOutput.isEmpty()) {
			toSend.setTag(SECONDARY_OUTPUT, new NBTTagCompound());
			secondaryOutput.writeToNBT(toSend.getCompoundTag(SECONDARY_OUTPUT));
			toSend.setInteger(SECONDARY_CHANCE, secondaryChance);
		}
		FMLInterModComms.sendMessage(MOD_ID, "AddInsolatorRecipe", toSend);
	}

	public static void removeInsolatorRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(PRIMARY_INPUT, new NBTTagCompound());
		toSend.setTag(SECONDARY_INPUT, new NBTTagCompound());

		primaryInput.writeToNBT(toSend.getCompoundTag(PRIMARY_INPUT));
		secondaryInput.writeToNBT(toSend.getCompoundTag(SECONDARY_INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveInsolatorRecipe", toSend);
	}

	/* COMPACTOR */
	public static void addCompactorPressRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		FMLInterModComms.sendMessage(MOD_ID, "AddCompactorPressRecipe", toSend);
	}

	public static void addCompactorStorageRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		FMLInterModComms.sendMessage(MOD_ID, "AddCompactorStorageRecipe", toSend);
	}

	public static void addCompactorMintRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		FMLInterModComms.sendMessage(MOD_ID, "AddCompactorMintRecipe", toSend);
	}

	public static void removeCompactorPressRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveCompactorPressRecipe", toSend);
	}

	public static void removeCompactorStorageRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveCompactorStorageRecipe", toSend);
	}

	public static void removeCompactorMintRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveCompactorMintRecipe", toSend);
	}

	/* CRUCIBLE */
	public static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output) {

		if (input.isEmpty() || output == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));

		FMLInterModComms.sendMessage(MOD_ID, "AddCrucibleRecipe", toSend);
	}

	public static void removeCrucibleRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveCrucibleRecipe", toSend);
	}

	/* REFINERY */
	public static void addRefineryRecipe(int energy, FluidStack input, FluidStack output, ItemStack outputItem) {

		if (input == null || output == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		if (!outputItem.isEmpty()) {
			toSend.setTag(SECONDARY_OUTPUT, new NBTTagCompound());
			outputItem.writeToNBT(toSend.getCompoundTag(SECONDARY_OUTPUT));
		}
		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));

		FMLInterModComms.sendMessage(MOD_ID, "AddRefineryRecipe", toSend);
	}

	public static void removeRefineryRecipe(FluidStack input) {

		if (input == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveRefineryRecipe", toSend);
	}

	/* TRANSPOSER */
	public static void addTransposerFill(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		toSend.setBoolean("reversible", reversible);
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));

		FMLInterModComms.sendMessage(MOD_ID, "AddTransposerFillRecipe", toSend);
	}

	public static void addTransposerExtract(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		toSend.setBoolean("reversible", reversible);
		toSend.setInteger("chance", chance);
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));

		FMLInterModComms.sendMessage(MOD_ID, "AddTransposerExtractRecipe", toSend);
	}

	public static void removeTransposerFill(ItemStack input, FluidStack fluid) {

		if (input.isEmpty() || fluid == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveTransposerFillRecipe", toSend);
	}

	public static void removeTransposerExtract(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveTransposerExtractRecipe", toSend);
	}

	/* CHARGER */
	public static void addChargerRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger(ENERGY, energy);
		toSend.setTag(INPUT, new NBTTagCompound());
		toSend.setTag(OUTPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		output.writeToNBT(toSend.getCompoundTag(OUTPUT));
		FMLInterModComms.sendMessage(MOD_ID, "AddChargerRecipe", toSend);
	}

	public static void removeChargerRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag(INPUT, new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag(INPUT));
		FMLInterModComms.sendMessage(MOD_ID, "RemoveChargerRecipe", toSend);
	}

	/**
	 * DYNAMOS
	 */

	/* MAGMATIC */
	public static void addMagmaticFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString(FLUID_NAME, fluidName);
		toSend.setInteger(ENERGY, energy);

		FMLInterModComms.sendMessage(MOD_ID, "MagmaticFuel", toSend);
	}

	/* COMPRESSION */
	public static void addCompressionFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString(FLUID_NAME, fluidName);
		toSend.setInteger(ENERGY, energy);

		FMLInterModComms.sendMessage(MOD_ID, "CompressionFuel", toSend);
	}

	/* REACTANT */
	public static void addReactantFuel(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString(FLUID_NAME, fluidName);
		toSend.setInteger(ENERGY, energy);

		FMLInterModComms.sendMessage(MOD_ID, "ReactantFuel", toSend);
	}

	/* COOLANT */
	public static void addCoolant(String fluidName, int energy) {

		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString(FLUID_NAME, fluidName);
		toSend.setInteger(ENERGY, energy);

		FMLInterModComms.sendMessage(MOD_ID, "Coolant", toSend);
	}

}
