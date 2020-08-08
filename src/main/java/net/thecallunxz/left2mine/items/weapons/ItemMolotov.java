package net.thecallunxz.left2mine.items.weapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.entities.EntityMolotov;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class ItemMolotov extends ItemBase {
   public ItemMolotov(String name, ItemBase.EnumItemType type) {
      super(name, type);
      this.maxStackSize = 1;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(worldIn);
      if (!playerIn.isPotionActive(InitPotions.incapacitated) && !Left2MineUtilities.isPinnedPotionActive(playerIn)) {
         itemstack.shrink(1);
         IEquip equip = (IEquip)playerIn.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         equip.removeEquipped(itemstack);
         if (!worldIn.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), playerIn));
         }

         IStats stats = (IStats)playerIn.getCapability(StatsProvider.STATS, (EnumFacing)null);
         stats.setMolotovsUsed(stats.getMolotovsUsed() + 1);
         if (!worldIn.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.MOLOTOVSUSED, stats.getMolotovsUsed(), playerIn));
         }

         if (!worldIn.isRemote) {
            EntityMolotov molotov = new EntityMolotov(worldIn, (EntityPlayerMP)playerIn);
            playerIn.world.spawnEntity(molotov);
         }

         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, itemstack);
      }
   }
}
