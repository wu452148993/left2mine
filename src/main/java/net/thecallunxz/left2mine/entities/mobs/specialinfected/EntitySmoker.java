package net.thecallunxz.left2mine.entities.mobs.specialinfected;

import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.entities.ai.AIPinnedTarget;
import net.thecallunxz.left2mine.entities.ai.AISmokerFlee;
import net.thecallunxz.left2mine.entities.ai.AISmokerMeleeAttack;
import net.thecallunxz.left2mine.entities.ai.AISmokerTongue;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.ITriggerDeath;
import net.thecallunxz.left2mine.entities.mobs.ITriggerPush;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.SmokerCorpse;

public class EntitySmoker extends EntitySpecialInfected implements ITriggerPush, ITriggerDeath {
   private static final DataParameter<Boolean> CANSEE;
   private static final DataParameter<Integer> SEEN_TICKS;
   private static final DataParameter<Boolean> SMOKED;
   private static final DataParameter<Optional<UUID>> SMOKED_ID;
   private long rechargeTime;
   private long lastWarn;

   public EntitySmoker(World worldIn) {
      super(worldIn);
      this.tasks.addTask(1, new AISmokerTongue(this));
      this.tasks.addTask(2, new AISmokerFlee(this, EntityPlayer.class, 10.0F, 1.1D, 1.2D));
      this.tasks.addTask(3, new AISmokerMeleeAttack(this));
      this.targetTasks.addTask(1, new AIPinnedTarget(this, EntityPlayer.class, false));
      this.rechargeTime = 0L;
      this.lastWarn = 0L;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CANSEE, false);
      this.dataManager.register(SEEN_TICKS, 0);
      this.dataManager.register(SMOKED, false);
      this.dataManager.register(SMOKED_ID, Optional.absent());
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("SeenTicks", this.getSeenTicks());
      compound.setBoolean("Smoked", this.isSmoked());
      compound.setLong("lastWarn", this.getLastWarn());
      compound.setLong("rechargeTime", this.getRechargeTime());
      if (this.getSmokedId() != null) {
         compound.setUniqueId("SmokedUUID", this.getSmokedId());
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setSeenTicks(compound.getInteger("SeenTicks"));
      this.setSmoked(compound.getBoolean("Smoked"));
      this.setLastWarn(compound.getLong("lastWarn"));
      this.setRechargeTime(compound.getLong("rechargeTime"));
      if (compound.getUniqueId("SmokedUUID") != null) {
         this.setSmokedId(compound.getUniqueId("SmokedUUID"));
      }

   }

   public boolean onPush() {
      if (this.isSmoked()) {
         EntityPlayer victim = this.getEntityWorld().getPlayerEntityByUUID(this.getSmokedId());
         if (victim != null) {
            this.setRechargeTime(this.world.getTotalWorldTime() + 200L);
            victim.removePotionEffect(InitPotions.smoker_tongued);
         }
      }

      this.setSeenTicks(0);
      this.setSmoked(false);
      return true;
   }

   public void onDeath() {
      if (this.isSmoked()) {
         EntityPlayer victim = this.getEntityWorld().getPlayerEntityByUUID(this.getSmokedId());
         if (victim != null) {
            this.setRechargeTime(this.world.getTotalWorldTime() + 200L);
            victim.removePotionEffect(InitPotions.smoker_tongued);
         }
      }

      this.setSeenTicks(0);
      this.setSmoked(false);
   }

   public CustomCorpse getCorpse() {
      return new SmokerCorpse();
   }

   public int getTalkInterval() {
      return 140;
   }

   public void playLivingSound() {
      super.playLivingSound();
      if (!this.isSilent() && !this.isSmoked() && !this.getEntityWorld().isRemote) {
         if (this.canSee()) {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 11));
         } else {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 10));
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
      return InitSounds.smoker_death;
   }

   @Nullable
   public UUID getSmokedId() {
      return (UUID)((Optional)this.dataManager.get(SMOKED_ID)).orNull();
   }

   public void setSmokedId(@Nullable UUID uuid) {
      this.dataManager.set(SMOKED_ID, Optional.fromNullable(uuid));
   }

   public boolean isSmoked() {
      return (Boolean)this.dataManager.get(SMOKED);
   }

   public void setSmoked(boolean bool) {
      this.dataManager.set(SMOKED, bool);
   }

   public void setCanSee(boolean canSee) {
      this.dataManager.set(CANSEE, canSee);
   }

   public boolean canSee() {
      return (Boolean)this.dataManager.get(CANSEE);
   }

   public int getSeenTicks() {
      return (Integer)this.dataManager.get(SEEN_TICKS);
   }

   public void setSeenTicks(int ticks) {
      this.dataManager.set(SEEN_TICKS, ticks);
   }

   public long getLastWarn() {
      return this.lastWarn;
   }

   public void setLastWarn(long lastWarn) {
      this.lastWarn = lastWarn;
   }

   public long getRechargeTime() {
      return this.rechargeTime;
   }

   public void setRechargeTime(long rechargeTime) {
      this.rechargeTime = rechargeTime;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.315D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   static {
      CANSEE = EntityDataManager.createKey(EntitySmoker.class, DataSerializers.BOOLEAN);
      SEEN_TICKS = EntityDataManager.createKey(EntitySmoker.class, DataSerializers.VARINT);
      SMOKED = EntityDataManager.createKey(EntitySmoker.class, DataSerializers.BOOLEAN);
      SMOKED_ID = EntityDataManager.createKey(EntitySmoker.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   }
}
