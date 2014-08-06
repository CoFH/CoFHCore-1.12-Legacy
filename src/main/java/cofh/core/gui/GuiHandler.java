package cofh.core.gui;

import cofh.core.block.TileCoFHBase;
import cofh.core.gui.client.GuiFriendsList;
import cofh.core.gui.container.ContainerFriendsList;
import cpw.mods.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GuiHandler implements IGuiHandler {

	public static final int TILE_ID = 0;
	public static final int FRIENDS_ID = 1;

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
		case TILE_ID:
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileCoFHBase) {
				return ((TileCoFHBase) tile).getGuiClient(player.inventory);
			}
			return null;
		case FRIENDS_ID:
			return new GuiFriendsList(player.inventory);
		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
		case TILE_ID:
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileCoFHBase) {
				return ((TileCoFHBase) tile).getGuiServer(player.inventory);
			}
			return null;
		case FRIENDS_ID:
			return new ContainerFriendsList(player.inventory);
		default:
			return null;
		}
	}

}
