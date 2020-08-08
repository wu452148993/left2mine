package net.thecallunxz.left2mine.entities.mobs.specialinfected;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.entities.ExplosionBoomer;
import net.thecallunxz.left2mine.entities.ai.AIBoomerMeleeAttack;
import net.thecallunxz.left2mine.entities.ai.AIBoomerPuke;
import net.thecallunxz.left2mine.entities.ai.AICustomAttackTarget;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.ITriggerDeath;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.HunterCorpse;

public class EntityBoomer extends EntitySpecialInfected implements ITriggerDeath {
   private static final DataParameter<Boolean> CANSEE;
   private static final DataParameter<Integer> PUKE_TICKS;

   public EntityBoomer(World worldIn) {
      super(worldIn);
      this.tasks.addTask(1, new AIBoomerPuke(this));
      this.tasks.addTask(2, new AIBoomerMeleeAttack(this));
      this.targetTasks.addTask(1, new AICustomAttackTarget(this, EntityPlayer.class, false));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CANSEE, false);
      this.dataManager.register(PUKE_TICKS, 0);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("PukeTicks", this.getPukeTicks());
   }

   public void onDeath() {
      ExplosionBoomer explosion = new ExplosionBoomer(this.world, this, this.posX, this.posY, this.posZ, 1.8F, false, true);
      explosion.doExplosionA();
   }

   public CustomCorpse getCorpse() {
      return new HunterCorpse();
   }

   public int getTalkInterval() {
      return 140;
   }

   public void playLivingSound() {
      super.playLivingSound();
      if (!this.isSilent() && !this.getEntityWorld().isRemote) {
         if (this.canSee()) {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 7));
         } else {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 6));
         }
      }

   }

   private void applyEntityAI() {
      this.livingSoundTime = -this.getTalkInterval();
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
      this.world.profiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && 1 < this.livingSoundTime++) {
         this.applyEntityAI();
         this.playLivingSound();
      }

      this.world.profiler.endSection();
   }

   protected float getSoundVolume() {
      return 8.0F;
   }

   protected SoundEvent getDeathSound() {
      return null;
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setPukeTicks(compound.getInteger("PukeTicks"));
   }

   public int getPukeTicks() {
      return (Integer)this.dataManager.get(PUKE_TICKS);
   }

   public void setPukeTicks(int ticks) {
      this.dataManager.set(PUKE_TICKS, ticks);
   }

   public void setCanSee(boolean canSee) {
      this.dataManager.set(CANSEE, canSee);
   }

   public boolean canSee() {
      return (Boolean)this.dataManager.get(CANSEE);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.265D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   static {
      CANSEE = EntityDataManager.createKey(EntityBoomer.class, DataSerializers.BOOLEAN);
      PUKE_TICKS = EntityDataManager.createKey(EntityBoomer.class, DataSerializers.VARINT);
   }
}
