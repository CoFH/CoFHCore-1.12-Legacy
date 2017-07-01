package cofh.core.command;

import cofh.cofhworld.init.FeatureParser;
import cofh.cofhworld.feature.IFeatureGenerator;
import cofh.cofhworld.init.WorldHandler;
import com.google.common.base.Throwables;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandReloadWorldgen implements ISubCommand {

	public static ISubCommand instance = new CommandReloadWorldgen();

	@Override
	public String getCommandName() {

		return "reloadworldgen";
	}

	@Override
	public int getPermissionLevel() {

		return 3;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		for (IFeatureGenerator g : FeatureParser.parsedFeatures) {
			WorldHandler.removeFeature(g);
		}
		FeatureParser.parsedFeatures.clear();

		try {
			FeatureParser.parseGenerationFiles();
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.reloadworldgen.success");
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		return null;
	}

}
