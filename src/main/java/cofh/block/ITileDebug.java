package cofh.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITileDebug {

	public void debugTile(ForgeDirection side, EntityPlayer player);

}
