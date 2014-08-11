package cofh.core.command;

import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.EntityHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;

public class CommandTPX implements ISubCommand {

	public static CommandTPX instance = new CommandTPX();

	@Override
	public String getCommandName() {

		return "tpx";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		// TODO: allow selector commands to target anything (single player, all players[@a], specific entities [@e], etc.)
		// where it makes sense to allow it (e.g., not allowing teleporting a single thing to many things)

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}
		switch (arguments.length) {

		case 0: // () ???? how did we get here again?
		case 1: // (tpx) invalid command
			sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh tpx " + StringHelper.PINK + "[username] " + StringHelper.YELLOW
					+ "[<x> <y> <z>] <dimension id>"));
			break;
		case 2: // (tpx {<player>|<dimension>}) teleporting player to self, or self to dimension
			EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
			try {
				EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
				if (!player.equals(playerSender)) {
					player.mountEntity((Entity) null);
					if (playerSender.dimension == player.dimension) {
						player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
					} else {
						EntityHelper.transferPlayerToDimension(player, playerSender.dimension, playerSender.mcServer.getConfigurationManager());
						player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
					}
				} else {
					sender.addChatMessage(new ChatComponentText("Sucessfully teleported you to yourself!"));
				}
				break;
			} catch (Throwable t) {
				int dimension;
				try {
					dimension = Integer.parseInt(arguments[1]);
				} catch (Throwable p) { // not a number, assume they wanted a player
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t; // player error is
					}
					throw new RuntimeException(t);
				}
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + "That dimension does not exist."));
					break;
				}
				playerSender.mountEntity((Entity) null);
				if (playerSender.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getConfigurationManager());
				}
				playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
			}
			break;
		case 3: // (tpx <player> {<player>|<dimension>}) teleporting player to player or player to dimension
			EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
			try {
				EntityPlayerMP otherPlayer = CommandBase.getPlayer(sender, arguments[2]);
				if (!player.equals(otherPlayer)) {
					player.mountEntity((Entity) null);
					if (otherPlayer.dimension == player.dimension) {
						player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
					} else {
						EntityHelper.transferPlayerToDimension(player, otherPlayer.dimension, otherPlayer.mcServer.getConfigurationManager());
						player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
					}
				} else {
					sender.addChatMessage(new ChatComponentText("Sucessfully teleported " + arguments[1] + " to themself!"));
				}
				break;
			} catch (Throwable t) {
				int dimension;
				try {
					dimension = Integer.parseInt(arguments[2]);
				} catch (Throwable p) { // not a number, assume they wanted a player
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t; // player error is
					}
					throw new RuntimeException(t);
				}
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + "That dimension does not exist"));
					break;
				}
				player.mountEntity((Entity) null);
				if (player.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
				}
				player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
			}
			break;
		case 4: // (tpx <x> <y> <z>) teleporting self within dimension
			playerSender = CommandBase.getCommandSenderAsPlayer(sender);
			playerSender.setPositionAndUpdate(CommandBase.func_110666_a(playerSender, playerSender.posX, arguments[1]),
					CommandBase.func_110666_a(playerSender, playerSender.posY, arguments[2]),
					CommandBase.func_110666_a(playerSender, playerSender.posZ, arguments[3]));
			break;
		case 5: // (tpx <player> <x> <y> <z>) teleporting player within player's dimension
			player = CommandBase.getPlayer(sender, arguments[1]);
			player.mountEntity((Entity) null);
			player.setPositionAndUpdate(CommandBase.func_110666_a(player, player.posX, arguments[2]),
					CommandBase.func_110666_a(player, player.posY, arguments[3]), CommandBase.func_110666_a(player, player.posZ, arguments[4]));
			break;
		case 6: // (tpx <player> <x> <y> <z> <dimension>) teleporting player to dimension and location
		default: // ignore excess tokens. warn?
			player = CommandBase.getPlayer(sender, arguments[1]);
			int dimension = CommandBase.parseInt(sender, arguments[5]);

			if (!DimensionManager.isDimensionRegistered(dimension)) {
				sender.addChatMessage(new ChatComponentText(StringHelper.RED + "That dimension does not exist"));
				break;
			}
			player.mountEntity((Entity) null);
			if (player.dimension != dimension) {
				EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
			}
			player.setPositionAndUpdate(CommandBase.func_110666_a(player, player.posX, arguments[2]),
					CommandBase.func_110666_a(player, player.posY, arguments[3]), CommandBase.func_110666_a(player, player.posZ, arguments[4]));
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2 || args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		} else if (args.length >= 6) {
			Integer[] ids = DimensionManager.getIDs();
			String[] strings = new String[ids.length];

			for (int i = 0; i < ids.length; i++) {
				strings[i] = ids[i].toString();
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, strings);
		}

		return null;

	}

}
