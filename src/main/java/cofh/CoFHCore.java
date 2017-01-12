package cofh;

import cofh.core.CoFHProps;
import cofh.core.Proxy;
import cofh.core.RegistrySocial;
import cofh.core.command.CommandFriend;
import cofh.core.command.CommandHandler;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.entity.DropHandler;
import cofh.core.gui.GuiHandler;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketCore;
import cofh.core.network.PacketCore.PacketTypes;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketSocial;
import cofh.core.util.ConfigHandler;
import cofh.core.util.FMLEventHandler;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.core.util.crafting.RecipeSecure;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.core.util.crafting.RecipeUpgradeOverride;
import cofh.core.util.energy.FurnaceFuelHandler;
import cofh.core.util.fluid.BucketHandler;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.mod.BaseMod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

@Mod(modid = CoFHCore.modId, name = CoFHCore.modName, version = CoFHCore.version, dependencies = CoFHCore.dependencies, guiFactory = CoFHCore.modGuiFactory, customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class CoFHCore extends BaseMod {

    public static final String modId = "cofhcore";
    public static final String modName = "CoFH Core";
    public static final String version = "1.0.0";
    public static final String version_max = "1.1.0";
    public static final String dependencies = CoFHProps.FORGE_DEP;
    public static final String modGuiFactory = "cofh.core.gui.GuiConfigCoreFactory";

    public static final String version_group = "required-after:" + modId + "@[" + version + "," + version_max + ");";
    public static final String releaseURL = "https://raw.github.com/CoFH/VERSION/master/" + modId;

    @Instance(modId)
    public static CoFHCore instance;

    @SidedProxy(clientSide = "cofh.core.ProxyClient", serverSide = "cofh.core.Proxy")
    public static Proxy proxy;

    public static final Logger log = LogManager.getLogger(modId);
    public static final ConfigHandler configCore = new ConfigHandler(version);
    public static final ConfigHandler configLoot = new ConfigHandler(version);
    public static final ConfigHandler configClient = new ConfigHandler(version);
    public static final GuiHandler guiHandler = new GuiHandler();

    public static MinecraftServer server;

    /* INIT SEQUENCE */
    public CoFHCore() {

        super(log);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        CoFHProps.configDir = event.getModConfigurationDirectory();

        //UpdateManager.registerUpdater(new UpdateManager(this, releaseURL, CoFHProps.DOWNLOAD_URL));
        configCore.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/common.cfg"), true));
        configClient.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/client.cfg"), true));

        // EntityRegistry.registerModEntity(EntityCoFHArrow.class, "Arrow", 0, this, 150, 1, false);

        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.preInit();

        moduleCore();
        moduleLoot();

        FeatureParser.initialize();
        WorldHandler.initialize();
        FMLEventHandler.initialize();
        BucketHandler.initialize();
        FurnaceFuelHandler.initialize();
        PacketHandler.instance.initialize();
        RecipeSorter.register("cofh:augment", RecipeAugmentable.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");
        RecipeSorter.register("cofh:secure", RecipeSecure.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");
        RecipeSorter.register("cofh:upgrade", RecipeUpgrade.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");
        RecipeSorter.register("cofh:upgradeoverride", RecipeUpgradeOverride.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");

        registerOreDictionaryEntries();
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {

		/* Register Handlers */
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
        CommandHandler.registerSubCommand(CommandFriend.instance);
        SecurityHelper.setup();
        PacketCore.initialize();
        PacketSocial.initialize();
        RegistrySocial.initialize();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        OreDictionaryArbiter.initialize();
        CoFHEnchantment.postInit();

        proxy.registerKeyBinds();
        proxy.registerRenderInformation();
        proxy.registerTickHandlers();
        proxy.registerPacketInformation();

        PacketHandler.instance.postInit();
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {

        configCore.cleanUp(false, true);

        try {
            FeatureParser.parseGenerationFile();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {

    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {

        OreDictionaryArbiter.initialize();
        CommandHandler.initCommands(event);
        server = event.getServer();
    }

    public void handleConfigSync(PacketCoFHBase payload) {

        FMLEventHandler.instance.handleIdMappingEvent(null);
    }

    public PacketCoFHBase getConfigSync() {

        PacketCoFHBase payload = PacketCore.getPacket(PacketTypes.CONFIG_SYNC);

        return payload;
    }

    public void registerOreDictionaryEntries() {

        registerOreDictionaryEntry("blockCloth", new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
        registerOreDictionaryEntry("coal", new ItemStack(Items.COAL, 1, 0));
        registerOreDictionaryEntry("charcoal", new ItemStack(Items.COAL, 1, 1));
    }

    private boolean registerOreDictionaryEntry(String oreName, ItemStack ore) {

        if (OreDictionary.getOres(oreName).isEmpty()) {
            OreDictionary.registerOre(oreName, ore);
            return true;
        }
        return false;
    }

    private boolean moduleCore() {

        String comment;
        /* GENERAL */
        String category = "General";

        comment = "Set to TRUE to be informed of non-critical updates. You will still receive critical update notifications.";
        CoFHProps.enableUpdateNotice = configCore.get(category, "EnableUpdateNotifications", true, comment);

        comment = "Set to TRUE for this to log when a block is dismantled.";
        CoFHProps.enableDismantleLogging = configCore.get(category, "EnableDismantleLogging", false, comment);

        comment = "Set to TRUE to display death messages for any named entity.";
        CoFHProps.enableLivingEntityDeathMessages = configCore.get(category, "EnableGenericDeathMessage", true, comment);

        comment = "Set to FALSE to disable items on the ground from trying to stack. This can improve server performance.";
        CoFHProps.enableItemStacking = configCore.get(category, "EnableItemStacking", true, comment);

		/* HOLIDAY */
        category = "Holiday";

        comment = "Set this to TRUE to disable April Foolishness.";
        CoFHProps.holidayAprilFools = !configCore.get(category, "IHateApril", false, comment);

        comment = "Set this to TRUE to disable Christmas cheer. Scrooge. :(";
        CoFHProps.holidayChristmas = !configCore.get(category, "HoHoNo", false, comment);

		/* SECURITY */
        category = "Security";

        comment = "Set to TRUE to allow for Server Ops to access 'secure' blocks. Your players will be warned upon server connection.";
        CoFHProps.enableOpSecureAccess = configCore.get(category, "OpsCanAccessSecureBlocks", false, comment);

		/* WORLD TWEAKS */
        category = "World.Tweaks";

        comment = "Set this to a value > 1 to make trees grow more infrequently. Rate is 1 in N. Example: If this value is set to 3, trees will take 3x the time to grow, on average.";
        CoFHProps.treeGrowthChance = configCore.get(category, "TreeGrowthChance", 1, comment);

        configCore.save();

        return true;
    }

    private boolean moduleLoot() {

        configLoot.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/loot.cfg"), true));

        String comment;
		/* GENERAL */
        String category = "General";

        comment = "Set to false to disable this entire module.";
        boolean enable = configLoot.get(category, "EnableModule", true, comment);

        if (!enable) {
            configLoot.save();
            return false;
        }
		/* HEADS */
        category = "Heads";

        comment = "If enabled, mobs only drop heads when killed by players.";
        DropHandler.mobPvEOnly = configLoot.get(category, "MobsDropOnPvEOnly", DropHandler.mobPvEOnly, comment);

        comment = "If enabled, players only drop heads when killed by other players.";
        DropHandler.playerPvPOnly = configLoot.get(category, "PlayersDropOnPvPOnly", DropHandler.playerPvPOnly, comment);

        category = "Heads.Players";
        DropHandler.playersEnabled = configLoot.get(category, "Enabled", DropHandler.playersEnabled);
        DropHandler.playerChance = configLoot.get(category, "Chance", DropHandler.playerChance);

        category = "Heads.Creepers";
        DropHandler.creeperEnabled = configLoot.get(category, "Enabled", DropHandler.creeperEnabled);
        DropHandler.creeperChance = configLoot.get(category, "Chance", DropHandler.creeperChance);

        category = "Heads.Skeletons";
        DropHandler.skeletonEnabled = configLoot.get(category, "Enabled", DropHandler.skeletonEnabled);
        DropHandler.skeletonChance = configLoot.get(category, "Chance", DropHandler.skeletonChance);

        category = "Heads.WitherSkeletons";
        DropHandler.skeletonEnabled = configLoot.get(category, "Enabled", DropHandler.witherSkeletonEnabled);
        DropHandler.witherSkeletonChance = configLoot.get(category, "Chance", DropHandler.witherSkeletonChance);

        category = "Heads.Zombies";
        DropHandler.zombieEnabled = configLoot.get(category, "Enabled", DropHandler.zombieEnabled);
        DropHandler.zombieChance = configLoot.get(category, "Chance", DropHandler.zombieChance);

        configLoot.save();

        MinecraftForge.EVENT_BUS.register(DropHandler.instance);

        return true;
    }

    /* BaseMod */
    @Override
    public String getModId() {

        return modId;
    }

    @Override
    protected String getAssetDir() {

        return "cofh";
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
