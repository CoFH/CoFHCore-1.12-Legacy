package cofh.core.fluid;

import cofh.core.block.BlockCoFHBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.Random;

public class BlockFont extends BlockCoFHBase {

    public BlockFont(Material material, Block baseBlock, Fluid fluid) {

        super(material);
        setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

        if (metadata >= 8) {
            return null;
        }
        return new TileFont();
    }

    /* IDismantleable */

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate) {
        return null;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileFont) {
            ((TileFont) te).update();
        }
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
        return false;
    }

    /* IInitializer */
    @Override
    public boolean initialize() {

        return true;
    }

    @Override
    public boolean postInit() {

        return true;
    }

}
