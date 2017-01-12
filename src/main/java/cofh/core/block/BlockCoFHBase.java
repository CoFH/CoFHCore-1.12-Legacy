package cofh.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCoFHBase extends Block {

	protected String modName;
	protected String name;

	public BlockCoFHBase(Material material) {

		this(material, "cofh");
	}

	public BlockCoFHBase(Material material, String modName) {

		super(material);
		this.modName = modName;
	}

	@Override
	public Block setUnlocalizedName(String name) {

		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}
}
