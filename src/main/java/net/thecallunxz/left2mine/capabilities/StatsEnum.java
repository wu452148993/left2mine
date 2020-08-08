package net.thecallunxz.left2mine.capabilities;

import java.util.HashMap;
import java.util.Map;

public enum StatsEnum {
   CLEARALL(0),
   COMMONKILLED(1),
   PILLSUSED(2),
   MEDPACKSUSED(3),
   PIPEBOMBSUSED(4),
   DEATHS(5),
   INCAPACITATIONS(6),
   HUNTERSKILLED(7),
   BOOMERSKILLED(8),
   SMOKERSKILLED(9),
   MOLOTOVSUSED(10),
   TANKDAMAGE(11),
   WITCHDAMAGE(12);

   private int value;
   private static Map map = new HashMap();

   private StatsEnum(int value) {
      this.value = value;
   }

   public static StatsEnum valueOf(int id) {
      return (StatsEnum)map.get(id);
   }

   public int getId() {
      return this.value;
   }

   static {
      StatsEnum[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         StatsEnum stat = var0[var2];
         map.put(stat.value, stat);
      }

   }
}
