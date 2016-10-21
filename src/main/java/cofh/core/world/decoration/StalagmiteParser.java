package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenStalactite;
import cofh.lib.world.WorldGenStalagmite;
import com.google.gson.JsonObject;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class StalagmiteParser implements IGeneratorParser {

    private final boolean stalactite;

    public StalagmiteParser(boolean stalactite) {

        this.stalactite = stalactite;
    }

    @Override
    public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

        ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
        if (!genObject.has("genBody")) {
            log.info("Entry does not specify genBody for 'stalagmite' generator. Using air.");
            list.add(new WeightedRandomBlock(Blocks.AIR));
        } else {
            if (!FeatureParser.parseResList(genObject.get("genBody"), list, false)) {
                log.warn("Entry specifies invalid genBody for 'stalagmite' generator! Using air!");
                list.clear();
                list.add(new WeightedRandomBlock(Blocks.AIR));
            }
        }
        WorldGenStalagmite r = stalactite ? new WorldGenStalactite(resList, matList, list) : new WorldGenStalagmite(resList, matList, list);
        {
            if (genObject.has("minHeight")) {
                r.minHeight = genObject.get("minHeight").getAsInt();
            }
            if (genObject.has("heightVariance")) {
                r.heightVariance = genObject.get("heightVariance").getAsInt();
            }
            if (genObject.has("sizeVariance")) {
                r.sizeVariance = genObject.get("sizeVariance").getAsInt();
            }
            if (genObject.has("heightMod")) {
                r.heightMod = genObject.get("heightMod").getAsInt();
            }
            if (genObject.has("genSize")) {
                r.genSize = genObject.get("genSize").getAsInt();
            }
            if (genObject.has("smooth")) {
                r.smooth = genObject.get("smooth").getAsBoolean();
            }
            if (genObject.has("fat")) {
                r.fat = genObject.get("fat").getAsBoolean();
            }
            if (genObject.has("altSinc")) {
                r.altSinc = genObject.get("altSinc").getAsBoolean();
            }
        }
        return r;
    }

}
