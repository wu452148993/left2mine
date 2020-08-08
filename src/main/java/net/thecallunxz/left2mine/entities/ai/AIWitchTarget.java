package net.thecallunxz.left2mine.entities.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;

public class AIWitchTarget extends AICustomAttackTarget {
   public AIWitchTarget(EntityCreature creature, Class classTarget, boolean checkSight) {
      super(creature, classTarget, checkSight);
   }

   public boolean shouldExecute() {
      boolean bool = super.shouldExecute();
      if (bool && this.targetEntity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.targetEntity;
         IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (equip.getPinned()) {
            this.targetEntity = null;
         }

         return !equip.getPinned();
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      boolean bool = super.shouldContinueExecuting();
      if (bool && this.targetEntity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.targetEntity;
         IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (equip.getPinned()) {
            this.targetEntity = null;
         }

         return !equip.getPinned();
      } else {
         return false;
      }
   }

   protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
      if (target instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)target;
         IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (equip.getPinned()) {
            return false;
         }
      }

      return super.isSuitableTarget(target, includeInvincibles);
   }
}
