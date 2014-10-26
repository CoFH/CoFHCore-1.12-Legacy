package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;

public class DecorationParser extends SurfaceParser {

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.air, -1));
	}

	@Override
	protected String getDefaultTemplate() {
		return "decoration";
	}

}
