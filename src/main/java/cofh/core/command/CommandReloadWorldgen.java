package cofh.core.command;

import cofh.api.world.IFeatureGenerator;
import cofh.core.util.CoreUtils;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import com.google.common.base.Throwables;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandReloadWorldgen implements ISubCommand {

	public static ISubCommand instance = new CommandReloadWorldgen();

	@Override
	public String getCommandName() {

		return "reloadworldgen";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}

		for (IFeatureGenerator g : FeatureParser.parsedFeatures)
			WorldHandler.instance.removeFeature(g);

		try {
			FeatureParser.parseGenerationFile();
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		sender.addChatMessage(new ChatComponentTranslation("info.cofh.success"));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

}
