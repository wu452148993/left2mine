package net.thecallunxz.left2mine.blocks.nodeblocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;

public class BlockPlayerSpawn extends BlockNodeParent {
   public BlockPlayerSpawn(String name) {
      super(name);
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (super.canPlaceBlockAt(worldIn, pos)) {
         if (worldIn.getBlockState(pos).getBlock() == this) {
            return false;
         } else {
            WorldDataLeft2Mine data = WorldDataLeft2Mine.get(worldIn);
            if (data.isSpawnSet(worldIn)) {
               return false;
            } else {
               data.setSpawnPos(pos);
               NodeSelection.setSelectedNode(pos);
               return true;
            }
         }
      } else {
         return false;
      }
   }
}
