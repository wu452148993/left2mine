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
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.util.ClientCorpseUtil;

public class TankCorpseMessage implements IMessage {
   private double origX;
   private double origY;
   private double origZ;
   private int entID;
   private float entYaw;
   private float entPitch;
   private float power;
   private float damage;
   private long timeKilled;
   private Entity ent;

   public TankCorpseMessage() {
   }

   public TankCorpseMessage(double origX, double origY, double origZ, Entity ent, float power, float damage, long timeKilled) {
      this.ent = ent;
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

   public static class Handler implements IMessageHandler<TankCorpseMessage, IMessage> {
      public IMessage onMessage(final TankCorpseMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               Entity newEnt = Minecraft.getMinecraft().world.getEntityByID(message.entID);
               if (newEnt == null) {
                  double entX = ClientCorpseUtil.getXPosFromID(message.entID);
                  double entY = ClientCorpseUtil.getYPosFromID(message.entID);
                  double entZ = ClientCorpseUtil.getZPosFromID(message.entID);
                  newEnt = new EntityTank(Minecraft.getMinecraft().world);
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
