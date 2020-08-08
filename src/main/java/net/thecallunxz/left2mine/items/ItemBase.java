package net.thecallunxz.left2mine.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.ItemModelProvider;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.init.InitPotions;

public class ItemBase extends Item implements ItemModelProvider {
   protected String name;
   protected ItemBase.EnumItemType type;

   public ItemBase(String name, ItemBase.EnumItemType type) {
      this.name = name;
      this.setItemType(type);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(Main.tabL2M2);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      return playerIn.isPotionActive(InitPotions.incapacitated) ? new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(handIn)) : new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }

   public void registerItemModel(Item item) {
      Main.proxy.registerItemRenderer(this, 0, this.name);
   }

   public ItemBase setCreativeTab(CreativeTabs tab) {
      super.setCreativeTab(tab);
      return this;
   }

   public int getEntityLifespan(ItemStack itemStack, World world) {
      return Integer.MAX_VALUE;
   }

   public void setItemType(ItemBase.EnumItemType type) {
      this.type = type;
   }

   public ItemBase.EnumItemType getItemType() {
      return this.type;
   }

   public int getSlot(ItemBase.EnumItemType type) {
      int slot = 0;
      switch(this.getItemType()) {
      case PRIMARY_WEAPON:
         slot = 0;
         break;
      case SECONDARY_WEAPON:
         slot = 1;
         break;
      case PRIMARY_HEALING:
         slot = 2;
         break;
      case SECONDARY_HEALING:
         slot = 3;
         break;
      case GRENADE:
         slot = 4;
         break;
      case MISC:
         slot = 8;
      }

      return slot;
   }

   public static enum EnumItemType {
      PRIMARY_WEAPON,
      SECONDARY_WEAPON,
      PRIMARY_HEALING,
      SECONDARY_HEALING,
      GRENADE,
      MISC;
   }
}
