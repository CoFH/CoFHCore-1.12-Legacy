package cofh.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITileInfo {

	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug);

}
