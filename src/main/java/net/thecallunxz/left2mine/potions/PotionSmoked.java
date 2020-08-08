package net.thecallunxz.left2mine.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.ChangeSlotMessage;
import net.thecallunxz.left2mine.networking.client.PinnedMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class PotionSmoked extends PotionBase {
   Entity tetheredSmoker = null;

   public PotionSmoked(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
      super(isBadEffect, liquidR, liquidG, liquidB, name);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      PotionEffect potion = entityLivingBaseIn.getActivePotionEffect(this);
      entityLivingBaseIn.stepHeight = 1.1F;
      if (entityLivingBaseIn instanceof EntityPlayer && this.tetheredSmoker instanceof EntitySmoker && this.tetheredSmoker != null) {
         EntitySmoker infected = (EntitySmoker)this.tetheredSmoker;
         EntityPlayer victim = (EntityPlayer)entityLivingBaseIn;
         int k = 10 >> amplifier;
         int duration = potion.getDuration();
         boolean bool = false;
         if (k > 0) {
            bool = duration % k == 0;
         } else {
            bool = true;
         }

         if (bool && !victim.world.isRemote && !infected.isDead) {
            if (infected.getDistance(victim) > 1.0F) {
               Vec3d smokerVec = infected.getPositionVector();
               Vec3d victimVec = victim.getPositionVector();
               Vec3d vecPull = smokerVec.subtract(victimVec).normalize();
               victim.motionX = vecPull.x * 0.7D;
               victim.motionZ = vecPull.z * 0.7D;
            }

            if (infected.getDistance(victim) > 2.0F) {
               victim.attackEntityFrom(Main.SMOKER, DifficultyUtil.getSmokerTongueDamage(infected.world));
               victim.hurtResistantTime = 10;
            } else {
               victim.attackEntityFrom(Main.SMOKER, DifficultyUtil.getSmokerMaxTongueDamage(infected.world));
               victim.hurtResistantTime = 10;
            }
         }
      } else if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         World world = player.getEntityWorld();
         if (!world.isRemote) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            this.tetheredSmoker = Left2MineUtilities.getEntityByUUID(player.world, equip.getTetheredUUID());
         }
      }

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
                  player.removePotionEffect(InitPotions.smoker_tongued);
               } else if (entity instanceof EntitySmoker) {
                  EntitySmoker smoker = (EntitySmoker)entity;
                  if (!smoker.isSmoked()) {
                     smoker.setSmokedId(player.getUniqueID());
                     smoker.setSmoked(true);
                  } else if (smoker.getSmokedId() != player.getUniqueID()) {
                     player.removePotionEffect(InitPotions.smoker_tongued);
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
            this.tetheredSmoker = Left2MineUtilities.getEntityByUUID(player.world, equip.getTetheredUUID());
            equip.setPinned(true);
            Left2MinePacket.INSTANCE.sendToAll(new PinnedMessage(equip.getPinned(), player));
         }
      }

   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      entityLivingBaseIn.noClip = false;
      entityLivingBaseIn.stepHeight = 0.6F;
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
