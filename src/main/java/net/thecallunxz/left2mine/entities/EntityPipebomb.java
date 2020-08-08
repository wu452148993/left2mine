package net.thecallunxz.left2mine.entities;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.init.InitSounds;

public class EntityPipebomb extends EntityLiving {
   private EntityPlayerMP shooter;

   public EntityPipebomb(World worldIn) {
      super(worldIn);
   }

   public EntityPipebomb(World worldIn, EntityPlayerMP shooter) {
      this(worldIn);
      this.shooter = shooter;
      double speed = 2.5D;
      float size = 0.01F;
      Vec3d dir = this.getGunAccuracy(shooter.rotationPitch, shooter.rotationYaw, 0.0F, worldIn.rand);
      this.motionX = dir.x * speed + shooter.motionX;
      this.motionY = dir.y * speed;
      this.motionZ = dir.z * speed + shooter.motionZ;
      this.rotationYaw = -shooter.rotationYawHead;
      this.setSize(size, size);
      this.setPosition(shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
   }

   protected boolean canDespawn() {
      return false;
   }

   public void applyEntityCollision(Entity entityIn) {
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.onGround) {
         this.rotationPitch -= 45.0F;
      } else {
         this.rotationPitch = 90.0F;
      }

      if (this.ticksExisted > 115) {
         this.setDead();
         if (!this.world.isRemote) {
            this.explode();
         }
      }

      if (this.ticksExisted == 1 || this.ticksExisted == 15 || this.ticksExisted == 28 || this.ticksExisted == 40 || this.ticksExisted == 51 || this.ticksExisted == 61 || this.ticksExisted == 70 || this.ticksExisted == 78 || this.ticksExisted == 85 || this.ticksExisted == 91 || this.ticksExisted == 96 || this.ticksExisted == 100 || this.ticksExisted == 103 || this.ticksExisted == 105 || this.ticksExisted == 107 || this.ticksExisted == 109 || this.ticksExisted == 111 || this.ticksExisted == 112 || this.ticksExisted == 113 || this.ticksExisted == 114 || this.ticksExisted == 115) {
         this.world.playSound((EntityPlayer)null, this.getPosition(), InitSounds.beep, SoundCategory.PLAYERS, 2.0F, 1.0F);
      }

      Iterator var1 = this.world.loadedEntityList.iterator();

      while(var1.hasNext()) {
         Entity ent = (Entity)var1.next();
         if (ent instanceof EntityCommonInfected && ent.getDistance(this) < 15.0F) {
            ((EntityCommonInfected)ent).setAttackTarget(this);
         }
      }

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   private void explode() {
      Iterator var1 = this.world.loadedEntityList.iterator();

      while(var1.hasNext()) {
         Entity ent = (Entity)var1.next();
         if (ent instanceof EntityCommonInfected && ent.getDistance(this) < 50.0F) {
            ((EntityCommonInfected)ent).setAttackTarget((EntityLivingBase)null);
         }
      }

      ExplosionPipebomb explosion = new ExplosionPipebomb(this.world, this, this.posX, this.posY, this.posZ, 3.0F, false, true, this.shooter);
      explosion.doExplosionA();
      explosion.doExplosionB(true);
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public Vec3d getGunAccuracy(float pitch, float yaw, float accuracy, Random rand) {
      float randAccPitch = rand.nextFloat() * accuracy;
      float randAccYaw = rand.nextFloat() * accuracy;
      pitch += rand.nextBoolean() ? randAccPitch : -randAccPitch;
      yaw += rand.nextBoolean() ? randAccYaw : -randAccYaw;
      float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
      float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
      float f2 = -MathHelper.cos(-pitch * 0.017453292F);
      float f3 = MathHelper.sin(-pitch * 0.017453292F);
      return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
   }
}
