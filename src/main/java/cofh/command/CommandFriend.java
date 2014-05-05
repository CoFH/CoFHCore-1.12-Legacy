package cofh.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import cofh.CoFHCore;
import cofh.social.RegistryFriends;
import cofh.util.StringHelper;

public class CommandFriend implements ISubCommand {

	public static CommandFriend instance = new CommandFriend();

	public boolean validUsername(String username) {

		return username.replaceAll("[a-zA-Z0-9_]", "").matches("");
	}

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "friend";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length > 2) {
			if (arguments[1].equalsIgnoreCase("add")) {
				if (validUsername(arguments[2])) {
					if (RegistryFriends.addFriend(sender.getCommandSenderName(), arguments[2])) {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN
								+ " successfully added to your friends list."));
					} else {
						sender.addChatMessage(new ChatComponentText(StringHelper.RED + "Sorry, there was a problem adding " + StringHelper.YELLOW
								+ arguments[2] + StringHelper.RED + " to your list."));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + "Sorry, that is not an valid username."));
				}
			} else if (arguments[1].equalsIgnoreCase("remove")) {
				if (validUsername(arguments[2])) {
					if (RegistryFriends.removeFriend(sender.getCommandSenderName(), arguments[2])) {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN
								+ " successfully removed from your friends list."));
					} else {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.RED + " was not your friend."));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + "Sorry, that is not an valid username."));
				}
			} else {
				sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh friend " + StringHelper.YELLOW + "<add|remove> " + StringHelper.PINK
						+ "<username>"));
			}
		} else if (arguments.length > 1 && arguments[1].equalsIgnoreCase("gui")) {
			if (sender instanceof EntityPlayerMP) {
				EntityPlayerMP thePlayer = (EntityPlayerMP) sender;
				RegistryFriends.sendFriendsToPlayer(thePlayer);
				thePlayer.openGui(CoFHCore.instance, RegistryFriends.guiId, thePlayer.worldObj, (int) thePlayer.posX, (int) thePlayer.posY,
						(int) thePlayer.posZ);
			}
		} else {
			sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh friend " + StringHelper.YELLOW + "<add|remove> " + StringHelper.PINK
					+ "<username>"));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "add", "remove", "gui" });
		} else if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
