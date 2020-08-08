package net.thecallunxz.left2mine.entities.mobs;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.entities.ai.AICustomDoorBreak;
import net.thecallunxz.left2mine.entities.ai.AICustomHurtAttack;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.pathfinding.PathNavigateSpecial;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public abstract class EntitySpecialInfected extends EntityCustomMob implements IRagdollEntities {
   private int randomCheck;

   public EntitySpecialInfected(World worldIn) {
      super(worldIn);
      this.tasks.addTask(0, new EntitySpecialInfected.AIDespawn(worldIn, this));
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new AICustomDoorBreak(this, true));
      this.targetTasks.addTask(2, new AICustomHurtAttack(this, false));
      this.randomCheck = this.world.rand.nextInt(60) + 200;
   }

   protected void entityInit() {
      super.entityInit();
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected boolean canDespawn() {
      return false;
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSpecial(this, worldIn);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
   }

   public int getMaxFallHeight() {
      return 100;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      return super.writeToNBT(nbt);
   }

   protected DamageSource getDamageSource() {
      return new EntityDamageSource("mob", this);
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public int getTalkInterval() {
      return 20;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return null;
   }

   public int getRandomCheck() {
      return this.randomCheck;
   }

   static class AIDespawn extends EntityAIBase {
      World world;
      EntitySpecialInfected entityspecial;

      public AIDespawn(World worldIn, EntitySpecialInfected entityspecial) {
         this.world = worldIn;
         this.entityspecial = entityspecial;
         this.setMutexBits(4);
      }

      public boolean shouldContinueExecuting() {
         return true;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
         if (this.entityspecial.ticksExisted % this.entityspecial.getRandomCheck() == 0) {
            EntityPlayer player = this.world.getClosestPlayer(this.entityspecial.posX, this.entityspecial.posY, this.entityspecial.posZ, 100.0D, predicate);
            if (player == null) {
               if (Left2MineUtilities.canPlayersSeeEntity(this.world, this.entityspecial)) {
                  return;
               }

               if (this.entityspecial instanceof EntityHunter) {
                  data.addLurkingSpecial("hunter");
               }

               if (this.entityspecial instanceof EntityBoomer) {
                  data.addLurkingSpecial("boomer");
               }

               if (this.entityspecial instanceof EntitySmoker) {
                  data.addLurkingSpecial("smoker");
               }

               data.setSpecialCount(data.getSpecialCount() - 1);
               this.entityspecial.setDead();
            }
         }

         if (this.entityspecial.ticksExisted % this.entityspecial.getRandomCheck() == 0) {
            if (this.entityspecial.getAttackTarget() != null) {
               ((PathNavigateGround)this.entityspecial.getNavigator()).setBreakDoors(true);
               ((PathNavigateGround)this.entityspecial.getNavigator()).getNodeProcessor().setCanEnterDoors(true);
               ((PathNavigateGround)this.entityspecial.getNavigator()).getNodeProcessor().setCanOpenDoors(true);
               if (this.entityspecial.getAttackTarget() instanceof EntityPlayer && Left2MineUtilities.getPathToPos(this.entityspecial.getPosition(), this.entityspecial.getAttackTarget().getPosition(), this.world).distanceSq(this.entityspecial.getAttackTarget().getPosition()) > 2.0D) {
                  if (Left2MineUtilities.canPlayersSeeEntity(this.world, this.entityspecial)) {
                     return;
                  }

                  if (this.entityspecial instanceof EntityHunter) {
                     data.addLurkingSpecial("hunter");
                  }

                  if (this.entityspecial instanceof EntityBoomer) {
                     data.addLurkingSpecial("boomer");
                  }

                  if (this.entityspecial instanceof EntitySmoker) {
                     data.addLurkingSpecial("smoker");
                  }

                  data.setSpecialCount(data.getSpecialCount() - 1);
                  this.entityspecial.setDead();
               }
            } else {
               ((PathNavigateGround)this.entityspecial.getNavigator()).setBreakDoors(false);
               ((PathNavigateGround)this.entityspecial.getNavigator()).getNodeProcessor().setCanEnterDoors(false);
               ((PathNavigateGround)this.entityspecial.getNavigator()).getNodeProcessor().setCanOpenDoors(false);
            }
         }

         if (!data.isInGame()) {
            data.setSpecialCount(data.getSpecialCount() - 1);
            this.entityspecial.setDead();
         }

      }
   }
}
