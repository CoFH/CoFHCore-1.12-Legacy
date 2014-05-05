package cofh.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import cofh.api.transport.IEnderAttuned;

public class RegistryEnderAttuned {

	public static Map<String, Map<Integer, List<IEnderAttuned>>> inputItem = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();
	public static Map<String, Map<Integer, List<IEnderAttuned>>> inputFluid = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();
	public static Map<String, Map<Integer, List<IEnderAttuned>>> inputEnergy = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();

	public static Map<String, Map<Integer, List<IEnderAttuned>>> outputItem = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();
	public static Map<String, Map<Integer, List<IEnderAttuned>>> outputFluid = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();
	public static Map<String, Map<Integer, List<IEnderAttuned>>> outputEnergy = new HashMap<String, Map<Integer, List<IEnderAttuned>>>();

	public static Configuration linkConf;

	public static Map<String, String> clientFrequencyNames;
	public static Map<String, String> clientFrequencyNamesReversed;

	public static void clear() {

		inputItem.clear();
		inputFluid.clear();
		inputEnergy.clear();
		outputItem.clear();
		outputFluid.clear();
		outputEnergy.clear();
	}

	public static List<IEnderAttuned> getLinkedStuff(IEnderAttuned theAttuned, Map<String, Map<Integer, List<IEnderAttuned>>> mapToUse) {

		if (mapToUse.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return mapToUse.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderAttuned> getLinkedItemInputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, inputItem);
	}

	public static List<IEnderAttuned> getLinkedItemOutputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, outputItem);
	}

	public static List<IEnderAttuned> getLinkedFluidInputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, inputFluid);
	}

	public static List<IEnderAttuned> getLinkedFluidOutputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, outputFluid);
	}

	public static List<IEnderAttuned> getLinkedPowerInputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, inputEnergy);
	}

	public static List<IEnderAttuned> getLinkedEnergyOutputs(IEnderAttuned theAttuned) {

		return getLinkedStuff(theAttuned, outputEnergy);
	}

	/* HELPER FUNCTIONS */
	public static void add(IEnderAttuned theAttuned, Map<String, Map<Integer, List<IEnderAttuned>>> mapToUseInput,
			Map<String, Map<Integer, List<IEnderAttuned>>> mapToUseOutput, boolean canSend, boolean canReceive) {

		if (canSend) {
			if (mapToUseInput.get(theAttuned.getOwnerString()) == null) {
				mapToUseInput.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderAttuned>>());
			}
			if (mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				mapToUseInput.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderAttuned>());
			}
			if (!mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
		if (canReceive) {
			if (mapToUseOutput.get(theAttuned.getOwnerString()) == null) {
				mapToUseOutput.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderAttuned>>());
			}
			if (mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				mapToUseOutput.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderAttuned>());
			}
			if (!mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
	}

	public static void remove(IEnderAttuned theAttuned, Map<String, Map<Integer, List<IEnderAttuned>>> mapToUseInput,
			Map<String, Map<Integer, List<IEnderAttuned>>> mapToUseOutput) {

		if (mapToUseInput.get(theAttuned.getOwnerString()) != null) {
			if (mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (mapToUseInput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					mapToUseInput.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
		if (mapToUseOutput.get(theAttuned.getOwnerString()) != null) {
			if (mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (mapToUseOutput.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					mapToUseOutput.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
	}

	public static void add(IEnderAttuned theAttuned) {

		add(theAttuned, inputItem, outputItem, theAttuned.canSendItems(), theAttuned.canReceiveItems());
		add(theAttuned, inputFluid, outputFluid, theAttuned.canSendFluid(), theAttuned.canReceiveFluid());
		add(theAttuned, inputEnergy, outputEnergy, theAttuned.canSendEnergy(), theAttuned.canReceiveEnergy());
	}

	public static void remove(IEnderAttuned theAttuned) {

		remove(theAttuned, inputItem, outputItem);
		remove(theAttuned, inputFluid, outputFluid);
		remove(theAttuned, inputEnergy, outputEnergy);
	}

	public static void sortClientNames() {

		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(clientFrequencyNames.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {

			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {

				int int1 = Integer.valueOf(clientFrequencyNamesReversed.get(o1.getValue()));
				int int2 = Integer.valueOf(clientFrequencyNamesReversed.get(o2.getValue()));
				return int1 > int2 ? 1 : int1 == int2 ? 0 : -1;
			}
		});
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		clientFrequencyNames = result;
	}

	public static void clearClientNames() {

		clientFrequencyNames = new LinkedHashMap<String, String>();
		clientFrequencyNamesReversed = new LinkedHashMap<String, String>();
	}

	public static void addClientNames(String owner, String name) {

		if (!owner.isEmpty()) {
			clientFrequencyNames.put(owner, name);
			clientFrequencyNamesReversed.put(name, owner);
		}
	}

}
