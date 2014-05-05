package cofh;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.command.CommandFriend;
import cofh.command.CommandHandler;
import cofh.core.CoFHProps;
import cofh.social.Proxy;
import cofh.social.RegistryFriends;
import cofh.social.SocialPacket;
import cofh.util.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "CoFHSocial", name = "CoFH Social", version = CoFHProps.VERSION, dependencies = "required-after:CoFHCore@[" + CoFHProps.VERSION + ",)")
public class CoFHSocial {

	@Instance("CoFHSocial")
	public static CoFHSocial instance;

	@SidedProxy(clientSide = "cofh.social.ProxyClient", serverSide = "cofh.social.Proxy")
	public static Proxy proxy;

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CommandHandler.registerSubCommand(CommandFriend.instance);

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHSocial.cfg")));

		RegistryFriends.initialize();

		config.save();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		SocialPacket.initialize();

		config.cleanUp(false, true);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

	}

	public static final Logger log = LogManager.getLogger("CoFHSocial");
	public static final ConfigHandler config = new ConfigHandler(CoFHProps.VERSION);

	static {
		// log.setParent(FMLLog.getLogger());
		// TODO: set parent?
	}

}
