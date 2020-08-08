package net.thecallunxz.left2mine.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AITargetCanPath extends AICustomAttackTarget {
   public AITargetCanPath(EntityCreature creature, Class classTarget, boolean checkSight) {
      super(creature, classTarget, checkSight);
   }

   public boolean shouldExecute() {
      boolean bool = super.shouldExecute();
      return bool && Left2MineUtilities.getPathToPos(this.taskOwner.getPosition(), this.targetEntity.getPosition(), this.targetEntity.getEntityWorld()).distanceSq(this.targetEntity.getPosition()) < 3.0D;
   }
}
