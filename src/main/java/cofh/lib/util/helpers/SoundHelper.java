package cofh.lib.util.helpers;

import cofh.lib.audio.SoundBase;
import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;

/**
 * Contains various helper functions to assist with Sound manipulation.
 *
 * @author King Lemming
 *
 */
public class SoundHelper {

	public static final SoundHandler soundManager = FMLClientHandler.instance().getClient().getSoundHandler();
	
	private SoundHelper() {
		
	}

	/**
	 * This allows you to have some tricky functionality with Tile Entities. Just be sure you aren't dumb.
	 */
	public static void playSound(Object sound) {

		if (sound instanceof ISound) {
			soundManager.playSound((ISound) sound);
		}
	}

	public static void playSound(ISound sound) {

		soundManager.playSound(sound);
	}

	public static void playSound(String soundName, float x, float y, float z, float volume, float pitch) {

		soundManager.playSound(new SoundBase(soundName, volume, pitch, x, y, z));
	}

}
