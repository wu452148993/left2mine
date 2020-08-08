package net.thecallunxz.left2mine.sounds;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.events.BacteriaMusicDirector;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;

@SideOnly(Side.CLIENT)
public class MovingSoundEntity extends MovingSound {
   private final Entity tracking;
   private SoundEvent event;
   private Entity tracking2;
   private float distance;

   private MovingSoundEntity(Entity tracking, SoundEvent event, SoundCategory cat) {
      super(event, cat);
      this.distance = 0.0F;
      this.tracking = tracking;
      this.tracking2 = null;
      this.repeat = false;
      this.event = event;
      this.repeatDelay = 1;
      this.volume = 1.0F;
      if (tracking != null) {
         if (tracking instanceof EntitySpecialInfected) {
            if (!event.equals(InitSounds.hunter_bacteria) && !event.equals(InitSounds.boomer_bacteria) && !event.equals(InitSounds.smoker_bacteria) && !event.equals(InitSounds.smoker_warn) && !event.equals(InitSounds.hunter_stalk)) {
               this.volume = 2.0F;
            } else {
               this.volume = 8.0F;
               this.repeat = false;
            }
         }

         if (event.equals(InitSounds.horde_germ)) {
            this.repeat = false;
            this.volume = 2.0F;
            this.attenuationType = AttenuationType.NONE;
         }

         if (event.equals(InitSounds.tank_music)) {
            this.repeat = true;
            this.volume = 2.0F;
            this.attenuationType = AttenuationType.NONE;
         }

         if (event.equals(InitSounds.tank_angry)) {
            this.volume = 2.0F;
         }

         if (event.equals(InitSounds.witch_cry)) {
            this.volume = 1.75F;
         }

         if (event.equals(InitSounds.witch_music1)) {
            this.volume = 2.0F;
            this.repeat = true;
         }

         if (event.equals(InitSounds.witch_music2)) {
            this.volume = 2.0F;
            this.repeat = true;
         }

         if (event.equals(InitSounds.witch_cryangry)) {
            this.volume = 2.0F;
         }

         if (event.equals(InitSounds.witch_angry)) {
            this.volume = 2.0F;
         }

         if (event.equals(InitSounds.hunter_music)) {
            this.repeat = true;
            this.volume = 2.0F;
         }

         if (event.equals(InitSounds.smoker_music)) {
            this.repeat = true;
            this.volume = 2.0F;
         }

         if (event.equals(InitSounds.boomer_music)) {
            this.repeat = true;
            this.volume = 8.0F;
            this.attenuationType = AttenuationType.NONE;
         }

      }
   }

   public MovingSoundEntity(Entity tracking, Entity tracking2, SoundEvent event, SoundCategory cat) {
      this(tracking, event, cat);
      this.tracking2 = tracking2;
   }

   public static MovingSoundEntity createSound(Entity tracking, SoundEvent event, SoundCategory cat) {
      return new MovingSoundEntity(tracking, event, cat);
   }

   public static MovingSoundEntity createSound(Entity tracking, Entity tracking2, SoundEvent event, SoundCategory cat) {
      return new MovingSoundEntity(tracking, tracking2, event, cat);
   }

