package cofh.api.modhelpers;

import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.Loader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class EE3Helper {

	public static final boolean EE3_PRESENT;

	private static Method addRecipe;
	private static Method setAsNotLearnable;
	private static Method setAsNotRecoverable;
	private static Method addPreAssignedEnergyValue;

	static {
		boolean found = false;
		if (Loader.isModLoaded("EE3")) {
			try {
				Class<?> aClass = Class.forName("com.pahimar.ee3.api.exchange.RecipeRegistryProxy");
				addRecipe = aClass.getDeclaredMethod("addRecipe", Object.class, List.class);

				aClass = Class.forName("com.pahimar.ee3.api.knowledge.AbilityRegistryProxy");
				setAsNotLearnable = aClass.getDeclaredMethod("setAsNotLearnable", Object.class);
				setAsNotRecoverable = aClass.getDeclaredMethod("setAsNotRecoverable", Object.class);

				aClass = Class.forName("com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy");
				addPreAssignedEnergyValue = aClass.getDeclaredMethod("addPreAssignedEnergyValue", Object.class, float.class);

				found = true;
			} catch (Exception e) {
				found = false;
			}
		}
		EE3_PRESENT = found;
	}

	private EE3Helper() {

	}

	public static void addPreAssignedEnergyValue(Object object, float val) throws Throwable {

		if (EE3_PRESENT) {
			addPreAssignedEnergyValue.invoke(null, object, val);
		}
	}

	public static void setAsNotLearnable(Object o) throws Throwable {

		if (EE3_PRESENT) {
			setAsNotLearnable.invoke(null, o);
		}
	}

	public static void setAsNotRecoverable(Object o) throws Throwable {

		if (EE3_PRESENT) {
			setAsNotRecoverable.invoke(null, o);
		}
	}

	private static int gcd(int a, int b) {

		int temp;
		if (a == 0) {
			return b;
		}

		if (b > a) {
			b = b % a;
		}

		while (b > 0) {
			temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	private static int[] getRatio(double prob, int i) {

		if (prob == (int) prob) {
			return new int[] { (int) prob, 1 };
		}
		int[] a = reduceRatio(new int[] { (int) Math.ceil(prob * i), i });
		int[] b = reduceRatio(new int[] { (int) Math.floor(prob * i), i });
		return a[1] < b[1] ? a : b; // choose smaller denominator
	}

	private static int[] reduceRatio(int[] v) {

		int k = gcd(v[0], v[1]);
		v[0] /= k;
		v[1] /= k;
		return v;
	}

	public static void addProbabilisticRecipe(ItemStack output, double prob, ItemStack... input) throws Throwable {

		if (!EE3_PRESENT || output == null || prob == 0) {
			return;
		}
		if (prob == 1.0) {
			addRecipe(output, (Object[]) input);
		} else {
			int[] ratio = getRatio(output.stackSize * prob, 16);

			ArrayList<ItemStack> multInput = new ArrayList<ItemStack>();
			for (int i = 0; i < ratio[1]; i++) {
				for (ItemStack stack : input) {
					multInput.add(stack.copy());
				}
			}

			addRecipe(ItemHelper.cloneStack(output, ratio[0]), multInput.toArray());
		}
	}

	public static void addRecipe(Object output, Object... inputs) throws Throwable {

		if (!EE3_PRESENT || output == null) {
			return;
		}
		ArrayList<Object> items = new ArrayList<Object>(inputs.length);

		for (Object a : inputs) {
			if (a == null) {
				continue;
			}
			if (a instanceof ItemStack) {
				ItemStack input = (ItemStack) a;

				int k = input.stackSize;
				input = input.copy();
				input.stackSize = 1;
				for (int i = 0; i < k; i++) {
					items.add(input.copy());
				}
			} else {
				items.add(a);
			}

		}
		if (items.isEmpty()) {
			return;
		}
		addRecipe_do(output, items);
	}

	private static void addRecipe_do(Object itemStack, List<Object> items) throws Throwable {

		if (EE3_PRESENT) {
			addRecipe.invoke(null, itemStack, items);
		}
	}

}
