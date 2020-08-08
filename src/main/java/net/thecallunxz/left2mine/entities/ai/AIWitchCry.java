package net.thecallunxz.left2mine.entities.ai;

import com.google.common.base.Predicate;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class AIWitchCry extends EntityAIBase {
   private EntityWitch infected;
   private long lastScream;

   public AIWitchCry(EntityWitch infected) {
      this.infected = infected;
      this.lastScream = 0L;
      this.setMutexBits(8);
   }

   public boolean shouldExecute() {
      return this.infected.getAttackingState() == 0;
   }

   public boolean shouldContinueExecuting() {
      return this.infected.getAttackingState() == 0;
   }

   public void updateTask() {
      Iterator var2;
      if (this.infected.ticksExisted % 5 == 0) {
         boolean angerPlus = false;
         var2 = this.infected.world.loadedEntityList.iterator();

         label127:
         while(true) {
            while(true) {
               Entity ent;
               EntityPlayer entityplayer;
               Predicate predicate;
               do {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           if (!angerPlus) {
                              if (this.infected.getAngerLevel() > 600) {
                                 this.infected.setAngerLevel(Math.max(0, this.infected.getAngerLevel() - 10));
                              } else {
                                 this.infected.setAngerLevel(Math.max(0, this.infected.getAngerLevel() - 2));
                              }
                           }
                           break label127;
                        }

                        ent = (Entity)var2.next();
                     } while(!(ent instanceof EntityPlayer));

                     entityplayer = (EntityPlayer)ent;
                     predicate = EntitySelectors.CAN_AI_TARGET;
                  } while(!predicate.apply(entityplayer));
               } while(Left2MineUtilities.RayCastCheckOpaque(new Vec3d(entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ), new Vec3d(this.infected.posX, this.infected.posY, this.infected.posZ), this.infected.world, 0) != null && Left2MineUtilities.RayCastCheckOpaque(new Vec3d(entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ), new Vec3d(this.infected.posX, this.infected.posY + (double)this.infected.height, this.infected.posZ), this.infected.world, 0) != null);

               IEquip equip = (IEquip)entityplayer.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               if (ent.getDistance(this.infected) < 2.0F) {
                  this.infected.setAngerLevel(this.infected.getAngerLevel() + 50);
                  if (Left2MineUtilities.canSeeHigh(entityplayer, this.infected) || Left2MineUtilities.canSeeLow(entityplayer, this.infected)) {
                     if (equip.isFlashlightOn()) {
                        this.infected.setAngerLevel(this.infected.getAngerLevel() + 150);
                     }

                     this.infected.setAngerLevel(this.infected.getAngerLevel() + 75);
                  }

                  angerPlus = true;
               } else if (ent.getDistance(this.infected) < 5.0F) {
                  this.infected.setAngerLevel(this.infected.getAngerLevel() + 25);
                  if (Left2MineUtilities.canSeeHigh(entityplayer, this.infected) || Left2MineUtilities.canSeeLow(entityplayer, this.infected)) {
                     if (equip.isFlashlightOn()) {
                        this.infected.setAngerLevel(this.infected.getAngerLevel() + 100);
                     }

                     this.infected.setAngerLevel(this.infected.getAngerLevel() + 25);
                  }

                  angerPlus = true;
               } else if (ent.getDistance(this.infected) < 10.0F) {
                  this.infected.setAngerLevel(this.infected.getAngerLevel() + 10);
                  if (Left2MineUtilities.canSeeHigh(entityplayer, this.infected) || Left2MineUtilities.canSeeLow(entityplayer, this.infected)) {
                     if (equip.isFlashlightOn()) {
                        this.infected.setAngerLevel(this.infected.getAngerLevel() + 75);
                     }

                     this.infected.setAngerLevel(this.infected.getAngerLevel() + 10);
                  }

                  angerPlus = true;
               } else if (ent.getDistance(this.infected) < 20.0F) {
                  if ((Left2MineUtilities.canSeeHigh(entityplayer, this.infected) || Left2MineUtilities.canSeeLow(entityplayer, this.infected)) && equip.isFlashlightOn()) {
                     this.infected.setAngerLevel(this.infected.getAngerLevel() + 50);
                  }

                  angerPlus = true;
               }
            }
         }
      }

      if (this.lastScream % 60L == 0L && this.infected.getAngerLevel() > 600) {
         Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this.infected, 20));
      }

      if (this.infected.getAngerLevel() > 600) {
         ++this.lastScream;
      } else {
         this.lastScream = 0L;
      }

      if (this.infected.getAngerLevel() >= 1000) {
         EntityPlayer player = this.infected.getEntityWorld().getClosestPlayerToEntity(this.infected, 100.0D);
         if (player != null) {
            var2 = this.infected.getEntityWorld().playerEntities.iterator();

            while(var2.hasNext()) {
               EntityPlayer players = (EntityPlayer)var2.next();
               players.sendMessage(new TextComponentTranslation("message.witch.startled", new Object[]{player.getName()}));
            }

            this.infected.setAttackTarget(player);
            this.infected.setAttackingState(1);
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this.infected, 18));
         }
      }

   }
}
