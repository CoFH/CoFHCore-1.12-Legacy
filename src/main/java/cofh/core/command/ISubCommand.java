package cofh.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface ISubCommand {

	String getCommandName();

	int getPermissionLevel();

	void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException;

	List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args);

}
