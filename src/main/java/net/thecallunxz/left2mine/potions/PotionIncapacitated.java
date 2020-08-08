package net.thecallunxz.left2mine.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.ChangeSlotMessage;
import net.thecallunxz.left2mine.networking.client.LyingMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class PotionIncapacitated extends PotionBase {
   public PotionIncapacitated(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
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

      EntityPlayer player;
      if (bool && entityLivingBaseIn instanceof EntityPlayer) {
         player = (EntityPlayer)entityLivingBaseIn;
         if (!Left2MineUtilities.isPinnedPotionActive(player)) {
            entityLivingBaseIn.attackEntityFrom(Main.DOWN, 1.0F);
         }
      }

      if (entityLivingBaseIn instanceof EntityPlayer) {
         player = (EntityPlayer)entityLivingBaseIn;
         if (!Left2MineUtilities.isPinnedPotionActive(player)) {
            player.inventory.currentItem = 1;
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      entityLivingBaseIn.setPositionAndUpdate(entityLivingBaseIn.posX, entityLivingBaseIn.posY - 1.0D, entityLivingBaseIn.posZ);
      entityLivingBaseIn.setHealth(1.0F);
      entityLivingBaseIn.setAbsorptionAmount(59.0F);
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         player.setGlowing(true);
         if (!player.getEntityWorld().isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.setLying(true);
            Left2MinePacket.INSTANCE.sendToAll(new LyingMessage(equip.getLying(), player));
         }
      }

   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      entityLivingBaseIn.setPositionAndUpdate(entityLivingBaseIn.posX, entityLivingBaseIn.posY + 1.0D, entityLivingBaseIn.posZ);
      entityLivingBaseIn.setHealth(1.0F);
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(entityLivingBaseIn.world);
      if (data.isInGame()) {
         entityLivingBaseIn.setAbsorptionAmount(19.0F);
      } else {
         entityLivingBaseIn.setAbsorptionAmount(0.0F);
      }

      entityLivingBaseIn.noClip = false;
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         if (!player.getEntityWorld().isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equip.getPinned() && !equip.isPuked()) {
               player.setGlowing(false);
            }

            equip.setLying(false);
            equip.setLives(equip.getLives() - 1);
            if (equip.getLives() == 1 && !equip.isPuked()) {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(1), (EntityPlayerMP)player);
            }

            Left2MinePacket.INSTANCE.sendToAll(new LyingMessage(equip.getLying(), player));
            Left2MinePacket.INSTANCE.sendTo(new ChangeSlotMessage(0), (EntityPlayerMP)player);
         }
      }

   }
}
