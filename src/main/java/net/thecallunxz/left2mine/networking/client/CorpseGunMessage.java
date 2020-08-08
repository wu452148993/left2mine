package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.util.ClientCorpseUtil;

public class CorpseGunMessage implements IMessage {
   private double origX;
   private double origY;
   private double origZ;
   private int skinNum;
   private int entID;
   private float entYaw;
   private float entPitch;
   private float power;
   private float damage;
   private long timeKilled;
   private Entity ent;

   public CorpseGunMessage() {
   }

   public CorpseGunMessage(double origX, double origY, double origZ, Entity ent, float power, float damage, long timeKilled) {
      this.ent = ent;
      if (ent instanceof EntityCommonInfected) {
         this.skinNum = ((EntityCommonInfected)ent).getSkinNumber();
      } else {
         this.skinNum = 0;
      }

      this.power = power;
      this.damage = damage;
      this.timeKilled = timeKilled;
      this.origX = origX;
      this.origY = origY;
      this.origZ = origZ;
      this.entID = ent.getEntityId();
      this.entYaw = ent.rotationYaw;
      this.entPitch = ent.rotationPitch;
   }

   public void fromBytes(ByteBuf buf) {
      this.skinNum = buf.readInt();
      this.origX = buf.readDouble();
      this.origY = buf.readDouble();
      this.origZ = buf.readDouble();
      this.entID = buf.readInt();
      this.entYaw = buf.readFloat();
      this.entPitch = buf.readFloat();
      this.power = buf.readFloat();
      this.damage = buf.readFloat();
      this.timeKilled = buf.readLong();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.skinNum);
      buf.writeDouble(this.origX);
      buf.writeDouble(this.origY);
      buf.writeDouble(this.origZ);
      buf.writeInt(this.entID);
      buf.writeFloat(this.entYaw);
      buf.writeFloat(this.entPitch);
      buf.writeFloat(this.power);
      buf.writeFloat(this.damage);
      buf.writeLong(this.timeKilled);
   }

   public static class Handler implements IMessageHandler<CorpseGunMessage, IMessage> {
      public IMessage onMessage(final CorpseGunMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               Entity newEnt = Minecraft.getMinecraft().world.getEntityByID(message.entID);
               if (newEnt == null) {
                  double entX = ClientCorpseUtil.getXPosFromID(message.entID);
                  double entY = ClientCorpseUtil.getYPosFromID(message.entID);
                  double entZ = ClientCorpseUtil.getZPosFromID(message.entID);
                  newEnt = new EntityCommonInfected(Minecraft.getMinecraft().world, message.skinNum);
                  ((Entity)newEnt).setPositionAndRotation(entX, entY, entZ, message.entYaw, message.entPitch);
               }

               Main.proxy.spawnCorpse((Entity)newEnt, new Vec3d(message.origX, message.origY, message.origZ), message.power, message.damage, 0.0F);
               ((Entity)newEnt).setDead();
            }
         });
         return null;
      }
   }
}
