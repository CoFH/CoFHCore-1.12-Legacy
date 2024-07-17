package cofh.lib.audio;

import net.minecraft.client.audio.ISound;

public interface ISoundSource {

	/**
	 * Should actually return an ISound. The object return prevents server crashes.
	 */
	ISound getSound();

	boolean shouldPlaySound();
}
