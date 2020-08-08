package net.thecallunxz.left2mine.items.usable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;

public class ItemFirstAid extends ItemBase {
   int timer = 0;

   public ItemFirstAid(String name, ItemBase.EnumItemType type) {
      super(name, type);
      this.setMaxStackSize(1);
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      return true;
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         IStats stats = (IStats)entityplayer.getCapability(StatsProvider.STATS, (EnumFacing)null);
         stats.setMedpacksUsed(stats.getMedpacksUsed() + 1);
         if (!worldIn.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.MEDPACKSUSED, stats.getMedpacksUsed(), entityplayer));
         }

         float damaged = entityplayer.getMaxHealth() - entityplayer.getHealth();
         damaged = (float)Math.ceil((double)(damaged * 0.8F));
         entityplayer.setAbsorptionAmount(MathHelper.clamp(entityplayer.getAbsorptionAmount() - damaged, 0.0F, 20.0F));
         entityplayer.setHealth(damaged + entityplayer.getHealth());
         IEquip equip = (IEquip)entityplayer.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         equip.removeEquipped(stack);
         if (!worldIn.isRemote) {
            equip.setLives(3);
            Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(0), (EntityPlayerMP)entityplayer);
            Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), entityplayer));
         }
      }

      stack.shrink(1);
      return stack;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 100;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (super.onItemRightClick(worldIn, playerIn, handIn).getType() == EnumActionResult.SUCCESS) {
         playerIn.getHeldItem(handIn);
         playerIn.setActiveHand(handIn);
         if (!worldIn.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(playerIn, 5));
         }

         return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
      } else {
         return new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
      }
   }
}
