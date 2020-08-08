package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;

public class CorpsePushMessage implements IMessage {
   private double explosionX;
   private double explosionY;
   private double explosionZ;
   private double entX;
   private double entY;
   private double entZ;
   private float entYaw;
   private float entPitch;
   private int type;
   private Entity ent;
   private int skinNum;

   public CorpsePushMessage() {
   }

   public CorpsePushMessage(double explosionX, double explosionY, double explosionZ, Entity ent, int type) {
      this.ent = ent;
      this.type = type;
      this.entX = ent.posX;
      this.entY = ent.posY;
      this.entZ = ent.posZ;
      this.entYaw = ent.rotationYaw;
      this.entPitch = ent.rotationPitch;
      this.explosionX = explosionX;
      this.explosionY = explosionY;
      this.explosionZ = explosionZ;
      if (ent instanceof EntityCommonInfected) {
         this.skinNum = ((EntityCommonInfected)ent).getSkinNumber();
      } else {
         this.skinNum = 0;
      }

   }

   public void fromBytes(ByteBuf buf) {
      this.explosionX = buf.readDouble();
      this.explosionY = buf.readDouble();
      this.explosionZ = buf.readDouble();
      this.entX = buf.readDouble();
      this.entY = buf.readDouble();
      this.entZ = buf.readDouble();
      this.entYaw = buf.readFloat();
      this.entPitch = buf.readFloat();
      this.type = buf.readInt();
      this.skinNum = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.explosionX);
      buf.writeDouble(this.explosionY);
      buf.writeDouble(this.explosionZ);
      buf.writeDouble(this.entX);
      buf.writeDouble(this.entY);
      buf.writeDouble(this.entZ);
      buf.writeFloat(this.entYaw);
      buf.writeFloat(this.entPitch);
      buf.writeInt(this.type);
      buf.writeInt(this.skinNum);
   }

   public static class Handler implements IMessageHandler<CorpsePushMessage, IMessage> {
      public IMessage onMessage(final CorpsePushMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.type == 0) {
                  EntityCommonInfected newEnt = new EntityCommonInfected(Minecraft.getMinecraft().world, message.skinNum);
                  newEnt.setPositionAndRotation(message.entX, message.entY, message.entZ, message.entYaw, message.entPitch);
                  Main.proxy.spawnCorpseExplosion(newEnt, message.explosionX, message.explosionY, message.explosionZ);
                  newEnt.setDead();
               }

            }
         });
         return null;
      }
   }
}
