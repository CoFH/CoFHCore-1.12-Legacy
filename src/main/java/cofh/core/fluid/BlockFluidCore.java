package cofh.core.fluid;

import cofh.core.render.IModelRegister;
import cofh.core.render.particle.EntityDropParticleFX;
import cofh.core.util.StateMapper;
import cofh.core.util.core.IInitializer;
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
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class BlockFluidCore extends BlockFluidClassic implements IInitializer, IModelRegister {

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

		return setParticleColor(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, (c & 255) / 255f);
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
			return false;
		}
		if (testingHead) {
			return true;
		}
		//		if (iblockstate.getMaterial().equals(this.blockMaterial)) {
		//			if (yToTest <= blockpos.getY() + 1 + getQuantaPercentage(world, blockpos)) {
		//				return true;
		//			}
		//		}
		return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

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

	/* ACCESSORS */
	public int getDensityDir() {

		return densityDir;
	}

	public float getParticleRed() {

		return particleRed;
	}

	public float getParticleGreen() {

		return particleGreen;
	}

	public float getParticleBlue() {

		return particleBlue;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {

		return new Vec3d(getParticleRed(), getParticleGreen(), getParticleBlue());
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

}
