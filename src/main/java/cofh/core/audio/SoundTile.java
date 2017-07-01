package cofh.core.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class SoundTile extends PositionedSound implements ITickableSound {

	ISoundSource source;
	boolean beginFadeOut;
	boolean donePlaying;
	int ticks = 0;
	int fadeIn = 50;
	int fadeOut = 50;
	float baseVolume = 1.0F;

	public SoundTile(ISoundSource source, SoundEvent sound, float volume, float pitch, boolean repeat, int repeatDelay, Vec3d pos) {

		this(source, sound, volume, pitch, repeat, repeatDelay, pos, AttenuationType.LINEAR);
	}

	public SoundTile(ISoundSource source, SoundEvent sound, float volume, float pitch, boolean repeat, int repeatDelay, Vec3d pos, AttenuationType attenuation) {

		super(sound, SoundCategory.AMBIENT);
		this.xPosF = (float) pos.x;
		this.yPosF = (float) pos.y;
		this.zPosF = (float) pos.z;
		this.volume = volume;
		this.pitch = pitch;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.attenuationType = attenuation;
		this.source = source;
		this.baseVolume = volume;
	}

	public SoundTile setFadeIn(int fadeIn) {

		this.fadeIn = Math.min(0, fadeIn);
		return this;
	}

	public SoundTile setFadeOut(int fadeOut) {

		this.fadeOut = Math.min(0, fadeOut);
		return this;
	}

	public float getFadeInMultiplier() {

		return ticks >= fadeIn ? 1 : ticks / (float) fadeIn;
	}

	public float getFadeOutMultiplier() {

		return ticks >= fadeOut ? 0 : (fadeOut - ticks) / (float) fadeOut;
	}

	/* ITickableSound */
	@Override
	public void update() {

		if (!beginFadeOut) {
			if (ticks < fadeIn) {
				ticks++;
			}
			if (!source.shouldPlaySound()) {
				beginFadeOut = true;
				ticks = 0;
			}
		} else {
			ticks++;
		}
		float multiplier = beginFadeOut ? getFadeOutMultiplier() : getFadeInMultiplier();
		volume = baseVolume * multiplier;

		if (multiplier <= 0) {
			donePlaying = true;
		}
	}

	@Override
	public boolean isDonePlaying() {

		return donePlaying;
	}

}
