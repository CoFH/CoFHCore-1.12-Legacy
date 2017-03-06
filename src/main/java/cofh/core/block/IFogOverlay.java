package cofh.core.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Implement this on your block to change fog colour when the player is submerged.
 * Useful for fluids.
 *
 * @author covers1624
 */
public interface IFogOverlay {

	/**
	 * This is called AFTER vanilla handles fog colour for its fluids in EntityRenderer.
	 *
	 * Vanilla handling is between:
	 * Ln ~1758 -- 1781
	 *
	 * @param state            The State at the Entities head.
	 * @param renderViewEntity The entity at the ViewPort.
	 * @param fogColourRed     The current Red fog colour.
	 * @param fogColourGreen   The current Green fog colour.
	 * @param fogColourBlue    The current Blue fog colour.
	 * @return The modified or new fog colour.(x=red, y=green, z=blue)
	 */
	Vec3d getFog(IBlockState state, Entity renderViewEntity, float fogColourRed, float fogColourGreen, float fogColourBlue);

}
