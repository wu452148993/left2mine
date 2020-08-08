package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.particle.EntitySmokerFX;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.util.ClientCorpseUtil;

public class SmokerCorpseMessage implements IMessage {
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

   public SmokerCorpseMessage() {
   }

   public SmokerCorpseMessage(double origX, double origY, double origZ, Entity ent, float power, float damage, long timeKilled) {
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

   public static class Handler implements IMessageHandler<SmokerCorpseMessage, IMessage> {
      public IMessage onMessage(final SmokerCorpseMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               Entity newEnt = Minecraft.getMinecraft().world.getEntityByID(message.entID);
               double d3;
               double d4;
               if (newEnt == null) {
                  double entX = ClientCorpseUtil.getXPosFromID(message.entID);
                  d3 = ClientCorpseUtil.getYPosFromID(message.entID);
                  d4 = ClientCorpseUtil.getZPosFromID(message.entID);
                  newEnt = new EntitySmoker(Minecraft.getMinecraft().world);
                  ((Entity)newEnt).setPositionAndRotation(entX, d3, d4, message.entYaw, message.entPitch);
               }

               Main.proxy.spawnCorpse((Entity)newEnt, new Vec3d(message.origX, message.origY, message.origZ), message.power, message.damage, 0.0F);
               ((Entity)newEnt).world.playSound(Minecraft.getMinecraft().player, ((Entity)newEnt).getPosition(), InitSounds.smoker_explode, SoundCategory.HOSTILE, 8.0F, 1.0F);
               int particleCount = 500;
               if (Left2MineConfig.reducedParticles) {
                  particleCount = 250;
               }

               for(int j = 0; j < particleCount; ++j) {
                  d3 = (double)((Entity)newEnt).world.rand.nextFloat() - 0.5D;
                  d4 = (double)((Entity)newEnt).world.rand.nextFloat() - 0.5D;
                  double d5 = (double)((Entity)newEnt).world.rand.nextFloat() - 0.5D;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                  d3 /= d6;
                  d4 /= d6;
                  d5 /= d6;
                  Particle origBile = new EntitySmokerFX(((Entity)newEnt).world, ((Entity)newEnt).posX, ((Entity)newEnt).posY + 1.5D, ((Entity)newEnt).posZ, (double)((Entity)newEnt).world.rand.nextFloat() * (d3 / 2.0D), (double)((Entity)newEnt).world.rand.nextFloat() * (d4 / 2.0D), (double)((Entity)newEnt).world.rand.nextFloat() * (d5 / 2.0D), 75.0D);
                  Minecraft.getMinecraft().effectRenderer.addEffect(origBile);
               }

               ((Entity)newEnt).setDead();
            }
         });
         return null;
      }
   }
}
