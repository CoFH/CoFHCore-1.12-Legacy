package cofh.core.item;

import net.minecraft.item.Item;

public abstract class ItemCoFHBase extends Item {

	protected String modName;
	protected String name;

	public ItemCoFHBase(String modName) {

		this.modName = modName;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

}
