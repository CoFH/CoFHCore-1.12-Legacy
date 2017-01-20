package cofh.core.util.nbt;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;

public class NBTCopyHelper {

	public static ResultNBT copyAndHashNBT(NBTTagCompound tag) {

		int hash = Constants.NBT.TAG_COMPOUND;
		NBTTagCompound copy = new NBTTagCompound();
		for (String s : tag.getKeySet()) {
			Result result = copyAndHash(tag.getTag(s));
			hash += s.hashCode() ^ result.hash;
			copy.setTag(s, result.base);
		}

		return new ResultNBT(copy, hash);
	}

	private static Result copyAndHash(NBTBase base) {

		byte id = base.getId();
		switch (id) {
			case Constants.NBT.TAG_BYTE: {
				NBTTagByte cache = NBTCache.toCachedByte((NBTTagByte) base);
				return new Result(cache, cache.hashCode());
			}

			case Constants.NBT.TAG_SHORT: {
				NBTTagShort cache = NBTCache.toCachedShort((NBTTagShort) base);
				return new Result(cache, cache.hashCode());
			}

			case Constants.NBT.TAG_INT: {
				NBTTagInt cache = NBTCache.toCachedInt((NBTTagInt) base);
				return new Result(cache, cache.hashCode());
			}

			case Constants.NBT.TAG_LONG: {
				NBTTagLong cache = NBTCache.toCachedLong((NBTTagLong) base);
				return new Result(cache, cache.hashCode());
			}

			case Constants.NBT.TAG_COMPOUND: {
				int hash = Constants.NBT.TAG_COMPOUND;
				NBTTagCompound copy = new NBTTagCompound();
				NBTTagCompound tag = (NBTTagCompound) base;
				for (String s : tag.getKeySet()) {
					Result result = copyAndHash(tag.getTag(s));
					hash += s.hashCode() ^ result.hash;
					copy.setTag(s, result.base);
				}
				return new Result(copy, hash);
			}

			case Constants.NBT.TAG_LIST: {
				NBTTagList list = (NBTTagList) base;
				int tagType = list.getTagType();
				if (tagType == 0) {
					return new Result(list.copy(), list.hashCode());
				} else {
					NBTTagList copy = new NBTTagList();
					int hash = Constants.NBT.TAG_LIST;
					for (int i = 0; i < list.tagCount(); i++) {
						Result result = copyAndHash(list.get(i));
						hash = hash * 31 + result.hash;
						copy.appendTag(result.base);
					}
					return new Result(copy, hash);
				}
			}

			case Constants.NBT.TAG_FLOAT: {
				NBTTagFloat cache = NBTCache.toCachedFloat((NBTTagFloat) base);
				return new Result(cache, cache.hashCode());
			}

			case Constants.NBT.TAG_DOUBLE: {
				NBTTagDouble cache = NBTCache.toCachedDouble((NBTTagDouble) base);
				return new Result(cache, cache.hashCode());
			}

			default:
				return new Result(base.copy(), base.hashCode());
		}
	}

	public static class ResultNBT {

		public final NBTTagCompound copy;
		public final int hash;

		public ResultNBT(NBTTagCompound copy, int hash) {

			this.copy = copy;
			this.hash = hash;
		}
	}

	private static class Result {

		public final NBTBase base;
		public final int hash;

		public Result(NBTBase base, int hash) {

			this.base = base;
			this.hash = hash;
		}
	}
}
