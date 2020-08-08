package net.thecallunxz.left2mine.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.World;

public class PathUtil {
   public static ArrayList<PathNode> getNeighbourNodes(PathNode node) {
      ArrayList<PathNode> nodes = new ArrayList();
      World world = node.getWorld();

      for(int x = -1; x <= -1; ++x) {
         for(int y = -1; y <= -1; ++y) {
            for(int z = -1; z <= -1; ++z) {
               if (x != 0 || y != 0 || z != 0) {
                  nodes.add(new PathNode(node.getPos().add(x, y, z), world));
               }
            }
         }
      }

      return nodes;
   }

   public static ArrayList<PathNode> retracePath(PathNode start, PathNode end) {
      ArrayList<PathNode> nodes = new ArrayList();
      PathNode currentNode = end;

      while(currentNode != start) {
         nodes.add(currentNode);
         if (currentNode.getParent() == null) {
            currentNode = start;
         } else {
            currentNode = currentNode.getParent();
         }
      }

      Collections.reverse(nodes);
      return nodes;
   }

   public static int getCostDistance(PathNode start, PathNode end) {
      int destX = Math.abs(start.getPos().getX() - end.getPos().getX());
      int destZ = Math.abs(start.getPos().getZ() - end.getPos().getZ());
      return destX > destZ ? 14 * destZ + 10 * (destX - destZ) : 14 * destX + 10 * (destZ - destX);
   }

   public static PathPoint[] createPointArray(ArrayList<PathNode> points) {
      System.out.println(points);
      PathPoint[] pointsarray = new PathPoint[points.size()];

      for(int i = 0; i < points.size(); ++i) {
         pointsarray[i] = new PathPoint(((PathNode)points.get(i)).getPos().getX(), ((PathNode)points.get(i)).getPos().getY(), ((PathNode)points.get(i)).getPos().getZ());
      }

      return pointsarray;
   }
}
