package net.thecallunxz.left2mine.entities.ai;

import java.util.Iterator;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.init.InitSounds;

public class AICustomHurtAttack extends AICustomTarget {
   private final boolean entityCallsForHelp;
   private int revengeTimerOld;

   public AICustomHurtAttack(EntityCreature creatureIn, boolean entityCallsForHelpIn) {
      super(creatureIn, true);
      this.entityCallsForHelp = entityCallsForHelpIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.taskOwner.getRevengeTarget();
      return entitylivingbase != null;
   }

   public void startExecuting() {
      if (this.entityCallsForHelp) {
         this.alertOthers();
      }

      this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
      this.target = this.taskOwner.getAttackTarget();
      this.revengeTimerOld = this.taskOwner.getRevengeTimer();
      this.unseenMemoryTicks = 300;
      super.startExecuting();
   }

   protected void alertOthers() {
      if (this.taskOwner instanceof EntityCommonInfected && !((EntityCommonInfected)this.taskOwner).isPanic() && this.taskOwner.getAttackTarget() == null) {
         Iterator var1 = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), (new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D)).grow(25.0D, 10.0D, 25.0D)).iterator();

         while(var1.hasNext()) {
            EntityCreature entitycreature = (EntityCreature)var1.next();
            if (entitycreature instanceof EntityCommonInfected && Math.pow((double)(entitycreature.ticksExisted - this.taskOwner.ticksExisted), 2.0D) < 1600.0D && entitycreature.getAttackTarget() == null) {
               entitycreature.setAttackTarget(this.target);
            }
         }

         this.taskOwner.world.playSound((EntityPlayer)null, this.taskOwner.getPosition(), InitSounds.common_ambient_rage, SoundCategory.HOSTILE, 1.25F, 1.0F);
      }

   }

   protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
      creatureIn.setAttackTarget(entityLivingBaseIn);
   }
}
