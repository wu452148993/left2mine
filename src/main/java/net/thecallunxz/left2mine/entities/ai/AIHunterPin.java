package net.thecallunxz.left2mine.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.AnimationAngleMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AIHunterPin extends EntityAIBase {
   private long hunterHitTime;
   private EntityHunter infected;
   private int hand;

   public AIHunterPin(EntityHunter infected) {
      this.infected = infected;
      this.hunterHitTime = 0L;
      this.hand = 0;
      this.setMutexBits(8);
   }

   public boolean shouldExecute() {
      return true;
   }

   public boolean shouldContinueExecuting() {
      return true;
   }

   public void updateTask() {
      if (!this.infected.isPinned()) {
         this.infected.setPounceTicks(this.infected.getPounceTicks() + 1);
      }

      if (this.infected.getPounceTicks() < -10) {
         this.infected.setSneaking(false);
      }

      if (this.infected.isPouncing()) {
         this.infected.getNavigator().clearPath();
         if (this.infected.onGround) {
            if (this.infected.getAttackTarget() != null) {
               this.infected.getNavigator().tryMoveToEntityLiving(this.infected.getAttackTarget(), 1.0D);
            }

            this.infected.setPouncing(false);
         }
      }

      EntityLivingBase entitylivingbase = this.infected.getAttackTarget();
      double distance = 10.0D;
      if (entitylivingbase != null) {
         double distance1 = this.infected.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
         double distance2 = this.infected.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().maxY, entitylivingbase.posZ);
         distance = Math.min(distance1, distance2);
      }

      IEquip equip;
      EntityPlayer pinned;
      if (this.infected.isPouncing() && !this.infected.isPinned() && distance < 1.5D && entitylivingbase instanceof EntityPlayer) {
         pinned = (EntityPlayer)entitylivingbase;
         equip = (IEquip)pinned.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         BlockPos highPos = Left2MineUtilities.getHighestBelow(pinned.getPosition().getX(), pinned.getPosition().getY(), pinned.getPosition().getZ(), pinned.getEntityWorld());
         if (!equip.getPinned() && highPos != null) {
            this.infected.setSneaking(false);
            this.infected.setPouncing(false);
            this.infected.setPinnedId(pinned.getUniqueID());
            this.infected.setPinAngle(this.infected.rotationYaw);
            this.infected.setPinned(true);
            if (!equip.getLying()) {
               pinned.setPositionAndUpdate((double)highPos.getX() + 0.5D, (double)highPos.getY() + 1.0D, (double)highPos.getZ() + 0.5D);
            }

            Left2MinePacket.INSTANCE.sendToDimension(new MovingSoundMessage(this.infected, 4), this.infected.dimension);
            equip.setTetheredUUID(this.infected.getUniqueID());
            equip.setAnimationAngle(this.infected.rotationYaw);
            Left2MinePacket.INSTANCE.sendToAll(new AnimationAngleMessage(equip.getAnimationAngle(), pinned));
            pinned.addPotionEffect(new PotionEffect(InitPotions.hunter_pinned, 9999, 0, true, false));
         }
      }

      if (this.infected.isPinned()) {
         this.infected.rotationYaw = this.infected.getPinAngle();
         this.infected.rotationYawHead = this.infected.getPinAngle();
         this.infected.motionX = 0.0D;
         this.infected.motionY = 0.0D;
         this.infected.motionZ = 0.0D;
         pinned = this.infected.getEntityWorld().getPlayerEntityByUUID(this.infected.getPinnedId());
         if (pinned == null) {
            this.infected.setPounceTicks(-100);
            this.infected.setPinned(false);
            return;
         }

         equip = (IEquip)pinned.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (pinned.isDead || pinned.isSpectator() || !equip.getPinned()) {
            this.infected.setPounceTicks(-100);
            this.infected.setPinned(false);
            return;
         }

         if (equip.getLying()) {
            this.infected.setPositionAndUpdate(pinned.posX, pinned.posY + 1.0D, pinned.posZ);
         } else {
            this.infected.setPositionAndUpdate(pinned.posX, pinned.posY, pinned.posZ);
         }

         World world = this.infected.getEntityWorld();
         if (this.hunterHitTime <= world.getTotalWorldTime()) {
            this.hunterHitTime = world.getTotalWorldTime() + 8L;
            if (this.hand == 0) {
               this.infected.swingArm(EnumHand.MAIN_HAND);
               Left2MinePacket.INSTANCE.sendToDimension(new MovingSoundMessage(this.infected, 1), this.infected.dimension);
               this.hand = 1;
            } else {
               this.infected.swingArm(EnumHand.OFF_HAND);
               this.hand = 0;
            }

            int origResistantTime = pinned.hurtResistantTime;
            equip.setAnimationAngle(this.infected.getPinAngle());
            pinned.attackEntityFrom(Main.HUNTER, DifficultyUtil.getHunterPounceDamage(world));
            pinned.hurtResistantTime = origResistantTime;
         }
      }

   }
}
