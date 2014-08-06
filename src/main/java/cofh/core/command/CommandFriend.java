package cofh.core.command;

import cofh.CoFHCore;
import cofh.core.gui.GuiHandler;
import cofh.core.util.SocialRegistry;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

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
					if (SocialRegistry.addFriend(sender.getCommandSenderName(), arguments[2])) {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN + " "
								+ StringHelper.localize("info.cofh.command.friend.0")));
					} else {
						sender.addChatMessage(new ChatComponentText(StringHelper.RED + StringHelper.localize("info.cofh.command.friend.1") + " "
								+ StringHelper.YELLOW + arguments[2] + StringHelper.RED + " " + StringHelper.localize("info.cofh.command.friend.2")));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + StringHelper.localize("info.cofh.command.friend.3")));
				}
			} else if (arguments[1].equalsIgnoreCase("remove")) {
				if (validUsername(arguments[2])) {
					if (SocialRegistry.removeFriend(sender.getCommandSenderName(), arguments[2])) {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.GREEN + " "
								+ StringHelper.localize("info.cofh.command.friend.4")));
					} else {
						sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + arguments[2] + StringHelper.RED + " "
								+ StringHelper.localize("info.cofh.command.friend.5")));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(StringHelper.RED + StringHelper.localize("info.cofh.command.friend.3")));
				}
			} else {
				sender.addChatMessage(new ChatComponentText(StringHelper.localize("info.cofh.command.syntaxError") + " /cofh friend " + StringHelper.YELLOW
						+ "|<gui|add|remove> " + StringHelper.PINK + "<username>"));
			}
		} else if (arguments.length > 1 && arguments[1].equalsIgnoreCase("gui")) {
			if (sender instanceof EntityPlayerMP) {
				EntityPlayerMP thePlayer = (EntityPlayerMP) sender;
				SocialRegistry.sendFriendsToPlayer(thePlayer);
				thePlayer.openGui(CoFHCore.instance, GuiHandler.FRIENDS_ID, thePlayer.worldObj, (int) thePlayer.posX, (int) thePlayer.posY,
						(int) thePlayer.posZ);
			}
		} else {
			sender.addChatMessage(new ChatComponentText(StringHelper.localize("info.cofh.command.syntaxError") + " /cofh friend " + StringHelper.YELLOW
					+ "<gui|add|remove> " + StringHelper.PINK + "<username>"));
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
