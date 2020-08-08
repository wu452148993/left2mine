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
import net.thecallunxz.left2mine.entities.ai.AIHunterFlee;
import net.thecallunxz.left2mine.entities.ai.AIHunterMeleeAttack;
import net.thecallunxz.left2mine.entities.ai.AIHunterPin;
import net.thecallunxz.left2mine.entities.ai.AIHunterPounceAttack;
import net.thecallunxz.left2mine.entities.ai.AIPinnedTarget;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.ITriggerDeath;
import net.thecallunxz.left2mine.entities.mobs.ITriggerPush;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.HunterCorpse;

public class EntityHunter extends EntitySpecialInfected implements ITriggerPush, ITriggerDeath {
   private static final DataParameter<Boolean> POUNCING;
   private static final DataParameter<Boolean> PINNEDDOWN;
   private static final DataParameter<Integer> POUNCE_TICKS;
   private static final DataParameter<Float> PINANGLE;
   private static final DataParameter<Optional<UUID>> PINNED_ID;
   private long lastWarn;

   public EntityHunter(World worldIn) {
      super(worldIn);
      this.tasks.addTask(1, new AIHunterPin(this));
      this.tasks.addTask(2, new AIHunterFlee(this, EntityPlayer.class, 2.5F, 1.1D, 1.1D));
      this.tasks.addTask(3, new AIHunterMeleeAttack(this));
      this.tasks.addTask(4, new AIHunterPounceAttack(this, 30, -10));
      this.targetTasks.addTask(1, new AIPinnedTarget(this, EntityPlayer.class, false));
      this.lastWarn = 0L;
   }

   protected float getSoundVolume() {
      return 8.0F;
   }

   public boolean onPush() {
      if (this.getPounceTicks() > -20) {
         this.setPounceTicks(-20);
      }

      if (this.isPinned()) {
         this.setPounceTicks(-100);
         EntityPlayer pinned = this.getEntityWorld().getPlayerEntityByUUID(this.getPinnedId());
         if (pinned != null) {
            pinned.removePotionEffect(InitPotions.hunter_pinned);
         }

         this.setPinned(false);
      }

      return true;
   }

   public void onDeath() {
      if (this.isPinned()) {
         EntityPlayer pinned = this.getEntityWorld().getPlayerEntityByUUID(this.getPinnedId());
         if (pinned != null) {
            pinned.removePotionEffect(InitPotions.hunter_pinned);
         }

         this.setPinned(false);
      }

   }

   public CustomCorpse getCorpse() {
      return new HunterCorpse();
   }

   protected SoundEvent getDeathSound() {
      return InitSounds.hunter_death;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(POUNCING, false);
      this.dataManager.register(PINNEDDOWN, false);
      this.dataManager.register(POUNCE_TICKS, -10);
      this.dataManager.register(PINANGLE, 0.0F);
      this.dataManager.register(PINNED_ID, Optional.absent());
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Pouncing", this.isPouncing());
      compound.setBoolean("PinnedDown", this.isPinned());
      compound.setInteger("PounceTicks", this.getPounceTicks());
      compound.setFloat("PinAngle", this.getPinAngle());
      compound.setLong("lastWarn", this.getLastWarn());
      if (this.getPinnedId() != null) {
         compound.setUniqueId("PinnedUUID", this.getPinnedId());
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setPouncing(compound.getBoolean("Pouncing"));
      this.setPinned(compound.getBoolean("PinnedDown"));
      this.setPounceTicks(compound.getInteger("PounceTicks"));
      this.setPinAngle(compound.getFloat("PinAngle"));
      this.setLastWarn(compound.getLong("lastWarn"));
      if (compound.getUniqueId("PinnedUUID") != null) {
         this.setPinnedId(compound.getUniqueId("PinnedUUID"));
      }

   }

   public boolean isPouncing() {
      return (Boolean)this.dataManager.get(POUNCING);
   }

   public void setPouncing(boolean pouncing) {
      this.dataManager.set(POUNCING, pouncing);
   }

   public boolean isPinned() {
      return (Boolean)this.dataManager.get(PINNEDDOWN);
   }

   public void setPinned(boolean pinned) {
      if (!pinned) {
         this.setPinnedId((UUID)null);
      }

      this.dataManager.set(PINNEDDOWN, pinned);
   }

   public int getPounceTicks() {
      return (Integer)this.dataManager.get(POUNCE_TICKS);
   }

   public void setPounceTicks(int ticks) {
      this.dataManager.set(POUNCE_TICKS, ticks);
   }

   public float getPinAngle() {
      return (Float)this.dataManager.get(PINANGLE);
   }

   public void setPinAngle(float angle) {
      this.dataManager.set(PINANGLE, angle);
   }

   @Nullable
   public UUID getPinnedId() {
      return (UUID)((Optional)this.dataManager.get(PINNED_ID)).orNull();
   }

   public void setPinnedId(@Nullable UUID uuid) {
      this.dataManager.set(PINNED_ID, Optional.fromNullable(uuid));
   }

   public long getLastWarn() {
      return this.lastWarn;
   }

   public void setLastWarn(long lastWarn) {
      this.lastWarn = lastWarn;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.34D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   static {
      POUNCING = EntityDataManager.createKey(EntityHunter.class, DataSerializers.BOOLEAN);
      PINNEDDOWN = EntityDataManager.createKey(EntityHunter.class, DataSerializers.BOOLEAN);
      POUNCE_TICKS = EntityDataManager.createKey(EntityHunter.class, DataSerializers.VARINT);
      PINANGLE = EntityDataManager.createKey(EntityHunter.class, DataSerializers.FLOAT);
      PINNED_ID = EntityDataManager.createKey(EntityHunter.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   }
}
