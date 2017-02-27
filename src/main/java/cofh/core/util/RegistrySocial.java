package cofh.core.util;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketSocial.PacketTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class RegistrySocial {

	public static void initialize() {

		friendConf = new Configuration(new File(CoreProps.configDir, "/cofh/core/friends.cfg"));
		friendConf.load();
	}

	public static Configuration friendConf;
	public static List<String> clientPlayerFriends = new LinkedList<>();

	public synchronized static boolean addFriend(GameProfile owner, String friendName) {

		if (owner == null || friendName == null) {
			return false;
		}
		friendConf.get(owner.getId().toString(), friendName.toLowerCase(Locale.US), 1);
		friendConf.save();
		return true;
	}

	public synchronized static boolean removeFriend(GameProfile owner, String friendName) {

		if (owner == null || friendName == null) {
			return false;
		}
		String id = owner.getId().toString();
		friendName = friendName.toLowerCase(Locale.US);
		if (friendConf.hasCategory(id)) {
			if (friendConf.getCategory(id).containsKey(friendName)) {
				friendConf.getCategory(id).remove(friendName);
				friendConf.save();
				return true;
			}
		}
		return false;
	}

	public static boolean playerHasAccess(String playerName, GameProfile owner) {

		if (owner == null || playerName == null) {
			return false;
		}
		if (playerName.equals(owner.getName())) {
			return true;
		}
		String id = owner.getId().toString();
		return (friendConf.hasCategory(id) && friendConf.getCategory(id).containsKey(playerName.toLowerCase(Locale.US)));
	}

	public synchronized static void sendFriendsToPlayer(EntityPlayerMP thePlayer) {

		PacketSocial aPacket = new PacketSocial();
		aPacket.addByte(PacketTypes.FRIEND_LIST.ordinal());
		String id = thePlayer.getGameProfile().getId().toString();
		aPacket.addInt(friendConf.getCategory(id).keySet().size());
		for (String theName : friendConf.getCategory(id).keySet()) {
			aPacket.addString(theName);
		}
		PacketHandler.sendTo(aPacket, thePlayer);
	}

}
