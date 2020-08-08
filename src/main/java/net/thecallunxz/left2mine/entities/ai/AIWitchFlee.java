package net.thecallunxz.left2mine.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;

public class AIWitchFlee<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> canBeSeenSelector;
   protected EntityCreature entity;
   private final double farSpeed;
   private final double nearSpeed;
   protected T closestLivingEntity;
   private float avoidDistance;
   private Path entityPathEntity;
   private final PathNavigate entityPathNavigate;
   private final Class<T> classToAvoid;
   private final Predicate<? super T> avoidTargetSelector;

   public AIWitchFlee(EntityCreature entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
   }

   public AIWitchFlee(EntityCreature entityIn, Class<T> classToAvoidIn, Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this.canBeSeenSelector = new Predicate<Entity>() {
         public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_.isEntityAlive() && !AIWitchFlee.this.entity.isOnSameTeam(p_apply_1_);
         }
      };
      this.entity = entityIn;
      this.classToAvoid = classToAvoidIn;
      this.avoidTargetSelector = avoidTargetSelectorIn;
      this.avoidDistance = avoidDistanceIn;
      this.farSpeed = farSpeedIn;
      this.nearSpeed = nearSpeedIn;
      this.entityPathNavigate = entityIn.getNavigator();
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      float origDistance = this.avoidDistance;
      if (this.entity instanceof EntityWitch && ((EntityWitch)this.entity).getAttackingState() != 2) {
         return false;
      } else {
         List<T> list = this.entity.world.getEntitiesWithinAABB(this.classToAvoid, this.entity.getEntityBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(new Predicate[]{EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector}));
         this.avoidDistance = origDistance;
         if (list.isEmpty()) {
            return false;
         } else {
            this.closestLivingEntity = (Entity)list.get(0);
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));
            if (vec3d == null) {
               return false;
            } else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity)) {
               return false;
            } else {
               this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
               return this.entityPathEntity != null;
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.entity instanceof EntityWitch && ((EntityWitch)this.entity).getAttackingState() != 2) {
         return false;
      } else {
         return !this.entityPathNavigate.noPath();
      }
   }

   public void startExecuting() {
      this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
   }

   public void resetTask() {
      this.closestLivingEntity = null;
   }

   public void updateTask() {
      if (this.entity.getDistanceSq(this.closestLivingEntity) < 49.0D) {
         this.entity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.entity.getNavigator().setSpeed(this.farSpeed);
      }

   }
}
