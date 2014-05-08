package cofh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.command.CommandHandler;
import cofh.core.CoFHProps;
import cofh.core.Proxy;
import cofh.entity.CoFHPlayerTracker;
import cofh.gui.GuiHandler;
import cofh.mod.BaseMod;
import cofh.network.PacketHandler;
import cofh.updater.UpdateManager;
import cofh.util.ConfigHandler;
import cofh.util.RegistryEnderAttuned;
import cofh.util.StringHelper;
import cofh.util.fluid.BucketHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = CoFHCore.modId, name = CoFHCore.modName, version = CoFHCore.version, dependencies = "required-after:Forge@[" + CoFHProps.FORGE_REQ + ",)")
public class CoFHCore extends BaseMod {

	public static final String modId = "CoFHCore";
	public static final String version = CoFHProps.VERSION;
	public static final String modName = CoFHProps.NAME;

	@Instance("CoFHCore")
	public static CoFHCore instance;
	public static final ConfigHandler config = new ConfigHandler(CoFHProps.VERSION);
	public static Logger log = LogManager.getLogger(modId);

	@SidedProxy(clientSide = "cofh.core.ProxyClient", serverSide = "cofh.core.Proxy")
	public static Proxy proxy;

	/* INIT SEQUENCE */
	public CoFHCore() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		UpdateManager.registerUpdater(new UpdateManager(this, CoFHProps.RELEASE_URL));

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
		CoFHPlayerTracker.initialize();
		BucketHandler.initialize();

		registerOreDictionaryEntries();
		fixOreDerptionary();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		PacketHandler.instance.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		proxy.registerRenderInformation();
		proxy.registerTickHandlers();
		proxy.registerPacketInformation();

		PacketHandler.instance.postInit();
		config.cleanUp(false, true);
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

		// registerOreDictionaryEntry("sandstone", new
		// ItemStack(Blocks.sandstone, 1, OreDictionary.WILDCARD_VALUE));
		// registerOreDictionaryEntry("glass", Blocks.glass);
		// registerOreDictionaryEntry("oreCoal", Blocks.coal_ore);
		registerOreDictionaryEntry("cloth", new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		// registerOreDictionaryEntry("blockGold", Blocks.gold_block);
		// registerOreDictionaryEntry("blockIron", Blocks.iron_block);
		// registerOreDictionaryEntry("blockDiamond", Blocks.diamond_block);
		// registerOreDictionaryEntry("blockGlowstone", Blocks.glowstone);
		// registerOreDictionaryEntry("blockEmerald", Blocks.emerald_block);
		// registerOreDictionaryEntry("blockRedstone", Blocks.redstone_block);
		registerOreDictionaryEntry("coal", new ItemStack(Items.coal, 1, 0));
		registerOreDictionaryEntry("charcoal", new ItemStack(Items.coal, 1, 1));
		// registerOreDictionaryEntry("ingotIron", Items.iron_ingot);
		// registerOreDictionaryEntry("ingotGold", Items.gold_ingot);
		// registerOreDictionaryEntry("dustRedstone", Items.redstone);
		// registerOreDictionaryEntry("slimeball", Items.slime_ball);
		// registerOreDictionaryEntry("dustGlowstone", Items.glowstone_dust);
		// registerOreDictionaryEntry("nuggetGold", Items.gold_nugget);
	}

	public void fixOreDerptionary() {

		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
		List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();

		ItemStack[] stairs = new ItemStack[4];

		stairs[0] = new ItemStack(Blocks.oak_stairs);
		stairs[1] = new ItemStack(Blocks.spruce_stairs);
		stairs[2] = new ItemStack(Blocks.birch_stairs);
		stairs[3] = new ItemStack(Blocks.jungle_stairs);

		for (Object obj : recipes) {
			if (obj instanceof ShapedOreRecipe) {
				ShapedOreRecipe recipe = (ShapedOreRecipe) obj;
				ItemStack output = recipe.getRecipeOutput();
				for (int i = 0; i < 4; i++) {
					if (output.getItem() == stairs[i].getItem()) {
						recipesToRemove.add(recipe);
					}
				}
			} else if (obj instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) obj;
				ItemStack output = recipe.getRecipeOutput();
				for (int i = 0; i < 4; i++) {
					if (output.getItem() == stairs[i].getItem()) {
						recipesToRemove.add(recipe);
					}
				}
			}
		}

		recipesToAdd
				.add(new ShapedOreRecipe(new ItemStack(Blocks.oak_stairs, 4), new Object[] { "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 0) }));
		recipesToAdd.add(new ShapedOreRecipe(new ItemStack(Blocks.spruce_stairs, 4), new Object[] { "#  ", "## ", "###", '#',
				new ItemStack(Blocks.planks, 1, 1) }));
		recipesToAdd.add(new ShapedOreRecipe(new ItemStack(Blocks.birch_stairs, 4),
				new Object[] { "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 2) }));
		recipesToAdd.add(new ShapedOreRecipe(new ItemStack(Blocks.jungle_stairs, 4), new Object[] { "#  ", "## ", "###", '#',
				new ItemStack(Blocks.planks, 1, 3) }));

		recipes.removeAll(recipesToRemove);
		recipes.addAll(recipesToAdd);
	}

	private boolean registerOreDictionaryEntry(String oreName, Block ore) {

		return registerOreDictionaryEntry(oreName, new ItemStack(ore));
	}

	private boolean registerOreDictionaryEntry(String oreName, Item ore) {

		return registerOreDictionaryEntry(oreName, new ItemStack(ore));
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
