package cofh.core.audio;

import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISoundSource {

	@SideOnly (Side.CLIENT)
	ISound getSound();

	@SideOnly (Side.CLIENT)
	boolean shouldPlaySound();

}
