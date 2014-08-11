package cofh.core.command;

import cofh.core.util.CoreUtils;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandEnchant implements ISubCommand {

	public static ISubCommand instance = new CommandEnchant();

	@Override
	public String getCommandName() {

		return "enchant";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}
		int l = args.length;
		int i = 1;
		EntityPlayerMP player = null;
		switch (l) {

		case 0:
		case 1:
			// TODO: error
			break;

		default:
		case 4:
		case 3:
			try {
				player = CommandBase.getPlayer(sender, args[i++]);
			} catch (Throwable t) {
				if (l != 3) {
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t;
					} else {
						throw new RuntimeException(t);
					}
				}
				--i;
			}
		case 2:
			if (player == null) {
				player = CommandBase.getCommandSenderAsPlayer(sender);
			}
			int id = CommandBase.parseIntBounded(sender, args[i++], 0, Enchantment.enchantmentsList.length - 1);
			int level = 1;
			ItemStack itemstack = player.getCurrentEquippedItem();

			if (itemstack == null) {
				throw new CommandException("commands.enchant.noItem", new Object[0]);
			} else {
				Enchantment enchantment = Enchantment.enchantmentsList[id];

				if (enchantment == null) {
					throw new NumberInvalidException("commands.enchant.notFound", new Object[] { Integer.valueOf(id) });
				}
				if (i < l) {
					level = CommandBase.parseInt(sender, args[i++]);
				}

				itemstack.addEnchantment(enchantment, level);
				// CommandBase.func_152373_a(sender, this, "commands.enchant.success", new Object[0]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
