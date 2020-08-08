package net.thecallunxz.left2mine.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.init.InitSounds;

public class FlashlightMessage implements IMessage {
   private int turnOn;

   public FlashlightMessage() {
   }

   public FlashlightMessage(int turnOn) {
      this.turnOn = turnOn;
   }

   public void fromBytes(ByteBuf buf) {
      this.turnOn = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.turnOn);
   }

   public static class Handler implements IMessageHandler<FlashlightMessage, IMessage> {
      public IMessage onMessage(final FlashlightMessage message, final MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayerMP player = ctx.getServerHandler().player;
               player.getEntityWorld().playSound((EntityPlayer)null, player.getPosition(), InitSounds.flashlight, SoundCategory.PLAYERS, 1.0F, 1.0F);
               IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               switch(message.turnOn) {
               case 0:
                  equip.setFlashlight(false);
                  break;
               case 1:
                  equip.setFlashlight(true);
               }

            }
         });
         return null;
      }
   }
}
