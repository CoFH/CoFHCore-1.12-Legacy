package cofh.api.core;

public interface IFilterable {

	int FLAG_BLACKLIST = 0;
	int FLAG_ORE_DICT = 1;
	int FLAG_NBT = 2;
	int FLAG_METADATA = 3;

	void setFlag(int flag, boolean value);

}
