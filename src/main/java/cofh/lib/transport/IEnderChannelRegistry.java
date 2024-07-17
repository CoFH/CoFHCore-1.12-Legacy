package cofh.lib.transport;

import com.google.common.primitives.Ints;

import java.util.List;

public interface IEnderChannelRegistry {

	public List<Frequency> getFrequencyList(String channel);

	public String getFrequency(String channel, int freq);

	public String setFrequency(String channel, int freq, String name);

	public String removeFrequency(String channel, int freq);

	public int updated();

	public static class Frequency implements Comparable<Frequency> {

		public final int freq;
		public final String name;

		public Frequency(int freq, String name) {

			this.freq = freq;
			this.name = name;
		}

		@Override
		public int compareTo(Frequency o) {

			if (o == null) {
				return 1;
			}
			return Ints.compare(freq, o.freq);
		}

		@Override
		public boolean equals(Object o) {

			if (o instanceof Frequency) {
				return ((Frequency) o).freq == freq;
			}
			return false;
		}

		@Override
		public int hashCode() {

			return freq;
		}
	}
}
