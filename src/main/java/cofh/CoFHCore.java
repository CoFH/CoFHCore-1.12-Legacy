package cofh;

import cofh.api.transport.RegistryEnderAttuned;
import cofh.command.CommandHandler;
import cofh.core.CoFHProps;
import cofh.core.Proxy;
import cofh.enchantment.CoFHEnchantment;
import cofh.gui.GuiHandler;
import cofh.mod.BaseMod;
import cofh.network.PacketHandler;
import cofh.updater.UpdateManager;
import cofh.util.ConfigHandler;
import cofh.util.FMLEventHandler;
import cofh.util.RecipeUpgrade;
import cofh.util.StringHelper;
import cofh.util.fluid.BucketHandler;
import cofh.util.oredict.OreDictionaryArbiter;
import cofh.world.FeatureParser;
import cofh.world.WorldHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import java.io.File;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CoFHCore.modId, name = CoFHCore.modName, version = CoFHCore.version, dependencies = CoFHCore.dependencies)
public class CoFHCore extends BaseMod {

	public static final String modId = "CoFHCore";
	public static final String modName = "CoFH Core";
	public static final String version = CoFHProps.VERSION;
	public static final String dependencies = CoFHProps.DEPENDENCIES;
	public static final String releaseURL = "http://teamcofh.com/cofhcore/version/version.txt";

	@Instance(modId)
	public static CoFHCore instance;
	public static final ConfigHandler config = new ConfigHandler(version);
	public static Logger log = LogManager.getLogger(modId);

	@SidedProxy(clientSide = "cofh.core.ProxyClient", serverSide = "cofh.core.Proxy")
	public static Proxy proxy;

	/* INIT SEQUENCE */
	public CoFHCore() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		UpdateManager.registerUpdater(new UpdateManager(this, releaseURL));

		CoFHProps.configDir = event.getModConfigurationDirectory();

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHCore.cfg")));

		String category = "general";

		String comment = "Enable this to be informed of non-critical updates. You will still receive critical update notifications.";
		CoFHProps.enableUpdateNotice = config.get(category, "EnableUpdateNotifications", true, comment);

		category = "gui";
		CoFHProps.enableInformationTabs = config.get(category, "EnableInformationTabs", true);
		CoFHProps.enableTutorialTabs = config.get(category, "EnableTutorialTabs", true);

		category = "gui.hud";
		CoFHProps.enableItemPickupModule = config.get(category, "EnableItemPickupModule", true,
				"Enable messages that notify you of item pickups. Note: You cannot disable this if your Minecraft username is \"Jadedcat\"");

		category = "gui.tooltips";
		comment = "This adds a tooltip prompting you to press Shift for more details on various items.";
		StringHelper.displayShiftForDetail = config.get(category, "DisplayHoldShiftForDetail", true, comment);

		comment = "This option determines if items contained in other items are displayed as a single quantity or a stack count.";
		StringHelper.displayStackCount = config.get(category, "DisplayStackCountInInventory", false, comment);

		category = "security";
		comment = "Enable this to allow for Server Ops to access 'secure' blocks. Your players will be warned upon server connection. (Default: false)";
		CoFHProps.enableOpSecureAccess = config.get(category, "OpsCanAccessSecureBlocks", false, comment);

		comment = "Enable this to be warned about Ops having access to 'secure' blocks when connecting to a server. (Default: true)";
		CoFHProps.enableOpSecureAccessWarning = config.get(category, "OpsCanAccessSecureBlocksWarning", true, comment);

		config.save();

		MinecraftForge.EVENT_BUS.register(proxy);
		WorldHandler.initialize();
		FMLEventHandler.initialize();
		BucketHandler.initialize();
		OreDictionaryArbiter.initialize();
		RecipeSorter.register("cofh:upgrade", RecipeUpgrade.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");

		registerOreDictionaryEntries();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		PacketHandler.instance.initialize();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

		proxy.registerKeyBinds();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		CoFHEnchantment.postInit();

		proxy.registerRenderInformation();
		proxy.registerTickHandlers();
		proxy.registerPacketInformation();

		PacketHandler.instance.postInit();
		config.cleanUp(false, true);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		try {
			FeatureParser.parseGenerationFile();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

		RegistryEnderAttuned.linkConf = new Configuration(new File(DimensionManager.getCurrentSaveRootDirectory(), "/cofh/EnderFrequencies.cfg"));
		RegistryEnderAttuned.linkConf.load();
		CommandHandler.initCommands(event);
		server = event.getServer();
	}

	public static MinecraftServer server;

	public static final GuiHandler guiHandler = new GuiHandler();

	static {
		// log.setParent(FMLLog.getLogger());
		// TODO: parents?
	}

	public void registerOreDictionaryEntries() {

		registerOreDictionaryEntry("cloth", new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		registerOreDictionaryEntry("coal", new ItemStack(Items.coal, 1, 0));
		registerOreDictionaryEntry("charcoal", new ItemStack(Items.coal, 1, 1));

		registerOreDictionaryEntry("ingotIron", new ItemStack(Items.iron_ingot));
		registerOreDictionaryEntry("ingotGold", new ItemStack(Items.gold_ingot));
	}

	private boolean registerOreDictionaryEntry(String oreName, ItemStack ore) {

		if (OreDictionary.getOres(oreName).isEmpty()) {
			OreDictionary.registerOre(oreName, ore);
			return true;
		}
		return false;
	}

	/* BaseMod */
	@Override
	public String getModId() {

		return modId;
	}

	@Override
	public String getModName() {

		return modName;
	}

	@Override
	public String getModVersion() {

		return version;
	}

}
