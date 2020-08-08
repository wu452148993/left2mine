package net.thecallunxz.left2mine.entities.ai;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.thecallunxz.left2mine.entities.mobs.DamageSourceMaul;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.util.DifficultyUtil;

public class AIBoomerMeleeAttack extends EntityAIAttackMelee {
   public AIBoomerMeleeAttack(EntityBoomer entityBoomer) {
      super(entityBoomer, 1.0D, true);
   }

   protected void checkAndPerformAttack(EntityLivingBase living, double number) {
      Random rand = new Random();
      double d0 = this.getAttackReachSqr(living);
      if (number <= d0 * 0.75D && this.attackTick <= 0) {
         this.attackTick = 40;
         this.attacker.swingArm(rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
         living.attackEntityFrom(new DamageSourceMaul("maul", this.attacker), DifficultyUtil.getCommonDamage(living.getEntityWorld()));
         living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1, true, false));
         living.hurtResistantTime = 10;
      }

   }

   protected double getAttackReachSqr(EntityLivingBase attackTarget) {
      return (double)(2.0F + attackTarget.width);
   }
}
