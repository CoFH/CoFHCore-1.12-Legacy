package cofh.core.command;

import cofh.api.world.IFeatureGenerator;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import com.google.common.base.Throwables;

import java.util.List;

import net.minecraft.command.ICommandSender;

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
	public void handleCommand(ICommandSender sender, String[] args) {

		for (IFeatureGenerator g : FeatureParser.parsedFeatures)
			WorldHandler.instance.removeFeature(g);

		try {
			FeatureParser.parseGenerationFile();
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.reloadworldgen.success");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

}
