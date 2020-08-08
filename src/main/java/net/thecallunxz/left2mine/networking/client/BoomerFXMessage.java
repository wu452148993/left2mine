package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.entities.particle.EntityBoomerFX;

public class BoomerFXMessage implements IMessage {
   private double explosionX;
   private double explosionY;
   private double explosionZ;
   private double speedX;
   private double speedY;
   private double speedZ;

   public BoomerFXMessage() {
   }

   public BoomerFXMessage(double explosionX, double explosionY, double explosionZ, double speedX, double speedY, double speedZ, double particleX, double particleY, double particleZ) {
      this.explosionX = explosionX;
      this.explosionY = explosionY;
      this.explosionZ = explosionZ;
      this.speedX = speedX;
      this.speedY = speedY;
      this.speedZ = speedZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.explosionX = buf.readDouble();
      this.explosionY = buf.readDouble();
      this.explosionZ = buf.readDouble();
      this.speedX = buf.readDouble();
      this.speedY = buf.readDouble();
      this.speedZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.explosionX);
      buf.writeDouble(this.explosionY);
      buf.writeDouble(this.explosionZ);
      buf.writeDouble(this.speedX);
      buf.writeDouble(this.speedY);
      buf.writeDouble(this.speedZ);
   }

   public static class Handler implements IMessageHandler<BoomerFXMessage, IMessage> {
      public IMessage onMessage(final BoomerFXMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               World world = Minecraft.getMinecraft().world;
               Particle bile = new EntityBoomerFX(world, message.explosionX, message.explosionY, message.explosionZ, message.speedX, message.speedY, message.speedZ, 16.0D);
               Minecraft.getMinecraft().effectRenderer.addEffect(bile);
            }
         });
         return null;
      }
   }
}
