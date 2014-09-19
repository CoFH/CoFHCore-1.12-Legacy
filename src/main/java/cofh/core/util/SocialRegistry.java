package cofh.core.util;

import cofh.core.CoFHProps;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketSocial.PacketTypes;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

public class SocialRegistry {

	public static void initialize() {

		friendConf = new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHSocial-Friends.cfg"));
		friendConf.load();
	}

	public static Configuration friendConf;
	public static List<String> clientPlayerFriends = new LinkedList<String>();

	public static boolean addFriend(String ownerName, String friendName) {

		if (ownerName == null || friendName == null) {
			return false;
		}
		friendConf.get(ownerName.toLowerCase(), friendName.toLowerCase(), 1);
		friendConf.save();
		return true;
	}

	public static boolean removeFriend(String ownerName, String friendName) {

		if (ownerName == null || friendName == null) {
			return false;
		}
		ownerName = ownerName.toLowerCase();
		friendName = friendName.toLowerCase();
		if (friendConf.hasCategory(ownerName)) {
			if (friendConf.getCategory(ownerName).containsKey(friendName)) {
				friendConf.getCategory(ownerName).remove(friendName);
				friendConf.save();
				return true;
			}
		}
		return false;
	}

	public static boolean playerHasAccess(String playerName, String ownerName) {

		return playerName != null
				&& ownerName != null
				&& (playerName.toLowerCase().matches(ownerName.toLowerCase()) || friendConf.hasCategory(ownerName.toLowerCase()) ? friendConf.getCategory(
						ownerName.toLowerCase()).containsKey(playerName.toLowerCase()) ? true : false : false);
	}

	public static void sendFriendsToPlayer(EntityPlayerMP thePlayer) {

		PacketSocial aPacket = new PacketSocial();
		aPacket.addByte(PacketTypes.FRIEND_LIST.ordinal());
		aPacket.addInt(friendConf.getCategory(thePlayer.getCommandSenderName().toLowerCase()).keySet().size());
		for (String theName : friendConf.getCategory(thePlayer.getCommandSenderName().toLowerCase()).keySet()) {
			aPacket.addString(theName);
		}
		PacketHandler.sendTo(aPacket, thePlayer);
	}

}
