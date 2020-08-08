package net.thecallunxz.left2mine.util;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ClientCorpseUtil {
   private static HashMap<Integer, Double> xHash = new HashMap();
   private static HashMap<Integer, Double> yHash = new HashMap();
   private static HashMap<Integer, Double> zHash = new HashMap();

   public static double getXPosFromID(int entityID) {
      return xHash.get(entityID) != null ? (Double)xHash.get(entityID) : 0.0D;
   }

   public static double getYPosFromID(int entityID) {
      return yHash.get(entityID) != null ? (Double)yHash.get(entityID) : 0.0D;
   }

   public static double getZPosFromID(int entityID) {
      return zHash.get(entityID) != null ? (Double)zHash.get(entityID) : 0.0D;
   }

   public static void saveEntityPos(World world, int entityID) {
      Entity ent = world.getEntityByID(entityID);
      if (world.getEntityByID(entityID) != null) {
         xHash.put(entityID, ent.posX);
         yHash.put(entityID, ent.posY);
         zHash.put(entityID, ent.posZ);
      }

   }
}
