package net.thecallunxz.left2mine.ragdoll.physics;

import java.util.List;

public class RagdollMaths {
   public static List<RagdollBase> corpses;
   public static long previousTime;
   public static long currentTime;
   public static int fixedDeltaTime = 16;
   public static float fixedDeltaTimeSeconds;
   public static int leftOverDeltaTime;
   public static int constraintAccuracy;

   void update() {
      currentTime = System.currentTimeMillis();
      long deltaTimeMS = currentTime - previousTime;
      previousTime = currentTime;
      int timeStepAmt = (int)((float)(deltaTimeMS + (long)leftOverDeltaTime) / (float)fixedDeltaTime);
      if (timeStepAmt < 5) {
         timeStepAmt = 5;
      }

      leftOverDeltaTime = (int)deltaTimeMS - timeStepAmt * fixedDeltaTime;

      for(int iteration = 1; iteration <= timeStepAmt; ++iteration) {
         for(int i = 0; i < corpses.size(); ++i) {
            ((RagdollBase)corpses.get(i)).update();
         }
      }

   }

   static {
      fixedDeltaTimeSeconds = (float)fixedDeltaTime / 1000.0F;
      leftOverDeltaTime = 0;
      constraintAccuracy = 1;
   }
}
