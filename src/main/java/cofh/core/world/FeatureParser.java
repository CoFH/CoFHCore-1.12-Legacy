package cofh.core.world;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.api.world.IGeneratorParser;
import cofh.core.CoFHProps;
import cofh.core.util.CoreUtils;
import cofh.core.world.decoration.BoulderParser;
import cofh.core.world.decoration.ClusterParser;
import cofh.core.world.decoration.DungeonParser;
import cofh.core.world.decoration.GeodeParser;
import cofh.core.world.decoration.LakeParser;
import cofh.core.world.decoration.LargeLakeParser;
import cofh.core.world.decoration.LargeVeinParser;
import cofh.core.world.decoration.PlateParser;
import cofh.core.world.decoration.SmallTreeParser;
import cofh.core.world.decoration.SpikeParser;
import cofh.core.world.decoration.StalagmiteParser;
import cofh.core.world.feature.CaveParser;
import cofh.core.world.feature.DecorationParser;
import cofh.core.world.feature.FractalParser;
import cofh.core.world.feature.NormalParser;
import cofh.core.world.feature.SurfaceParser;
import cofh.core.world.feature.UnderfluidParser;
import cofh.core.world.feature.UniformParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.WeightedRandomItemStack;
import cofh.lib.util.WeightedRandomNBTTag;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.biome.BiomeInfo;
import cofh.lib.world.biome.BiomeInfoRarity;
import cofh.lib.world.biome.BiomeInfoSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase.TempCategory;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeatureParser {

	private static File worldGenFolder;
	private static File vanillaGen;
	private static final String vanillaGenInternal = "assets/cofh/world/Vanilla.json";
	private static HashMap<String, IFeatureParser> templateHandlers = new HashMap<String, IFeatureParser>();
	private static HashMap<String, IGeneratorParser> generatorHandlers = new HashMap<String, IGeneratorParser>();
	private static Logger log = LogManager.getLogger("CoFHWorld");
	public static ArrayList<IFeatureGenerator> parsedFeatures = new ArrayList<IFeatureGenerator>();

	private FeatureParser() {

	}

	public static boolean registerTemplate(String template, IFeatureParser handler) {

		// TODO: provide this function through IFeatureHandler?
		if (!templateHandlers.containsKey(template)) {
			templateHandlers.put(template, handler);
			return true;
		}
		log.error("Attempted to register duplicate template '" + template + "'!");
		return false;
	}

	public static boolean registerGenerator(String template, IGeneratorParser handler) {

		// TODO: provide this function through IFeatureHandler?
		if (!generatorHandlers.containsKey(template)) {
			generatorHandlers.put(template, handler);
			return true;
		}
		log.error("Attempted to register duplicate generator '" + template + "'!");
		return false;
	}

	public static void initialize() {

		worldGenFolder = new File(CoFHProps.configDir, "/cofh/world/");

		if (!worldGenFolder.exists()) {
			try {
				worldGenFolder.mkdir();
			} catch (Throwable t) {
				// pokemon!
			}
		}
		vanillaGen = new File(CoFHProps.configDir, "/cofh/world/Vanilla.json");

		try {
			if (vanillaGen.createNewFile()) {
				CoreUtils.copyFileUsingStream(vanillaGenInternal, vanillaGen);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		log.info("Registering Default Templates.");
		registerTemplate("normal", new NormalParser());
		registerTemplate("uniform", new UniformParser());
		registerTemplate("surface", new SurfaceParser());
		registerTemplate("fractal", new FractalParser());
		registerTemplate("decoration", new DecorationParser());
		registerTemplate("underwater", new UnderfluidParser(true));
		registerTemplate("underfluid", new UnderfluidParser(false));
		registerTemplate("cave", new CaveParser());

		log.info("Registering default generators");
		registerGenerator(null, new ClusterParser(false));
		registerGenerator("", new ClusterParser(false));
		registerGenerator("cluster", new ClusterParser(false));
		registerGenerator("sparse-cluster", new ClusterParser(true));
		registerGenerator("large-vein", new LargeVeinParser());
		registerGenerator("decoration", new DecorationParser());
		registerGenerator("lake", new LakeParser());
		registerGenerator("plate", new PlateParser());
		registerGenerator("geode", new GeodeParser());
		registerGenerator("spike", new SpikeParser());
		registerGenerator("boulder", new BoulderParser());
		registerGenerator("dungeon", new DungeonParser());
		registerGenerator("stalagmite", new StalagmiteParser(false));
		registerGenerator("stalactite", new StalagmiteParser(true));
		registerGenerator("small-tree", new SmallTreeParser());

		log.info("Complete");
	}

	private static void addFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String name) {

				if (name == null) {
					return false;
				}
				return name.toLowerCase(Locale.US).endsWith(".json") || new File(file, name).isDirectory();
			}
		});

		if (fList == null || fList.length <= 0) {
			log.error("There are no World Generation files present in " + folder + ".");
			return;
		}
		log.info("CoFH Core found " + fList.length + " World Generation files present in " + folder + "/.");
		list.addAll(Arrays.asList(fList));
	}

	public static void parseGenerationFile() {

		JsonParser parser = new JsonParser();

		ArrayList<File> worldGenList = new ArrayList<File>(5);
		addFiles(worldGenList, worldGenFolder);
		for (int i = 0, e = worldGenList.size(); i < e; ++i) {
			File genFile = worldGenList.get(i);
			if (genFile.equals(vanillaGen)) {
				if (!WorldHandler.genReplaceVanilla) {
					worldGenList.remove(i);
				}
				break;
			}
		}

		for (int i = 0; i < worldGenList.size(); ++i) {

			File genFile = worldGenList.get(i);
			if (genFile.isDirectory()) {
				addFiles(worldGenList, genFile);
				continue;
			}

			JsonObject genList;
			try {
				genList = (JsonObject) parser.parse(new InputStreamReader(new FileInputStream(genFile), "utf8"));
			} catch (Throwable t) {
				log.error("Critical error reading from a world generation file: " + genFile + " > Please be sure the file is correct!", t);
				continue;
			}

			log.info("Reading world generation info from: " + genFile + ":");
			for (Entry<String, JsonElement> genEntry : genList.entrySet()) {
				try {
					if (parseGenerationEntry(genEntry.getKey(), genEntry.getValue())) {
						log.debug("Generation entry successfully parsed: \"" + genEntry.getKey() + "\"");
					} else {
						log.error("Error parsing generation entry: \"" + genEntry.getKey() + "\" > Please check the parameters. It *may* be a duplicate.");
					}
				} catch (Throwable t) {
					log.fatal("There was a severe error parsing '" + genEntry.getKey() + "'!", t);
				}
			}
		}
	}

	private static boolean parseGenerationEntry(String featureName, JsonElement genEntry) {

		JsonObject genObject = genEntry.getAsJsonObject();

		if (genObject.has("enabled")) {
			if (!genObject.get("enabled").getAsBoolean()) {
				log.info('"' + featureName + "\" is disabled.");
				return true;
			}
		}

		String templateName = parseTemplate(genObject);
		IFeatureParser template = templateHandlers.get(templateName);
		if (template != null) {
			IFeatureGenerator feature = template.parseFeature(featureName, genObject, log);
			if (feature != null) {
				parsedFeatures.add(feature);
				return WorldHandler.addFeature(feature);
			}
			log.warn("Template '" + templateName + "' failed to parse its entry!");
		} else {
			log.warn("Unknown template + '" + templateName + "'.");
		}

		return false;
	}

	private static String parseTemplate(JsonObject genObject) {

		JsonElement genElement = genObject.get("template");
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();

			return genObject.get("type").getAsString();
		} else {
			return genElement.getAsString();
		}
	}

	// TODO: move these helper functions outside core?

	public static WorldGenerator parseGenerator(String def, JsonObject genObject, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		JsonElement genElement = genObject.get("template");
		String name = def;
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();
		}
		if (genObject.has("generator")) {
			genElement = genObject.get("generator");
			if (genElement.isJsonObject()) {
				genObject = genElement.getAsJsonObject();
				name = genObject.get("type").getAsString();
			} else {
				name = genElement.getAsString();
			}
			if (!generatorHandlers.containsKey(name)) {
				log.warn("Unknown generator '%s'! using '%s'", name, def);
				name = def;
			}
		}

		IGeneratorParser parser = generatorHandlers.get(name);
		if (parser == null) {
			throw new IllegalStateException("Generator " + name + " is not registered!");
		}
		return parser.parseGenerator(name, genObject, log, resList, clusterSize, matList);
	}

	public static BiomeInfoSet parseBiomeRestrictions(JsonObject genObject) {

		BiomeInfoSet set = null;
		if (genObject.has("biomes")) {
			JsonArray restrictionList = genObject.getAsJsonArray("biomes");
			set = new BiomeInfoSet(restrictionList.size());
			for (int i = 0, e = restrictionList.size(); i < e; i++) {
				BiomeInfo info = null;
				JsonElement element = restrictionList.get(i);
				if (element.isJsonNull()) {
					log.info("Null biome entry. Ignoring.");
				} else if (element.isJsonObject()) {
					JsonObject obj = element.getAsJsonObject();
					String type = obj.get("type").getAsString();
					boolean wl = obj.has("whitelist") ? obj.get("whitelist").getAsBoolean() : true;
					JsonElement value = obj.get("entry");
					JsonArray array = value.isJsonArray() ? value.getAsJsonArray() : null;
					String entry = array != null ? null : value.getAsString();
					int rarity = obj.has("rarity") ? obj.get("rarity").getAsInt() : -1;

					l: if (type.equalsIgnoreCase("name")) {
						if (array != null) {
							ArrayList<String> names = new ArrayList<String>();
							for (int k = 0, j = array.size(); k < j; k++) {
								names.add(array.get(k).getAsString());
							}
							if (rarity > 0) {
								info = new BiomeInfoRarity(names, 4, true, rarity);
							} else {
								info = new BiomeInfo(names, 4, true);
							}
						} else {
							if (rarity > 0) {
								info = new BiomeInfoRarity(entry, rarity);
							} else {
								info = new BiomeInfo(entry);
							}
						}
					} else {
						Object data = null;
						int t = -1;
						if (type.equalsIgnoreCase("temperature")) {
							if (array != null) {
								ArrayList<TempCategory> temps = new ArrayList<TempCategory>();
								for (int k = 0, j = array.size(); k < j; k++) {
									temps.add(TempCategory.valueOf(array.get(k).getAsString()));
								}
								data = EnumSet.copyOf(temps);
								t = 5;
							} else {
								data = TempCategory.valueOf(entry);
								t = 1;
							}
						} else if (type.equalsIgnoreCase("dictionary")) {
							if (array != null) {
								ArrayList<Type> tags = new ArrayList<Type>();
								for (int k = 0, j = array.size(); k < j; k++) {
									Type a = Type.valueOf(array.get(k).getAsString());
									if (a != null) {
										tags.add(a);
									}
								}
								data = tags.toArray(new Type[tags.size()]);
								t = 6;
							} else {
								data = Type.valueOf(entry);
								t = 2;
							}
						} else {
							log.warn("Biome entry of unknown type");
							break l;
						}
						if (data != null) {
							if (rarity > 0) {
								info = new BiomeInfoRarity(data, t, wl, rarity);
							} else {
								info = new BiomeInfo(data, t, wl);
							}
						}
					}
				} else {
					info = new BiomeInfo(element.getAsString());
				}
				if (info != null) {
					set.add(info);
				}
			}
		}
		return set;
	}

	public static Block parseBlockName(String blockRaw) {

		String[] blockTokens = blockRaw.split(":", 2);
		int i = 0;
		return GameRegistry.findBlock(blockTokens.length > 1 ? blockTokens[i++] : "minecraft", blockTokens[i]);
	}

	public static WeightedRandomBlock parseBlockEntry(JsonElement genElement, boolean clamp) {

		final int min = clamp ? 0 : -1;
		if (genElement.isJsonNull()) {
			log.warn("Null Block entry!");
			return null;
		} else if (genElement.isJsonObject()) {
			JsonObject blockElement = genElement.getAsJsonObject();
			if (!blockElement.has("name")) {
				log.error("Block entry needs a name!");
				return null;
			}
			Block block = parseBlockName(blockElement.get("name").getAsString());
			if (block == null) {
				log.error("Invalid block entry!");
				return null;
			}
			int metadata = blockElement.has("metadata") ? MathHelper.clamp(blockElement.get("metadata").getAsInt(), min, 15) : min;
			int weight = blockElement.has("weight") ? MathHelper.clamp(blockElement.get("weight").getAsInt(), 1, 1000000) : 100;
			return new WeightedRandomBlock(block, metadata, weight);
		} else {
			Block block = parseBlockName(genElement.getAsString());
			if (block == null) {
				log.error("Invalid block entry!");
				return null;
			}
			return new WeightedRandomBlock(block, min);
		}
	}

	public static boolean parseResList(JsonElement genElement, List<WeightedRandomBlock> resList, boolean clamp) {

		if (genElement == null) {
			return false;
		}

		if (genElement.isJsonArray()) {
			JsonArray blockList = genElement.getAsJsonArray();

			for (int i = 0, e = blockList.size(); i < e; i++) {
				WeightedRandomBlock entry = parseBlockEntry(blockList.get(i), clamp);
				if (entry == null) {
					return false;
				}
				resList.add(entry);
			}
		} else {
			WeightedRandomBlock entry = parseBlockEntry(genElement, clamp);
			if (entry == null) {
				return false;
			}
			resList.add(entry);
		}
		return true;
	}

	public static WeightedRandomNBTTag parseEntityEntry(JsonElement genElement) {

		if (genElement.isJsonNull()) {
			log.warn("Null entity entry!");
			return null;
		} else if (genElement.isJsonObject()) {
			JsonObject genObject = genElement.getAsJsonObject();
			NBTTagCompound data;
			if (genObject.has("spawnerTag")) {
				try {
					data = (NBTTagCompound) JsonToNBT.func_150315_a(genObject.get("spawnerTag").toString());
				} catch (NBTException e) {
					log.error("Invalid entity entry!", e);
					return null;
				}
			} else {
				data = new NBTTagCompound();
				String type = genObject.get("entity").getAsString();
				if (type == null) {
					log.error("Invalid entity entry!");
					return null;
				}
				data.setString("EntityId", type);
			}
			return new WeightedRandomNBTTag(genObject.get("weight").getAsInt(), data);
		} else {
			String type = genElement.getAsString();
			if (type == null) {
				log.error("Invalid entity entry!");
				return null;
			}
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("EntityId", type);
			return new WeightedRandomNBTTag(100, tag);
		}
	}

	public static boolean parseEntityList(JsonElement genElement, List<WeightedRandomNBTTag> list) {

		if (genElement.isJsonArray()) {
			JsonArray blockList = genElement.getAsJsonArray();

			for (int i = 0, e = blockList.size(); i < e; i++) {
				WeightedRandomNBTTag entry = parseEntityEntry(blockList.get(i));
				if (entry == null) {
					return false;
				}
				list.add(entry);
			}
		} else {
			WeightedRandomNBTTag entry = parseEntityEntry(genElement);
			if (entry == null) {
				return false;
			}
			list.add(entry);
		}
		return true;
	}

	public static DungeonMob parseWeightedStringEntry(JsonElement genElement) {

		int weight = 100;
		String type = null;
		if (genElement.isJsonNull()) {
			log.warn("Null string entry!");
			return null;
		} else if (genElement.isJsonObject()) {
			JsonObject genObject = genElement.getAsJsonObject();
			type = genObject.get("name").getAsString();
			if (type == null) {
				log.warn("Invalid string entry!");
				return null;
			}
			if (genObject.has("weight")) {
				weight = genObject.get("weight").getAsInt();
			}
		} else {
			type = genElement.getAsString();
			if (type == null) {
				log.warn("Invalid string entry!");
				return null;
			}
		}
		return new DungeonMob(weight, type);
	}

	public static boolean parseWeightedStringList(JsonElement genElement, List<DungeonMob> list) {

		if (genElement.isJsonArray()) {
			JsonArray blockList = genElement.getAsJsonArray();

			for (int i = 0, e = blockList.size(); i < e; i++) {
				DungeonMob entry = parseWeightedStringEntry(blockList.get(i));
				if (entry == null) {
					return false;
				}
				list.add(entry);
			}
		} else {
			DungeonMob entry = parseWeightedStringEntry(genElement);
			if (entry == null) {
				return false;
			}
			list.add(entry);
		}
		return true;
	}

	public static WeightedRandomItemStack parseWeightedRandomItem(JsonElement genElement) {

		if (genElement.isJsonNull()) {
			return null;
		}
		int metadata = 0, stackSize = 1, chance = 100;
		ItemStack stack;

		if (genElement.isJsonPrimitive()) {
			stack = new ItemStack(GameData.getItemRegistry().getObject(genElement.getAsString()), 1, metadata);
		} else {
			JsonObject item = genElement.getAsJsonObject();

			if (item.has("meta")) {
				metadata = item.get("meta").getAsInt();
			} else if (item.has("metadata")) {
				metadata = item.get("metadata").getAsInt();
			}
			if (item.has("stackSize")) {
				stackSize = item.get("stackSize").getAsInt();
			} else if (item.has("quantity")) {
				stackSize = item.get("quantity").getAsInt();
			} else if (item.has("amount")) {
				stackSize = item.get("amount").getAsInt();
			}
			if (stackSize <= 0) {
				stackSize = 1;
			}
			if (item.has("weight")) {
				chance = item.get("weight").getAsInt();
			} else if (item.has("chance")) {
				chance = item.get("chance").getAsInt();
			}
			if (item.has("oreName") && ItemHelper.oreNameExists(item.get("oreName").getAsString())) {
				ItemStack oreStack = OreDictionary.getOres(item.get("oreName").getAsString()).get(0);
				stack = ItemHelper.cloneStack(oreStack, stackSize);
			} else {
				if (!item.has("name")) {
					log.error("Item entry missing valid name or oreName!");
				}
				stack = new ItemStack(GameData.getItemRegistry().getObject(item.get("name").getAsString()), stackSize, metadata);
			}
			if (item.has("nbt")) {
				try {
					NBTBase nbtbase = JsonToNBT.func_150315_a(item.get("nbt").getAsString());

					if (!(nbtbase instanceof NBTTagCompound)) {
						log.error("Item has invalid NBT data.");
					}
					stack.setTagCompound((NBTTagCompound) nbtbase);
				} catch (NBTException t) {
					log.error("Item has invalid NBT data.", t);
				}
			}
		}
		if (stack.getItem() == null) {
			return null;
		}
		return new WeightedRandomItemStack(stack, chance);
	}

	public static boolean parseWeightedItemList(JsonElement genElement, List<WeightedRandomItemStack> res) {

		if (!genElement.isJsonArray()) {
			WeightedRandomItemStack entry = parseWeightedRandomItem(genElement);
			if (entry == null) {
				return false;
			}
			res.add(entry);
		} else {
			JsonArray list = genElement.getAsJsonArray();

			for (int i = 0, e = list.size(); i < e; ++i) {
				WeightedRandomItemStack entry = parseWeightedRandomItem(list.get(i));
				if (entry == null) {
					return false;
				}
				res.add(entry);
			}
		}
		return true;
	}

}
