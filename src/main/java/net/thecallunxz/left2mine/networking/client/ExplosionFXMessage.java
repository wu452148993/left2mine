package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;

public class ExplosionFXMessage implements IMessage {
   private double explosionX;
   private double explosionY;
   private double explosionZ;

   public ExplosionFXMessage() {
   }

   public ExplosionFXMessage(double explosionX, double explosionY, double explosionZ) {
      this.explosionX = explosionX;
      this.explosionY = explosionY;
      this.explosionZ = explosionZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.explosionX = buf.readDouble();
      this.explosionY = buf.readDouble();
      this.explosionZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.explosionX);
      buf.writeDouble(this.explosionY);
      buf.writeDouble(this.explosionZ);
   }

   public static class Handler implements IMessageHandler<ExplosionFXMessage, IMessage> {
      public IMessage onMessage(final ExplosionFXMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               Main.proxy.pipebombExplosion(Minecraft.getMinecraft().world, message.explosionX, message.explosionY, message.explosionZ);
            }
         });
         return null;
      }
   }
}
