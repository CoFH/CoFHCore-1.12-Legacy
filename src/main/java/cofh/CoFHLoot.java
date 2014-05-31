package cofh;

import cofh.core.CoFHProps;
import cofh.entity.DropHandler;
import cofh.util.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "CoFHLoot", name = "CoFH Loot", version = CoFHCore.version, dependencies = "required-after:CoFHCore@[" + CoFHCore.version + ",)",
		canBeDeactivated = true)
public class CoFHLoot {

	@Instance("CoFHLoot")
	public static CoFHLoot instance;

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHLoot.cfg")));

		String category = "general";
		String comment = null;

		category = "feature.heads";

		comment = "If enabled, mobs only drop heads when killed by players.";
		DropHandler.mobPvEOnly = config.get(category, "MobsDropOnPvEOnly", DropHandler.mobPvEOnly, comment);

		comment = "If enabled, players only drop heads when killed by other players.";
		DropHandler.playerPvPOnly = config.get(category, "PlayersDropOnPvPOnly", DropHandler.playerPvPOnly, comment);

		category = "feature.heads.enable";

		DropHandler.playersEnabled = config.get(category, "PlayersDropHeads", DropHandler.playersEnabled);
		DropHandler.creeperEnabled = config.get(category, "CreepersDropHeads", DropHandler.creeperEnabled);
		DropHandler.skeletonEnabled = config.get(category, "SkeletonsDropHeads", DropHandler.skeletonEnabled);
		DropHandler.skeletonEnabled = config.get(category, "WitherSkeletonsDropHeads", DropHandler.witherSkeletonEnabled);
		DropHandler.zombieEnabled = config.get(category, "ZombiesDropHeads", DropHandler.zombieEnabled);

		category = "feature.heads.chance";

		DropHandler.playerChance = config.get(category, "PlayerDropChance", DropHandler.playerChance);
		DropHandler.creeperChance = config.get(category, "CreeperDropChance", DropHandler.creeperChance);
		DropHandler.skeletonChance = config.get(category, "SkeletonDropChance", DropHandler.skeletonChance);
		DropHandler.witherSkeletonChance = config.get(category, "WitherSkeletonDropChance", DropHandler.witherSkeletonChance);
		DropHandler.zombieChance = config.get(category, "ZombieDropChance", DropHandler.zombieChance);

		config.save();

		MinecraftForge.EVENT_BUS.register(DropHandler.instance);
	}

	@EventHandler
	public void postInit(FMLModDisabledEvent event) {

		MinecraftForge.EVENT_BUS.unregister(DropHandler.instance);
	}

	public static final Logger log = LogManager.getLogger("CoFHLoot");
	public static ConfigHandler config = new ConfigHandler(CoFHCore.version);

	static {
		// log.setParent(FMLLog.getLogger());
		// TODO: parents?
	}

}
