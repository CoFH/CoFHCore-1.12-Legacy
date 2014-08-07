package cofh.core.command;

import com.google.common.base.Throwables;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class CommandClearBlock implements ISubCommand {

	public static ISubCommand instance = new CommandClearBlock();

	@Override
	public String getCommandName() {

		return "clearblocks";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (args.length < 6) {
			// TODO: error
			return;
		}
		World world = CommandBase.getCommandSenderAsPlayer(sender).worldObj;
		if (world.isRemote) return;

		EntityPlayer center = null;
		int i = 1;
		int xS, xL;
		try {
			xS = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable t) {
			center = CommandBase.getPlayer(sender, args[i - 1]);
			xS = CommandBase.parseInt(sender, args[i++]);
		}
		int yS = CommandBase.parseInt(sender, args[i++]), yL;
		int zS = CommandBase.parseInt(sender, args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(sender, args[i++]);
			yL = CommandBase.parseInt(sender, args[i++]);
			zL = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) throw Throwables.propagate(e);
			--i;
			xL = xS; yL = yS; zL = zS;
		}

		if (center != null) {
			xS = (int)center.posX - xS;
			yS = (int)center.posY - yS;
			zS = (int)center.posZ - zS;

			xL = (int)center.posX + xL;
			yL = (int)center.posY + yL;
			zL = (int)center.posZ + zL;
		}

		yS &= ~yS >> 31; // max(yS, 0)
		yL &= ~yL >> 31; // max(yL, 0)

		if (xL < xS) { t = xS; xS = xL; xL = t; }
		if (yL < yS) { t = yS; yS = yL; yL = t; }
		if (zL < zS) { t = zS; zS = zL; zL = t; }

		if (yS > 255) {
			// TODO: error
			return;
		} else if (yL > 255)
			yL = 255;

		THashSet<Chunk> set = new THashSet<Chunk>();

		for (int e = args.length; i < e; ++i) {
			String blockRaw = args[i];
			int meta = -1;
			t = blockRaw.indexOf('#');
			if (t > 0) {
				meta = CommandBase.parseInt(sender, blockRaw.substring(t + 1));
				blockRaw = blockRaw.substring(0, t);
			}
			Block block = Block.getBlockFromName(blockRaw);
			if (block == Blocks.air) continue;

			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(x, z);
					int cX = x & 15, cZ = z & 15;
					for (int y = yS; y <= yL; ++y) {
						boolean v = meta == -1 || chunk.getBlockMetadata(cX, y, cZ) == meta;
						if (v && chunk.getBlock(cX, y, cZ) == block) {
							if (chunk.func_150807_a(cX, y, cZ, Blocks.air, 0))
								set.add(chunk);
						}
					}
				}
			}
		}

		if (world instanceof WorldServer) {
			TObjectHashIterator<Chunk> c = set.iterator();
			for (int k = 0, e = set.size(); k < e; ++k) {
				Chunk chunk = c.next();
				PlayerManager manager = ((WorldServer)world).getPlayerManager();
				if (manager == null)
					return;
				PlayerInstance watcher = manager.getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null)
					watcher.sendToAllPlayersWatchingChunk(new S21PacketChunkData(chunk, false, -1));
			}
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
