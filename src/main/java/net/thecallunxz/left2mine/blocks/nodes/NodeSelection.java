package net.thecallunxz.left2mine.blocks.nodes;

import java.awt.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;

public class NodeSelection {
   private static BlockPos selectedNodeGlobal;
   private static boolean isNodeSelected = false;

   public static BlockPos getSelectedNode() {
      return selectedNodeGlobal;
   }

   public static int getSelectedColour(World world) {
      if (isNodeSelected && world.getTileEntity(selectedNodeGlobal) instanceof TileEntityNodeParent) {
         TileEntityNodeParent par = (TileEntityNodeParent)world.getTileEntity(selectedNodeGlobal);
         if (par != null) {
            return (new Color(par.getRedColour(), par.getGreenColour(), par.getBlueColour())).getRGB();
         }
      }

      return Color.white.getRGB();
   }

   public static float getRedColour(World world) {
      if (isNodeSelected && world.getTileEntity(selectedNodeGlobal) instanceof TileEntityNodeParent) {
         TileEntityNodeParent par = (TileEntityNodeParent)world.getTileEntity(selectedNodeGlobal);
         if (par != null) {
            return par.getRedColour();
         }
      }

      return 1.0F;
   }

   public static float getBlueColour(World world) {
      if (isNodeSelected && world.getTileEntity(selectedNodeGlobal) instanceof TileEntityNodeParent) {
         TileEntityNodeParent par = (TileEntityNodeParent)world.getTileEntity(selectedNodeGlobal);
         if (par != null) {
            return par.getBlueColour();
         }
      }

      return 1.0F;
   }

   public static float getGreenColour(World world) {
      if (isNodeSelected && world.getTileEntity(selectedNodeGlobal) instanceof TileEntityNodeParent) {
         TileEntityNodeParent par = (TileEntityNodeParent)world.getTileEntity(selectedNodeGlobal);
         if (par != null) {
            return par.getGreenColour();
         }
      }

      return 1.0F;
   }

   public static boolean isNodeSelected() {
      return isNodeSelected;
   }

   public static void clearNode() {
      isNodeSelected = false;
   }

   public static void setSelectedNode(BlockPos node) {
      selectedNodeGlobal = node;
      isNodeSelected = true;
   }
}
