package net.thecallunxz.left2mine.init;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;

@SideOnly(Side.CLIENT)
public class InitColourManager {
   private static final Minecraft minecraft = Minecraft.getMinecraft();

   public static void registerColourHandlers() {
      BlockColors blockColors = minecraft.getBlockColors();
      ItemColors itemColors = minecraft.getItemColors();
      registerItemColourHandlers(itemColors);
      registerBlockColourHandlers(itemColors);
   }

   private static void registerBlockColourHandlers(ItemColors itemColors) {
      IItemColor itemColourHandler = (stack, tintIndex) -> {
         World world = minecraft.player.getEntityWorld();
         return NodeSelection.getSelectedColour(world);
      };
      itemColors.registerItemColorHandler(itemColourHandler, new Block[]{InitBlocks.zombiespawn});
      itemColors.registerItemColorHandler(itemColourHandler, new Block[]{InitBlocks.bossspawn});
      itemColors.registerItemColorHandler(itemColourHandler, new Block[]{InitBlocks.hordespawn});
      itemColors.registerItemColorHandler(itemColourHandler, new Block[]{InitBlocks.itemspawn});
   }

   private static void registerItemColourHandlers(ItemColors itemColors) {
      IItemColor itemColourHandler = (stack, tintIndex) -> {
         return Left2MineClientUtilities.getNodeLinkerColour(stack);
      };
      itemColors.registerItemColorHandler(itemColourHandler, new Item[]{InitItems.nodelinker});
   }
}
