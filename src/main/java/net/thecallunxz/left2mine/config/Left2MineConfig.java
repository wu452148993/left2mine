package net.thecallunxz.left2mine.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "left2mine"
)
@LangKey("left2mine.config.title")
public class Left2MineConfig {
   @Comment({"Low Spec Mode:", "Low spec mode to make the director use less resources", "However, the director will be less active and less will happen during the game!"})
   public static boolean lowSpecDirectorMode = false;
   @Comment({"Reduced Particles:", "Reduced particles can help fps be more stable during gameplay"})
   public static boolean reducedParticles = false;
   @Comment({"Bullet Impact:", "2: Decals and sound", "1: Just sound", "0: Nothing"})
   public static int bulletImpact = 2;
   @Comment({"Blood Amount:", "3: Blood decals and particles on hit and death", "2: Particles on hit. Blood decals and particles on death", "1: Nothing on hit. Blood decals and particles on death", "0: No blood"})
   public static int bloodAmount = 3;
   @Comment({"Corpse Max Count:", "Amount of corpses which can exist before the game removes corpses to place more"})
   public static int maxCorpseCount = 40;
   @Comment({"Corpse Despawn Age:", "Amount of time a corpse exists before it is removed from the game"})
   public static float corpseDespawnAge = 1500.0F;
   @Comment({"Random Zombie Skins:", "If true the skins of the zombies will be selected from a small selection of skins"})
   public static boolean randomCommonInfectedSkins = true;

   @EventBusSubscriber(
      modid = "left2mine"
   )
   private static class EventHandler {
      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("left2mine")) {
            ConfigManager.sync("left2mine", Type.INSTANCE);
         }

      }
   }
}
