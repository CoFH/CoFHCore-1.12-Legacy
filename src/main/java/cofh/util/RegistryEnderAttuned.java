package cofh.util;

import cofh.api.transport.IEnderAttuned;
import cofh.api.transport.IEnderEnergyHandler;
import cofh.api.transport.IEnderFluidHandler;
import cofh.api.transport.IEnderItemHandler;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

public class RegistryEnderAttuned {

	public static Map<String, Map<Integer, List<IEnderItemHandler>>> inputItem = new THashMap();
	public static Map<String, Map<Integer, List<IEnderFluidHandler>>> inputFluid = new THashMap();
	public static Map<String, Map<Integer, List<IEnderEnergyHandler>>> inputEnergy = new THashMap();

	public static Map<String, Map<Integer, List<IEnderItemHandler>>> outputItem = new THashMap();
	public static Map<String, Map<Integer, List<IEnderFluidHandler>>> outputFluid = new THashMap();
	public static Map<String, Map<Integer, List<IEnderEnergyHandler>>> outputEnergy = new THashMap();

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

	public static List<IEnderItemHandler> getLinkedItemInputs(IEnderAttuned theAttuned) {

		if (inputItem.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderItemHandler> getLinkedItemOutputs(IEnderAttuned theAttuned) {

		if (outputItem.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderFluidHandler> getLinkedFluidInputs(IEnderAttuned theAttuned) {

		if (inputFluid.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderFluidHandler> getLinkedFluidOutputs(IEnderAttuned theAttuned) {

		if (outputFluid.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderEnergyHandler> getLinkedEnergyInputs(IEnderAttuned theAttuned) {

		if (inputEnergy.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	public static List<IEnderEnergyHandler> getLinkedEnergyOutputs(IEnderAttuned theAttuned) {

		if (outputEnergy.get(theAttuned.getOwnerString()) == null) {
			return null;
		}
		return outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency());
	}

	/* HELPER FUNCTIONS */
	public static void addItemHandler(IEnderItemHandler theAttuned) {

		if (theAttuned.canSendItems()) {
			if (inputItem.get(theAttuned.getOwnerString()) == null) {
				inputItem.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderItemHandler>>());
			}
			if (inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				inputItem.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderItemHandler>());
			}
			if (!inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
		if (theAttuned.canReceiveItems()) {
			if (outputItem.get(theAttuned.getOwnerString()) == null) {
				outputItem.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderItemHandler>>());
			}
			if (outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				outputItem.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderItemHandler>());
			}
			if (!outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
	}

	public static void addFluidHandler(IEnderFluidHandler theAttuned) {

		if (theAttuned.canSendFluid()) {
			if (inputFluid.get(theAttuned.getOwnerString()) == null) {
				inputFluid.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderFluidHandler>>());
			}
			if (inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				inputFluid.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderFluidHandler>());
			}
			if (!inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
		if (theAttuned.canReceiveFluid()) {
			if (outputFluid.get(theAttuned.getOwnerString()) == null) {
				outputFluid.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderFluidHandler>>());
			}
			if (outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				outputFluid.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderFluidHandler>());
			}
			if (!outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
	}

	public static void addEnergyHandler(IEnderEnergyHandler theAttuned) {

		if (theAttuned.canSendEnergy()) {
			if (inputEnergy.get(theAttuned.getOwnerString()) == null) {
				inputEnergy.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderEnergyHandler>>());
			}
			if (inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				inputEnergy.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderEnergyHandler>());
			}
			if (!inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
		if (theAttuned.canReceiveEnergy()) {
			if (outputEnergy.get(theAttuned.getOwnerString()) == null) {
				outputEnergy.put(theAttuned.getOwnerString(), new HashMap<Integer, List<IEnderEnergyHandler>>());
			}
			if (outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) == null) {
				outputEnergy.get(theAttuned.getOwnerString()).put(theAttuned.getFrequency(), new ArrayList<IEnderEnergyHandler>());
			}
			if (!outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).contains(theAttuned)) {
				outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).add(theAttuned);
			}
		}
	}

	public static void removeItemHandler(IEnderItemHandler theAttuned) {

		if (inputItem.get(theAttuned.getOwnerString()) != null) {
			if (inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (inputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					inputItem.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
		if (outputItem.get(theAttuned.getOwnerString()) != null) {
			if (outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (outputItem.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					outputItem.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
	}

	public static void removeFluidHandler(IEnderFluidHandler theAttuned) {

		if (inputFluid.get(theAttuned.getOwnerString()) != null) {
			if (inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (inputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					inputFluid.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
		if (outputFluid.get(theAttuned.getOwnerString()) != null) {
			if (outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (outputFluid.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					outputFluid.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
	}

	public static void removeEnergyHandler(IEnderEnergyHandler theAttuned) {

		if (inputEnergy.get(theAttuned.getOwnerString()) != null) {
			if (inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (inputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					inputEnergy.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
		if (outputEnergy.get(theAttuned.getOwnerString()) != null) {
			if (outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()) != null) {
				outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).remove(theAttuned);
				if (outputEnergy.get(theAttuned.getOwnerString()).get(theAttuned.getFrequency()).size() == 0) {
					outputEnergy.get(theAttuned.getOwnerString()).remove(theAttuned.getFrequency());
				}
			}
		}
	}

	public static void add(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			addItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			addFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			addEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
	}

	public static void remove(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			removeItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			removeFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			removeEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
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
