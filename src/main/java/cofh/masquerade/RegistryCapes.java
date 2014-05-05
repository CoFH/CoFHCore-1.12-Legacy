package cofh.masquerade;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import ibxm.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cofh.CoFHCore;
import cofh.api.core.ISimpleRegistry;
import cofh.core.CoFHProps;
import cofh.util.CoreUtils;

public class RegistryCapes implements ISimpleRegistry {

	private static TMap<String, String> capeMap = new THashMap<String, String>();

	public static boolean allowPlayersUse = true;
	public static boolean allowOpsUse = true;
	public static boolean allowOpsOthers = false;

	public static Configuration capeConf;

	public static void initialize() {

		registerDefaults();

		capeConf = new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHMasquerade-Capes.cfg"));
		capeConf.load();

		if (!capeConf.hasCategory("capes")) {
			return;
		}
		for (String name : capeConf.getCategory("capes").keySet()) {
			Property curCape = capeConf.getCategory("capes").get(name);
			addCape(name, curCape.getString(), false);
		}
		CoFHCore.log.info("Loaded " + capeConf.getCategory("capes").keySet().size() + " capes.");
	}

	public static boolean addCape(String playerName, String capeURL, boolean saveToFile) {

		if (playerName == null || capeURL == null) {
			return false;
		}
		playerName = playerName.toLowerCase();
		capeMap.put(playerName, capeURL);
		if (saveToFile) {
			capeConf.get("capes", playerName, capeURL);
			capeConf.save();
		}
		return true;
	}

	public static boolean removeCape(String playerName, boolean removeFromFile) {

		if (playerName == null) {
			return false;
		}
		playerName = playerName.toLowerCase();
		capeMap.remove(playerName);
		if (removeFromFile) {
			if (capeConf.hasCategory("capes")) {
				capeConf.getCategory("capes").remove(playerName);
				capeConf.save();
			} else {
				registerDefaults();
				return false;
			}
		}
		registerDefaults();
		return true;
	}

	public static String getPlayerCape(String playerName) {

		if (playerName == null || !capeMap.containsKey(playerName.toLowerCase())) {
			return String.format("http://skins.minecraft.net/MinecraftCloaks/%s.png", new Object[] { StringUtils.stripControlCodes(playerName) });
		}
		return capeMap.get(playerName.toLowerCase());
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
			capeMap = new THashMap<String, String>();
			int count = data.readByte(); // Fuck you if you can fit more then
											// 255 people on your modded server :P
			String name, link;
			for (int i = 0; i < count; i++) {
				name = data.readUTF();
				link = data.readUTF();

				addCape(name, link, false);
			}
		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: CJ");
		}
	}

	public static void readAddPacket(DataInputStream data) {

		try {
			String name, link;
			name = data.readUTF();
			link = data.readUTF();

			addCape(name, link, false);

		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: CA");
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
				if (getPlayerCape(players.get(i).getCommandSenderName()) != null) {
					sendCount++;
				}
			}
			data.writeByte(MasqueradePacketHandler.Type.CAPE_JOIN.ordinal());
			data.writeByte(sendCount);

			for (int i = 0; i < players.size(); i++) {
				if (getPlayerCape(players.get(i).getCommandSenderName()) != null) {
					data.writeUTF(players.get(i).getCommandSenderName());
					data.writeUTF(getPlayerCape(players.get(i).getCommandSenderName()));
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

			if (getPlayerCape(playerUsername) == null) {
				return;
			}
			data.writeByte(MasqueradePacketHandler.Type.CAPE_ADD.ordinal());
			data.writeUTF(playerUsername);
			data.writeUTF(getPlayerCape(playerUsername));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToAllPlayers(Payload.getPacket(MasqueradePacketHandler.packetID, bytes));
	}

	public static void readRemovePacket(DataInputStream data) {

		try {
			String name;
			name = data.readUTF();

			removeCape(name, false);

		} catch (IOException e) {
			CoFHCore.log.error("Packet Payload Failure. ID: CR");
		}
	}

	public static void sendRemovePacket(String playerUsername) {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeByte(MasqueradePacketHandler.Type.CAPE_REMOVE.ordinal());
			data.writeUTF(playerUsername);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToAllPlayers(Payload.getPacket(MasqueradePacketHandler.packetID, bytes));
	}

	public static void registerDefaults() {

		addCape("kingLemmingcofh", "https://dl.dropbox.com/u/57416963/Minecraft/Capes/kinglemmingcofh.png", false);
		addCape("zeldokavira", "https://dl.dropbox.com/u/27292492/cape/zeldo.png", false);
		addCape("jadedcat", "https://dl.dropbox.com/u/57416963/Minecraft/Capes/jadedcat.png", false);
	}

	/* ISimpleRegistry */
	@Override
	public boolean register(String playerName, String capeURL) {

		return addCape(playerName, capeURL, false);
	}

}
