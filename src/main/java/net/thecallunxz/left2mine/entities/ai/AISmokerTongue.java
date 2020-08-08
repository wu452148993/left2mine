package net.thecallunxz.left2mine.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.AnimationAngleMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AISmokerTongue extends EntityAIBase {
   private EntitySmoker infected;
   private long smokerHitTime;
   private int hand;
   private int maxChargeTime;

   public AISmokerTongue(EntitySmoker infected) {
      this.infected = infected;
      this.maxChargeTime = DifficultyUtil.getSmokerChargeTime(infected.world);
      this.setMutexBits(8);
   }

   public boolean shouldExecute() {
      return true;
   }

   public boolean shouldContinueExecuting() {
      return true;
   }

   public void updateTask() {
      EntityPlayer victim;
      IEquip equip;
      if (!this.infected.isSmoked()) {
         if (this.infected.getAttackTarget() == null) {
            this.infected.setCanSee(false);
            this.infected.setSeenTicks(0);
            return;
         }

         if (Left2MineUtilities.canEntitySeeEntity(this.infected, this.infected.getAttackTarget(), this.infected.getEntityWorld())) {
            this.infected.setCanSee(true);
         } else {
            this.infected.setCanSee(false);
            this.infected.setSeenTicks(0);
         }

         if (this.infected.canSee() && !this.infected.isSmoked() && this.infected.getRechargeTime() < this.infected.world.getTotalWorldTime()) {
            this.infected.setSeenTicks(MathHelper.clamp(this.infected.getSeenTicks() + 1, 0, this.maxChargeTime));
         }

         if (this.infected.getSeenTicks() >= 5 && this.infected.getAttackTarget() instanceof EntityPlayer) {
            victim = (EntityPlayer)this.infected.getAttackTarget();
            equip = (IEquip)victim.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.getPinned()) {
               if (this.infected.getLastWarn() < this.infected.world.getTotalWorldTime()) {
                  Left2MinePacket.INSTANCE.sendToDimension(new MovingSoundMessage(this.infected, 12), this.infected.dimension);
                  this.infected.setLastWarn(this.infected.world.getTotalWorldTime() + (long)this.maxChargeTime);
               }

               this.infected.getNavigator().clearPath();
               this.infected.getLookHelper().setLookPositionWithEntity(victim, 90.0F, 90.0F);
            }
         }

         if (this.infected.getSeenTicks() == this.maxChargeTime && Left2MineUtilities.canEntitySeeEntity(this.infected, this.infected.getAttackTarget(), this.infected.getEntityWorld()) && this.infected.getAttackTarget() instanceof EntityPlayer) {
            victim = (EntityPlayer)this.infected.getAttackTarget();
            equip = (IEquip)victim.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.getPinned()) {
               this.infected.setSmoked(true);
               this.infected.setSmokedId(victim.getUniqueID());
               equip.setTetheredUUID(this.infected.getUniqueID());
               this.infected.getNavigator().clearPath();
               this.infected.getLookHelper().setLookPosition(victim.posX, victim.posY, victim.posZ, 90.0F, 90.0F);
               victim.addPotionEffect(new PotionEffect(InitPotions.smoker_tongued, 9999, 0, true, false));
               equip.setAnimationAngle(MathHelper.wrapDegrees(this.infected.rotationYawHead + 180.0F));
               Left2MinePacket.INSTANCE.sendToAll(new AnimationAngleMessage(equip.getAnimationAngle(), victim));
               Left2MinePacket.INSTANCE.sendToDimension(new MovingSoundMessage(this.infected, victim, 13), this.infected.dimension);
               this.infected.world.playSound((EntityPlayer)null, this.infected.posX, this.infected.posY, this.infected.posZ, InitSounds.smoker_launchtongue, SoundCategory.HOSTILE, 8.0F, 1.0F);
               this.infected.setRechargeTime(this.infected.world.getTotalWorldTime() + 200L);
            }

            this.infected.setSeenTicks(0);
         }
      } else {
         this.infected.getNavigator().clearPath();
         this.infected.motionX = 0.0D;
         this.infected.motionY = 0.0D;
         this.infected.motionZ = 0.0D;
         victim = this.infected.getEntityWorld().getPlayerEntityByUUID(this.infected.getSmokedId());
         if (victim == null) {
            this.infected.setRechargeTime(this.infected.world.getTotalWorldTime() + 200L);
            this.infected.setSeenTicks(0);
            this.infected.setSmoked(false);
            return;
         }

         equip = (IEquip)victim.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (victim.isDead || victim.isSpectator() || !equip.getPinned()) {
            this.infected.setRechargeTime(this.infected.world.getTotalWorldTime() + 200L);
            this.infected.setSeenTicks(0);
            this.infected.setSmoked(false);
            return;
         }

         this.infected.getNavigator().clearPath();
         if (equip.getLying()) {
            this.infected.getLookHelper().setLookPosition(victim.posX, victim.posY + 1.0D, victim.posZ, 90.0F, 90.0F);
         } else {
            this.infected.getLookHelper().setLookPosition(victim.posX, victim.posY, victim.posZ, 90.0F, 90.0F);
         }

         if (this.infected.world.getTotalWorldTime() % 20L == 0L) {
            equip.setAnimationAngle(MathHelper.wrapDegrees(this.infected.rotationYawHead + 180.0F));
            Left2MinePacket.INSTANCE.sendToAll(new AnimationAngleMessage(equip.getAnimationAngle(), victim));
         }

         if (this.smokerHitTime <= this.infected.world.getTotalWorldTime()) {
            this.smokerHitTime = this.infected.world.getTotalWorldTime() + 8L;
            if (this.infected.getDistance(victim) < 2.0F) {
               if (this.hand == 0) {
                  this.infected.swingArm(EnumHand.MAIN_HAND);
                  this.hand = 1;
               } else {
                  this.infected.swingArm(EnumHand.OFF_HAND);
                  this.hand = 0;
               }
            }
         }
      }

   }
}
