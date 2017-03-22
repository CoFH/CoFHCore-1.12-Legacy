package cofh.core.fluid;

import cofh.core.block.IFogOverlay;
import cofh.core.render.IModelRegister;
import cofh.core.render.particle.EntityDropParticleFX;
import cofh.core.util.StateMapper;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class BlockFluidCore extends BlockFluidClassic implements IInitializer, IFogOverlay, IModelRegister {

	protected String modName;
	protected String name;

	protected float particleRed = 1.0F;
	protected float particleGreen = 1.0F;
	protected float particleBlue = 1.0F;
	protected boolean shouldDisplaceFluids = false;

	public BlockFluidCore(Fluid fluid, Material material, String modName, String name) {

		super(fluid, material);

		this.name = name;
		this.modName = modName;

		setUnlocalizedName(modName + ".fluid." + name);
		displacements.put(this, false);
	}

	public BlockFluidCore(Fluid fluid, Material material, String name) {

		this(fluid, material, "cofh", name);
	}

	public BlockFluidCore setParticleColor(int c) {

		return setParticleColor(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, ((c >> 0) & 255) / 255f);
	}

	public BlockFluidCore setParticleColor(float particleRed, float particleGreen, float particleBlue) {

		this.particleRed = particleRed;
		this.particleGreen = particleGreen;
		this.particleBlue = particleBlue;

		return this;
	}

	public BlockFluidCore setDisplaceFluids(boolean a) {

		this.shouldDisplaceFluids = a;
		return this;
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {

		if (this.density < 0) {
			return null;
		}
		if (iblockstate.getMaterial().isLiquid()) {
			double fluidHeight = (double) ((float) (blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL)));
			if (yToTest >= fluidHeight) {
				return true;
			}
		}
		return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

		super.randomDisplayTick(state, world, pos, rand);

		double px = pos.getX() + rand.nextFloat();
		double py = pos.getY() - 1.05D;
		double pz = pos.getZ() + rand.nextFloat();

		if (density < 0) {
			py = pos.getY() + 2.10D;
		}
		if (rand.nextInt(20) == 0 && world.isSideSolid(pos.add(0, densityDir, 0), densityDir == -1 ? EnumFacing.UP : EnumFacing.DOWN) && !world.getBlockState(pos.add(0, 2 * densityDir, 0)).getMaterial().blocksMovement()) {
			Particle fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue, densityDir);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.EntityLiving.SpawnPlacementType type) {

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

	/* Implementing https://github.com/MinecraftForge/MinecraftForge/pull/3747 TODO Remove in 1.11 */

	private boolean isFluid(IBlockState state) {

		return state.getMaterial().isLiquid() || state.getBlock() instanceof IFluidBlock;
	}

	private float getFluidHeightForRender(IBlockAccess world, BlockPos pos, IBlockState up) {

		IBlockState here = world.getBlockState(pos);
		if (here.getBlock() == this) {
			if (isFluid(up)) {
				return 1.0F;
			}
			if (getMetaFromState(here) == getMaxRenderHeightMeta()) {
				return 0.875F;
			}
		}
		if (here.getBlock() instanceof BlockLiquid) {
			return Math.min(1 - BlockLiquid.getLiquidHeightPercent(here.getValue(BlockLiquid.LEVEL)), 14F / 16F);
		}
		return !here.getMaterial().isSolid() && up.getBlock() == this ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {

		IExtendedBlockState state = (IExtendedBlockState) oldState;
		state = state.withProperty(FLOW_DIRECTION, (float) getFlowDirection(worldIn, pos));
		IBlockState[][] upBlockState = new IBlockState[3][3];
		float[][] height = new float[3][3];
		float[][] corner = new float[2][2];
		upBlockState[1][1] = worldIn.getBlockState(pos.down(densityDir));
		height[1][1] = getFluidHeightForRender(worldIn, pos, upBlockState[1][1]);
		if (height[1][1] == 1) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					corner[i][j] = 1;
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (i != 1 || j != 1) {
						upBlockState[i][j] = worldIn.getBlockState(pos.add(i - 1, 0, j - 1).down(densityDir));
						height[i][j] = getFluidHeightForRender(worldIn, pos.add(i - 1, 0, j - 1), upBlockState[i][j]);
					}
				}
			}
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					corner[i][j] = getFluidHeightAverage(height[i][j], height[i][j + 1], height[i + 1][j], height[i + 1][j + 1]);
				}
			}
			//check for downflow above corners
			boolean n = isFluid(upBlockState[0][1]);
			boolean s = isFluid(upBlockState[2][1]);
			boolean w = isFluid(upBlockState[1][0]);
			boolean e = isFluid(upBlockState[1][2]);
			boolean nw = isFluid(upBlockState[0][0]);
			boolean ne = isFluid(upBlockState[0][2]);
			boolean sw = isFluid(upBlockState[2][0]);
			boolean se = isFluid(upBlockState[2][2]);
			if (nw || n || w) {
				corner[0][0] = 1;
			}
			if (ne || n || e) {
				corner[0][1] = 1;
			}
			if (sw || s || w) {
				corner[1][0] = 1;
			}
			if (se || s || e) {
				corner[1][1] = 1;
			}
		}

		state = state.withProperty(LEVEL_CORNERS[0], corner[0][0]);
		state = state.withProperty(LEVEL_CORNERS[1], corner[0][1]);
		state = state.withProperty(LEVEL_CORNERS[2], corner[1][1]);
		state = state.withProperty(LEVEL_CORNERS[3], corner[1][0]);
		return state;
	}

	/* IFogOverlay */
	public Vec3d getFog(IBlockState state, Entity renderViewEntity, float fogColourRed, float fogColourGreen, float fogColourBlue) {

		return new Vec3d(particleRed, particleGreen, particleBlue);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		Item item = Item.getItemFromBlock(this);
		StateMapper mapper = new StateMapper(modName, "fluid", name);

		// Item Model
		ModelBakery.registerItemVariants(item);
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
