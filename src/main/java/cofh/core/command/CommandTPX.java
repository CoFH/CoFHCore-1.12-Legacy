package cofh.core.command;

import cofh.core.util.helpers.EntityHelper;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class CommandTPX implements ISubCommand {

	public static final CommandTPX INSTANCE = new CommandTPX();

	@Override
	public String getCommandName() {

		return "tpx";
	}

	@Override
	public int getPermissionLevel() {

		return 2;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		// TODO: allow selector commands to target anything (single player, all players[@a], specific entities [@e], etc.)
		// where it makes sense to allow it (e.g., not allowing teleporting a single thing to many things)

		switch (arguments.length) {
			case 0: // () ???? how did we get here again?
			case 1: // (tpx) invalid command
				sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
				throw new WrongUsageException("chat.cofh.command." + getCommandName() + ".syntax");
			case 2: // (tpx {<player>|<dimension>}) teleporting player to self, or self to dimension
				EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
				try {
					EntityPlayerMP player = CommandBase.getPlayer(server, sender, arguments[1]);
					if (!player.equals(playerSender)) {
						player.getPassengers().forEach(Entity::dismountRidingEntity);
						player.dismountRidingEntity();
						if (playerSender.dimension == player.dimension) {
							player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
							CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.otherToSelf", player.getName(), player.posX, player.posY, player.posZ);
						} else {
							EntityHelper.transferPlayerToDimension(player, playerSender.dimension, playerSender.mcServer.getPlayerList());
							player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
							CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionOtherToSelf", player.getName(), player.world.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
						}
					} else {
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.tpx.snark.0"));
					}
					break;
				} catch (PlayerNotFoundException t) {
					int dimension;
					try {
						dimension = CommandBase.parseInt(arguments[1]);
					} catch (CommandException p) { // not a number, assume they wanted a player
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command." + getCommandName() + ".syntax"));
						throw t;
					}
					if (!DimensionManager.isDimensionRegistered(dimension)) {
						throw new CommandException("chat.cofh.command.world.notFound");
					}
					playerSender.getPassengers().forEach(Entity::dismountRidingEntity);
					playerSender.dismountRidingEntity();
					if (playerSender.dimension != dimension) {
						EntityHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getPlayerList());
					}
					playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
					CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionSelf", playerSender.world.provider.getDimensionType().getName(), playerSender.posX, playerSender.posY, playerSender.posZ);
				}
				break;
			case 3: // (tpx <player> {<player>|<dimension>}) teleporting player to player or player to dimension
				EntityPlayerMP player = CommandBase.getPlayer(server, sender, arguments[1]);
				try {
					EntityPlayerMP otherPlayer = CommandBase.getPlayer(server, sender, arguments[2]);
					if (!player.equals(otherPlayer)) {
						player.getPassengers().forEach(Entity::dismountRidingEntity);
						player.dismountRidingEntity();
						if (otherPlayer.dimension == player.dimension) {
							player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
							CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.otherTo", player.getName(), otherPlayer.getName(), player.posX, player.posY, player.posZ);
						} else {
							EntityHelper.transferPlayerToDimension(player, otherPlayer.dimension, otherPlayer.mcServer.getPlayerList());
							player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
							CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionOtherTo", player.getName(), otherPlayer.getName(), player.world.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
						}
					} else {
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.tpx.snark.1", arguments[1]));
					}
					break;
				} catch (PlayerNotFoundException t) {
					int dimension = 0;
					try {
						dimension = CommandBase.parseInt(arguments[2]);
					} catch (CommandException p) { // not a number, assume they wanted a player
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command." + getCommandName() + ".syntax"));
						throw t;
					}
					if (!DimensionManager.isDimensionRegistered(dimension)) {
						throw new CommandException("chat.cofh.command.world.notFound");
					}
					player.getPassengers().forEach(Entity::dismountRidingEntity);
					player.dismountRidingEntity();
					if (player.dimension != dimension) {
						EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getPlayerList());
					}
					player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
					CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionOther", player.getName(), player.world.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
				}
				break;
			case 4: // (tpx <x> <y> <z>) teleporting self within dimension
				playerSender = CommandBase.getCommandSenderAsPlayer(sender);
				playerSender.setPositionAndUpdate(CommandBase.parseDouble(playerSender.posX, arguments[1], true), CommandBase.parseDouble(playerSender.posY, arguments[2], true), CommandBase.parseDouble(playerSender.posZ, arguments[3], true));
				CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.self", playerSender.posX, playerSender.posY, playerSender.posZ);
				break;
			case 5: // (tpx {<player> <x> <y> <z> | <x> <y> <z> <dimension>}) teleporting player within player's dimension or self to dimension
				try {
					player = CommandBase.getPlayer(server, sender, arguments[1]);
					player.getPassengers().forEach(Entity::dismountRidingEntity);
					player.dismountRidingEntity();
					player.setPositionAndUpdate(CommandBase.parseDouble(player.posX, arguments[2], true), CommandBase.parseDouble(player.posY, arguments[3], true), CommandBase.parseDouble(player.posZ, arguments[4], true));
					CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.other", player.getName(), player.posX, player.posY, player.posZ);
				} catch (PlayerNotFoundException t) {
					int dimension;
					try {
						dimension = CommandBase.parseInt(arguments[4]);
					} catch (CommandException p) {
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command." + getCommandName() + ".syntax"));
						throw t;
					}
					playerSender = CommandBase.getCommandSenderAsPlayer(sender);
					if (!DimensionManager.isDimensionRegistered(dimension)) {
						throw new CommandException("chat.cofh.command.world.notFound");
					}
					playerSender.getPassengers().forEach(Entity::dismountRidingEntity);
					playerSender.dismountRidingEntity();
					if (playerSender.dimension != dimension) {
						EntityHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getPlayerList());
					}
					playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
					CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionSelf", playerSender.world.provider.getDimensionType().getName(), playerSender.posX, playerSender.posY, playerSender.posZ);
				}
				break;
			case 6: // (tpx <player> <x> <y> <z> <dimension>) teleporting player to dimension and location
			default: // ignore excess tokens. warn?
				player = CommandBase.getPlayer(server, sender, arguments[1]);
				int dimension = CommandBase.parseInt(arguments[5]);

				if (!DimensionManager.isDimensionRegistered(dimension)) {
					throw new CommandException("chat.cofh.command.world.notFound");
				}
				player.getPassengers().forEach(Entity::dismountRidingEntity);
				player.dismountRidingEntity();
				if (player.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getPlayerList());
				}
				player.setPositionAndUpdate(CommandBase.parseDouble(player.posX, arguments[2], true), CommandBase.parseDouble(player.posY, arguments[3], true), CommandBase.parseDouble(player.posZ, arguments[4], true));
				CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.tpx.dimensionOther", player.getName(), player.world.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
				break;
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2 || args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
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
