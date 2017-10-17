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

		saturation = new PotionType("saturation", new PotionEffect(MobEffects.SATURATION, 1));
		saturationStrong = new PotionType("saturation", new PotionEffect(MobEffects.SATURATION, 1, 1));

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

		saturation.setRegistryName("saturation");
		saturationStrong.setRegistryName("saturation2");

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
	public static PotionType saturationStrong;

}
