package cofh;

import cofh.core.command.CommandHandler;
import cofh.core.energy.FurnaceFuelHandler;
import cofh.core.gui.GuiHandler;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.key.KeyHandlerCore;
import cofh.core.key.PacketKey;
import cofh.core.network.*;
import cofh.core.proxy.Proxy;
import cofh.core.util.ConfigHandler;
import cofh.core.util.RegistrySocial;
import cofh.core.util.oredict.OreDictionaryArbiter;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

@Mod (modid = CoFHCore.MOD_ID, name = CoFHCore.MOD_NAME, version = CoFHCore.VERSION, dependencies = CoFHCore.DEPENDENCIES, updateJSON = CoFHCore.UPDATE_URL)
public class CoFHCore {

	public static final String MOD_ID = "cofhcore";
	public static final String MOD_NAME = "CoFH Core";

	public static final String VERSION = "4.3.0";
	public static final String VERSION_MAX = "4.4.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoreProps.FORGE_DEP;
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
		PacketHandler.preInit();
		addOreDictionaryEntries();

		/* Register Handlers */
		registerHandlers();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		addCraftingRecipes();

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		OreDictionaryArbiter.initialize();

		PacketHandler.postInit();

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CoreProps.loadComplete();
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

		CommandHandler.initCommands(event);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		OreDictionaryArbiter.refresh();
		FurnaceFuelHandler.refresh();
	}

	/* HELPERS */
	private void registerHandlers() {

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);
		MinecraftForge.EVENT_BUS.register(KeyHandlerCore.instance);
		MinecraftForge.EVENT_BUS.register(proxy);

		FurnaceFuelHandler.initialize();

		RegistrySocial.initialize();

		PacketCore.initialize();
		PacketIndexedChat.initialize();
		PacketSocial.initialize();
		PacketKey.initialize();
		PacketTileInfo.initialize();
		PacketTile.initialize();
	}

	private void addCraftingRecipes() {

		// @formatter:off
		if (CoreProps.enableHorseArmorCrafting) {
			addShapedRecipe(new ItemStack(Items.IRON_HORSE_ARMOR, 1),
					"  H",
					"ICI",
					"III",
					'C', "blockWool",
					'H', Items.IRON_HELMET,
					'I', "ingotIron"
			);

			addShapedRecipe(new ItemStack(Items.GOLDEN_HORSE_ARMOR),
					"  H",
					"ICI",
					"III",
					'C', "blockWool",
					'H', Items.GOLDEN_HELMET,
					'I', "ingotGold"
			);

			addShapedRecipe(new ItemStack(Items.DIAMOND_HORSE_ARMOR),
					"  H",
					"ICI",
					"III",
					'C', "blockWool",
					'H', Items.DIAMOND_HELMET,
					'I', "gemDiamond"
			);
		}
		if (CoreProps.enableSaddleCrafting) {
			addShapedRecipe(new ItemStack(Items.SADDLE),
					"LLL",
					"LIL",
					"I I",
					'I', "ingotIron",
					'L', Items.LEATHER
			);
		}
		// @formatter:on
	}

	private void addOreDictionaryEntries() {

		OreDictionary.registerOre("blockWool", new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("coal", new ItemStack(Items.COAL, 1, 0));
		OreDictionary.registerOre("charcoal", new ItemStack(Items.COAL, 1, 1));
	}

}
