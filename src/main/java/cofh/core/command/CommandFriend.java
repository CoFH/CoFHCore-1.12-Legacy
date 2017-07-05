package cofh.core.command;

import cofh.CoFHCore;
import cofh.core.gui.GuiHandler;
import cofh.core.util.RegistrySocial;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class CommandFriend implements ISubCommand {

	public static final CommandFriend INSTANCE = new CommandFriend();

	public boolean validUsername(String username) {

		return username.replaceAll("[a-zA-Z0-9_]", "").matches("");
	}

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "friend";
	}

	@Override
	public int getPermissionLevel() {

		return -1;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		if (arguments.length > 2) {
			EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
			if (arguments[1].equalsIgnoreCase("add")) {
				if (validUsername(arguments[2])) {
					if (RegistrySocial.addFriend(player.getGameProfile(), arguments[2])) {
						sender.sendMessage(new TextComponentString(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN + " " + StringHelper.localize("chat.cofh.command.friend.0")));
					} else {
						sender.sendMessage(new TextComponentString(StringHelper.RED + StringHelper.localize("chat.cofh.command.friend.1") + " " + StringHelper.YELLOW + arguments[2] + StringHelper.RED + " " + StringHelper.localize("chat.cofh.command.friend.2")));
					}
				} else {
					sender.sendMessage(new TextComponentString(StringHelper.RED + StringHelper.localize("chat.cofh.command.friend.3")));
				}
			} else if (arguments[1].equalsIgnoreCase("remove")) {
				if (validUsername(arguments[2])) {
					if (RegistrySocial.removeFriend(player.getGameProfile(), arguments[2])) {
						sender.sendMessage(new TextComponentString(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN + " " + StringHelper.localize("chat.cofh.command.friend.4")));
					} else {
						sender.sendMessage(new TextComponentString(StringHelper.YELLOW + arguments[2] + StringHelper.RED + " " + StringHelper.localize("chat.cofh.command.friend.5")));
					}
				} else {
					sender.sendMessage(new TextComponentString(StringHelper.RED + StringHelper.localize("chat.cofh.command.friend.3")));
				}
			} else {
				sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
				throw new WrongUsageException("chat.cofh.command." + getCommandName() + ".syntax");
			}
		} else if (arguments.length > 1 && (arguments[1].equalsIgnoreCase("gui") || arguments[1].equalsIgnoreCase("list"))) {
			if (sender instanceof EntityPlayerMP) {
				EntityPlayerMP thePlayer = (EntityPlayerMP) sender;
				RegistrySocial.sendFriendsToPlayer(thePlayer);
				thePlayer.openGui(CoFHCore.instance, GuiHandler.FRIENDS_ID, thePlayer.world, (int) thePlayer.posX, (int) thePlayer.posY, (int) thePlayer.posZ);
			}
		} else {
			sender.sendMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("chat.cofh.command." + getCommandName() + ".syntax");
		}
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove", "gui");
		} else if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return null;
	}

}
