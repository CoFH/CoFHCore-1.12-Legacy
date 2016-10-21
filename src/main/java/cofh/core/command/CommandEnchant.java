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
                sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
                throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
            default:
            case 4:
            case 3:
                try {
                    player = CommandBase.getPlayer(server, sender, args[i++]);
                } catch (CommandException t) {
                    if (l != 3) {
                        sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
                        sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
                        throw t;
                    }
                    --i;
                }
            case 2:
                if (player == null) {
                    player = CommandBase.getCommandSenderAsPlayer(sender);
                }
                int id = CommandBase.parseInt(args[i++], 0, Enchantment.REGISTRY.getKeys().size() - 1);
                int level = 1;
                ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (itemstack == null) {
                    itemstack = player.getHeldItem(EnumHand.OFF_HAND);
                }

                if (itemstack == null) {
                    throw new CommandException("commands.enchant.noItem", new Object[0]);
                } else {
                    Enchantment enchantment = Enchantment.getEnchantmentByID(id);

                    if (enchantment == null) {
                        throw new NumberInvalidException("commands.enchant.notFound", new Object[] { Integer.valueOf(id) });
                    }
                    if (i < l) {
                        level = CommandBase.parseInt(args[i++]);
                    }

                    itemstack.addEnchantment(enchantment, level);
                    CommandHandler.logAdminCommand(sender, this, "commands.enchant.success");
                }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
        return null;
    }

}
