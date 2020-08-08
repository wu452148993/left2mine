package net.thecallunxz.left2mine.items.usable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class ItemPills extends ItemBase {
   public ItemPills(String name, ItemBase.EnumItemType type) {
      super(name, type);
      this.setMaxStackSize(1);
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      if (entity instanceof EntityPlayer) {
      }

      return true;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (super.onItemRightClick(worldIn, playerIn, handIn).getType() == EnumActionResult.SUCCESS) {
         ItemStack itemstack = playerIn.getHeldItem(handIn);
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(worldIn);
         if (playerIn.getAbsorptionAmount() + playerIn.getHealth() >= 20.0F) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         }

         if (!data.isInGame()) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         }

         if (playerIn.isPotionActive(InitPotions.incapacitated) || Left2MineUtilities.isPinnedPotionActive(playerIn)) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         }

         if (!playerIn.capabilities.isCreativeMode) {
            IEquip equip = (IEquip)playerIn.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.removeEquipped(itemstack);
            Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), playerIn));
            itemstack.shrink(1);
            worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, InitSounds.pills_use, SoundCategory.PLAYERS, 1.0F, 1.0F);
            float currentMixedHealth = playerIn.getAbsorptionAmount() + playerIn.getHealth();
            float newamount = currentMixedHealth + 10.0F;
            if (newamount > 20.0F) {
               playerIn.setAbsorptionAmount((float)Math.ceil((double)(playerIn.getAbsorptionAmount() + 20.0F - currentMixedHealth)));
            } else {
               playerIn.setAbsorptionAmount((float)Math.ceil((double)(newamount - playerIn.getHealth())));
            }

            IStats stats = (IStats)playerIn.getCapability(StatsProvider.STATS, (EnumFacing)null);
            stats.setPillsUsed(stats.getPillsUsed() + 1);
            Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.PILLSUSED, stats.getPillsUsed(), playerIn));
            if (!playerIn.getEntityWorld().isRemote) {
               playerIn.ticksExisted = 1;
            }

            return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
         }
      }

      return new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
   }
}
