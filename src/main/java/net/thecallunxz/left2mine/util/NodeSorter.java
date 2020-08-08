package net.thecallunxz.left2mine.util;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;

public class NodeSorter implements Comparator<BlockPos> {
   private BlockPos startNode;

   public NodeSorter(BlockPos startNode) {
      this.startNode = startNode;
   }

   public int compare(BlockPos pos1, BlockPos pos2) {
      double distance1 = pos1.distanceSq(this.startNode);
      double distance2 = pos2.distanceSq(this.startNode);
      return (int)(distance1 - distance2);
   }
}
