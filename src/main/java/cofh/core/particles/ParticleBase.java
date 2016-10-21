package cofh.core.particles;

import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class ParticleBase {
    public final static float BASE_GRAVITY = 0.04F;
    protected static final ResourceLocation MC_PARTICLES = new ResourceLocation("textures/particle/particles.png");
    protected static final ResourceLocation MC_BLOCKS = TextureMap.LOCATION_BLOCKS_TEXTURE;
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
    protected final Vector3 prev;
    protected final Vector3 pos;
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
        pos = new Vector3(x, y, z);
        prev = new Vector3(x, y, z);
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
        if (!moveEntity(motX, motY, motZ)) {
            return false;
        }
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

    public void copyVecValuesFrom(Vector3 dst, Vector3 src) {
        dst.x = src.x;
        dst.y = src.y;
        dst.z = src.z;
    }

    protected boolean moveEntity(double motX, double motY, double motZ) {
        World worldObj = Minecraft.getMinecraft().theWorld;
        if (worldObj == null) {
            return false;
        }

        if (noClip()) {
            pos.copy().add(motX, motY, motZ);
            return true;
        } else {
            if (motX == 0 && motY == 0 && motZ == 0) {
                return true;
            }

            pos.x += motX;
            pos.y += motY;
            pos.z += motZ;

            RayTraceResult mop = worldObj.rayTraceBlocks(prev.vec3(), pos.vec3());
            if (mop != null) {
                if (!collide(mop)) {
                    return false;
                }

                this.motX = pos.x - prev.x;
                this.motY = pos.y - prev.y;
                this.motZ = pos.z - prev.z;

            }
            return true;
        }
    }

    private boolean collide(RayTraceResult mop) {
        if (killOnCollide()) {
            return false;
        }

        copyVecValuesFrom(pos, new Vector3(mop.hitVec));

        onGround = this.motY < 0 && mop.sideHit == EnumFacing.UP;
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
        int x = MathHelper.floor_double(pos.x);
        int z = MathHelper.floor_double(pos.z);

        World worldObj = Minecraft.getMinecraft().theWorld;
        if (worldObj != null && worldObj.isBlockLoaded(new BlockPos(x, 0, z))) {
            int y = MathHelper.floor_double(pos.y);
            return worldObj.getCombinedLight(new BlockPos(x, y, z), 0);
        } else {
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void render(VertexBuffer buffer, double partialTicks);

    @SideOnly(Side.CLIENT)
    public void renderParticle(VertexBuffer buffer, double partialTicks, double size, double u0, double u1, double v0, double v1) {
        double x = pos.x + (pos.x - prev.x) * partialTicks - interpPosX;
        double y = pos.y + (pos.y - prev.y) * partialTicks - interpPosY;
        double z = pos.z + (pos.z - prev.z) * partialTicks - interpPosZ;
        //buffer.setColorRGBA_F(r, g, b, a);
        int brightness = brightness((float) partialTicks);
        int l1 = brightness >> 16 & 65535;
        int l2 = brightness & 65535;
        buffer.pos(x - rX * size - rYZ * size, y - rXZ * size, z - rZ * size - rXY * size).tex(u1, v1).lightmap(l1, l2).color(r, g, b, a).endVertex();
        buffer.pos(x - rX * size + rYZ * size, y + rXZ * size, z - rZ * size + rXY * size).tex(u1, v0).lightmap(l1, l2).color(r, g, b, a).endVertex();
        buffer.pos(x + rX * size + rYZ * size, y + rXZ * size, z + rZ * size + rXY * size).tex(u0, v0).lightmap(l1, l2).color(r, g, b, a).endVertex();
        buffer.pos(x + rX * size - rYZ * size, y - rXZ * size, z + rZ * size - rXY * size).tex(u0, v1).lightmap(l1, l2).color(r, g, b, a).endVertex();
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
