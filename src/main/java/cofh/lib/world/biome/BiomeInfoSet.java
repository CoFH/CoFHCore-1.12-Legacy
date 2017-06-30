package cofh.lib.world.biome;

import net.minecraft.world.biome.Biome;

import java.util.*;

public class BiomeInfoSet implements Set<BiomeInfo> {

	protected BiomeInfo[] elementData;
	protected int size, modCount;

	public BiomeInfoSet() {

		this(10);
	}

	public BiomeInfoSet(int size) {

		elementData = new BiomeInfo[size];
	}

	public BiomeInfoSet(Collection<? extends BiomeInfo> c) {

		elementData = c.toArray(new BiomeInfo[c.size()]);
	}

	public void ensureCapacity(int minCapacity) {

		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			elementData = Arrays.copyOf(elementData, newCapacity);
		}
	}

	@Override
	public int size() {

		return size;
	}

	@Override
	public boolean isEmpty() {

		return size == 0;
	}

	@Override
	public boolean contains(Object o) {

		BiomeInfo[] oldData = elementData;
		if (o instanceof Biome) {
			Biome bgb = (Biome) o;
			for (int i = 0, e = size; i < e; ++i) {
				if (oldData[i] != null && oldData[i].isBiomeEqual(bgb, null)) {
					return true;
				}
			}
			return false;
		}
		for (int i = 0, e = size; i < e; ++i) {
			if (oldData[i] == o || (oldData[i] != null && o != null && oldData[i].equals(o))) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(Biome bgb, Random rand) {

		BiomeInfo[] oldData = elementData;
		for (int i = 0, e = size; i < e; ++i) {
			if (oldData[i] != null && oldData[i].isBiomeEqual(bgb, rand)) {
				return true;
			}
		}
		return false;
	}

	public BiomeInfo get(int i) {

		if ((i < 0) | i >= size) {
			throw new IndexOutOfBoundsException();
		}
		return elementData[i];
	}

	@Override
	public Iterator<BiomeInfo> iterator() {

		return new Itr();
	}

	@Override
	public Object[] toArray() {

		return Arrays.copyOf(elementData, size, Object[].class);
	}

	@Override
	public <T> T[] toArray(T[] a) {

		if (a.length < size) {
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		}
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	@Override
	public boolean add(BiomeInfo e) {

		int i = size;
		ensureCapacity(size = i + 1);
		elementData[i] = e;
		return true;
	}

	@Override
	public boolean remove(Object o) {

		for (int i = 0, e = size; i < e; ++i) {
			if (elementData[i] == o) {
				fastRemove(i);
				return true;
			}
		}
		return false;
	}

	protected void fastRemove(int index) {

		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		}
		elementData[--size] = null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		boolean r = true;
		for (Object i : c) {
			r |= contains(i);
		}
		return r;
	}

	@Override
	public boolean addAll(Collection<? extends BiomeInfo> c) {

		int oSize = c.size();
		if (oSize == 0) {
			return false;
		}
		BiomeInfo[] a = c.toArray(new BiomeInfo[oSize]);
		ensureCapacity(size + oSize);
		System.arraycopy(a, 0, elementData, size, oSize);
		size += oSize;
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		BiomeInfo[] oldData = elementData;
		int e = oldData.length;
		elementData = new BiomeInfo[c.size() / 2];
		size = 0;
		boolean r = true;
		for (Object o : c) {
			for (int i = 0; i < e; ++i) {
				if (oldData[i] == o || (oldData[i] != null && o != null && oldData[i].equals(o))) {
					r |= add(oldData[i]);
				}
			}
		}
		return r;
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		boolean r = true;
		for (Object i : c) {
			r |= remove(i);
		}
		return r;
	}

	@Override
	public void clear() {

		modCount++;

		for (int i = 0; i < size; i++) {
			elementData[i] = null;
		}

		size = 0;
	}

	protected class Itr implements Iterator<BiomeInfo> {

		protected int cursor = 0;
		protected int expectedModCount = BiomeInfoSet.this.modCount;
		protected boolean lastRet = false;

		@Override
		public boolean hasNext() {

			return cursor != BiomeInfoSet.this.size;
		}

		@Override
		public BiomeInfo next() {

			checkForComodification();
			try {
				if (cursor < size) {
					lastRet = true;
					return BiomeInfoSet.this.elementData[cursor++];
				}
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {

			if (!lastRet) {
				throw new IllegalStateException();
			}
			checkForComodification();

			try {
				lastRet = false;
				BiomeInfoSet.this.remove(--cursor);
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		protected final void checkForComodification() {

			if (BiomeInfoSet.this.modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}
}
