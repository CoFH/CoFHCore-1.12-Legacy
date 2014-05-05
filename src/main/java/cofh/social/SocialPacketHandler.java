package cofh.social;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cofh.CoFHSocial;
import cofh.network.PacketHandler;

public class SocialPacketHandler implements IGeneralPacketHandler {

	public static SocialPacketHandler instance = new SocialPacketHandler();

	public static int packetID;

	public static void initialize() {

		packetID = PacketHandler.getAvailablePacketIdAndRegister(instance);
	}

	@Override
	public void handlePacket(int id, Payload payload, EntityPlayer player) throws Exception {

		switch (Type.values()[payload.getByte()]) {
		case FRIEND_LIST:
			int size = payload.getInt();
			RegistryFriends.clientPlayerFriends = new LinkedList<String>();
			for (int i = 0; i < size; i++) {
				RegistryFriends.clientPlayerFriends.add(payload.getString());
			}
			java.util.Collections.sort(RegistryFriends.clientPlayerFriends);
			CoFHSocial.proxy.updateFriendListGui();
			return;
		case ADD_FRIEND:
			RegistryFriends.addFriend(player.getCommandSenderName(), payload.getString());
			RegistryFriends.sendFriendsToPlayer((EntityPlayerMP) player);
			return;
		case REMOVE_FRIEND:
			RegistryFriends.removeFriend(player.getCommandSenderName(), payload.getString());
			RegistryFriends.sendFriendsToPlayer((EntityPlayerMP) player);
			return;
		}
	}

	public enum Type {
		FRIEND_LIST, ADD_FRIEND, REMOVE_FRIEND
	}

}
