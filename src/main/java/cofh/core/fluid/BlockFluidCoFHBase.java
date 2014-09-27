package cofh.core.fluid;

import cofh.core.render.IconRegistry;
import cofh.lib.render.particle.EntityDropParticleFX;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public abstract class BlockFluidCoFHBase extends BlockFluidClassic {

	String name = "";
	String modName = "cofh";
	protected float particleRed = 1.0F;
	protected float particleGreen = 1.0F;
	protected float particleBlue = 1.0F;
	protected boolean shouldDisplaceFluids = false;

	public BlockFluidCoFHBase(Fluid fluid, Material material, String name) {

		super(fluid, material);

		this.name = StringHelper.titleCase(name);

		setRenderPass(1);
		setBlockName(modName + ".fluid." + name);
		displacements.put(this, false);
	}

	public BlockFluidCoFHBase(String modName, Fluid fluid, Material material, String name) {

		super(fluid, material);

		this.name = StringHelper.titleCase(name);
		this.modName = modName;

		setRenderPass(1);
		setBlockName(modName + ".fluid." + name);
		displacements.put(this, false);
	}

	public BlockFluidCoFHBase setParticleColor(int c) {

		return setParticleColor(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, ((c >> 0) & 255) / 255f);
	}

	public BlockFluidCoFHBase setParticleColor(float particleRed, float particleGreen, float particleBlue) {

		this.particleRed = particleRed;
		this.particleGreen = particleGreen;
		this.particleBlue = particleBlue;

		return this;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
	{
		return false;
	}

	public boolean preInit() {

		return true;
	}

	public BlockFluidCoFHBase setDisplaceFluids(boolean a) {

		this.shouldDisplaceFluids = a;
		return this;
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Fluid" + name, modName + ":fluid/Fluid_" + name + "_Still", ir);
		IconRegistry.addIcon("Fluid" + name + "1", modName + ":fluid/Fluid_" + name + "_Flow", ir);
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		return side <= 1 ? IconRegistry.getIcon("Fluid" + name) : IconRegistry.getIcon("Fluid" + name, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

		super.randomDisplayTick(world, x, y, z, rand);

		double px = x + rand.nextFloat();
		double py = y - 1.05D;
		double pz = z + rand.nextFloat();

		if (density < 0) {
			py = y + 2.10D;
		}
		if (rand.nextInt(20) == 0 && world.isSideSolid(x, y + densityDir, z, densityDir == -1 ? ForgeDirection.UP : ForgeDirection.DOWN)
				&& !world.getBlock(x, y + 2 * densityDir, z).getMaterial().blocksMovement()) {
			EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue, densityDir);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {

		if (!shouldDisplaceFluids && world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {

		if (!shouldDisplaceFluids && world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, x, y, z);
	}

}
