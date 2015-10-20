package cofh.core.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class ParticleBase {
	public final static float BASE_GRAVITY = 0.04F;
	protected static final ResourceLocation MC_PARTICLES = new ResourceLocation("textures/particle/particles.png");
	protected static final ResourceLocation MC_BLOCKS = TextureMap.locationBlocksTexture;
	protected static final ResourceLocation MC_ITEMS = TextureMap.locationItemsTexture;
	public static final Random rand = new Random();
	public static double interpPosX;
	public static double interpPosY;
	public static double interpPosZ;
	public static float rX;
	public static float rZ;
	public static float rYZ;
	public static float rXY;
	public static float rXZ;

	public final ResourceLocation location;
	protected final Vec3 prev;
	protected final Vec3 pos;
	protected double motX, motY, motZ;
	protected float r = 1;
	protected float g = 1;
	protected float b = 1;
	protected float a = 1;
	protected float size = 0.2F;
	protected int life;
	protected int maxLife;
	protected boolean onGround;
	protected float gravity = BASE_GRAVITY;

	protected ParticleBase(double x, double y, double z, double motX, double motY, double motZ, float size, int life, ResourceLocation location) {
		pos = Vec3.createVectorHelper(x, y, z);
		prev = Vec3.createVectorHelper(x, y, z);
		this.motX = motX;
		this.motY = motY;
		this.motZ = motZ;
		this.size = 0.1F * size;
		this.life = 0;
		this.maxLife = life;
		this.location = location;
	}

	public final boolean advance() {
		copyVecValuesFrom(prev, pos);
		return checkLife() && handleMovement();
	}

	public boolean handleMovement() {
		motY -= gravity;
		if (!moveEntity(motX, motY, motZ)) return false;
		applyFriction();
		return true;
	}

	public void applyFriction() {
		motX *= 0.9800000190734863D;
		motY *= 0.9800000190734863D;
		motZ *= 0.9800000190734863D;
		if (this.onGround) {
			motX *= 0.699999988079071D;
			motZ *= 0.699999988079071D;
		}
	}

	public boolean checkLife() {
		return life++ < maxLife;
	}

	public void copyVecValuesFrom(Vec3 dst, Vec3 src) {
		dst.xCoord = src.xCoord;
		dst.yCoord = src.yCoord;
		dst.zCoord = src.zCoord;
	}

	protected boolean moveEntity(double motX, double motY, double motZ) {
		World worldObj = Minecraft.getMinecraft().theWorld;
		if (worldObj == null) return false;

		if (noClip()) {
			pos.addVector(motX, motY, motZ);
			return true;
		} else {
			if (motX == 0 && motY == 0 && motZ == 0) return true;

			pos.xCoord += motX;
			pos.yCoord += motY;
			pos.zCoord += motZ;

			MovingObjectPosition mop = worldObj.rayTraceBlocks(prev, pos);
			if (mop != null) {
				if (!collide(mop)) return false;

				this.motX = pos.xCoord - prev.xCoord;
				this.motY = pos.yCoord - prev.yCoord;
				this.motZ = pos.zCoord - prev.zCoord;

			}
			return true;
		}
	}

	private boolean collide(MovingObjectPosition mop) {
		if (killOnCollide())
			return false;

		copyVecValuesFrom(pos, mop.hitVec);

		onGround = this.motY < 0 && mop.sideHit == 1;
		return true;
	}

	protected boolean killOnCollide() {
		return true;
	}

	protected boolean noClip() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected int brightness(float partialTicks) {
		int x = MathHelper.floor_double(pos.xCoord);
		int z = MathHelper.floor_double(pos.zCoord);

		World worldObj = Minecraft.getMinecraft().theWorld;
		if (worldObj != null && worldObj.blockExists(x, 0, z)) {
			int y = MathHelper.floor_double(pos.yCoord);
			return worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0);
		} else {
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public abstract void render(Tessellator tessellator, double partialTicks);

	@SideOnly(Side.CLIENT)
	public void renderParticle(Tessellator tessellator, double partialTicks, double size, double u0, double u1, double v0, double v1) {
		double x = pos.xCoord + (pos.xCoord - prev.xCoord) * partialTicks - interpPosX;
		double y = pos.yCoord + (pos.yCoord - prev.yCoord) * partialTicks - interpPosY;
		double z = pos.zCoord + (pos.zCoord - prev.zCoord) * partialTicks - interpPosZ;
		tessellator.setColorRGBA_F(r, g, b, a);
		tessellator.addVertexWithUV(x - rX * size - rYZ * size, y - rXZ * size, z - rZ * size - rXY * size, u1, v1);
		tessellator.addVertexWithUV(x - rX * size + rYZ * size, y + rXZ * size, z - rZ * size + rXY * size, u1, v0);
		tessellator.addVertexWithUV(x + rX * size + rYZ * size, y + rXZ * size, z + rZ * size + rXY * size, u0, v0);
		tessellator.addVertexWithUV(x + rX * size - rYZ * size, y - rXZ * size, z + rZ * size - rXY * size, u0, v1);
	}


	public <T extends ParticleBase> T setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return (T) this;
	}

	public <T extends ParticleBase> T setColor(int color) {
		this.r = (float) ((color & 16711680) >> 16) / 255.0F;
		this.g = (float) ((color & 65280) >> 8) / 255.0F;
		this.b = (float) ((color & 255)) / 255.0F;
		return (T) this;
	}

	public <T extends ParticleBase> T setAlpha(float a) {
		this.a = a;
		return (T) this;
	}

	public <T extends ParticleBase> T setSize(float size) {
		this.size = 0.1F * size;
		return (T) this;
	}
}
