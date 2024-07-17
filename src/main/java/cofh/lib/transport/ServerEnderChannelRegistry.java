package cofh.lib.transport;

import cofh.lib.network.ByteBufHelper;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ServerEnderChannelRegistry implements IEnderChannelRegistry {

	protected Configuration linkConf;
	protected HashMap<String, TIntObjectHashMap<String>> channels;
	private int modCount;

	public ServerEnderChannelRegistry(Configuration config) {

		channels = new HashMap<String, TIntObjectHashMap<String>>();

		linkConf = config;
		load();
	}

	protected void load() {

		++modCount;
		for (String channel : linkConf.getCategoryNames()) {
			ConfigCategory category = linkConf.getCategory(channel);
			TIntObjectHashMap<String> map = channels.get(channel);
			if (map == null) {
				channels.put(channel, map = new TIntObjectHashMap<String>());
			}
			for (Property prop : category.values()) {
				try {
					int freq = Integer.parseInt(prop.getName());
					map.put(freq, prop.getString());
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

	/**
	 * Returns nulls or a ByteBuf of the frequency<->name mappings for <code>channel</code><br>
	 * <b>Format:</b>
	 * <ul>
	 * <code><b>VarInt</b> entries : <b>VarInt</b> frequency ; <b>VarInt</b> length ; <b>byte[]</b> UTF8</code>
	 * </ul>
	 *
	 * @param channel
	 *            The channel to get frequency data for
	 * @return A ByteBuf of the frequency data for <code>channel</code> or null if the channel does not exist.
	 */
	public ByteBuf getFrequencyData(String channel) {

		TIntObjectHashMap<String> map = channels.get(channel);
		ByteBuf ret = Unpooled.buffer();
		if (map != null) {
			TIntObjectIterator<String> iter = map.iterator(); // allocate before size() so a comod throws correctly
			ByteBufHelper.writeVarInt(map.size(), ret);
			ByteBufHelper.writeString(channel, ret);
			for (; iter.hasNext();) {
				iter.advance();
				ByteBufHelper.writeVarInt(iter.key(), ret);
				ByteBufHelper.writeString(iter.value(), ret);
			}
		} else {
			ByteBufHelper.writeVarInt(0, ret);
			ByteBufHelper.writeString(channel, ret);
		}
		return ret;
	}

	@Override
	public List<Frequency> getFrequencyList(String channel) {

		LinkedList<Frequency> ret = new LinkedList<Frequency>();
		TIntObjectHashMap<String> map = channels.get(channel);
		if (map != null) {
			for (TIntObjectIterator<String> iter = map.iterator(); iter.hasNext();) {
				iter.advance();
				ret.add(new Frequency(iter.key(), iter.value()));
			}
		}
		return ret;
	}

	@Override
	public String getFrequency(String channel, int freq) {

		TIntObjectHashMap<String> map = channels.get(channel);
		if (map != null) {
			return map.get(freq);
		}
		return null;
	}

	@Override
	public String setFrequency(String channel, int freq, String name) {

		TIntObjectHashMap<String> map = channels.get(channel);
		if (map == null) {
			channels.put(channel, map = new TIntObjectHashMap<String>());
		}
		String old = map.put(freq, name);
		++modCount;
		linkConf.get(channel, String.valueOf(freq), "").set(name);
		return old;
	}

	@Override
	public String removeFrequency(String channel, int freq) {

		TIntObjectHashMap<String> map = channels.get(channel);
		if (map == null) {
			return null;
		}
		String old = map.remove(freq);
		if (old != null) {
			++modCount;
			linkConf.getCategory(channel).remove(String.valueOf(freq));
		}
		return old;
	}

	@Override
	public int updated() {

		return modCount;
	}

}
