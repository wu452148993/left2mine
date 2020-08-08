package net.thecallunxz.left2mine.blocks.nodeblocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;

public class BlockSaferoomNode extends BlockNodeParent {
   public BlockSaferoomNode(String name) {
      super(name);
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (super.canPlaceBlockAt(worldIn, pos)) {
         if (worldIn.getBlockState(pos).getBlock() == this) {
            return false;
         } else {
            NodeSelection.setSelectedNode(pos);
            return true;
         }
      } else {
         return false;
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntitySaferoomNode();
   }
}
