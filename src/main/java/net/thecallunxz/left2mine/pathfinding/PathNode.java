package net.thecallunxz.left2mine.pathfinding;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PathNode {
   private PathNode parent;
   private BlockPos pos;
   private World world;
   private int gCost;
   private int hCost;

   public PathNode(BlockPos pos, World world) {
      this.pos = pos;
      this.world = world;
   }

   public int getFCost() {
      return this.gCost + this.hCost;
   }

   public int getGCost() {
      return this.gCost;
   }

   public int getHCost() {
      return this.hCost;
   }

   public void setGCost(int cost) {
      this.gCost = cost;
   }

   public void setHCost(int cost) {
      this.hCost = cost;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public World getWorld() {
      return this.world;
   }

   public void setParent(PathNode currentNode) {
      this.parent = currentNode;
   }

   public PathNode getParent() {
      return this.parent;
   }
}
