package net.thecallunxz.left2mine.entities.mobs.specialinfected;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.ai.AICustomDoorBreak;
import net.thecallunxz.left2mine.entities.ai.AIWitchCry;
import net.thecallunxz.left2mine.entities.ai.AIWitchFlee;
import net.thecallunxz.left2mine.entities.ai.AIWitchWatch;
import net.thecallunxz.left2mine.entities.mobs.DamageSourceMaul;
import net.thecallunxz.left2mine.entities.mobs.EntityBossInfected;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.entities.mobs.ITriggerPush;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.pathfinding.PathNavigateSpecial;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.TankCorpse;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class EntityWitch extends EntityBossInfected implements ITriggerPush, IRagdollEntities {
   private static final DataParameter<Integer> ATTACK_STATE;
   private static final DataParameter<Integer> ANGER_LEVEL;
   private boolean cryMusicAttached;
   private boolean screamMusicAttached;

   public EntityWitch(World worldIn) {
      super(worldIn);
      this.tasks.addTask(0, new EntityWitch.AIDespawn(worldIn, this));
      this.tasks.addTask(1, new AIWitchCry(this));
      this.tasks.addTask(2, new EntityAISwimming(this));
      this.tasks.addTask(3, new AICustomDoorBreak(this, true));
      this.tasks.addTask(4, new AIWitchFlee(this, EntityPlayer.class, 10.0F, 0.9D, 1.0D));
      this.tasks.addTask(5, new EntityWitch.AIAttack(this));
      this.tasks.addTask(6, new AIWitchWatch(this, EntityPlayer.class, 20.0F));
      this.cryMusicAttached = false;
      this.screamMusicAttached = false;
   }

   public boolean isCryMusicAttached() {
      return this.cryMusicAttached;
   }

   public void setCryMusicAttached(boolean cryMusicAttached) {
      this.cryMusicAttached = cryMusicAttached;
   }

   public boolean isScreamMusicAttached() {
      return this.screamMusicAttached;
   }

   public void setScreamMusicAttached(boolean screamMusicAttached) {
      this.screamMusicAttached = screamMusicAttached;
   }

   protected boolean canDespawn() {
      return false;
   }

   public boolean onPush() {
      if (this.getAttackingState() == 0) {
         this.setAngerLevel(2000);
      }

      return false;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(ATTACK_STATE, 0);
      this.dataManager.register(ANGER_LEVEL, 0);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSpecial(this, worldIn);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
   }

   public CustomCorpse getCorpse() {
      return new TankCorpse();
   }

   public int getMaxFallHeight() {
      return 100;
   }

   protected DamageSource getDamageSource() {
      return new EntityDamageSource("mob", this);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("AttackState", this.getAttackingState());
      compound.setInteger("AngerLevel", this.getAngerLevel());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setAttackingState(compound.getInteger("AttackState"));
      this.setAngerLevel(compound.getInteger("AngerLevel"));
   }

   protected float getSoundVolume() {
      return this.getAttackingState() == 2 ? 1.0F : 3.0F;
   }

   public void playLivingSound() {
      super.playLivingSound();
      if (!this.isSilent() && !this.getEntityWorld().isRemote) {
         switch(this.getAttackingState()) {
         case 0:
            if (this.getAngerLevel() < 600) {
               Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 17));
            }
            break;
         case 1:
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 18));
            break;
         case 2:
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 19));
         }
      }

   }

   public int getTalkInterval() {
      return this.getAttackingState() != 1 && this.getAngerLevel() < 500 ? 60 : 20;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return InitSounds.witch_die;
   }

   public int getAttackingState() {
      return (Integer)this.dataManager.get(ATTACK_STATE);
   }

   public void setAttackingState(int state) {
      this.dataManager.set(ATTACK_STATE, state);
   }

   public int getAngerLevel() {
      return (Integer)this.dataManager.get(ANGER_LEVEL);
   }

   public void setAngerLevel(int level) {
      this.dataManager.set(ANGER_LEVEL, level);
   }

   static {
      ATTACK_STATE = EntityDataManager.createKey(EntityWitch.class, DataSerializers.VARINT);
      ANGER_LEVEL = EntityDataManager.createKey(EntityWitch.class, DataSerializers.VARINT);
   }

   static class AIDespawn extends EntityAIBase {
      World world;
      EntityWitch entitywitch;

      public AIDespawn(World worldIn, EntityWitch entitywitch) {
         this.world = worldIn;
         this.entitywitch = entitywitch;
         this.setMutexBits(4);
      }

      public boolean shouldContinueExecuting() {
         return true;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         if (this.entitywitch.getAngerLevel() < 850 && this.entitywitch.getAttackingState() == 0) {
            this.entitywitch.setSize(0.8F, 1.1F);
         } else {
            this.entitywitch.setSize(0.6F, 1.8F);
         }

         if (this.entitywitch.getAttackingState() == 1) {
            if (this.entitywitch.getAttackTarget() == null || this.entitywitch.getAttackTarget().isDead) {
               this.entitywitch.setAttackingState(2);
               Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this.entitywitch, 19));
            }

            if (this.entitywitch.getAttackTarget() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)this.entitywitch.getAttackTarget();
               if (player.isSpectator()) {
                  this.entitywitch.setAttackingState(2);
                  Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this.entitywitch, 19));
               }
            }
         }

         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
         if (this.entitywitch.ticksExisted % 200 == 0) {
            EntityPlayer player = this.world.getClosestPlayer(this.entitywitch.posX, this.entitywitch.posY, this.entitywitch.posZ, 100.0D, predicate);
            if (player == null) {
               this.entitywitch.setDead();
               data.setBossCount(data.getBossCount() - 1);
            }
         }

         if (!data.isInGame()) {
            this.entitywitch.setDead();
            data.setBossCount(data.getBossCount() - 1);
         }

         if (this.entitywitch.ticksExisted % 10 == 0 && this.entitywitch.getAttackingState() == 2 && !Left2MineUtilities.canPlayersSeeEntity(this.world, this.entitywitch)) {
            this.entitywitch.setDead();
            data.setBossCount(data.getBossCount() - 1);
         }

      }
   }

   static class AIAttack extends EntityAIAttackMelee {
      public AIAttack(EntityWitch infected) {
         super(infected, 1.0D, true);
      }

      public boolean shouldExecute() {
         if (super.shouldExecute()) {
            return ((EntityWitch)this.attacker).getAttackingState() == 1;
         } else {
            return false;
         }
      }

      protected void checkAndPerformAttack(EntityLivingBase living, double number) {
         new Random();
         double d0 = this.getAttackReachSqr(living);
         if (number <= d0 * 0.75D && this.attackTick <= 0) {
            this.attackTick = 10;
            if (living.world.rand.nextBoolean()) {
               this.attacker.swingArm(EnumHand.MAIN_HAND);
            } else {
               this.attacker.swingArm(EnumHand.OFF_HAND);
            }

            if (living instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)living;
               IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               if (!equip.getLying() && !player.isPotionActive(InitPotions.incapacitated)) {
                  living.attackEntityFrom(new DamageSourceMaul("witch", this.attacker), 100.0F);
               } else {
                  living.attackEntityFrom(new DamageSourceMaul("witch", this.attacker), 1.0F);
               }
            } else {
               living.attackEntityFrom(new DamageSourceMaul("witch", this.attacker), 1.0F);
            }

            living.hurtResistantTime = 10;
         }

      }

      protected double getAttackReachSqr(EntityLivingBase attackTarget) {
         return (double)(5.0F + attackTarget.width);
      }
   }
}
