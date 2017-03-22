package cofh.core.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Implement this on your block to change fog color when the player is submerged.
 * Useful for fluids.
 *
 * @author covers1624
 */
public interface IFogOverlay {

	/**
	 * This is called AFTER vanilla handles fog color for its fluids in EntityRenderer.
	 *
	 * Vanilla handling is between:
	 * Ln ~1758 -- 1781
	 *
	 * @param state            The State at the Entities head.
	 * @param renderViewEntity The entity at the ViewPort.
	 * @param fogColorRed      The current Red fog color.
	 * @param fogColorGreen    The current Green fog color.
	 * @param fogColorBlue     The current Blue fog color.
	 * @return The modified or new fog color.(x=red, y=green, z=blue)
	 */
	Vec3d getFog(IBlockState state, Entity renderViewEntity, float fogColorRed, float fogColorGreen, float fogColorBlue);

}
