package net.thecallunxz.left2mine.pathfinding;

import java.util.ArrayList;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AStarPath {
   public static Path makePath(PathNode start, PathNode end) {
      ArrayList<PathNode> openSet = new ArrayList();
      ArrayList<PathNode> closedSet = new ArrayList();
      new ArrayList();
      openSet.add(start);
      boolean looping = true;
      int count = 0;

      while(openSet.size() > 0) {
         ++count;
         if (count > 100) {
            break;
         }

         PathNode currentNode = (PathNode)openSet.get(0);

         for(int i = 1; i < openSet.size(); ++i) {
            if (((PathNode)openSet.get(i)).getFCost() < currentNode.getFCost() || ((PathNode)openSet.get(i)).getFCost() == currentNode.getFCost() && ((PathNode)openSet.get(i)).getHCost() < currentNode.getHCost()) {
               currentNode = (PathNode)openSet.get(i);
            }
         }

         openSet.remove(currentNode);
         closedSet.add(currentNode);
         if (currentNode.getPos() == end.getPos()) {
            break;
         }

         ArrayList<PathNode> neighbourNodes = PathUtil.getNeighbourNodes(currentNode);

         for(int i = 0; i < neighbourNodes.size(); ++i) {
            PathNode node = (PathNode)neighbourNodes.get(i);
            BlockPos pos = node.getPos();
            World world = node.getWorld();
            if (world.getBlockState(pos).getBlock().isPassable(world, pos) && world.getBlockState(pos.up()).getBlock().isPassable(world, pos) && !world.getBlockState(pos.down()).getBlock().isPassable(world, pos)) {
               int newMovementCostToNeighbour = currentNode.getGCost() + PathUtil.getCostDistance(currentNode, node);
               if (newMovementCostToNeighbour < node.getGCost() || !openSet.contains(node)) {
                  node.setGCost(newMovementCostToNeighbour);
                  node.setHCost(PathUtil.getCostDistance(node, end));
                  node.setParent(currentNode);
                  if (!openSet.contains(node)) {
                     openSet.add(node);
                  }
               }
            }
         }
      }

      ArrayList<PathNode> points = PathUtil.retracePath(start, end);
      PathPoint[] pointsArray = PathUtil.createPointArray(points);
      Path path = new Path(pointsArray);
      return path;
   }
}
