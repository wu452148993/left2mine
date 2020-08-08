package net.thecallunxz.left2mine.entities.ai;

import com.google.common.base.Predicate;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.BoomerFXMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AIBoomerPuke extends EntityAIBase {
   private EntityBoomer infected;
   private boolean initialVomit;

   public AIBoomerPuke(EntityBoomer infected) {
      this.infected = infected;
      this.initialVomit = false;
      this.setMutexBits(8);
   }

   public boolean shouldExecute() {
      return true;
   }

   public boolean shouldContinueExecuting() {
      return true;
   }

   public void updateTask() {
      if (this.infected.getAttackTarget() == null) {
         this.infected.setCanSee(false);
      } else {
         if (this.infected.getDistance(this.infected.getAttackTarget()) >= 5.0F && !Left2MineUtilities.canEntitySeeEntity(this.infected, this.infected.getAttackTarget(), this.infected.getEntityWorld())) {
            this.infected.setCanSee(false);
         } else {
            this.infected.setCanSee(true);
         }

         if (this.infected.getPukeTicks() > 0) {
            this.infected.setPukeTicks(Math.max(this.infected.getPukeTicks() - 1, 0));
         }

         if (this.infected.getPukeTicks() == 0 && Left2MineUtilities.canEntitySeeEntity(this.infected, this.infected.getAttackTarget(), this.infected.getEntityWorld()) && this.infected.getDistance(this.infected.getAttackTarget()) < 4.0F) {
            this.infected.setPukeTicks(this.infected.getPukeTicks() - 1);
         }

         if (this.infected.getPukeTicks() < 0) {
            this.infected.setPukeTicks(this.infected.getPukeTicks() - 1);
            this.infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 99, true, false));
            EntityLivingBase entitylivingbase = this.infected.getAttackTarget();
            double d0 = entitylivingbase.posX - this.infected.posX;
            double d2 = entitylivingbase.posZ - this.infected.posZ;
            float f = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;
            this.infected.setPositionAndRotation(this.infected.posX, this.infected.posY, this.infected.posZ, f, this.infected.rotationPitch);
            this.infected.rotationYaw = f;
            this.infected.rotationYawHead = f;
            if (this.infected.getPukeTicks() % 1 == 0) {
               double x0 = (double)((float)entitylivingbase.posX - 0.75F) + (double)this.infected.world.rand.nextFloat() * 1.5D;
               double d1 = (double)((float)entitylivingbase.posY + this.infected.world.rand.nextFloat());
               double x2 = (double)((float)entitylivingbase.posZ - 0.75F) + (double)this.infected.world.rand.nextFloat() * 1.5D;
               double d3 = x0 - this.infected.posX;
               double d4 = d1 - this.infected.posY;
               double d5 = x2 - this.infected.posZ;
               double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
               d3 /= d6;
               d4 /= d6;
               d5 /= d6;
               double d7 = 0.5D / (d6 / 10.0D + 0.1D);
               d7 *= (double)(this.infected.world.rand.nextFloat() * this.infected.world.rand.nextFloat() + 0.3F);
               d3 *= d7;
               d4 *= d7;
               d5 *= d7;
               Left2MinePacket.INSTANCE.sendToAll(new BoomerFXMessage(this.infected.posX, (d1 + this.infected.posY + 1.25D) / 2.0D, this.infected.posZ, d3, d4, d5, x0, d1, x2));
               Left2MinePacket.INSTANCE.sendToAll(new BoomerFXMessage(this.infected.posX, (d1 + this.infected.posY + 1.25D) / 2.0D, this.infected.posZ, d3, d4, d5, x0, d1, x2));
            }

            Iterator var23 = this.infected.world.loadedEntityList.iterator();

            label74:
            while(true) {
               EntityPlayer entityplayer;
               IEquip equip;
               do {
                  Entity ent;
                  do {
                     Predicate predicate;
                     do {
                        do {
                           do {
                              if (!var23.hasNext()) {
                                 if (!this.initialVomit) {
                                    Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this.infected, 8));
                                    this.initialVomit = true;
                                 }
                                 break label74;
                              }

                              ent = (Entity)var23.next();
                           } while(!(ent instanceof EntityPlayer));

                           predicate = EntitySelectors.CAN_AI_TARGET;
                           entityplayer = (EntityPlayer)ent;
                        } while(!predicate.apply(entityplayer));
                     } while(ent.getDistance(this.infected) >= 5.0F);
                  } while(!Left2MineUtilities.canBoomerHitHigh(this.infected, ent) && !Left2MineUtilities.canBoomerHitLow(this.infected, ent) && !ent.equals(this.infected.getAttackTarget()));

                  equip = (IEquip)entityplayer.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               } while(equip.isPuked() && entityplayer.isPotionActive(InitPotions.puked));

               entityplayer.addPotionEffect(new PotionEffect(InitPotions.puked, 260, 0, true, false));
            }
         }

         if (this.infected.getPukeTicks() <= -40) {
            this.initialVomit = false;
            this.infected.setPukeTicks(1200);
         }

      }
   }
}