   public void update() {
      if (this.tracking == null) {
         this.repeat = false;
         this.donePlaying = true;
      } else if (this.event == null) {
         this.repeat = false;
         this.donePlaying = true;
      } else {
         if (this.tracking instanceof EntityHunter && ((EntityHunter)this.tracking).isPouncing() && this.event.equals(InitSounds.hunter_stalk)) {
            this.repeat = false;
            this.donePlaying = true;
         }

         if (this.event.equals(InitSounds.boomer_music) && !((EntityPlayer)this.tracking).isPotionActive(InitPotions.puked)) {
            this.repeat = false;
            this.donePlaying = true;
         }

         if (this.event.equals(InitSounds.bandaging)) {
            EntityPlayer player = (EntityPlayer)this.tracking;
            if (player.getItemInUseCount() == 0) {
               this.repeat = false;
               this.donePlaying = true;
            }
         }

         if (this.event.equals(InitSounds.hunter_music) && this.tracking instanceof EntityHunter) {
            EntityHunter hunt = (EntityHunter)this.tracking;
            if (!hunt.isPinned() && hunt.getPounceTicks() < 0) {
               this.repeat = false;
               this.donePlaying = true;
            }
         }

         if (this.event.equals(InitSounds.smoker_music) && this.tracking instanceof EntitySmoker) {
            EntitySmoker smoker = (EntitySmoker)this.tracking;
            if (!smoker.isSmoked() && smoker.getSeenTicks() < 5) {
               this.repeat = false;
               this.donePlaying = true;
            }
         }

         if (this.event.equals(InitSounds.tank_music)) {
            WorldClient world = Minecraft.getMinecraft().world;
            SoundHandler sound = Minecraft.getMinecraft().getSoundHandler();
            boolean foundTank = false;
            if (world != null && sound != null) {
               Iterator var4 = world.loadedEntityList.iterator();

               while(var4.hasNext()) {
                  Entity ent = (Entity)var4.next();
                  if (ent instanceof EntityTank) {
                     foundTank = true;
                     break;
                  }
               }

               if (!foundTank) {
                  this.volume = Math.min(this.volume - 0.02F, 1.0F);
               } else {
                  this.volume = Math.min(this.volume + 0.02F, 2.0F);
               }
            }
         }

         EntityWitch witch;
         if (this.event.equals(InitSounds.witch_cry)) {
            witch = (EntityWitch)this.tracking;
            if (witch.getAngerLevel() > 600) {
               this.repeat = false;
               this.donePlaying = true;
            }
         }

         if (this.event.equals(InitSounds.witch_music1)) {
            witch = (EntityWitch)this.tracking;
            if (witch.getAttackingState() != 0) {
               this.repeat = false;
               this.donePlaying = true;
               witch.setCryMusicAttached(false);
            }

            if (BacteriaMusicDirector.playingTankMusic) {
               this.repeat = false;
               this.donePlaying = true;
               witch.setCryMusicAttached(false);
            }
         }

         if (this.event.equals(InitSounds.witch_music2)) {
            witch = (EntityWitch)this.tracking;
            if (witch.getAttackingState() != 1) {
               this.repeat = false;
               this.donePlaying = true;
               witch.setScreamMusicAttached(false);
            }

            if (BacteriaMusicDirector.playingTankMusic) {
               this.repeat = false;
               this.donePlaying = true;
               witch.setScreamMusicAttached(false);
            }
         }

         if ((this.tracking.isDead || this.tracking == null) && (this.event.equals(InitSounds.witch_music1) || this.event.equals(InitSounds.witch_music2))) {
            this.volume -= 0.05F;
         }

         if ((this.tracking.isDead || this.tracking == null) && !this.event.equals(InitSounds.witch_flee) && !this.event.equals(InitSounds.witch_music1) && !this.event.equals(InitSounds.witch_music2) && !this.event.equals(InitSounds.tank_music) && !this.event.equals(InitSounds.hunter_bacteria) && !this.event.equals(InitSounds.smoker_bacteria) && !this.event.equals(InitSounds.boomer_bacteria)) {
            this.repeat = false;
            this.donePlaying = true;
         } else {
            if (this.tracking2 != null) {
               this.xPosF = (float)this.tracking2.posX;
               this.yPosF = (float)this.tracking2.posY;
               this.zPosF = (float)this.tracking2.posZ;
            } else {
               this.xPosF = (float)this.tracking.posX;
               this.yPosF = (float)this.tracking.posY;
               this.zPosF = (float)this.tracking.posZ;
            }

            if (this.volume <= 0.0F) {
               if (this.event.equals(InitSounds.tank_music)) {
                  BacteriaMusicDirector.playingTankMusic = false;
               }

               this.repeat = false;
               this.donePlaying = true;
            }
         }

      }
   }
}
