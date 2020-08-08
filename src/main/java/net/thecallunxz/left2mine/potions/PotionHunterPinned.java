package net.thecallunxz.left2mine.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.ChangeSlotMessage;
import net.thecallunxz.left2mine.networking.client.PinnedMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class PotionHunterPinned extends PotionBase {
   public PotionHunterPinned(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
      super(isBadEffect, liquidR, liquidG, liquidB, name);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      PotionEffect potion = entityLivingBaseIn.getActivePotionEffect(this);
      entityLivingBaseIn.motionX = 0.0D;
      entityLivingBaseIn.motionY = 0.0D;
      entityLivingBaseIn.motionZ = 0.0D;
      entityLivingBaseIn.noClip = true;
      int k = 50 >> amplifier;
      int duration = potion.getDuration();
      boolean bool = false;
      if (k > 0) {
         bool = duration % k == 0;
      } else {
         bool = true;
      }

      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         player.inventory.currentItem = 8;
         World world = player.getEntityWorld();
         if (!world.isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (bool) {
               Entity entity = Left2MineUtilities.getEntityByUUID(world, equip.getTetheredUUID());
               if (entity == null) {
                  player.removePotionEffect(InitPotions.hunter_pinned);
               } else if (entity instanceof EntityHunter) {
                  EntityHunter hunt = (EntityHunter)entity;
                  if (!hunt.isPinned()) {
                     hunt.setPinnedId(player.getUniqueID());
                     hunt.setPinned(true);
                  } else if (hunt.getPinnedId() != player.getUniqueID()) {
                     player.removePotionEffect(InitPotions.hunter_pinned);
                  }
               }
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
            if (player.getEntityWorld().playerEntities.size() == 1) {
               player.ticksExisted = player.getEntityWorld().rand.nextInt(60) + 1;
            }

            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.setPinned(true);
            Left2MinePacket.INSTANCE.sendToAll(new PinnedMessage(equip.getPinned(), player));
         }
      }

   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      entityLivingBaseIn.noClip = false;
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         if (!player.getEntityWorld().isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.getLying() && !equip.isPuked()) {
               player.setGlowing(false);
            }

            equip.setPinned(false);
            if (equip.getLives() == 1 && !equip.isPuked()) {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(1), (EntityPlayerMP)player);
            }

            Left2MinePacket.INSTANCE.sendToAll(new PinnedMessage(equip.getPinned(), player));
            Left2MinePacket.INSTANCE.sendTo(new ChangeSlotMessage(0), (EntityPlayerMP)player);
         }
      }

   }
}
