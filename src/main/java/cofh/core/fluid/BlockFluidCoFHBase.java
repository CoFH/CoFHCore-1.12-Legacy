package cofh.core.fluid;

import cofh.api.core.IInitializer;
import cofh.api.core.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.lib.render.particle.EntityDropParticleFX;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockFluidCoFHBase extends BlockFluidClassic implements IInitializer, IModelRegister {

	protected String modName;
	protected String name;

	protected float particleRed = 1.0F;
	protected float particleGreen = 1.0F;
	protected float particleBlue = 1.0F;
	protected boolean shouldDisplaceFluids = false;

	public BlockFluidCoFHBase(Fluid fluid, Material material, String modName, String name) {

		super(fluid, material);

		this.name = name;
		this.modName = modName;

		setRenderLayer(BlockRenderLayer.TRANSLUCENT);
		setUnlocalizedName(modName + ".fluid." + name);
		displacements.put(this, false);
	}

	public BlockFluidCoFHBase(Fluid fluid, Material material, String name) {

		this(fluid, material, "cofh", name);
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

	public BlockFluidCoFHBase setDisplaceFluids(boolean a) {

		this.shouldDisplaceFluids = a;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

		super.randomDisplayTick(state, world, pos, rand);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		double px = x + rand.nextFloat();
		double py = y - 1.05D;
		double pz = z + rand.nextFloat();

		if (density < 0) {
			py = y + 2.10D;
		}
		if (rand.nextInt(20) == 0 && world.isSideSolid(new BlockPos(x, y + densityDir, z), densityDir == -1 ? EnumFacing.UP : EnumFacing.DOWN)
				&& !world.getBlockState(new BlockPos(x, y + 2 * densityDir, z)).getMaterial().blocksMovement()) {
			Particle fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue, densityDir);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return definedFluid.getLuminosity();
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
		return false;
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {

		if (!shouldDisplaceFluids && world.getBlockState(pos).getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {

		if (!shouldDisplaceFluids && world.getBlockState(pos).getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, pos);
	}

	/* IModelRegister */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		Item item = Item.getItemFromBlock(this);
		StateMapper mapper = new StateMapper(modName, "fluid", name);

		// Item Model
		ModelLoader.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, mapper);
		// Block Model
		ModelLoader.setCustomStateMapper(this, mapper);
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		return false;
	}

	@Override
	public boolean postInit() {

		return false;
	}

}
