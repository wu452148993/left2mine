package net.thecallunxz.left2mine.util;

import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;

public class DifficultyUtil {
   public static float getDamageMultiplier(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 1.0F;
      case 2:
         return 1.0F;
      case 3:
         return 0.75F;
      case 4:
         return 0.5F;
      default:
         return 1.0F;
      }
   }

   public static double getSpawnRadius(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 5.0D;
      case 2:
         return 4.0D;
      case 3:
         return 3.0D;
      case 4:
         return 2.0D;
      default:
         return 5.0D;
      }
   }

   public static int getHordeSize(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         if (data.isInPanicEvent()) {
            return 2;
         }

         return 5;
      case 2:
         if (data.isInPanicEvent()) {
            return 5;
         }

         return 8;
      case 3:
         if (data.isInPanicEvent()) {
            return 8;
         }

         return 10;
      case 4:
         if (data.isInPanicEvent()) {
            return 12;
         }

         return 12;
      default:
         return 5;
      }
   }

   public static int getPanicMaxTimer(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 1100;
      case 2:
         return 900;
      case 3:
         return 700;
      case 4:
         return 500;
      default:
         return 900;
      }
   }

   public static int getBossMaxTimer(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 4500;
      case 2:
         return 3500;
      case 3:
         return 2500;
      case 4:
         return 2000;
      default:
         return 3500;
      }
   }

   public static int getBossMaxTimerSurvival(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 1200;
      case 2:
         return 1200;
      case 3:
         return 1200;
      case 4:
         return 900;
      default:
         return 1200;
      }
   }

   public static int getPanicEventMaxTimer(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 200;
      case 2:
         return 180;
      case 3:
         return 160;
      case 4:
         return 140;
      default:
         return 180;
      }
   }

   public static int getSpecialMaxTimer(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 900;
      case 2:
         return 850;
      case 3:
         return 700;
      case 4:
         return 650;
      default:
         return 900;
      }
   }

   public static int getSpecialEventMaxTimer(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 450;
      case 2:
         return 400;
      case 3:
         return 300;
      case 4:
         return 200;
      default:
         return 200;
      }
   }

   public static float getFriendlyFireReduction(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 1.0E-4F;
      case 2:
         return 0.025F;
      case 3:
         return 0.05F;
      case 4:
         return 0.1F;
      default:
         return 0.025F;
      }
   }

   public static float getCommonDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 0.5F;
      case 2:
         return 1.0F;
      case 3:
         return 1.5F;
      case 4:
         return 2.5F;
      default:
         return 1.0F;
      }
   }

   public static float getHunterPounceDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 0.5F;
      case 2:
         return 1.0F;
      case 3:
         return 1.5F;
      case 4:
         return 2.0F;
      default:
         return 1.0F;
      }
   }

   public static float getHunterClawDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 0.25F;
      case 2:
         return 0.5F;
      case 3:
         return 1.0F;
      case 4:
         return 2.0F;
      default:
         return 0.5F;
      }
   }

   public static float getSmokerTongueDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 0.5F;
      case 2:
         return 0.5F;
      case 3:
         return 1.0F;
      case 4:
         return 1.5F;
      default:
         return 0.5F;
      }
   }

   public static float getSmokerMaxTongueDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 1.0F;
      case 2:
         return 1.5F;
      case 3:
         return 2.0F;
      case 4:
         return 2.5F;
      default:
         return 1.0F;
      }
   }

   public static int getSmokerChargeTime(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 45;
      case 2:
         return 40;
      case 3:
         return 35;
      case 4:
         return 30;
      default:
         return 40;
      }
   }

   public static double getTankHP(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 600.0D;
      case 2:
         return 600.0D;
      case 3:
         return 900.0D;
      case 4:
         return 1200.0D;
      default:
         return 900.0D;
      }
   }

   public static float getTankDamage(World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      switch(data.getDifficulty()) {
      case 1:
         return 3.0F;
      case 2:
         return 5.0F;
      case 3:
         return 7.0F;
      case 4:
         return 10.0F;
      default:
         return 5.0F;
      }
   }
}
