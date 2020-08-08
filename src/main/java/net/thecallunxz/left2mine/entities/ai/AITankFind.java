package net.thecallunxz.left2mine.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AITankFind extends EntityAIBase {
   private EntityTank infected;

   public AITankFind(EntityTank infected) {
      this.infected = infected;
      this.setMutexBits(8);
   }

   public boolean shouldExecute() {
      return !this.infected.isAttacking();
   }

   public boolean shouldContinueExecuting() {
      return !this.infected.isAttacking();
   }

   public void updateTask() {
      if (this.infected.ticksExisted % 2 == 0 && Left2MineUtilities.canPlayersSeeEntity(this.infected.getEntityWorld(), this.infected)) {
         this.infected.setAttacking(true);
      }

   }
}
