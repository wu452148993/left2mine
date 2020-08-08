package net.thecallunxz.left2mine.entities.mobs;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.entities.EntityPipebomb;
import net.thecallunxz.left2mine.entities.ai.AIBurnPanic;
import net.thecallunxz.left2mine.entities.ai.AICustomAttackTarget;
import net.thecallunxz.left2mine.entities.ai.AICustomDoorBreak;
import net.thecallunxz.left2mine.entities.ai.AICustomHurtAttack;
import net.thecallunxz.left2mine.entities.ai.AICustomWander;
import net.thecallunxz.left2mine.entities.ai.AITargetCanPath;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.pathfinding.PathNavigateSpecial;
import net.thecallunxz.left2mine.ragdoll.corpse.CommonCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class EntityCommonInfected extends EntityCustomMob implements IRagdollEntities {
   private static final DataParameter<Boolean> PANIC;
   private int randomCheck;
   private int skinNumber;

   public EntityCommonInfected(World worldIn) {
      super(worldIn);
      this.skinNumber = 0;
      this.tasks.addTask(0, new EntityCommonInfected.AIDespawn(worldIn, this));
      this.tasks.addTask(1, new AIBurnPanic(this, 1.1D));
      this.tasks.addTask(2, new EntityAISwimming(this));
      this.tasks.addTask(3, new AICustomDoorBreak(this, false));
      this.tasks.addTask(4, new EntityCommonInfected.AIAttack(this));
      this.tasks.addTask(5, new EntityCommonInfected.AIPanicFind(worldIn, this));
      this.tasks.addTask(6, new AICustomWander(this, 0.25D, 5));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new AICustomAttackTarget(this, EntityPipebomb.class, true));
      this.targetTasks.addTask(2, new AICustomHurtAttack(this, false));
      this.targetTasks.addTask(3, new AITargetCanPath(this, EntityPlayer.class, true));
      this.skinNumber = this.world.rand.nextInt(8);
      this.randomCheck = this.world.rand.nextInt(60) + 20;
   }

   public EntityCommonInfected(World worldIn, int num) {
      this(worldIn);
      this.skinNumber = num;
   }

   protected boolean canDespawn() {
      return false;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.34D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(PANIC, false);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSpecial(this, worldIn);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
   }

   public void setSkinNumber(int num) {
      this.skinNumber = num;
   }

   public int getSkinNumber() {
      return this.skinNumber;
   }

   public CustomCorpse getCorpse() {
      return new CommonCorpse();
   }

   public int getMaxFallHeight() {
      return 100;
   }

   protected DamageSource getDamageSource() {
      return new EntityDamageSource("mob", this);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Panic", this.isPanic());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setPanic(compound.getBoolean("Panic"));
   }

   protected float getSoundVolume() {
      return (this.getAttackTarget() != null || this.isPanic()) && this.getHealth() > 0.0F ? 1.25F : 0.6F;
   }

   public int getTalkInterval() {
      return this.getAttackTarget() == null && !this.isPanic() ? 40 : 5;
   }

   protected SoundEvent getAmbientSound() {
      return this.getAttackTarget() == null && !this.isPanic() ? InitSounds.common_ambient : InitSounds.common_ambient_rage;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return InitSounds.common_death;
   }

   public int getRandomCheck() {
      return this.randomCheck;
   }

   public boolean isPanic() {
      return (Boolean)this.dataManager.get(PANIC);
   }

   public void setPanic(boolean panic) {
      this.dataManager.set(PANIC, panic);
   }

   static {
      PANIC = EntityDataManager.createKey(EntityCommonInfected.class, DataSerializers.BOOLEAN);
   }

   static class AIPanicFind extends EntityAIBase {
      World world;
      EntityCommonInfected entitycommon;

      public AIPanicFind(World worldIn, EntityCommonInfected entityCommonInfected) {
         this.world = worldIn;
         this.entitycommon = entityCommonInfected;
         this.setMutexBits(1);
      }

      public boolean shouldContinueExecuting() {
         return this.entitycommon.isPanic();
      }

      public boolean shouldExecute() {
         return this.entitycommon.isPanic();
      }

      public void updateTask() {
         Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
         EntityPlayer player = this.world.getClosestPlayer(this.entitycommon.posX, this.entitycommon.posY, this.entitycommon.posZ, 50.0D, predicate);
         if (this.entitycommon.isPanic() && player != null) {
            if (this.entitycommon.getAttackTarget() == null) {
               this.entitycommon.setAttackTarget(player);
               if (this.entitycommon.ticksExisted % this.entitycommon.getRandomCheck() == 0) {
                  this.entitycommon.getNavigator().tryMoveToEntityLiving(player, 1.0D);
               }
            } else if (this.entitycommon.ticksExisted % this.entitycommon.getRandomCheck() == 0) {
               this.entitycommon.getNavigator().tryMoveToEntityLiving(this.entitycommon.getAttackTarget(), 1.0D);
            }
         }

      }
   }

   static class AIDespawn extends EntityAIBase {
      World world;
      EntityCommonInfected entitycommon;

      public AIDespawn(World worldIn, EntityCommonInfected entityCommonInfected) {
         this.world = worldIn;
         this.entitycommon = entityCommonInfected;
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
         if (this.entitycommon.ticksExisted % this.entitycommon.getRandomCheck() == 0) {
            EntityPlayer player = this.world.getClosestPlayer(this.entitycommon.posX, this.entitycommon.posY, this.entitycommon.posZ, 100.0D, predicate);
            if (player == null) {
               if (Left2MineUtilities.canPlayersSeeEntity(this.world, this.entitycommon)) {
                  return;
               }

               if (this.entitycommon.isPanic()) {
                  data.setLurkingCount(data.getLurkingCount() + 1);
                  data.setHordeCount(data.getHordeCount() - 1);
               } else {
                  data.setWanderingCount(data.getWanderingCount() - 1);
               }

               this.entitycommon.setDead();
            }
         }

         if (this.entitycommon.ticksExisted % this.entitycommon.getRandomCheck() == 0) {
            if (this.entitycommon.getAttackTarget() != null) {
               if (this.entitycommon.getAttackTarget() instanceof EntityPipebomb) {
                  EntityPipebomb pipebomb = (EntityPipebomb)this.entitycommon.getAttackTarget();
                  if (pipebomb.ticksExisted > 115) {
                     this.entitycommon.setAttackTarget((EntityLivingBase)null);
                  }
               }

               ((PathNavigateGround)this.entitycommon.getNavigator()).setBreakDoors(true);
               ((PathNavigateGround)this.entitycommon.getNavigator()).getNodeProcessor().setCanEnterDoors(true);
               ((PathNavigateGround)this.entitycommon.getNavigator()).getNodeProcessor().setCanOpenDoors(true);
               if (this.entitycommon.getAttackTarget() instanceof EntityPlayer && Left2MineUtilities.getPathToPos(this.entitycommon.getPosition(), this.entitycommon.getAttackTarget().getPosition(), this.world).distanceSq(this.entitycommon.getAttackTarget().getPosition()) > 2.0D) {
                  if (Left2MineUtilities.canPlayersSeeEntity(this.world, this.entitycommon)) {
                     return;
                  }

                  data.setLurkingCount(data.getLurkingCount() + 1);
                  if (this.entitycommon.isPanic()) {
                     data.setHordeCount(data.getHordeCount() - 1);
                  } else {
                     data.setWanderingCount(data.getWanderingCount() - 1);
                  }

                  this.entitycommon.setDead();
               }
            } else {
               ((PathNavigateGround)this.entitycommon.getNavigator()).setBreakDoors(false);
               ((PathNavigateGround)this.entitycommon.getNavigator()).getNodeProcessor().setCanEnterDoors(false);
               ((PathNavigateGround)this.entitycommon.getNavigator()).getNodeProcessor().setCanOpenDoors(false);
            }
         }

         if (!data.isInGame()) {
            if (this.entitycommon.isPanic()) {
               data.setHordeCount(data.getHordeCount() - 1);
            } else {
               data.setWanderingCount(data.getWanderingCount() - 1);
            }

            this.entitycommon.setDead();
         }

      }
   }

   static class AIAttack extends EntityAIAttackMelee {
      public AIAttack(EntityCommonInfected infected) {
         super(infected, 1.0D, true);
      }

      protected void checkAndPerformAttack(EntityLivingBase living, double number) {
         Random rand = new Random();
         double d0 = this.getAttackReachSqr(living);
         if (number <= d0 * 0.75D && this.attackTick <= 0) {
            this.attackTick = 40;
            this.attacker.swingArm(rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            living.attackEntityFrom(new DamageSourceMaul("maul", this.attacker), DifficultyUtil.getCommonDamage(living.getEntityWorld()));
            living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1, true, false));
            living.hurtResistantTime = 10;
         }

      }

      protected double getAttackReachSqr(EntityLivingBase attackTarget) {
         return (double)(3.0F + attackTarget.width);
      }
   }
}
