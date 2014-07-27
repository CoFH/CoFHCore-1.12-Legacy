package cofh.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;
import cofh.util.StringHelper;

public class CommandTPX implements ISubCommand {
    
    public static CommandTPX instance = new CommandTPX();
    
    @Override
    public String getCommandName() {
        
        return "tpx";
    }
    
    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        
        switch(arguments.length){
        
        case 0: sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh tpx " + StringHelper.PINK + "[username] " + StringHelper.YELLOW + "<x> <y> <z> <dimension id>")); break;
        case 1: sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh tpx " + StringHelper.PINK + "[username] " + StringHelper.YELLOW + "<x> <y> <z> <dimension id>")); break;
        case 2:
            EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
            EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
            if(!player.equals(playerSender)){
                player.mountEntity((Entity)null);
                if(playerSender.dimension == player.dimension){
                    playerSender.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                }else{
                    playerSender.mcServer.getConfigurationManager().transferPlayerToDimension(player, player.dimension);
                    playerSender.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                }
            }else{
                sender.addChatMessage(new ChatComponentText("Teleport to somebody besides yourself!"));
            }
            break;
        case 3: sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh tpx " + StringHelper.PINK + "[username] " + StringHelper.YELLOW + "<x> <y> <z> <dimension id>")); break;
        case 4: sender.addChatMessage(new ChatComponentText("Invalid Syntax. /cofh tpx " + StringHelper.PINK + "[username] " + StringHelper.YELLOW + "<x> <y> <z> <dimension id>")); break;
        case 5:
            player = CommandBase.getPlayer(sender, arguments[1]);
            player.mountEntity((Entity)null);
            player.setPositionAndUpdate(CommandBase.func_110666_a(player, player.posX, arguments[2]), CommandBase.func_110666_a(player, player.posY, arguments[3]), CommandBase.func_110666_a(player, player.posZ, arguments[4]));
            break;
        default:
            player = CommandBase.getPlayer(sender, arguments[1]);
            int dimension = CommandBase.parseInt(sender, arguments[5]);
            
            if(!DimensionManager.isDimensionRegistered(dimension)){
                sender.addChatMessage(new ChatComponentText(StringHelper.RED + "That dimension does not exist")); break;
            }
            
            if(player.dimension != dimension){
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dimension);
            }
            player.mountEntity((Entity)null);
            player.setPositionAndUpdate(CommandBase.func_110666_a(player, player.posX, arguments[2]), CommandBase.func_110666_a(player, player.posY, arguments[3]), CommandBase.func_110666_a(player, player.posZ, arguments[4]));
            break;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        
        if(args.length == 2){
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()); 
        }else if(args.length >= 6){
            
            Integer[] ids = DimensionManager.getIDs();
            String[] strings = new String[ids.length];
            
            for(int i = 0; i < ids.length; i++){
                strings[i] = ids[i].toString();
            }
            
            return CommandBase.getListOfStringsMatchingLastWord(args, strings); 
        }
        
        return null;
        
    }
    
}

