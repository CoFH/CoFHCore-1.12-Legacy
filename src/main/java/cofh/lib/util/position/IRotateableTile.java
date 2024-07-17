package cofh.lib.util.position;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRotateableTile {

	public boolean canRotate();

	public boolean canRotate(ForgeDirection axis);

	public void rotate(ForgeDirection axis);

	public void rotateDirectlyTo(int facing);

	public ForgeDirection getDirectionFacing();

}
