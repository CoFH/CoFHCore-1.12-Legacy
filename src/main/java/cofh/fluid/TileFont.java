package cofh.fluid;

import cofh.block.TileCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

public class TileFont extends TileCoFHBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileFont.class, "cofh.Font");
	}

	Fluid fluid;
	Block fluidBlock;
	int amount;

	public TileFont(Fluid fluid, int amount) {

		this.fluid = fluid;
		this.fluidBlock = fluid.getBlock();
		this.amount = amount;
	}

	@Override
	public String getName() {

		return "tile.cofh.font.name";
	}

	@Override
	public int getType() {

		return 0;
	}

}
