package cofh.lib.transport;

import cofh.api.transport.IEnderAttuned;
import cofh.api.transport.IEnderDestination;
import cofh.api.transport.IEnderEnergyHandler;
import cofh.api.transport.IEnderFluidHandler;
import cofh.api.transport.IEnderItemHandler;
import cofh.lib.util.ArrayHashList;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class EnderRegistry {

	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>> inputItem;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>> inputFluid;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>> inputEnergy;

	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>> outputItem;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>> outputFluid;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>> outputEnergy;

	private HashMap<String, TIntObjectHashMap<EnderDestination>> outputTeleport;
	private HashMap<String, BitSet> usedTeleports;

	private Configuration linkConf;

	public EnderRegistry(Configuration config) {

		inputItem = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>>();
		inputFluid = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>>();
		inputEnergy = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>>();

		outputItem = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>>();
		outputFluid = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>>();
		outputEnergy = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>>();

		outputTeleport = new HashMap<String, TIntObjectHashMap<EnderDestination>>();
		usedTeleports = new HashMap<String, BitSet>();

		linkConf = config;
		load();
	}

	private void load() {

		for (String channel : linkConf.getCategoryNames()) {
			ConfigCategory category = linkConf.getCategory(channel);
			TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
			BitSet set = usedTeleports.get(channel);
			if (map == null) {
				outputTeleport.put(channel, map = new TIntObjectHashMap<EnderDestination>());
				usedTeleports.put(channel, set = new BitSet());
			}
			for (Property prop : category.values()) {
				try {
					int freq = Integer.parseInt(prop.getName());
					String[] data = prop.getString().split("@");
					if (data.length != 2) {
						continue;
					}
					int dimension = Integer.parseInt(data[0]);
					data = data[1].split(",");
					if (data.length != 3) {
						continue;
					}
					int x, y, z;
					x = Integer.parseInt(data[0]);
					y = Integer.parseInt(data[1]);
					z = Integer.parseInt(data[2]);
					EnderDestination dest = new EnderDestination(x, y, z, dimension);
					map.put(freq, dest);
					set.set(freq);
				} catch (Throwable p) {
				}
			}
		}
	}

	public void save() {

		if (linkConf.hasChanged()) {
			linkConf.save();
		}
	}

	private <T> T getElement(HashMap<String, TIntObjectHashMap<T>> map, IEnderAttuned theAttuned) {

		TIntObjectHashMap<T> list = map.get(theAttuned.getChannelString());
		if (list != null) {
			return list.get(theAttuned.getFrequency());
		}
		return null;
	}

	public List<IEnderItemHandler> getLinkedItemInputs(IEnderItemHandler theAttuned) {

		return getElement(inputItem, theAttuned);
	}

	public List<IEnderItemHandler> getLinkedItemOutputs(IEnderItemHandler theAttuned) {

		return getElement(outputItem, theAttuned);
	}

	public List<IEnderFluidHandler> getLinkedFluidInputs(IEnderFluidHandler theAttuned) {

		return getElement(inputFluid, theAttuned);
	}

	public List<IEnderFluidHandler> getLinkedFluidOutputs(IEnderFluidHandler theAttuned) {

		return getElement(outputFluid, theAttuned);
	}

	public List<IEnderEnergyHandler> getLinkedEnergyInputs(IEnderEnergyHandler theAttuned) {

		return getElement(inputEnergy, theAttuned);
	}

	public List<IEnderEnergyHandler> getLinkedEnergyOutputs(IEnderEnergyHandler theAttuned) {

		return getElement(outputEnergy, theAttuned);
	}

	public int findFreeFrequency(String channel) {

		BitSet set = usedTeleports.get(channel);
		if (set == null) {
			return 0;
		}
		return set.nextClearBit(0);
	}

	public boolean hasDestination(IEnderDestination theAttuned) {

		return hasDestination(theAttuned, true);
	}

	public boolean hasDestination(IEnderDestination theAttuned, boolean to) {

		String channel = theAttuned.getChannelString();
		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
		if (map == null) {
			return false;
		}
		int freq = to ? theAttuned.getDestination() : theAttuned.getFrequency();
		EnderDestination dest = map.get(freq);
		boolean r = dest == null ? false : dest.hasOutput();
		usedTeleports.get(channel).set(freq, r);
		return r;
	}

	public IEnderDestination getDestination(IEnderDestination theAttuned) {

		return getDestination(theAttuned, true);
	}

	public IEnderDestination getDestination(IEnderDestination theAttuned, boolean requireLoaded) {

		final String channel = theAttuned.getChannelString();
		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
		if (map == null) {
			return null;
		}
		final int frequency = theAttuned.getDestination();
		final EnderDestination dest = map.get(frequency);
		if (dest == null) {
			return null;
		}
		IEnderDestination out = dest.getOutput(requireLoaded);
		if (requireLoaded && out == null && !dest.isInvalid) {
			return new IEnderDestination() {

				@Override
				public String getChannelString() {

					return channel;
				}

				@Override
				public int getFrequency() {

					return frequency;
				}

				@Override
				public boolean setFrequency(int frequency) {

					return false;
				}

				@Override
				public boolean clearFrequency() {

					return false;
				}

				@Override
				public boolean isNotValid() {

					return true;
				}

				@Override
				public int x() {

					return dest.x;
				}

				@Override
				public int y() {

					return dest.y;
				}

				@Override
				public int z() {

					return dest.z;
				}

				@Override
				public int dimension() {

					return dest.dimension;
				}

				@Override
				public int getDestination() {

					return -1;
				}

				@Override
				public boolean setDestination(int frequency) {

					return false;
				}

				@Override
				public boolean clearDestination() {

					return false;
				}

			};
		}
		return out;
	}

	/* HELPER FUNCTIONS */
	private <T extends IEnderAttuned> boolean addHandler(HashMap<String, TIntObjectHashMap<ArrayHashList<T>>> map, T theAttuned) {

		String channel = theAttuned.getChannelString();
		TIntObjectHashMap<ArrayHashList<T>> list = map.get(channel);
		if (list == null) {
			map.put(channel, list = new TIntObjectHashMap<ArrayHashList<T>>());
		}
		int freq = theAttuned.getFrequency();
		ArrayHashList<T> array = list.get(freq);
		if (array == null) {
			list.put(freq, array = new ArrayHashList<T>());
		}
		return array.add(theAttuned);
	}

	public void addItemHandler(IEnderItemHandler theAttuned) {

		if (theAttuned.canSendItems()) {
			addHandler(inputItem, theAttuned);
		}
		if (theAttuned.canReceiveItems()) {
			addHandler(outputItem, theAttuned);
		}
	}

	public void addFluidHandler(IEnderFluidHandler theAttuned) {

		if (theAttuned.canSendFluid()) {
			addHandler(inputFluid, theAttuned);
		}
		if (theAttuned.canReceiveFluid()) {
			addHandler(outputFluid, theAttuned);
		}
	}

	public void addEnergyHandler(IEnderEnergyHandler theAttuned) {

		if (theAttuned.canSendEnergy()) {
			addHandler(inputEnergy, theAttuned);
		}
		if (theAttuned.canReceiveEnergy()) {
			addHandler(outputEnergy, theAttuned);
		}
	}

	public void addDestination(IEnderDestination theAttuned) {

		if (!hasDestination(theAttuned, false)) {
			String channel = theAttuned.getChannelString();
			TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
			BitSet set = usedTeleports.get(channel);
			if (map == null) {
				outputTeleport.put(channel, map = new TIntObjectHashMap<EnderDestination>());
				usedTeleports.put(channel, set = new BitSet());
			}
			int freq = theAttuned.getFrequency();
			EnderDestination dest = new EnderDestination(theAttuned);
			map.put(freq, dest);
			set.set(freq);
			linkConf.get(channel, String.valueOf(freq), "").set(dest.toString());
		}
	}

	private <T> boolean removeHandler(HashMap<String, TIntObjectHashMap<ArrayHashList<T>>> map, IEnderAttuned theAttuned) {

		TIntObjectHashMap<ArrayHashList<T>> list = map.get(theAttuned.getChannelString());
		if (list == null) {
			return false;
		}
		ArrayHashList<T> array = list.get(theAttuned.getFrequency());
		if (array == null) {
			return false;
		}
		return array.remove(theAttuned);
	}

	public void removeItemHandler(IEnderItemHandler theAttuned) {

		removeHandler(inputItem, theAttuned);
		removeHandler(outputItem, theAttuned);
	}

	public void removeFluidHandler(IEnderFluidHandler theAttuned) {

		removeHandler(inputFluid, theAttuned);
		removeHandler(outputFluid, theAttuned);
	}

	public void removeEnergyHandler(IEnderEnergyHandler theAttuned) {

		removeHandler(inputEnergy, theAttuned);
		removeHandler(outputEnergy, theAttuned);
	}

	public void removeDestination(IEnderDestination theAttuned) {

		String channel = theAttuned.getChannelString();
		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
		if (map == null) {
			return;
		}
		int freq = theAttuned.getFrequency();
		EnderDestination dest = map.get(freq);
		if (dest == null) {
			return;
		}
		if (dest.dimension == theAttuned.dimension()
				&& dest.x == theAttuned.x() && dest.y == theAttuned.y() && dest.z == theAttuned.z()) {
			map.remove(freq);
			usedTeleports.get(channel).set(freq, false);
			linkConf.getCategory(channel).remove(String.valueOf(freq));
		}
	}

	public void add(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			addItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			addFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			addEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderDestination) {
			addDestination((IEnderDestination) theAttuned);
		}
	}

	public void remove(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			removeItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			removeFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			removeEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderDestination) {
			removeDestination((IEnderDestination) theAttuned);
		}
	}

	private static class EnderDestination {

		private final int dimension;
		private final int x, y, z;
		private IEnderDestination output;
		private boolean isInvalid;

		public EnderDestination(IEnderDestination output) {

			x = output.x();
			y = output.y();
			z = output.z();
			dimension = output.dimension();
			this.output = output;
		}

		private EnderDestination(int x, int y, int z, int dimension) {

			this.x = x;
			this.y = y;
			this.z = z;
			this.dimension = dimension;
		}

		public boolean hasOutput() {

			return !isInvalid && DimensionManager.isDimensionRegistered(dimension);
		}

		public IEnderDestination getOutput(boolean onlyLoaded) {

			if (output == null || output.isNotValid()) {
				output = null;
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					return null;
				}
				WorldServer world = DimensionManager.getWorld(dimension);
				if (world == null && !onlyLoaded) {
					DimensionManager.initDimension(dimension);
					world = DimensionManager.getWorld(dimension);
				} else {
					return null;
				}
				if (world.blockExists(x, y, z)) {
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof IEnderDestination) {
						output = (IEnderDestination) te;
					} else {
						isInvalid = true;
					}
				}
			}
			return output;
		}

		@Override
		public String toString() {

			return String.format("%s@%s,%s,%s", dimension, x, y, z);
		}

		@Override
		public int hashCode() {

			return dimension ^ (y + x * (z * 100));
		}

		@Override
		public boolean equals(Object o) {

			if (o == null || o.getClass() != EnderDestination.class) {
				return false;
			}
			EnderDestination other = (EnderDestination) o;
			return other.x == x && other.y == y && other.z == z && other.dimension == dimension;
		}

	}

}
