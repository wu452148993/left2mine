package net.thecallunxz.left2mine.entities.ai;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AIHunterPounceAttack extends EntityAIAttackMelee {
   EntityHunter infected;
   int maxPounceTick;
   int resetPounceTick;

   public AIHunterPounceAttack(EntityHunter infected, int maxPounceTick, int resetPounceTick) {
      super(infected, 1.0D, true);
      this.maxPounceTick = maxPounceTick;
      this.resetPounceTick = resetPounceTick;
      this.infected = infected;
   }

   public boolean shouldExecute() {
      if (super.shouldExecute() && !this.infected.isPinned()) {
         this.infected.setPounceTicks(0);
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting() && !this.infected.isPinned();
   }

   public void updateTask() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      double d0;
      if (!Left2MineUtilities.canEntityGetEntity(this.attacker, entitylivingbase, this.attacker.getEntityWorld()) || this.attacker.getDistance(entitylivingbase) > 20.0F && !this.infected.isSneaking()) {
         super.updateTask();
         this.infected.setPounceTicks(0);
         this.attacker.setSneaking(false);
      } else {
         super.updateTask();
         double d0 = entitylivingbase.posX - this.infected.posX;
         d0 = entitylivingbase.posZ - this.infected.posZ;
         float f = (float)(MathHelper.atan2(d0, d0) * 57.29577951308232D) - 90.0F;
         this.infected.rotationYawHead = f;
         this.infected.rotationYaw = f;
         if (this.infected.getPounceTicks() >= -10) {
            if (this.infected.getLastWarn() < this.infected.world.getTotalWorldTime()) {
               Left2MinePacket.INSTANCE.sendToDimension(new MovingSoundMessage(this.infected, 0), this.infected.dimension);
               this.infected.setLastWarn(this.infected.world.getTotalWorldTime() + 60L);
            }

            this.attacker.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 2, true, false));
            this.attacker.setSneaking(true);
         } else {
            this.attacker.setSneaking(false);
         }
      }

      if (this.infected.getPounceTicks() >= this.maxPounceTick) {
         Random rand = new Random();
         Vec3d motion = Left2MineUtilities.calculateVelocity(this.infected.getPositionVector(), entitylivingbase.getPositionVector().addVector(rand.nextDouble() - 0.5D, 0.0D, rand.nextDouble() - 0.5D), Math.max(entitylivingbase.getPosition().getY() - this.infected.getPosition().getY() + 2, 1));
         this.infected.getNavigator().clearPath();
         this.infected.world.playSound((EntityPlayer)null, this.infected.posX, this.infected.posY, this.infected.posZ, InitSounds.hunter_pounce, SoundCategory.HOSTILE, 8.0F, 1.0F);
         d0 = entitylivingbase.posX - this.infected.posX;
         double d2 = entitylivingbase.posZ - this.infected.posZ;
         float f = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;
         this.infected.rotationYawHead = f;
         this.infected.rotationYaw = f;
         this.infected.motionX = motion.x * 3.4D;
         this.infected.motionY = motion.y;
         this.infected.motionZ = motion.z * 3.4D;
         this.infected.setPounceTicks(this.resetPounceTick + rand.nextInt(30));
         this.infected.onGround = false;
         this.infected.setPouncing(true);
      }

   }

   protected void checkAndPerformAttack(EntityLivingBase living, double number) {
   }

   protected double getAttackReachSqr(EntityLivingBase attackTarget) {
      return (double)(2.0F + attackTarget.width);
   }
}
