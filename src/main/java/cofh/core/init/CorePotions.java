package cofh.core.init;

import cofh.CoFHCore;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class CorePotions {

	public static final CorePotions INSTANCE = new CorePotions();
	public static boolean disableAll = false;
	private static boolean registered = false;

	private CorePotions() {

	}

	/* INIT */
	public static void preInit() {

		int duration = 3600;
		int durationLong = 9600;
		int durationStrong = 1800;

		haste = new PotionType("haste", new PotionEffect(MobEffects.HASTE, duration));
		hasteLong = new PotionType("haste", new PotionEffect(MobEffects.HASTE, durationLong));
		hasteStrong = new PotionType("haste", new PotionEffect(MobEffects.HASTE, durationStrong, 1));

		resistance = new PotionType("resistance", new PotionEffect(MobEffects.RESISTANCE, duration));
		resistanceLong = new PotionType("resistance", new PotionEffect(MobEffects.RESISTANCE, durationLong));
		resistanceStrong = new PotionType("resistance", new PotionEffect(MobEffects.RESISTANCE, durationStrong, 1));

		levitation = new PotionType("levitation", new PotionEffect(MobEffects.LEVITATION, duration));
		levitationLong = new PotionType("levitation", new PotionEffect(MobEffects.LEVITATION, durationLong));

		absorption = new PotionType("absorption", new PotionEffect(MobEffects.ABSORPTION, duration));
		absorptionLong = new PotionType("absorption", new PotionEffect(MobEffects.ABSORPTION, durationLong));
		absorptionStrong = new PotionType("absorption", new PotionEffect(MobEffects.ABSORPTION, durationStrong, 1));

		luck = new PotionType("luck", new PotionEffect(MobEffects.LUCK, duration));
		luckLong = new PotionType("luck", new PotionEffect(MobEffects.LUCK, durationLong));
		luckStrong = new PotionType("luck", new PotionEffect(MobEffects.LUCK, durationStrong, 1));

		unluck = new PotionType("unluck", new PotionEffect(MobEffects.UNLUCK, duration / 2));
		unluckLong = new PotionType("unluck", new PotionEffect(MobEffects.UNLUCK, durationLong / 2));
		unluckStrong = new PotionType("unluck", new PotionEffect(MobEffects.UNLUCK, durationStrong / 2, 1));

		wither = new PotionType("wither", new PotionEffect(MobEffects.WITHER, duration / 4));
		witherLong = new PotionType("wither", new PotionEffect(MobEffects.WITHER, 2 * duration / 4));
		witherStrong = new PotionType("wither", new PotionEffect(MobEffects.WITHER, durationStrong / 4, 1));

		haste.setRegistryName("haste");
		hasteLong.setRegistryName("haste+");
		hasteStrong.setRegistryName("haste2");

		resistance.setRegistryName("resistance");
		resistanceLong.setRegistryName("resistance+");
		resistanceStrong.setRegistryName("resistance2");

		levitation.setRegistryName("levitation");
		levitationLong.setRegistryName("levitation+");

		absorption.setRegistryName("absorption");
		absorptionLong.setRegistryName("absorption+");
		absorptionStrong.setRegistryName("absorption2");

		luck.setRegistryName("luck");
		luckLong.setRegistryName("luck+");
		luckStrong.setRegistryName("luck2");

		unluck.setRegistryName("unluck");
		unluckLong.setRegistryName("unluck+");
		unluckStrong.setRegistryName("unluck2");

		wither.setRegistryName("wither");
		witherLong.setRegistryName("wither+");
		witherStrong.setRegistryName("wither2");
	}

	/* MUST BE CALLED IN PRE-INIT BY SOMETHING */
	public static void register() {

		if (disableAll || registered) {
			return;
		}
		ModContainer callingContainer = Loader.instance().activeModContainer();
		ModContainer cofhContainer = FMLCommonHandler.instance().findContainerFor(CoFHCore.MOD_ID);

		Loader.instance().setActiveModContainer(cofhContainer);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		registered = true;
		Loader.instance().setActiveModContainer(callingContainer);
	}

	public static boolean registered() {

		return registered;
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {

		IForgeRegistry<PotionType> registry = event.getRegistry();

		registry.register(haste);
		registry.register(hasteLong);
		registry.register(hasteStrong);

		registry.register(resistance);
		registry.register(resistanceLong);
		registry.register(resistanceStrong);

		registry.register(levitation);
		registry.register(levitationLong);

		registry.register(absorption);
		registry.register(absorptionLong);
		registry.register(absorptionStrong);

		registry.register(luck);
		registry.register(luckLong);
		registry.register(luckStrong);

		registry.register(unluck);
		registry.register(unluckLong);
		registry.register(unluckStrong);

		registry.register(wither);
		registry.register(witherLong);
		registry.register(witherStrong);

		int min = 3;
		int max = 5;

		createStrongPotionTypes(event, PotionTypes.LEAPING, min, max);
		createStrongPotionTypes(event, PotionTypes.SWIFTNESS, min, max);
		createStrongPotionTypes(event, PotionTypes.HEALING, min, max);
		createStrongPotionTypes(event, PotionTypes.HARMING, min, max);
		createStrongPotionTypes(event, PotionTypes.POISON, min, max);
		createStrongPotionTypes(event, PotionTypes.REGENERATION, min, max);
		createStrongPotionTypes(event, PotionTypes.STRENGTH, min, max);

		createStrongPotionTypes(event, CorePotions.haste, min, max);
		createStrongPotionTypes(event, CorePotions.resistance, min, max);
		createStrongPotionTypes(event, CorePotions.absorption, min, max);
		createStrongPotionTypes(event, CorePotions.luck, min, max);
		createStrongPotionTypes(event, CorePotions.unluck, min, max);
		createStrongPotionTypes(event, CorePotions.wither, min, max);

		min = 2;
		max = 3;

		createStrongPotionTypes(event, PotionTypes.LONG_LEAPING, min, max, "+");
		createStrongPotionTypes(event, PotionTypes.LONG_SWIFTNESS, min, max, "+");
		createStrongPotionTypes(event, PotionTypes.LONG_POISON, min, max, "+");
		createStrongPotionTypes(event, PotionTypes.LONG_REGENERATION, min, max, "+");
		createStrongPotionTypes(event, PotionTypes.LONG_STRENGTH, min, max, "+");

		createStrongPotionTypes(event, CorePotions.hasteLong, min, max, "+");
		createStrongPotionTypes(event, CorePotions.resistanceLong, min, max, "+");
		createStrongPotionTypes(event, CorePotions.absorptionLong, min, max, "+");
		createStrongPotionTypes(event, CorePotions.luckLong, min, max, "+");
		createStrongPotionTypes(event, CorePotions.unluckLong, min, max, "+");
		createStrongPotionTypes(event, CorePotions.witherLong, min, max, "+");
	}

	/* HELPERS */
	public void createStrongPotionTypes(RegistryEvent.Register<PotionType> event, PotionType type, int minAmplifier, int maxAmplifier) {

		createStrongPotionTypes(event, type, minAmplifier, maxAmplifier, "");
	}

	public void createStrongPotionTypes(RegistryEvent.Register<PotionType> event, PotionType type, int minAmplifier, int maxAmplifier, String postfix) {

		List<PotionEffect> baseEffects = type.getEffects();
		String baseName = type.getNamePrefixed("");

		if (baseEffects.isEmpty()) {
			return;
		}
		List<PotionEffect> effects = new ArrayList<>();

		for (int i = minAmplifier; i <= maxAmplifier; i++) {
			effects.clear();
			for (PotionEffect effect : baseEffects) {
				effects.add(new PotionEffect(effect.getPotion(), Math.max(1, effect.getDuration() / (1 + i)), i - 1));
			}
			PotionType potion = new PotionType(baseName, effects.toArray(new PotionEffect[effects.size()]));
			potion.setRegistryName(baseName + i + postfix);
			event.getRegistry().register(potion);
		}
	}

	/* REFERENCES */
	public static PotionType haste;
	public static PotionType hasteLong;
	public static PotionType hasteStrong;

	public static PotionType resistance;
	public static PotionType resistanceLong;
	public static PotionType resistanceStrong;

	public static PotionType levitation;
	public static PotionType levitationLong;

	public static PotionType absorption;
	public static PotionType absorptionLong;
	public static PotionType absorptionStrong;

	public static PotionType luck;
	public static PotionType luckLong;
	public static PotionType luckStrong;

	public static PotionType unluck;
	public static PotionType unluckLong;
	public static PotionType unluckStrong;

	public static PotionType wither;
	public static PotionType witherLong;
	public static PotionType witherStrong;

}
