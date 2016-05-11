package cofh.core.command;

import com.google.common.base.Throwables;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

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
	public void handleCommand(ICommandSender sender, String[] args) {

		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		}
		World world = sender.getEntityWorld();
		if (world.isRemote) {
			return;
		}

		ChunkCoordinates center = null;
		int i = 1;
		int xS, xL;
		if ("@".equals(args[i])) {
			center = sender.getPlayerCoordinates();
			++i;
			xS = CommandBase.parseInt(sender, args[i++]);
		} else {
			try {
				xS = CommandBase.parseInt(sender, args[i++]);
			} catch (Throwable t) {
				center = CommandBase.getPlayer(sender, args[i - 1]).getPlayerCoordinates();
				xS = CommandBase.parseInt(sender, args[i++]);
			}
		}
		int zS = CommandBase.parseInt(sender, args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(sender, args[i++]);
			zL = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) {
				throw Throwables.propagate(e);
			}
			--i;
			xL = xS;
			zL = zS;
		}

		if (center != null) {
			xS = center.posX - xS;
			zS = center.posZ - zS;

			xL = center.posX + xL;
			zL = center.posZ + zL;
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
		Block block = Blocks.chest, air = Blocks.air;
		try {
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
		}
		if (!set.isEmpty()) {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.success", blockCounter, xS, yS, zS, xL, yL, zL, "chest");
		} else {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.failure");
		}

		if (world instanceof WorldServer) {
			TObjectHashIterator<Chunk> c = set.iterator();
			for (int k = 0, e = set.size(); k < e; ++k) {
				Chunk chunk = c.next();
				PlayerManager manager = ((WorldServer) world).getPlayerManager();
				if (manager == null) {
					return;
				}
				PlayerInstance watcher = manager.getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null) {
					watcher.sendToAllPlayersWatchingChunk(new S21PacketChunkData(chunk, false, -1));
				}
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
