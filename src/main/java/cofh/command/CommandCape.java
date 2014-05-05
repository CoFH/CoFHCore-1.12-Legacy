package cofh.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import cofh.masquerade.RegistryCapes;
import cofh.util.StringHelper;

public class CommandCape implements ISubCommand {

	public static CommandCape instance = new CommandCape();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "cape";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (RegistryCapes.playerCanAccess(sender.getCommandSenderName())) {

			if (arguments.length < 2 || arguments.length > 4) {
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh cape set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE + "<URL>"));
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh cape clear " + StringHelper.YELLOW + "(PlayerName)"));
				return;
			}
			if (arguments[1].matches("set")) {

				if (arguments.length > 3) {
					if (RegistryCapes.playerCanSetOthers(sender.getCommandSenderName()) || sender.getCommandSenderName().equals(arguments[2])) {
						RegistryCapes.addCape(arguments[2], arguments[3], true);
						sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + arguments[2] + "'s cape successfully set to " + arguments[3]));
						RegistryCapes.sendAddPacket(arguments[2]);
					} else {
						sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
					}
				} else if (arguments.length > 2) {
					RegistryCapes.addCape(sender.getCommandSenderName(), arguments[2], true);
					sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + "Cape successfully set to " + arguments[2]));
					RegistryCapes.sendAddPacket(sender.getCommandSenderName());
				} else {
					sender.addChatMessage(new ChatComponentText("Syntax: /cofh cape set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE
							+ "<URL>"));
				}

			} else if (arguments[1].matches("clear")) {

				if (arguments.length > 3) {
					if (RegistryCapes.playerCanSetOthers(sender.getCommandSenderName()) || sender.getCommandSenderName().equals(arguments[2])) {
						RegistryCapes.removeCape(arguments[2], true);
						sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + arguments[2] + "'s cape successfully cleared."));
						RegistryCapes.sendRemovePacket(arguments[2]);
					} else {
						sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
					}
				} else {
					RegistryCapes.removeCape(sender.getCommandSenderName(), true);
					sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + "Cape successfully cleared."));
					RegistryCapes.sendRemovePacket(sender.getCommandSenderName());
				}
			} else if (arguments[1].matches("test")) {

			} else {

				sender.addChatMessage(new ChatComponentText("Syntax: /cofh cape set " + StringHelper.YELLOW + "(PlayerName) " + StringHelper.WHITE + "<URL>"));
				sender.addChatMessage(new ChatComponentText("Syntax: /cofh cape clear " + StringHelper.YELLOW + "(PlayerName)"));
				return;
			}
		} else {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "set", "clear" });
		} else if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
