package cofh.core.init;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CorePotions {

	public static final CorePotions INSTANCE = new CorePotions();

	private CorePotions() {

	}

	/* INIT */
	public static void preInit() {

		haste = new PotionType("haste", new PotionEffect(MobEffects.HASTE, 3600));
		hasteLong = new PotionType("long_haste", new PotionEffect(MobEffects.HASTE, 9600));
		hasteStrong = new PotionType("strong_haste", new PotionEffect(MobEffects.HASTE, 1800, 1));

		resistance = new PotionType("resistance", new PotionEffect(MobEffects.RESISTANCE, 3600));
		resistanceLong = new PotionType("long_resistance", new PotionEffect(MobEffects.RESISTANCE, 9600));
		resistanceStrong = new PotionType("strong_resistance", new PotionEffect(MobEffects.RESISTANCE, 1800, 1));

		levitation = new PotionType("levitation", new PotionEffect(MobEffects.LEVITATION, 3600));
		levitationLong = new PotionType("long_levitation", new PotionEffect(MobEffects.LEVITATION, 9600));

		absorption = new PotionType("absorption", new PotionEffect(MobEffects.ABSORPTION, 3600));
		absorptionLong = new PotionType("long_absorption", new PotionEffect(MobEffects.ABSORPTION, 9600));
		absorptionStrong = new PotionType("strong_absorption", new PotionEffect(MobEffects.ABSORPTION, 1800, 1));

		saturation = new PotionType("saturation", new PotionEffect(MobEffects.SATURATION, 3600));
		saturationLong = new PotionType("long_saturation", new PotionEffect(MobEffects.SATURATION, 9600));
		saturationStrong = new PotionType("strong_saturation", new PotionEffect(MobEffects.SATURATION, 1800, 1));

		haste.setRegistryName("haste");
		hasteLong.setRegistryName("long_haste");
		hasteStrong.setRegistryName("strong_haste");

		resistance.setRegistryName("resistance");
		resistanceLong.setRegistryName("long_resistance");
		resistanceStrong.setRegistryName("strong_resistance");

		levitation.setRegistryName("levitation");
		levitationLong.setRegistryName("long_levitation");

		absorption.setRegistryName("absorption");
		absorptionLong.setRegistryName("long_absorption");
		absorptionStrong.setRegistryName("strong_absorption");

		saturation.setRegistryName("saturation");
		saturationLong.setRegistryName("long_saturation");
		saturationStrong.setRegistryName("strong_saturation");

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

		event.getRegistry().register(saturation);
		event.getRegistry().register(saturationLong);
		event.getRegistry().register(saturationStrong);
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

	public static PotionType saturation;
	public static PotionType saturationLong;
	public static PotionType saturationStrong;

}
