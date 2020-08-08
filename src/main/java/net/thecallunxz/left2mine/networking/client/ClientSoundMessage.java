package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

public class ClientSoundMessage implements IMessage {
   private int soundID;

   public ClientSoundMessage() {
   }

   public ClientSoundMessage(int soundID) {
      this.soundID = soundID;
   }

   public void fromBytes(ByteBuf buf) {
      this.soundID = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.soundID);
   }

   public static class Handler implements IMessageHandler<ClientSoundMessage, IMessage> {
      public IMessage onMessage(final ClientSoundMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayer player = Minecraft.getMinecraft().player;
               switch(message.soundID) {
               case 0:
                  if (player.getHeldItemMainhand().getItem() instanceof ItemGun) {
                     GunProperties gun = ((ItemGun)player.getHeldItemMainhand().getItem()).getGun();
                     Minecraft.getMinecraft().getSoundHandler().playSound(MovingSoundEntity.createSound(player, gun.reloadSound, SoundCategory.PLAYERS));
                  }
                  break;
               case 1:
                  player.world.playSound(player, player.getPosition(), InitSounds.horde_germ, SoundCategory.HOSTILE, 25.0F, 1.0F);
               }

            }
         });
         return null;
      }
   }
}
