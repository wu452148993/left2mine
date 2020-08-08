package net.thecallunxz.left2mine.entities;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.init.InitBlocks;
import net.thecallunxz.left2mine.init.InitSounds;

public class EntityMolotov extends EntityLiving {
   private EntityPlayerMP shooter;

   public EntityMolotov(World worldIn) {
      super(worldIn);
   }

   public EntityMolotov(World worldIn, EntityPlayerMP shooter) {
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

   protected boolean canTriggerWalking() {
      return false;
   }

   public void applyEntityCollision(Entity entityIn) {
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.collidedHorizontally) {
         this.collidedHorizontally = false;
         this.motionX = 0.0D;
         this.motionZ = 0.0D;
      }

      if (!this.onGround) {
         this.rotationPitch -= 25.0F;
      } else {
         boolean burning = false;
         if (this.world.isAirBlock(this.getPosition())) {
            this.world.setBlockState(this.getPosition(), InitBlocks.molotovfire.getDefaultState());
            this.world.playSound((EntityPlayer)null, this.getPosition(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
            burning = true;
         } else if (this.world.isAirBlock(this.getPosition().up())) {
            this.world.setBlockState(this.getPosition().up(), InitBlocks.molotovfire.getDefaultState());
            this.world.playSound((EntityPlayer)null, this.getPosition().up(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
            burning = true;
         } else if (this.world.isAirBlock(this.getPosition().down())) {
            this.world.setBlockState(this.getPosition().down(), InitBlocks.molotovfire.getDefaultState());
            this.world.playSound((EntityPlayer)null, this.getPosition().down(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
            burning = true;
         } else if (this.world.isAirBlock(this.getPosition().down().down())) {
            this.world.setBlockState(this.getPosition().down().down(), InitBlocks.molotovfire.getDefaultState());
            this.world.playSound((EntityPlayer)null, this.getPosition().down().down(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
            burning = true;
         } else if (this.world.isAirBlock(this.getPosition().up().up())) {
            this.world.setBlockState(this.getPosition().up().up(), InitBlocks.molotovfire.getDefaultState());
            this.world.playSound((EntityPlayer)null, this.getPosition().up().up(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
            burning = true;
         }

         if (!burning) {
            BlockPos newPos = this.world.getTopSolidOrLiquidBlock(this.getPosition());
            newPos = newPos.up();
            if (this.world.isAirBlock(newPos)) {
               this.world.setBlockState(newPos, InitBlocks.molotovfire.getDefaultState());
               this.world.playSound((EntityPlayer)null, newPos, InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
               burning = true;
            } else if (this.world.isAirBlock(newPos.up())) {
               this.world.setBlockState(newPos.up(), InitBlocks.molotovfire.getDefaultState());
               this.world.playSound((EntityPlayer)null, newPos.up(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
               burning = true;
            } else if (this.world.isAirBlock(newPos.down())) {
               this.world.setBlockState(newPos.down(), InitBlocks.molotovfire.getDefaultState());
               this.world.playSound((EntityPlayer)null, newPos.down(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
               burning = true;
            } else if (this.world.isAirBlock(newPos.down().down())) {
               this.world.setBlockState(newPos.down().down(), InitBlocks.molotovfire.getDefaultState());
               this.world.playSound((EntityPlayer)null, newPos.down().down(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
               burning = true;
            } else if (this.world.isAirBlock(newPos.up().up())) {
               this.world.setBlockState(newPos.up().up(), InitBlocks.molotovfire.getDefaultState());
               this.world.playSound((EntityPlayer)null, newPos.up().up(), InitSounds.molotov_detonate, SoundCategory.PLAYERS, 2.0F, 1.0F);
               burning = true;
            }
         }

         this.setDead();
      }

   }

   public boolean isEmptyPos(BlockPos pos) {
      return this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) == null;
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
