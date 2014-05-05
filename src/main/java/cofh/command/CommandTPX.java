package cofh.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandTPX implements ISubCommand {

	public static CommandTPX instance = new CommandTPX();

	@Override
	public String getCommandName() {

		return "tpx";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		sender.addChatMessage(new ChatComponentText("Sorry about that, still working on this. Available commands are /cofh tps and /cofh killall."));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		// TODO Auto-generated method stub
		return null;
	}

}
