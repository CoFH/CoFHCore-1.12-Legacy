package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface IGeneratorParser {

	/**
	 * Parse a {@link Config} for usage with an {@link IFeatureGenerator}.
	 *
	 * @param name      The name of the generator entry.
	 * @param genObject The JsonObject to parse.
	 * @param log       The {@link Logger} to log debug/error/etc. messages to.
	 * @param resList   The processed list of resources to generate
	 * @param matList   The processed list of materials to generate in
	 * @return The {@link WorldGenerator} to be registered with an IFeatureGenerator
	 */
	WorldGenerator parseGenerator(String name, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList);

}
