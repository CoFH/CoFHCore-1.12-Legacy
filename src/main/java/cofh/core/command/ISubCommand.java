package cofh.core.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface ISubCommand {

	public int getPermissionLevel();

	public String getCommandName();

	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException;

	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args);

}
