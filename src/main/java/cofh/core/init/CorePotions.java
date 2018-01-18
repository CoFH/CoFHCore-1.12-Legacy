package cofh.core.init;

import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class CorePotions {

	public static final CorePotions INSTANCE = new CorePotions();

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

		wither.setRegistryName("wither");
		witherLong.setRegistryName("wither+");
		witherStrong.setRegistryName("wither2");

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {

		event.getRegistry().register(haste);
		event.getRegistry().register(hasteLong);
		event.getRegistry().register(hasteStrong);

		event.getRegistry().register(resistance);
		event.getRegistry().register(resistanceLong);
		event.getRegistry().register(resistanceStrong);

		event.getRegistry().register(levitation);
		event.getRegistry().register(levitationLong);

		event.getRegistry().register(absorption);
		event.getRegistry().register(absorptionLong);
		event.getRegistry().register(absorptionStrong);

		event.getRegistry().register(wither);
		event.getRegistry().register(witherLong);
		event.getRegistry().register(witherStrong);

		final int min = CoreProps.POTION_MIN;
		final int max = CoreProps.POTION_MAX;

		createStrongPotionTypes(event, PotionTypes.LEAPING, min, max);
		createStrongPotionTypes(event, PotionTypes.SWIFTNESS, min, max);
		createStrongPotionTypes(event, PotionTypes.HEALING, min, max);
		createStrongPotionTypes(event, PotionTypes.HARMING, min, max);
		createStrongPotionTypes(event, PotionTypes.POISON, min, max);
		createStrongPotionTypes(event, PotionTypes.REGENERATION, min, max);
		createStrongPotionTypes(event, PotionTypes.STRENGTH, min, max);

		createStrongPotionTypes(event, CorePotions.haste, min, Math.min(max, 4));
		createStrongPotionTypes(event, CorePotions.resistance, min, max);
		createStrongPotionTypes(event, CorePotions.absorption, min, max);
		createStrongPotionTypes(event, CorePotions.wither, min, max);
	}

	/* HELPERS */
	public void createStrongPotionTypes(RegistryEvent.Register<PotionType> event, PotionType type, int minAmplifier, int maxAmplifier) {

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
			potion.setRegistryName(baseName + i);
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

	public static PotionType wither;
	public static PotionType witherLong;
	public static PotionType witherStrong;

}
