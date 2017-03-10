package cofh.core.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface which can be attached to classes which have to register models. Useful for iteration.
 *
 * @author King Lemming
 */
public interface IModelRegister {

	@SideOnly (Side.CLIENT)
	void registerModels();

}
