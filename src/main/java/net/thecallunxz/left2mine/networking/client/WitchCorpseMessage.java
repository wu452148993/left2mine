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
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.util.ClientCorpseUtil;

public class WitchCorpseMessage implements IMessage {
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
   private boolean sitting;

   public WitchCorpseMessage() {
   }

   public WitchCorpseMessage(double origX, double origY, double origZ, Entity ent, float power, float damage, long timeKilled, boolean sitting) {
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
      this.sitting = sitting;
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
      this.sitting = buf.readBoolean();
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
      buf.writeBoolean(this.sitting);
   }

   public static class Handler implements IMessageHandler<WitchCorpseMessage, IMessage> {
      public IMessage onMessage(final WitchCorpseMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityWitch newEnt = (EntityWitch)Minecraft.getMinecraft().world.getEntityByID(message.entID);
               float yOffset;
               if (newEnt == null) {
                  yOffset = 0.0F;
                  if (message.sitting) {
                     yOffset = -0.6F;
                  }

                  double entX = ClientCorpseUtil.getXPosFromID(message.entID);
                  double entY = ClientCorpseUtil.getYPosFromID(message.entID);
                  double entZ = ClientCorpseUtil.getZPosFromID(message.entID);
                  newEnt = new EntityWitch(Minecraft.getMinecraft().world);
                  if (!message.sitting) {
                     newEnt.setAttackingState(1);
                  }

                  newEnt.setPositionAndRotation(entX, entY, entZ, message.entYaw, message.entPitch);
               } else {
                  yOffset = 0.0F;
                  if (message.sitting) {
                     yOffset = -0.6F;
                  }

                  newEnt.setPositionAndRotation(newEnt.posX, newEnt.posY + (double)yOffset, newEnt.posZ, message.entYaw, message.entPitch);
               }

               Main.proxy.spawnCorpse(newEnt, new Vec3d(message.origX, message.origY, message.origZ), message.power, message.damage, 0.0F);
               newEnt.setDead();
            }
         });
         return null;
      }
   }
}
