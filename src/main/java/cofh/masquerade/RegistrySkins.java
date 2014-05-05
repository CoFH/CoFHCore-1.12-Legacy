package cofh.masquerade;

import ibxm.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cofh.CoFHCore;
import cofh.api.core.ISimpleRegistry;
import cofh.core.CoFHProps;
import cofh.util.CoreUtils;

public class RegistrySkins implements ISimpleRegistry {

	private static HashMap<String, String> skinMap = new HashMap();

	public static boolean allowPlayersUse = true;
	public static boolean allowOpsUse = true;
	public static boolean allowOpsOthers = false;

	public static Configuration skinConf;

	public static void initialize() {

		registerDefaults();

		skinConf = new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHMasquerade-Skins.cfg"));
		skinConf.load();

		if (!skinConf.hasCategory("skins")) {
			return;
		}
		for (String name : skinConf.getCategory("skins").keySet()) {
			Property curSkin = skinConf.getCategory("skins").get(name);
			addSkin(name, curSkin.getString(), false);
		}
		CoFHCore.log.info("Loaded " + skinConf.getCategory("skins").keySet().size() + " skins.");
	}

	public static boolean addSkin(String playerName, String skinURL, boolean saveToFile) {

		if (playerName == null || skinURL == null) {
			return false;
		}
		playerName = playerName.toLowerCase();
		skinMap.put(playerName, skinURL);
		if (saveToFile) {
			skinConf.get("skins", playerName, skinURL);
			skinConf.save();
		}
		return true;
	}

	public static boolean removeSkin(String playerName, boolean removeFromFile) {

		if (playerName == null) {
			return false;
		}
		playerName = playerName.toLowerCase();
		skinMap.remove(playerName);
		if (removeFromFile) {
			if (skinConf.hasCategory("skins")) {
				skinConf.getCategory("skins").remove(playerName);
				skinConf.save();
			} else {
				registerDefaults();
				return false;
			}
		}
		registerDefaults();
		return true;
	}

	public static String getPlayerSkin(String playerName) {

		if (playerName == null || !skinMap.containsKey(playerName.toLowerCase())) {
			return String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(playerName) });
		}
		return skinMap.get(playerName.toLowerCase());
	}

	public static boolean playerCanAccess(String playerName) {

		return allowPlayersUse ? true : allowOpsUse && CoreUtils.isOp(playerName);
	}

	public static boolean playerCanSetOthers(String playerName) {

		return allowOpsOthers && CoreUtils.isOp(playerName) && (allowPlayersUse || allowOpsUse);
	}

	/* PACKETS */
	public static void readJoinPacket(DataInputStream data) {

		try {
			skinMap = new HashMap();
			int count = data.readByte(); // Fuck you if you can fit more then
											// 255 people on your modded server :P
			String name, link;
			for (int i = 0; i < count; i++) {
				name = data.readUTF();
				link = data.readUTF();

				addSkin(name, link, false);
			}
		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: SJ");
		}
	}

	public static void readAddPacket(DataInputStream data) {

		try {
			String name, link;
			name = data.readUTF();
			link = data.readUTF();

			addSkin(name, link, false);

		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: SA");
		}
	}

	public static void sendJoinPacket(EntityPlayer player) {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);

		int sendCount = 0;
		try {

			List<EntityPlayer> players = CoFHCore.proxy.getPlayerList();

			if (players.size() <= 0) {
				return;
			}

			for (int i = 0; i < players.size(); i++) {
				if (getPlayerSkin(players.get(i).getCommandSenderName()) != null) {
					sendCount++;
				}
			}

			data.writeByte(MasqueradePacketHandler.Type.SKIN_JOIN.ordinal());
			data.writeByte(sendCount);

			for (int i = 0; i < players.size(); i++) {
				if (getPlayerSkin(players.get(i).getCommandSenderName()) != null) {
					data.writeUTF(players.get(i).getCommandSenderName());
					data.writeUTF(getPlayerSkin(players.get(i).getCommandSenderName()));
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (sendCount == 0) {
			return;
		}
		PacketDispatcher.sendPacketToPlayer(Payload.getPacket(MasqueradePacketHandler.packetID, bytes), (Player) player);
	}

	public static void sendAddPacket(String playerUsername) {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {

			if (getPlayerSkin(playerUsername) == null) {
				return;
			}
			data.writeByte(MasqueradePacketHandler.Type.SKIN_ADD.ordinal());
			data.writeUTF(playerUsername);
			data.writeUTF(getPlayerSkin(playerUsername));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToAllPlayers(Payload.getPacket(MasqueradePacketHandler.packetID, bytes));
	}

	public static void readRemovePacket(DataInputStream data) {

		try {
			String name;
			name = data.readUTF();

			removeSkin(name, false);

		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: SR");
		}
	}

	public static void sendRemovePacket(String playerUsername) {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeByte(MasqueradePacketHandler.Type.SKIN_REMOVE.ordinal());
			data.writeUTF(playerUsername);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToAllPlayers(Payload.getPacket(MasqueradePacketHandler.packetID, bytes));
	}

	public static void registerDefaults() {

		// registerSkin("ZeldoKavira",
		// "https://dl.dropbox.com/u/27292492/cape/zeldo.png", false);
		// registerSkin("KingLemmingCoFH",
		// "https://dl.dropbox.com/u/57416963/Minecraft/kl_skin.png", false);
	}

	/* ISimpleRegistry */
	@Override
	public boolean register(String playerName, String skinURL) {

		return addSkin(playerName, skinURL, false);
	}

}
