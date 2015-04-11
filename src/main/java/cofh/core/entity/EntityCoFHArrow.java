package cofh.core.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCoFHArrow extends EntityArrow implements IEntityAdditionalSpawnData {

	public EntityCoFHArrow(World world) {

		super(world);
	}

	public EntityCoFHArrow(World world, double x, double y, double z) {

		super(world);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
		this.setPosition(x, y, z);
		this.yOffset = 0.0F;
	}

	public EntityCoFHArrow(World world, EntityLivingBase shooter, EntityLivingBase p_i1755_3_, float p_i1755_4_, float p_i1755_5_) {

		super(world);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = shooter;

		if (shooter instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}

		this.posY = shooter.posY + shooter.getEyeHeight() - 0.10000000149011612D;
		double d0 = p_i1755_3_.posX - shooter.posX;
		double d1 = p_i1755_3_.boundingBox.minY + p_i1755_3_.height / 3.0F - this.posY;
		double d2 = p_i1755_3_.posZ - shooter.posZ;
		double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

		if (d3 >= 1.0E-7D) {
			float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
			double d4 = d0 / d3;
			double d5 = d2 / d3;
			this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f2, f3);
			this.yOffset = 0.0F;
			float f4 = (float) d3 * 0.2F;
			this.setThrowableHeading(d0, d1 + f4, d2, p_i1755_4_, p_i1755_5_);
		}
	}

	public EntityCoFHArrow(World world, EntityLivingBase shooter, float speed) {

		super(world);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = shooter;

		if (shooter instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}

		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw,
				shooter.rotationPitch);
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI));
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed * 1.5F, 1.0F);
	}

	/* IEntityAdditionalSpawnData */
	@Override
	public void writeSpawnData(ByteBuf buffer) {

	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {

	}

}
