package cofh.core.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityFishHookCore extends EntityFishHook {

	int luckModifier = 0;
	int speedModifier = 0;

	public EntityFishHookCore(World world) {

		super(world);
	}

	@SideOnly (Side.CLIENT)
	public EntityFishHookCore(World world, double x, double y, double z, EntityPlayer player) {

		super(world, x, y, z, player);

	}

	public EntityFishHookCore(World world, EntityPlayer player, int luckMod, int speedMod) {

		super(world, player);
		luckModifier = luckMod;
		speedModifier = speedMod;
	}

	@Override
	public void onUpdate() {

		onEntityUpdate();

		if (this.worldObj.isRemote) {
			int i = this.getDataManager().get(DATA_HOOKED_ENTITY);

			if (i > 0 && this.caughtEntity == null) {
				this.caughtEntity = this.worldObj.getEntityByID(i - 1);
			}
		} else {
			ItemStack itemstack = this.angler.getHeldItemMainhand();

			if (this.angler.isDead || !this.angler.isEntityAlive() || itemstack == null || !(itemstack.getItem() instanceof ItemFishingRod) || this.getDistanceSqToEntity(this.angler) > 1024.0D) {
				this.setDead();
				this.angler.fishEntity = null;
				return;
			}
		}

		if (this.caughtEntity != null) {
			if (!this.caughtEntity.isDead) {
				this.posX = this.caughtEntity.posX;
				double d17 = (double) this.caughtEntity.height;
				this.posY = this.caughtEntity.getEntityBoundingBox().minY + d17 * 0.8D;
				this.posZ = this.caughtEntity.posZ;
				return;
			}

			this.caughtEntity = null;
		}

		if (this.fishPosRotationIncrements > 0) {
			double d3 = this.posX + (this.fishX - this.posX) / (double) this.fishPosRotationIncrements;
			double d4 = this.posY + (this.fishY - this.posY) / (double) this.fishPosRotationIncrements;
			double d6 = this.posZ + (this.fishZ - this.posZ) / (double) this.fishPosRotationIncrements;
			double d8 = MathHelper.wrapDegrees(this.fishYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d8 / (double) this.fishPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.fishPitch - (double) this.rotationPitch) / (double) this.fishPosRotationIncrements);
			--this.fishPosRotationIncrements;
			this.setPosition(d3, d4, d6);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else {
			if (this.inGround) {
				if (this.worldObj.getBlockState(this.pos).getBlock() == this.inTile) {
					++this.ticksInGround;

					if (this.ticksInGround == 1200) {
						this.setDead();
					}

					return;
				}

				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			if (!this.worldObj.isRemote) {
				Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
				Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				RayTraceResult raytraceresult = this.worldObj.rayTraceBlocks(vec3d1, vec3d);
				vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
				vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

				if (raytraceresult != null) {
					vec3d = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
				}

				Entity entity = null;
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0D));
				double d0 = 0.0D;

				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = (Entity) list.get(j);

					if (this.canBeHooked(entity1) && (entity1 != this.angler || this.ticksInAir >= 5)) {
						AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
						RayTraceResult raytraceresult1 = axisalignedbb1.calculateIntercept(vec3d1, vec3d);

						if (raytraceresult1 != null) {
							double d1 = vec3d1.squareDistanceTo(raytraceresult1.hitVec);

							if (d1 < d0 || d0 == 0.0D) {
								entity = entity1;
								d0 = d1;
							}
						}
					}
				}

				if (entity != null) {
					raytraceresult = new RayTraceResult(entity);
				}

				if (raytraceresult != null) {
					if (raytraceresult.entityHit != null) {
						this.caughtEntity = raytraceresult.entityHit;
						this.getDataManager().set(DATA_HOOKED_ENTITY, Integer.valueOf(this.caughtEntity.getEntityId() + 1));
					} else {
						this.inGround = true;
					}
				}
			}

			if (!this.inGround) {
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

				for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f2) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
					;
				}

				while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
					this.prevRotationPitch += 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
					this.prevRotationYaw -= 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
					this.prevRotationYaw += 360.0F;
				}

				this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
				this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
				float f3 = 0.92F;

				if (this.onGround || this.isCollidedHorizontally) {
					f3 = 0.5F;
				}

				int k = 5;
				double d5 = 0.0D;

				for (int l = 0; l < 5; ++l) {
					AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
					double d9 = axisalignedbb.maxY - axisalignedbb.minY;
					double d10 = axisalignedbb.minY + d9 * (double) l / 5.0D;
					double d11 = axisalignedbb.minY + d9 * (double) (l + 1) / 5.0D;
					AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb.minX, d10, axisalignedbb.minZ, axisalignedbb.maxX, d11, axisalignedbb.maxZ);

					if (this.worldObj.isAABBInMaterial(axisalignedbb2, Material.WATER)) {
						d5 += 0.2D;
					}
				}

				if (!this.worldObj.isRemote && d5 > 0.0D) {
					WorldServer worldserver = (WorldServer) this.worldObj;
					int i1 = 1;
					BlockPos blockpos = (new BlockPos(this)).up();

					if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(blockpos)) {
						i1 = 2;
					}

					if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(blockpos)) {
						--i1;
					}

					if (this.ticksCatchable > 0) {
						--this.ticksCatchable;

						if (this.ticksCatchable <= 0) {
							this.ticksCaughtDelay = 0;
							this.ticksCatchableDelay = 0;
						}
					} else if (this.ticksCatchableDelay > 0) {
						this.ticksCatchableDelay -= i1;

						if (this.ticksCatchableDelay <= 0) {
							this.motionY -= 0.20000000298023224D;
							this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
							float f6 = (float) MathHelper.floor_double(this.getEntityBoundingBox().minY);
							worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, (double) (f6 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, (double) (f6 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
						} else {
							this.fishApproachAngle = (float) ((double) this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
							float f5 = this.fishApproachAngle * 0.017453292F;
							float f8 = MathHelper.sin(f5);
							float f10 = MathHelper.cos(f5);
							double d13 = this.posX + (double) (f8 * (float) this.ticksCatchableDelay * 0.1F);
							double d15 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d16 = this.posZ + (double) (f10 * (float) this.ticksCatchableDelay * 0.1F);
							Block block1 = worldserver.getBlockState(new BlockPos((int) d13, (int) d15 - 1, (int) d16)).getBlock();

							if (block1 == Blocks.WATER || block1 == Blocks.FLOWING_WATER) {
								if (this.rand.nextFloat() < 0.15F) {
									worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d13, d15 - 0.10000000149011612D, d16, 1, (double) f8, 0.1D, (double) f10, 0.0D, new int[0]);
								}

								float f = f8 * 0.04F;
								float f1 = f10 * 0.04F;
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) f1, 0.01D, (double) (-f), 1.0D, new int[0]);
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) (-f1), 0.01D, (double) f, 1.0D, new int[0]);
							}
						}
					} else if (this.ticksCaughtDelay > 0) {
						this.ticksCaughtDelay -= i1;
						float f4 = 0.15F;

						if (this.ticksCaughtDelay < 20) {
							f4 = (float) ((double) f4 + (double) (20 - this.ticksCaughtDelay) * 0.05D);
						} else if (this.ticksCaughtDelay < 40) {
							f4 = (float) ((double) f4 + (double) (40 - this.ticksCaughtDelay) * 0.02D);
						} else if (this.ticksCaughtDelay < 60) {
							f4 = (float) ((double) f4 + (double) (60 - this.ticksCaughtDelay) * 0.01D);
						}

						if (this.rand.nextFloat() < f4) {
							float f7 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
							float f9 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
							double d12 = this.posX + (double) (MathHelper.sin(f7) * f9 * 0.1F);
							double d14 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d2 = this.posZ + (double) (MathHelper.cos(f7) * f9 * 0.1F);
							Block block = worldserver.getBlockState(new BlockPos((int) d12, (int) d14 - 1, (int) d2)).getBlock();

							if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
								worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d12, d14, d2, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
							}
						}

						if (this.ticksCaughtDelay <= 0) {
							this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
							this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
						}
					} else {
						this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
						this.ticksCaughtDelay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
					}

					if (this.ticksCatchable > 0) {
						this.motionY -= (double) (this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
					}
				}

				double d7 = d5 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033D * d7;

				if (d5 > 0.0D) {
					f3 = (float) ((double) f3 * 0.9D);
					this.motionY *= 0.8D;
				}

				this.motionX *= (double) f3;
				this.motionY *= (double) f3;
				this.motionZ *= (double) f3;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);
	}

	public int handleHookRetraction() {

		if (this.worldObj.isRemote) {
			return 0;
		} else {
			int i = 0;

			if (this.caughtEntity != null) {
				this.bringInHookedEntity();
				this.worldObj.setEntityState(this, (byte) 31);
				i = this.caughtEntity instanceof EntityItem ? 3 : 5;
			} else if (this.ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.worldObj);
				lootcontext$builder.withLuck((float) EnchantmentHelper.getLuckOfSeaModifier(this.angler) + this.angler.getLuck() + luckModifier);

				for (ItemStack itemstack : this.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build())) {
					EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
					double d0 = this.angler.posX - this.posX;
					double d1 = this.angler.posY - this.posY;
					double d2 = this.angler.posZ - this.posZ;
					double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
					double d4 = 0.1D;
					entityitem.motionX = d0 * 0.1D;
					entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
					entityitem.motionZ = d2 * 0.1D;
					this.worldObj.spawnEntityInWorld(entityitem);
					this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
				}

				i = 1;
			}

			if (this.inGround) {
				i = 2;
			}

			this.setDead();
			this.angler.fishEntity = null;
			return i;
		}
	}

	//    protected ItemStack func_146033_f() {
	//
	//        float f = this.worldObj.rand.nextFloat();
	//        int i = EnchantmentHelper.func_151386_g(this.field_146042_b) + luckModifier;
	//        int j = EnchantmentHelper.func_151387_h(this.field_146042_b) + speedModifier;
	//
	//        this.field_146042_b.addStat(net.minecraftforge.common.FishingHooks.getFishableCategory(f, i, j).stat, 1);
	//        return net.minecraftforge.common.FishingHooks.getRandomFishable(this.rand, f, i, j);
	//    }

}
