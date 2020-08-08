package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.sounds.MovingSoundEntity;

public class MovingSoundMessage implements IMessage {
   private Entity tracking;
   private Entity tracking2;
   private int type;

   public MovingSoundMessage() {
   }

   public MovingSoundMessage(Entity tracking, int type) {
      this.tracking = tracking;
      this.type = type;
      this.tracking2 = null;
   }

   public MovingSoundMessage(Entity tracking, Entity tracking2, int type) {
      this.tracking = tracking;
      this.type = type;
      this.tracking2 = tracking2;
   }

   public void fromBytes(ByteBuf buf) {
      int entityid = buf.readInt();
      if (Minecraft.getMinecraft() != null) {
         if (Minecraft.getMinecraft().world != null) {
            if (Minecraft.getMinecraft().world.getEntityByID(entityid) != null) {
               if (Minecraft.getMinecraft().world.getEntityByID(entityid) instanceof Entity) {
                  this.tracking = Minecraft.getMinecraft().world.getEntityByID(entityid);
               } else {
                  this.tracking = null;
               }
            } else {
               this.tracking = null;
            }

            this.type = buf.readInt();
            if (buf.readBoolean()) {
               int entityid2 = buf.readInt();
               if (Minecraft.getMinecraft().world.getEntityByID(entityid2) != null) {
                  if (Minecraft.getMinecraft().world.getEntityByID(entityid2) instanceof Entity) {
                     this.tracking2 = Minecraft.getMinecraft().world.getEntityByID(entityid2);
                  } else {
                     this.tracking2 = null;
                  }
               } else {
                  this.tracking2 = null;
               }
            } else {
               this.tracking2 = null;
            }

         }
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.tracking.getEntityId());
      buf.writeInt(this.type);
      if (this.tracking2 != null) {
         buf.writeBoolean(true);
         buf.writeInt(this.tracking2.getEntityId());
      } else {
         buf.writeBoolean(false);
      }

   }

   public static class Handler implements IMessageHandler<MovingSoundMessage, IMessage> {
      public IMessage onMessage(final MovingSoundMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.tracking != null) {
                  EntityPlayer player;
                  GunProperties gun;
                  switch(message.type) {
                  case 0:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.hunter_stalk, SoundCategory.HOSTILE));
                     break;
                  case 1:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.hunter_shred, SoundCategory.HOSTILE));
                     break;
                  case 2:
                     if (message.tracking instanceof EntityPlayer) {
                        player = (EntityPlayer)message.tracking;
                        if (player.getHeldItemMainhand().getItem() instanceof ItemGun) {
                           gun = ((ItemGun)player.getHeldItemMainhand().getItem()).getGun();
                           Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, gun.reloadSound, SoundCategory.PLAYERS));
                        }
                     }
                     break;
                  case 3:
                     if (message.tracking instanceof EntityPlayer) {
                        player = (EntityPlayer)message.tracking;
                        if (player.getHeldItemMainhand().getItem() instanceof ItemGun) {
                           gun = ((ItemGun)player.getHeldItemMainhand().getItem()).getGun();
                           Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, gun.pumpSound, SoundCategory.PLAYERS));
                        }
                     }
                     break;
                  case 4:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.hunter_music, SoundCategory.MUSIC));
                     break;
                  case 5:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.bandaging, SoundCategory.PLAYERS));
                     break;
                  case 6:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.boomer_idle, SoundCategory.HOSTILE));
                     break;
                  case 7:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.boomer_alert, SoundCategory.HOSTILE));
                     break;
                  case 8:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.boomer_vomit, SoundCategory.HOSTILE));
                     break;
                  case 9:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.boomer_music, SoundCategory.MUSIC));
                     break;
                  case 10:
                     MovingSoundEntity sound = MovingSoundEntity.createSound(message.tracking, InitSounds.smoker_lurk, SoundCategory.HOSTILE);
                     if (sound != null) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                     }
                     break;
                  case 11:
                     MovingSoundEntity sound2 = MovingSoundEntity.createSound(message.tracking, InitSounds.smoker_spotprey, SoundCategory.HOSTILE);
                     if (sound2 != null) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(sound2);
                     }
                     break;
                  case 12:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.smoker_warn, SoundCategory.HOSTILE));
                     break;
                  case 13:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, message.tracking2, InitSounds.smoker_music, SoundCategory.MUSIC));
                     break;
                  case 14:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.horde_germ, SoundCategory.MUSIC));
                     break;
                  case 15:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.tank_idle, SoundCategory.HOSTILE));
                     break;
                  case 16:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.tank_angry, SoundCategory.HOSTILE));
                     break;
                  case 17:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.witch_cry, SoundCategory.HOSTILE));
                     break;
                  case 18:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.witch_angry, SoundCategory.HOSTILE));
                     break;
                  case 19:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.witch_flee, SoundCategory.HOSTILE));
                     break;
                  case 20:
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(message.tracking, InitSounds.witch_cryangry, SoundCategory.HOSTILE));
                  }
               }

            }
         });
         return null;
      }
   }
}
