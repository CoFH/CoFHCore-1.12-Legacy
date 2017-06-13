package cofh.core.command;

import net.minecraft.command.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

//TODO Move command to Resource location ID's for enchantments.
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
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		int l = args.length;
		int i = 1;
		EntityPlayerMP player = null;
		switch (l) {

			case 0:
			case 1:
				sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
				throw new WrongUsageException("chat.cofh.command." + getCommandName() + ".syntax");
			default:
			case 4:
			case 3:
				try {
					player = CommandBase.getPlayer(server, sender, args[i++]);
				} catch (CommandException t) {
					if (l != 3) {
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
						sender.sendMessage(new TextComponentTranslation("chat.cofh.command." + getCommandName() + ".syntax"));
						throw t;
					}
					--i;
				}
			case 2:
				if (player == null) {
					player = CommandBase.getCommandSenderAsPlayer(sender);
				}
				String loc = args[i++];
				int level = 1;
				ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
				if (stack.isEmpty()) {
					stack = player.getHeldItem(EnumHand.OFF_HAND);
				}

				if (stack.isEmpty()) {
					throw new CommandException("commands.enchant.noItem", new Object[0]);
				} else {
					Enchantment enchantment;

					try {
						enchantment = Enchantment.getEnchantmentByID(CommandBase.parseInt(loc, 0, Enchantment.REGISTRY.getKeys().size() - 1));
					} catch (NumberInvalidException e) {
						enchantment = Enchantment.getEnchantmentByLocation(loc);
					}

					if (enchantment == null) {
						throw new NumberInvalidException("commands.enchant.notFound", new Object[] { Integer.valueOf(Enchantment.getEnchantmentID(enchantment)) });
					}
					if (i < l) {
						level = CommandBase.parseInt(args[i++]);
					}
					stack.addEnchantment(enchantment, level);
					CommandHandler.logAdminCommand(sender, this, "commands.enchant.success");
				}
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return null;
	}

}
