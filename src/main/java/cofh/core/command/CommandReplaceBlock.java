package cofh.core.command;

import com.google.common.base.Throwables;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class CommandReplaceBlock implements ISubCommand {

	public static ISubCommand instance = new CommandReplaceBlock();

	@Override
	public String getCommandName() {

		return "replaceblocks";
	}

	@Override
	public int getPermissionLevel() {

		return 3;
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (args.length < 7) {
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
		int yS = CommandBase.parseInt(sender, args[i++]), yL;
		int zS = CommandBase.parseInt(sender, args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(sender, args[i++]);
			yL = CommandBase.parseInt(sender, args[i++]);
			zL = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) {
				throw Throwables.propagate(e);
			}
			--i;
			xL = xS;
			yL = yS;
			zL = zS;
		}

		if (center != null) {
			xS = center.posX - xS;
			yS = center.posY - yS;
			zS = center.posZ - zS;

			xL = center.posX + xL;
			yL = center.posY + yL;
			zL = center.posZ + zL;
		}

		yS &= ~yS >> 31; // max(yS, 0)
		yL &= ~yL >> 31; // max(yL, 0)

		if (xL < xS) {
			t = xS;
			xS = xL;
			xL = t;
		}
		if (yL < yS) {
			t = yS;
			yS = yL;
			yL = t;
		}
		if (zL < zS) {
			t = zS;
			zS = zL;
			zL = t;
		}

		if (yS > 255) {
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
			return;
		} else if (yL > 255) {
			yL = 255;
		}

		Block replBlock;
		int replMeta;
		String blockReplRaw;
		{
			int meta = 0;
			String blockRaw = args[i];
			blockReplRaw = blockRaw;
			t = blockRaw.indexOf('#');
			if (t > 0) {
				meta = CommandBase.parseInt(sender, blockRaw.substring(t + 1));
				blockRaw = blockRaw.substring(0, t);
			}
			Block block = Block.getBlockFromName(blockRaw);
			if (block == Blocks.air || meta > 15 || meta < 0) {
				sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
				sender.addChatMessage(new ChatComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
				// TODO: more descriptive error
				return;
			}
			replBlock = block;
			replMeta = meta;
		}

		long blockCounter = ((long)xL - xS) * ((long)yL - yS) * ((long)zL - zS);
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.start",
			blockCounter, xS, yS, zS, xL, yL, zL, blockReplRaw);

		THashSet<Chunk> set = new THashSet<Chunk>();

		blockCounter = 0;
		for (int e = args.length; i < e; ++i) {
			String blockRaw = args[i];
			if (blockRaw.charAt(0) == '*') {
				if (blockRaw.equals("*fluid")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.getMaterial().isLiquid()) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*tree")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.isWood(world, x, y, z) || block.isLeaves(world, x, y, z)) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.startsWith("*repl")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.isReplaceable(world, x, y, z)) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*stone")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.isReplaceableOreGen(world, x, y, z, Blocks.stone) ||
										block.isReplaceableOreGen(world, x, y, z, Blocks.netherrack) ||
										block.isReplaceableOreGen(world, x, y, z, Blocks.end_stone)) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*rock")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.getMaterial() == Material.rock) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*sand")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								if (block.getMaterial() == Material.sand) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*dirt")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								Material m = block.getMaterial();
								if (m == Material.grass || m == Material.ground || m == Material.clay || m == Material.snow
										|| m == Material.craftedSnow || m == Material.ice || m == Material.packedIce) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.startsWith("*plant")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								Material m = block.getMaterial();
								if (m == Material.plants || m == Material.vine || m == Material.cactus || m == Material.leaves) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				} else if (blockRaw.equals("*fire")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(x, z);
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								Block block = chunk.getBlock(cX, y, cZ);
								Material m = block.getMaterial();
								if (m == Material.fire || m == Material.lava || block.isBurning(world, x, y, z)) {
									if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
										++blockCounter;
										set.add(chunk);
									}
								}
							}
						}
					}
				}
				continue;
			}
			int meta = -1;
			t = blockRaw.indexOf('#');
			if (t > 0) {
				meta = CommandBase.parseInt(sender, blockRaw.substring(t + 1));
				blockRaw = blockRaw.substring(0, t);
			}
			Block block = Block.getBlockFromName(blockRaw);
			if (block == Blocks.air) {
				continue;
			}

			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(x, z);
					int cX = x & 15, cZ = z & 15;
					for (int y = yS; y <= yL; ++y) {
						boolean v = meta == -1 || chunk.getBlockMetadata(cX, y, cZ) == meta;
						if (v && chunk.getBlock(cX, y, cZ) == block) {
							if (chunk.func_150807_a(cX, y, cZ, replBlock, replMeta)) {
								++blockCounter;
								set.add(chunk);
							}
						}
					}
				}
			}
		}
		if (!set.isEmpty()) {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.success",
				blockCounter, xS, yS, zS, xL, yL, zL, blockReplRaw);
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
