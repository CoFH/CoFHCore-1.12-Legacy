package cofh.core.gui;

import cofh.core.block.TileCore;
import cofh.core.gui.client.GuiAugments;
import cofh.core.gui.client.GuiFriendList;
import cofh.core.gui.container.ContainerAugments;
import cofh.core.gui.container.ContainerFriendsList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GuiHandler implements IGuiHandler {

	public static final int TILE_ID = 0;
	public static final int TILE_CONFIG_ID = 1;
	public static final int FRIENDS_ID = 2;
	public static final int AUGMENTS_ID = 3;

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
			case TILE_ID:
				TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getGuiClient(player.inventory);
				}
				return null;
			case TILE_CONFIG_ID:
				tile = world.getTileEntity(new BlockPos(x, y, z));
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getConfigGuiClient(player.inventory);
				}
				return null;
			case FRIENDS_ID:
				return new GuiFriendList(player.inventory);
			case AUGMENTS_ID:
				return new GuiAugments(player.inventory);
			default:
				return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
			case TILE_ID:
				TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getGuiServer(player.inventory);
				}
				return null;
			case TILE_CONFIG_ID:
				tile = world.getTileEntity(new BlockPos(x, y, z));
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getConfigGuiServer(player.inventory);
				}
				return null;
			case FRIENDS_ID:
				return new ContainerFriendsList(player.inventory);
			case AUGMENTS_ID:
				return new ContainerAugments(player.inventory);
			default:
				return null;
		}
	}

}
