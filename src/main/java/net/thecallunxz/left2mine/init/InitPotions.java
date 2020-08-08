package net.thecallunxz.left2mine.init;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.thecallunxz.left2mine.potions.PotionHunterPinned;
import net.thecallunxz.left2mine.potions.PotionIncapacitated;
import net.thecallunxz.left2mine.potions.PotionOnFire;
import net.thecallunxz.left2mine.potions.PotionPuked;
import net.thecallunxz.left2mine.potions.PotionSmoked;

@ObjectHolder("left2mine")
public class InitPotions {
   public static final PotionIncapacitated incapacitated = new PotionIncapacitated(false, 2, 2, 2, "incapacitated");
   public static final PotionHunterPinned hunter_pinned = new PotionHunterPinned(false, 2, 2, 2, "hunterpinned");
   public static final PotionSmoked smoker_tongued = new PotionSmoked(false, 2, 2, 2, "smoker_tongued");
   public static final PotionPuked puked = new PotionPuked(false, 2, 2, 2, "puked");
   public static final PotionOnFire on_fire = new PotionOnFire(false, 2, 2, 2, "on_fire");

   @EventBusSubscriber(
      modid = "left2mine"
   )
   public static class RegistrationHandler {
      @SubscribeEvent
      public static void registerPotions(Register<Potion> event) {
         event.getRegistry().registerAll(new Potion[]{InitPotions.incapacitated, InitPotions.hunter_pinned, InitPotions.smoker_tongued, InitPotions.puked, InitPotions.on_fire});
      }
   }
}
