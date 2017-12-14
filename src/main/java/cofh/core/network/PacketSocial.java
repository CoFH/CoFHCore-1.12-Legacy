package cofh.core.network;

import cofh.CoFHCore;
import cofh.core.util.RegistrySocial;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedList;

public class PacketSocial extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(PacketSocial.class);
	}

	public enum PacketTypes {
		FRIEND_LIST, ADD_FRIEND, REMOVE_FRIEND
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		switch (PacketTypes.values()[getByte()]) {
			case FRIEND_LIST:
				int size = getInt();
				RegistrySocial.clientPlayerFriends = new LinkedList<>();
				for (int i = 0; i < size; i++) {
					RegistrySocial.clientPlayerFriends.add(getString());
				}
				java.util.Collections.sort(RegistrySocial.clientPlayerFriends);
				CoFHCore.proxy.updateFriendListGui();
				return;
			case ADD_FRIEND:
				RegistrySocial.addFriend(((EntityPlayerMP) player).getGameProfile(), getString());
				RegistrySocial.sendFriendsToPlayer((EntityPlayerMP) player);
				return;
			case REMOVE_FRIEND:
				RegistrySocial.removeFriend(((EntityPlayerMP) player).getGameProfile(), getString());
				RegistrySocial.sendFriendsToPlayer((EntityPlayerMP) player);
		}
	}

}
