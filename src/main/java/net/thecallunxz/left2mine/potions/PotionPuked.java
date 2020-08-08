package net.thecallunxz.left2mine.potions;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.client.PukedMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class PotionPuked extends PotionBase {
   public PotionPuked(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
      super(isBadEffect, liquidR, liquidG, liquidB, name);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      PotionEffect potion = entityLivingBaseIn.getActivePotionEffect(this);
      World world = entityLivingBaseIn.getEntityWorld();
      int k = 25 >> amplifier;
      int duration = potion.getDuration();
      boolean bool = false;
      if (k > 0) {
         bool = duration % k == 0;
      } else {
         bool = true;
      }

      if (bool) {
         if (!world.isRemote) {
            WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
            data.setLurkingCount(data.getLurkingCount() + 1);
         }

         Iterator var14 = world.loadedEntityList.iterator();

         while(true) {
            EntityCommonInfected common;
            EntityPlayer player;
            IEquip equip;
            do {
               while(true) {
                  Entity ent;
                  do {
                     do {
                        if (!var14.hasNext()) {
                           return;
                        }

                        ent = (Entity)var14.next();
                     } while(ent.getDistance(entityLivingBaseIn) >= 20.0F);
                  } while(!(ent instanceof EntityCommonInfected));

                  common = (EntityCommonInfected)ent;
                  EntityLivingBase target = common.getAttackTarget();
                  if (target instanceof EntityPlayer) {
                     player = (EntityPlayer)target;
                     equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                     break;
                  }

                  if (target == null && Left2MineUtilities.canEntityPathToEntity(common, entityLivingBaseIn)) {
                     common.setAttackTarget(entityLivingBaseIn);
                  }
               }
            } while(equip.isPuked() && player.isPotionActive(InitPotions.puked));

            if (Left2MineUtilities.canEntityPathToEntity(common, entityLivingBaseIn)) {
               common.setAttackTarget(entityLivingBaseIn);
            }
         }
      }
   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         player.setGlowing(true);
         if (!player.getEntityWorld().isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.isPuked() || !player.isPotionActive(InitPotions.puked)) {
               Left2MinePacket.INSTANCE.sendTo(new MovingSoundMessage(player, 9), (EntityPlayerMP)player);
            }

            equip.setPuked(true);
            Left2MinePacket.INSTANCE.sendToAll(new PukedMessage(equip.isPuked(), player));
            if (equip.getLives() == 1) {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(3), (EntityPlayerMP)player);
            } else {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(2), (EntityPlayerMP)player);
            }
         }
      }

   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         if (!player.getEntityWorld().isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.getPinned() && !equip.getLying()) {
               player.setGlowing(false);
            }

            equip.setPuked(false);
            Left2MinePacket.INSTANCE.sendToAll(new PukedMessage(equip.isPuked(), player));
            if (equip.getLives() == 1) {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(1), (EntityPlayerMP)player);
            } else {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(0), (EntityPlayerMP)player);
            }
         }
      }

   }
}
