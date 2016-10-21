package cofh.core.command;

import com.google.common.base.Throwables;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class CommandCountBlock implements ISubCommand {

    public static ISubCommand instance = new CommandCountBlock();

    @Override
    public String getCommandName() {

        return "countblocks";
    }

    @Override
    public int getPermissionLevel() {

        return 3;
    }

    @Override
    public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 6) {
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
        int yS = CommandBase.parseInt(args[i++]), yL;
        int zS = CommandBase.parseInt(args[i++]), zL;
        int t = i + 1;

        try {
            xL = CommandBase.parseInt(args[i++]);
            yL = CommandBase.parseInt(args[i++]);
            zL = CommandBase.parseInt(args[i++]);
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
            xS = center.getX() - xS;
            yS = center.getY() - yS;
            zS = center.getZ() - zS;

            xL = center.getX() + xL;
            yL = center.getY() + yL;
            zL = center.getZ() + zL;
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
            sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
            sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
            return;
        } else if (yL > 255) {
            yL = 255;
        }

        long blockCounter = ((long) xL - xS) * ((long) yL - yS) * ((long) zL - zS);
        CommandHandler.logAdminCommand(sender, this, "info.cofh.command.countblocks.start", blockCounter, xS, yS, zS, xL, yL, zL);

        blockCounter = 0;
        for (int e = args.length; i < e; ++i) {
            String blockRaw = args[i];
            if (blockRaw.charAt(0) == '*') {
                if (blockRaw.equals("*fluid")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                if (state.getMaterial().isLiquid()) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*tree")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                if (state.getBlock().isWood(world, pos) || state.getBlock().isLeaves(state, world, pos)) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.startsWith("*repl")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                if (state.getBlock().isReplaceable(world, pos)) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*stone")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                Block block = state.getBlock();
                                if (block.isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE)) || block.isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.NETHERRACK)) || block.isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.END_STONE))) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*rock")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                if (state.getMaterial() == Material.ROCK) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*sand")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                if (state.getMaterial() == Material.SAND) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*dirt")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                Material m = state.getMaterial();
                                if (m == Material.GRASS || m == Material.GROUND || m == Material.CLAY || m == Material.SNOW || m == Material.CRAFTED_SNOW || m == Material.ICE || m == Material.PACKED_ICE) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.startsWith("*plant")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                Material m = state.getMaterial();
                                if (m == Material.PLANTS || m == Material.VINE || m == Material.CACTUS || m == Material.LEAVES) {
                                    ++blockCounter;
                                }
                            }
                        }
                    }
                } else if (blockRaw.equals("*fire")) {
                    for (int x = xS; x <= xL; ++x) {
                        for (int z = zS; z <= zL; ++z) {
                            Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                            int cX = x & 15, cZ = z & 15;
                            for (int y = yS; y <= yL; ++y) {
                                BlockPos pos = new BlockPos(x, y, z);
                                IBlockState state = chunk.getBlockState(pos);
                                Material m = state.getMaterial();
                                if (m == Material.FIRE || m == Material.LAVA || state.getBlock().isBurning(world, pos)) {
                                    ++blockCounter;
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
                meta = CommandBase.parseInt(blockRaw.substring(t + 1));
                blockRaw = blockRaw.substring(0, t);
            }
            Block block = Block.getBlockFromName(blockRaw);
            if (block == Blocks.AIR) {
                continue;
            }

            for (int x = xS; x <= xL; ++x) {
                for (int z = zS; z <= zL; ++z) {
                    Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
                    int cX = x & 15, cZ = z & 15;
                    for (int y = yS; y <= yL; ++y) {
                        BlockPos pos = new BlockPos(x, y, z);
                        IBlockState state = chunk.getBlockState(pos);
                        boolean v = meta == -1 || state.getBlock().getMetaFromState(state) == meta;
                        if (v && state.getBlock() == block) {
                            ++blockCounter;
                        }
                    }
                }
            }
        }
        if (blockCounter != 0) {
            CommandHandler.logAdminCommand(sender, this, "info.cofh.command.countblocks.success", blockCounter, xS, yS, zS, xL, yL, zL);
        } else {
            CommandHandler.logAdminCommand(sender, this, "info.cofh.command.countblocks.failure");
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
