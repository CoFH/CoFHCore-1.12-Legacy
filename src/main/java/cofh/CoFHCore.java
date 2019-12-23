package cofh;

import cofh.core.command.CommandHandler;
import cofh.core.enchantment.EnchantmentSmashing;
import cofh.core.energy.FurnaceFuelHandler;
import cofh.core.gui.GuiHandler;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CorePotions;
import cofh.core.init.CoreProps;
import cofh.core.key.KeyHandlerCore;
import cofh.core.key.PacketKey;
import cofh.core.network.*;
import cofh.core.proxy.Proxy;
import cofh.core.util.ConfigHandler;
import cofh.core.util.RegistrySocial;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.redstoneflux.RedstoneFluxProps;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = CoFHCore.MOD_ID, name = CoFHCore.MOD_NAME, version = CoFHCore.VERSION, dependencies = CoFHCore.DEPENDENCIES, updateJSON = CoFHCore.UPDATE_URL)
public class CoFHCore {

	public static final String MOD_ID = "cofhcore";
	public static final String MOD_NAME = "CoFH Core";

	public static final String VERSION = "4.6.6";
	public static final String VERSION_MAX = "4.7.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoreProps.FORGE_DEP + RedstoneFluxProps.VERSION_GROUP;
	public static final String MOD_GUI_FACTORY = "cofh.core.gui.GuiConfigCoreFactory";

	@Instance (MOD_ID)
	public static CoFHCore instance;

	@SidedProxy (clientSide = "cofh.core.proxy.ProxyClient", serverSide = "cofh.core.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG_CORE = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_LOOT = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);
	public static final GuiHandler GUI_HANDLER = new GuiHandler();

	public CoFHCore() {

		super();
	}

	/* INIT */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CoreProps.configDir = event.getModConfigurationDirectory();

		CONFIG_CORE.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/core/common.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/core/client.cfg"), true));

		CoreProps.preInit();
		CoreEnchantments.preInit();
		CorePotions.preInit();
		PacketHandler.preInit();
		addOreDictionaryEntries();

		CoreEnchantments.register();

		/* Register Handlers */
		registerHandlers();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		PacketHandler.postInit();

		EnchantmentSmashing.initialize();

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CONFIG_CORE.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {

		CoreProps.server = event.getServer();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

		event.registerServerCommand(CommandHandler.INSTANCE);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		OreDictionaryArbiter.refresh();
		FurnaceFuelHandler.refresh();
	}

	@EventHandler
	public void handleIMC(IMCEvent event) {

		OreDictionaryArbiter.initialize();
	}

	/* HELPERS */
	private void registerHandlers() {

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);
		MinecraftForge.EVENT_BUS.register(KeyHandlerCore.INSTANCE);

		CommandHandler.initialize();
		FurnaceFuelHandler.initialize();

		RegistrySocial.initialize();

		PacketCore.initialize();
		PacketIndexedChat.initialize();
		PacketSocial.initialize();
		PacketKey.initialize();
		PacketTileInfo.initialize();
		PacketTile.initialize();
	}

	private void addOreDictionaryEntries() {

		OreDictionary.registerOre("blockGlowstone", new ItemStack(Blocks.GLOWSTONE));
		OreDictionary.registerOre("blockWool", new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("coal", new ItemStack(Items.COAL));
		OreDictionary.registerOre("charcoal", new ItemStack(Items.COAL, 1, 1));
		OreDictionary.registerOre("dustBlaze", new ItemStack(Items.BLAZE_POWDER));
		OreDictionary.registerOre("rodBlaze", new ItemStack(Items.BLAZE_ROD));

		OreDictionary.registerOre("cropBeetroot", Items.BEETROOT);
		OreDictionary.registerOre("cropMelon", Blocks.MELON_BLOCK);
		OreDictionary.registerOre("cropPumpkin", Blocks.PUMPKIN);
		OreDictionary.registerOre("cropWheat", Items.WHEAT);

		OreDictionary.registerOre("seedBeetroot", Items.BEETROOT_SEEDS);
		OreDictionary.registerOre("seedMelon", Items.MELON_SEEDS);
		OreDictionary.registerOre("seedPumpkin", Items.PUMPKIN_SEEDS);
		OreDictionary.registerOre("seedWheat", Items.WHEAT_SEEDS);

		OreDictionary.registerOre("cofh:potion", Items.POTIONITEM);
		// OreDictionary.registerOre("cofh:potion", Items.SPLASH_POTION);
		// OreDictionary.registerOre("cofh:potion", Items.LINGERING_POTION);

	}

}
