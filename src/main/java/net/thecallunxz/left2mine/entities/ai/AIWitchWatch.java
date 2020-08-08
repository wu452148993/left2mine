package net.thecallunxz.left2mine.entities.ai;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;

public class AIWitchWatch extends EntityAIBase {
   protected EntityLiving entity;
   protected Entity closestEntity;
   protected float maxDistanceForPlayer;
   private int lookTime;
   private final float chance;
   protected Class<? extends Entity> watchedClass;

   public AIWitchWatch(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance) {
      this.entity = entityIn;
      this.watchedClass = watchTargetClass;
      this.maxDistanceForPlayer = maxDistance;
      this.chance = 0.02F;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      if (this.entity.getAttackTarget() != null) {
         this.closestEntity = this.entity.getAttackTarget();
      }

      if (this.watchedClass == EntityPlayer.class) {
         this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX, this.entity.posY, this.entity.posZ, (double)this.maxDistanceForPlayer, Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(this.entity)));
      } else {
         this.closestEntity = this.entity.world.findNearestEntityWithinAABB(this.watchedClass, this.entity.getEntityBoundingBox().grow((double)this.maxDistanceForPlayer, 3.0D, (double)this.maxDistanceForPlayer), this.entity);
      }

      return this.closestEntity != null && ((EntityWitch)this.entity).getAttackingState() == 0 && ((EntityWitch)this.entity).getAngerLevel() > 500;
   }

   public boolean shouldContinueExecuting() {
      if (!this.closestEntity.isEntityAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.closestEntity) > (double)(this.maxDistanceForPlayer * this.maxDistanceForPlayer)) {
         return false;
      } else {
         return ((EntityWitch)this.entity).getAttackingState() == 0 && ((EntityWitch)this.entity).getAngerLevel() > 500;
      }
   }

   public void startExecuting() {
      this.lookTime = 40 + this.entity.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.closestEntity = null;
   }

   public void updateTask() {
      if (this.entity.ticksExisted % 10 == 0) {
         this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX, this.entity.posY, this.entity.posZ, (double)this.maxDistanceForPlayer, Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(this.entity)));
      }

      this.entity.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, (float)this.entity.getHorizontalFaceSpeed(), (float)this.entity.getVerticalFaceSpeed());
      --this.lookTime;
   }
}
