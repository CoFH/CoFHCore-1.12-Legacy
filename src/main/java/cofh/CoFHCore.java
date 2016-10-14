package cofh;

import cofh.api.core.IBakeable;
import cofh.core.CoFHProps;
import cofh.core.Proxy;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketCore;
import cofh.core.network.PacketCore.PacketTypes;
import cofh.core.util.ConfigHandler;

import java.io.File;
import java.util.ArrayList;

import cofh.core.util.OreDictionaryArbiter;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CoFHCore.modId, name = CoFHCore.modName, version = CoFHCore.version, dependencies = CoFHCore.dependencies, guiFactory = CoFHCore.modGuiFactory,
		customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class CoFHCore {

	public static final String modId = "cofhcore";
	public static final String modName = "CoFH Core";
	public static final String version = "1.10.2A0.0.1";
	public static final String version_max = "1.10.2R2.0.0";
	public static final String dependencies = CoFHProps.FORGE_DEP;
	public static final String modGuiFactory = "cofh.core.gui.GuiConfigCoreFactory";

	public static final String version_group = "required-after:" + modId + "@[" + version + "," + version_max + ");";
	public static final String releaseURL = "https://raw.github.com/CoFH/VERSION/master/" + modId;

	@Instance(modId)
	public static CoFHCore instance;

	@SidedProxy(clientSide = "cofh.core.ProxyClient", serverSide = "cofh.core.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(modId);
	public static final ConfigHandler CONFIG_CORE = new ConfigHandler(version);
	public static final ConfigHandler CONFIG_LOOT = new ConfigHandler(version);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(version);

	static {
		FluidRegistry.enableUniversalBucket();
	}

	public static MinecraftServer server;

	private final ArrayList<IBakeable> oven = new ArrayList<IBakeable>();

	public static void registerBakeable(IBakeable a) {

		instance.oven.add(a);
	}

	/* INIT SEQUENCE */
	public CoFHCore() {

		//super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CoFHProps.configDir = event.getModConfigurationDirectory();

		CONFIG_CORE.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/common.cfg"), true));
		CONFIG_LOOT.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/loot.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/client.cfg"), true));

		FeatureParser.initialize();
		WorldHandler.initialize();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		OreDictionaryArbiter.initialize();

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CONFIG_CORE.cleanUp(false, true);
		CONFIG_LOOT.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		try {
			FeatureParser.parseGenerationFile();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		LOG.info(modName + ": Load Complete.");
	}

	@EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event) {

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

		server = event.getServer();
		for (IBakeable pastry : oven) {
			pastry.bake();
		}
	}

	/* IMC */
	@EventHandler
	public void handleIMC(IMCEvent event) {

	}

	/* SYNC */
	public PacketCoFHBase getConfigSync() {

		PacketCoFHBase payload = PacketCore.getPacket(PacketTypes.CONFIG_SYNC);

		return payload;
	}

	public void handleConfigSync(PacketCoFHBase payload) {

	}

}
