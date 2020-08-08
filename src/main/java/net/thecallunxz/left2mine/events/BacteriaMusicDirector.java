package net.thecallunxz.left2mine.events;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.sounds.MovingSoundEntity;

public class BacteriaMusicDirector {
   private static long lastHunterBacteriaSound = 0L;
   private static long lastBoomerBacteriaSound = 0L;
   private static long lastSmokerBacteriaSound = 0L;
   private static Entity trackingHunter = null;
   private static Entity trackingBoomer = null;
   private static Entity trackingSmoker = null;
   public static boolean playingTankMusic = false;
   public static long lastBacteriaSound = 0L;

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      WorldClient world = Minecraft.getMinecraft().world;
      SoundHandler sound = Minecraft.getMinecraft().getSoundHandler();
      if (world != null && sound != null && !playingTankMusic) {
         Iterator var4 = world.loadedEntityList.iterator();

         while(var4.hasNext()) {
            Entity ent = (Entity)var4.next();
            if (ent instanceof EntityHunter) {
               trackingHunter = ent;
            }

            if (ent instanceof EntityBoomer) {
               trackingBoomer = ent;
            }

            if (ent instanceof EntitySmoker) {
               trackingSmoker = ent;
            }

            if (ent instanceof EntityTank && ((EntityTank)ent).isAttacking()) {
               trackingHunter = null;
               trackingBoomer = null;
               trackingSmoker = null;
               if (!playingTankMusic) {
                  playingTankMusic = true;
                  Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(Minecraft.getMinecraft().player, InitSounds.tank_music, SoundCategory.MUSIC));
               }
               break;
            }

            if (ent instanceof EntityWitch) {
               EntityWitch witch = (EntityWitch)ent;
               if (witch.getAttackingState() == 0 && !witch.isCryMusicAttached()) {
                  witch.setCryMusicAttached(true);
                  Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(witch, InitSounds.witch_music1, SoundCategory.MUSIC));
               }

               if (witch.getAttackingState() == 1 && !witch.isScreamMusicAttached()) {
                  witch.setScreamMusicAttached(true);
                  Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(witch, InitSounds.witch_music2, SoundCategory.MUSIC));
               }
            }
         }

         if (trackingHunter != null && !trackingHunter.isDead && world.getTotalWorldTime() > lastHunterBacteriaSound && world.getTotalWorldTime() > lastBacteriaSound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(trackingHunter, InitSounds.hunter_bacteria, SoundCategory.MUSIC));
            lastHunterBacteriaSound = world.getTotalWorldTime() + 150L + (long)world.rand.nextInt(100);
            lastBacteriaSound = world.getTotalWorldTime() + 40L;
            trackingHunter = null;
         }

         if (trackingBoomer != null && !trackingBoomer.isDead && world.getTotalWorldTime() > lastBoomerBacteriaSound && world.getTotalWorldTime() > lastBacteriaSound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(trackingBoomer, InitSounds.boomer_bacteria, SoundCategory.MUSIC));
            lastBoomerBacteriaSound = world.getTotalWorldTime() + 150L + (long)world.rand.nextInt(100);
            lastBacteriaSound = world.getTotalWorldTime() + 40L;
            trackingBoomer = null;
         }

         if (trackingSmoker != null && !trackingSmoker.isDead && world.getTotalWorldTime() > lastSmokerBacteriaSound && world.getTotalWorldTime() > lastBacteriaSound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(trackingSmoker, InitSounds.smoker_bacteria, SoundCategory.MUSIC));
            lastSmokerBacteriaSound = world.getTotalWorldTime() + 150L + (long)world.rand.nextInt(100);
            lastBacteriaSound = world.getTotalWorldTime() + 40L;
            trackingSmoker = null;
         }
      }

   }
}
