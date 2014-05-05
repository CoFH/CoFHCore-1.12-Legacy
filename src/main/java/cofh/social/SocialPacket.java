package cofh.social;

import cofh.CoFHSocial;
import cofh.network.CoFHPacket;
import cofh.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedList;

public class SocialPacket extends CoFHPacket {

	public static void initialize() {

		PacketHandler.cofhPacketHandler.registerPacket(SocialPacket.class);
	}

	@Override
	public void handlePacket(EntityPlayer player) {

		switch (Type.values()[getByte()]) {
			case FRIEND_LIST:
				int size = getInt();
				RegistryFriends.clientPlayerFriends = new LinkedList<String>();
				for (int i = 0; i < size; i++) {
					RegistryFriends.clientPlayerFriends.add(getString());
				}
				java.util.Collections.sort(RegistryFriends.clientPlayerFriends);
				CoFHSocial.proxy.updateFriendListGui();
				return;
			case ADD_FRIEND:
				RegistryFriends.addFriend(player.getCommandSenderName(), getString());
				RegistryFriends.sendFriendsToPlayer((EntityPlayerMP) player);
				return;
			case REMOVE_FRIEND:
				RegistryFriends.removeFriend(player.getCommandSenderName(), getString());
				RegistryFriends.sendFriendsToPlayer((EntityPlayerMP) player);
				return;
		}
	}

	public enum Type {
		FRIEND_LIST, ADD_FRIEND, REMOVE_FRIEND
	}

}
