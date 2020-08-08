package net.thecallunxz.left2mine.entities.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.entities.mobs.EntityCustomMob;

public class AIBurnPanic extends EntityAIBase {
   protected final EntityCustomMob creature;
   protected double speed;
   protected double randPosX;
   protected double randPosY;
   protected double randPosZ;

   public AIBurnPanic(EntityCustomMob creature, double speedIn) {
      this.creature = creature;
      this.speed = speedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.creature.isOnFire()) {
         return false;
      } else {
         if (this.creature.isOnFire()) {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 3, 3);
            if (blockpos != null) {
               this.randPosX = (double)blockpos.getX();
               this.randPosY = (double)blockpos.getY();
               this.randPosZ = (double)blockpos.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean findRandomPosition() {
      Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.creature, 3, 3);
      if (vec3d == null) {
         return false;
      } else {
         this.randPosX = vec3d.x;
         this.randPosY = vec3d.y;
         this.randPosZ = vec3d.z;
         return true;
      }
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath();
   }

   @Nullable
   private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
      BlockPos blockpos = new BlockPos(entityIn);
      int i = blockpos.getX();
      int j = blockpos.getY();
      int k = blockpos.getZ();
      float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
      BlockPos blockpos1 = null;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for(int l = i - horizontalRange; l <= i + horizontalRange; ++l) {
         for(int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1) {
            for(int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1) {
               blockpos$mutableblockpos.setPos(l, i1, j1);
               worldIn.getBlockState(blockpos$mutableblockpos);
            }
         }
      }

      return (BlockPos)blockpos1;
   }
}
