package cofh.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import cofh.masquerade.RegistrySkins;
import cofh.util.StringHelper;

public class CommandSkin implements ISubCommand {

	public static CommandSkin instance = new CommandSkin();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "skin";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (RegistrySkins.playerCanAccess(sender.getCommandSenderName())) {

			if (arguments.length < 2 || arguments.length > 4) {
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh skin set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE + "<URL>"));
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh skin clear " + StringHelper.YELLOW + "(PlayerName)"));
				return;
			}
			if (arguments[1].matches("set")) {

				if (arguments.length > 3) {
					if (RegistrySkins.playerCanSetOthers(sender.getCommandSenderName()) || sender.getCommandSenderName().equals(arguments[2])) {
						RegistrySkins.addSkin(arguments[2], arguments[3], true);
						sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + arguments[2] + "'s skin successfully set to " + arguments[3]));
						RegistrySkins.sendAddPacket(arguments[2]);
					} else {
						sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
					}
				} else if (arguments.length > 2) {
					RegistrySkins.addSkin(sender.getCommandSenderName(), arguments[2], true);
					sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + "Skin successfully set to " + arguments[2]));
					RegistrySkins.sendAddPacket(sender.getCommandSenderName());
				} else {
					sender.addChatMessage(new ChatComponentText("Syntax: /cofh skin set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE
							+ "<URL>"));
				}

			} else if (arguments[1].matches("clear")) {

				if (arguments.length > 3) {
					if (RegistrySkins.playerCanSetOthers(sender.getCommandSenderName()) || sender.getCommandSenderName().equals(arguments[2])) {
						RegistrySkins.removeSkin(arguments[2], true);
						sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + arguments[2] + "'s skin successfully cleared."));
						RegistrySkins.sendRemovePacket(arguments[2]);
					} else {
						sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
					}
				} else {
					RegistrySkins.removeSkin(sender.getCommandSenderName(), true);
					sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + "Skin successfully cleared."));
					RegistrySkins.sendRemovePacket(sender.getCommandSenderName());
				}
			} else {
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh skin set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE + "<URL>"));
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh skin clear " + StringHelper.YELLOW + "(PlayerName)"));
				return;
			}
		} else {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "add", "remove" });
		} else if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
