package cofh.core.command;

import com.google.common.base.Throwables;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

@Deprecated
public class CommandFixMojangsShit implements ISubCommand {

    public static ISubCommand instance = new CommandFixMojangsShit();

    @Override
    public String getCommandName() {

        return "updatechests";
    }

    @Override
    public int getPermissionLevel() {

        return 4;
    }

    @Override
    public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 3) {
            sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
            throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
        }
        World world = sender.getEntityWorld();
        if (world.isRemote) {
            return;
        }

        BlockPos center = null;
        int i = 1;
        int xS, xL;
        if ("@".equals(args[i])) {
            center = sender.getPosition();
            ++i;
            xS = CommandBase.parseInt(args[i++]);
        } else {
            try {
                xS = CommandBase.parseInt(args[i++]);
            } catch (Throwable t) {
                center = CommandBase.getPlayer(server, sender, args[i - 1]).getPosition();
                xS = CommandBase.parseInt(args[i++]);
            }
        }
        int zS = CommandBase.parseInt(args[i++]), zL;
        int t = i + 1;

        try {
            xL = CommandBase.parseInt(args[i++]);
            zL = CommandBase.parseInt(args[i++]);
        } catch (Throwable e) {
            if (i > t || center == null) {
                throw Throwables.propagate(e);
            }
            --i;
            xL = xS;
            zL = zS;
        }

        if (center != null) {
            xS = center.getX() - xS;
            zS = center.getZ() - zS;

            xL = center.getX() + xL;
            zL = center.getZ() + zL;
        }

        if (xL < xS) {
            t = xS;
            xS = xL;
            xL = t;
        }
        if (zL < zS) {
            t = zS;
            zS = zL;
            zL = t;
        }

        int yS = 0, yL = 255;

        long blockCounter = ((long) xL - xS) * ((long) yL - yS) * ((long) zL - zS);
        CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.start", blockCounter, xS, yS, zS, xL, yL, zL, "chest");

        THashSet<Chunk> set = new THashSet<Chunk>();

        blockCounter = 0;
        Block block = Blocks.CHEST, air = Blocks.AIR;
        /*try {
			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(x, z);
					int cX = x & 15, cZ = z & 15;
					for (int y = yS; y <= yL; ++y) {
						if (chunk.getBlockMetadata(cX, y, cZ) < 2 && chunk.getBlock(cX, y, cZ) == block) {
							TileEntity tile = chunk.func_150806_e(cX, y, cZ);
							NBTTagCompound tag = new NBTTagCompound();
							tile.writeToNBT(tag);
							chunk.removeTileEntity(cX, y, cZ);
							if (chunk.func_150807_a(cX, y, cZ, air, 0) && chunk.func_150807_a(cX, y, cZ, block, 3)) {
								++blockCounter;
								chunk.func_150806_e(cX, y, cZ).readFromNBT(tag);
								set.add(chunk);
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			Throwables.propagate(e);
		}*/
        if (!set.isEmpty()) {
            CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.success", blockCounter, xS, yS, zS, xL, yL, zL, "chest");
        } else {
            CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.failure");
        }

        if (world instanceof WorldServer) {
            TObjectHashIterator<Chunk> c = set.iterator();
            for (int k = 0, e = set.size(); k < e; ++k) {
                Chunk chunk = c.next();
                PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
                if (manager == null) {
                    return;
                }
                PlayerChunkMapEntry watcher = manager.getEntry(chunk.xPosition, chunk.zPosition);
                if (watcher != null) {
                    watcher.sendPacket(new SPacketChunkData(chunk, -1));
                }
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
        return null;
    }

}
