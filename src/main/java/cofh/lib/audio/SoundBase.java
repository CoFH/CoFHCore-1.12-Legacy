package cofh.lib.audio;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Generic ISound class with lots of constructor functionality. Required because - of course - Mojang has no generic that lets you specify *any* arguments for
 * this.
 *
 * @author skyboy
 *
 *         Now with 50% more constructors!
 *         I didnt see the need for this to extend ISound. Maybe in the past that was needed but now positioned sound does everything this does.
 *         So now this entire class is basically just a giant constructor passthrough for PositionedSound.
 *
 *         -brandon3055
 */
@SideOnly (Side.CLIENT)
public class SoundBase extends PositionedSound {

	public SoundBase(SoundEvent sound, SoundCategory category) {

		this(sound, category, 0);
	}

	public SoundBase(SoundEvent sound, SoundCategory category, float volume) {

		this(sound, category, volume, 0);
	}

	public SoundBase(SoundEvent sound, SoundCategory category, float volume, float pitch) {

		this(sound, category, volume, pitch, false, 0);
	}

	public SoundBase(SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {

		this(sound.getSoundName(), category, volume, pitch, repeat, repeatDelay, 0, 0, 0, AttenuationType.NONE);
	}

	public SoundBase(SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {

		this(sound, category, volume, pitch, false, 0, x, y, z);
	}

	public SoundBase(SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z) {

		this(sound.getSoundName(), category, volume, pitch, repeat, repeatDelay, x, y, z, AttenuationType.LINEAR);
	}

	public SoundBase(String sound, SoundCategory category) {

		this(sound, category, 0);
	}

	public SoundBase(String sound, SoundCategory category, float volume) {

		this(sound, category, volume, 0);
	}

	public SoundBase(String sound, SoundCategory category, float volume, float pitch) {

		this(sound, category, volume, pitch, false, 0);
	}

	public SoundBase(String sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {

		this(sound, category, volume, pitch, repeat, repeatDelay, 0, 0, 0, AttenuationType.NONE);
	}

	public SoundBase(String sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {

		this(sound, category, volume, pitch, false, 0, x, y, z);
	}

	public SoundBase(String sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z) {

		this(sound, category, volume, pitch, repeat, repeatDelay, x, y, z, AttenuationType.LINEAR);
	}

	public SoundBase(String sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, AttenuationType attenuation) {

		this(new ResourceLocation(sound), category, volume, pitch, repeat, repeatDelay, x, y, z, attenuation);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category) {

		this(sound, category, 0);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume) {

		this(sound, category, volume, 0);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume, float pitch) {

		this(sound, category, volume, pitch, false, 0);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {

		this(sound, category, volume, pitch, repeat, repeatDelay, 0, 0, 0, AttenuationType.NONE);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {

		this(sound, category, volume, pitch, false, 0, x, y, z);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z) {

		this(sound, category, volume, pitch, repeat, repeatDelay, x, y, z, AttenuationType.LINEAR);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, AttenuationType attenuation) {

		super(sound, category);
		this.volume = volume;
		this.pitch = pitch;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.xPosF = (float) x;
		this.yPosF = (float) y;
		this.zPosF = (float) z;
		this.attenuationType = attenuation;
	}

	public SoundBase(SoundBase other) {

		this(other.getSoundLocation(), other.category, other.volume, other.pitch, other.repeat, other.repeatDelay, other.xPosF, other.yPosF, other.zPosF, other.attenuationType);
	}

}
