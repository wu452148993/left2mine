package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ExplosionDustMessage implements IMessage {
   private double explosionX;
   private double explosionY;
   private double explosionZ;
   private double speedX;
   private double speedY;
   private double speedZ;
   private double particleX;
   private double particleY;
   private double particleZ;

   public ExplosionDustMessage() {
   }

   public ExplosionDustMessage(double explosionX, double explosionY, double explosionZ, double speedX, double speedY, double speedZ, double particleX, double particleY, double particleZ) {
      this.explosionX = explosionX;
      this.explosionY = explosionY;
      this.explosionZ = explosionZ;
      this.speedX = speedX;
      this.speedY = speedY;
      this.speedZ = speedZ;
      this.particleX = particleX;
      this.particleY = particleY;
      this.particleZ = particleZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.explosionX = buf.readDouble();
      this.explosionY = buf.readDouble();
      this.explosionZ = buf.readDouble();
      this.speedX = buf.readDouble();
      this.speedY = buf.readDouble();
      this.speedZ = buf.readDouble();
      this.particleX = buf.readDouble();
      this.particleY = buf.readDouble();
      this.particleZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.explosionX);
      buf.writeDouble(this.explosionY);
      buf.writeDouble(this.explosionZ);
      buf.writeDouble(this.speedX);
      buf.writeDouble(this.speedY);
      buf.writeDouble(this.speedZ);
      buf.writeDouble(this.particleX);
      buf.writeDouble(this.particleY);
      buf.writeDouble(this.particleZ);
   }

   public static class Handler implements IMessageHandler<ExplosionDustMessage, IMessage> {
      public IMessage onMessage(final ExplosionDustMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               World world = Minecraft.getMinecraft().world;
               world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, message.explosionX, message.explosionY, message.explosionZ, message.speedX, message.speedY, message.speedZ, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, message.particleX, message.particleY, message.particleZ, message.speedX, message.speedY, message.speedZ, new int[0]);
            }
         });
         return null;
      }
   }
}
