package cofh;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.api.core.RegistryAccess;
import cofh.command.CommandCape;
import cofh.command.CommandHandler;
import cofh.command.CommandSkin;
import cofh.core.CoFHProps;
import cofh.masquerade.MasqueradePacketHandler;
import cofh.masquerade.RegistryCapes;
import cofh.masquerade.RegistrySkins;
import cofh.util.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "CoFHMasquerade", name = "CoFH Masquerade", version = CoFHProps.VERSION, dependencies = "required-after:CoFHCore@[" + CoFHProps.VERSION + ",)",
		canBeDeactivated = true)
public class CoFHMasquerade {

	@Instance("CoFHMasquerade")
	public static CoFHMasquerade instance;

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CommandHandler.registerSubCommand(CommandCape.instance);
		CommandHandler.registerSubCommand(CommandSkin.instance);

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHMasquerade.cfg")));

		RegistryAccess.capeRegistry = new RegistryCapes();
		RegistryAccess.skinRegistry = new RegistrySkins();

		RegistryCapes.initialize();
		RegistrySkins.initialize();

		String category = "general";
		String comment = null;

		category = "feature.capes";

		comment = "Allows all players to use /cofh cape set.";
		RegistryCapes.allowPlayersUse = config.get(category, "AllowEveryone", true, comment);

		comment = "Allows Ops to use /cofh cape set and /cofh cape set (user) if that is also allowed.";
		RegistryCapes.allowOpsUse = config.get(category, "AllowOps", true, comment);

		comment = "Allows Ops to set the capes of others, if Ops are allowed to set capes.";
		RegistryCapes.allowOpsOthers = config.get(category, "AllowOpsToSetOthers", false, comment);

		category = "feature.skins";

		comment = "Allows all players to use /cofh skin set.";
		RegistrySkins.allowPlayersUse = config.get(category, "AllowEveryone", true, comment);

		comment = "Allows Ops to use /cofh skin set and /cofh skin set (user) if that is also allowed.";
		RegistrySkins.allowOpsUse = config.get(category, "AllowOps", true, comment);

		comment = "Allows Ops to set the skins of others, if Ops are allowed to set skins.";
		RegistrySkins.allowOpsOthers = config.get(category, "AllowOpsToSetOthers", false, comment);

		config.save();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		MasqueradePacketHandler.initialize();
		config.cleanUp(false, false);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

	}

	@EventHandler
	public void postInit(FMLModDisabledEvent event) {

	}

	public static final Logger log = LogManager.getLogger("CoFHMasquerade");
	public static final ConfigHandler config = new ConfigHandler(CoFHProps.VERSION);

	static {
		// log.setParent(FMLLog.getLogger());
		// TODO: parents?
	}

}
