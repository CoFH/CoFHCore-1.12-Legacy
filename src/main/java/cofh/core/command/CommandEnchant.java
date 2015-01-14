package cofh.core.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEnchant implements ISubCommand {

	public static ISubCommand instance = new CommandEnchant();

	@Override
	public String getCommandName() {

		return "enchant";
	}

	@Override
	public int getPermissionLevel() {

		return 2;
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		int l = args.length;
		int i = 1;
		EntityPlayerMP player = null;
		switch (l) {

		case 0:
		case 1:
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		default:
		case 4:
		case 3:
			try {
				player = CommandBase.getPlayer(sender, args[i++]);
			} catch (CommandException t) {
				if (l != 3) {
					sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
					sender.addChatMessage(new ChatComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
					throw t;
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
				CommandHandler.logAdminCommand(sender, this, "commands.enchant.success");
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
