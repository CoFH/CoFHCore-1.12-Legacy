package cofh.lib.transport;

import cofh.lib.network.ByteBufHelper;

import gnu.trove.map.hash.TIntObjectHashMap;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientEnderChannelRegistry implements IEnderChannelRegistry {

	private TIntObjectHashMap<String> channel = new TIntObjectHashMap<String>();
	private ArrayList<Frequency> list = new ArrayList<Frequency>();
	private int modCount;
	protected String hostedChannel = "";

	public ClientEnderChannelRegistry() {

	}

	public void readFrequencyData(ByteBuf data) {

		++modCount;
		channel.clear();
		list.clear();
		int size = ByteBufHelper.readVarInt(data);
		hostedChannel = ByteBufHelper.readString(data);
		for (int i = 0; i < size; ++i) {
			int freq = ByteBufHelper.readVarInt(data);
			String name = ByteBufHelper.readString(data);
			channel.put(freq, name);
			list.add(new Frequency(freq, name));
		}
		Collections.sort(list);
	}

	public String getChannelName() {

		return hostedChannel;
	}

	@Override
	public List<Frequency> getFrequencyList(String _) {

		return list;
	}

	@Override
	public String getFrequency(String _, int freq) {

		return channel.get(freq);
	}

	@Override
	public String setFrequency(String _, int freq, String name) {

		++modCount;
		Frequency f = new Frequency(freq, name);
		int i = list.indexOf(f);
		if (i < 0) {
			list.add(f);
			Collections.sort(list);
		} else {
			list.set(i, f);
		}
		return channel.put(freq, name);
	}

	@Override
	public String removeFrequency(String _, int freq) {

		++modCount;
		list.remove(new Frequency(freq, ""));
		return channel.remove(freq);
	}

	@Override
	public int updated() {

		return modCount;
	}

}
