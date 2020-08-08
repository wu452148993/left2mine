package net.thecallunxz.left2mine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.thecallunxz.left2mine.ItemModelProvider;
import net.thecallunxz.left2mine.Main;

public class BlockBase extends Block implements ItemModelProvider {
   protected String name;

   public BlockBase(Material materialIn, String name) {
      super(materialIn);
      this.name = name;
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(Main.tabL2M);
   }

   public BlockBase(String string) {
      this(Material.ROCK, string);
   }

   public void registerItemModel(Item itemBlock) {
      Main.proxy.registerItemRenderer(itemBlock, 0, this.name);
   }

   public BlockBase setCreativeTab(CreativeTabs tab) {
      super.setCreativeTab(tab);
      return this;
   }
}
