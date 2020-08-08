package net.thecallunxz.left2mine.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.thecallunxz.left2mine.entities.mobs.EntityCustomMob;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.init.InitPotions;

public class PotionOnFire extends PotionBase {
   private int timeChange = 10;

   public PotionOnFire(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
      super(isBadEffect, liquidR, liquidG, liquidB, name);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      PotionEffect potion = entityLivingBaseIn.getActivePotionEffect(this);
      int i = this.timeChange >> amplifier;
      int duration = potion.getDuration();
      boolean boolDamage = false;
      if (i > 0) {
         boolDamage = duration % i == 0;
      } else {
         boolDamage = true;
      }

      if (boolDamage) {
         if (entityLivingBaseIn instanceof EntityCustomMob) {
            if (entityLivingBaseIn instanceof EntityTank) {
               entityLivingBaseIn.attackEntityFrom(DamageSource.IN_FIRE, 6.0F);
            } else if (entityLivingBaseIn instanceof EntitySpecialInfected) {
               entityLivingBaseIn.attackEntityFrom(DamageSource.IN_FIRE, entityLivingBaseIn.world.rand.nextFloat() * 3.0F + 1.0F);
            } else {
               entityLivingBaseIn.attackEntityFrom(DamageSource.IN_FIRE, entityLivingBaseIn.world.rand.nextFloat() * 3.0F);
            }

            if (entityLivingBaseIn instanceof EntityWitch) {
               EntityWitch witch = (EntityWitch)entityLivingBaseIn;
               if (witch.getAttackingState() == 0) {
                  witch.setAngerLevel(2000);
               }
            }

            entityLivingBaseIn.hurtResistantTime = 0;
         } else {
            entityLivingBaseIn.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            entityLivingBaseIn.hurtResistantTime = 0;
            if (entityLivingBaseIn.isPotionActive(InitPotions.on_fire)) {
               entityLivingBaseIn.removePotionEffect(InitPotions.on_fire);
            }
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn instanceof EntityCustomMob) {
         ((EntityCustomMob)entityLivingBaseIn).setOnFire(true);
      }

   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn instanceof EntityCustomMob) {
         ((EntityCustomMob)entityLivingBaseIn).setOnFire(false);
      }

   }
}
