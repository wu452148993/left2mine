package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.decals.EntityBlood;
import net.thecallunxz.left2mine.entities.decals.EntityBulletHole;
import net.thecallunxz.left2mine.entities.decals.EntityDecal;
import net.thecallunxz.left2mine.entities.decals.EntityScorch;

public class DecalMessage implements IMessage {
   private int decalIndex;
   private int decalSide;
   private double decalX;
   private double decalY;
   private double decalZ;
   private boolean flag;

   public DecalMessage() {
   }

   public DecalMessage(int decalIndex, EntityDecal.EnumDecalSide side, double x, double y, double z, boolean flag) {
      this.decalIndex = decalIndex;
      this.decalSide = side.getId();
      this.decalX = x;
      this.decalY = y;
      this.decalZ = z;
      this.flag = flag;
   }

   public void fromBytes(ByteBuf buf) {
      this.decalIndex = buf.readInt();
      this.decalSide = buf.readInt();
      this.decalX = buf.readDouble();
      this.decalY = buf.readDouble();
      this.decalZ = buf.readDouble();
      this.flag = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.decalIndex);
      buf.writeInt(this.decalSide);
      buf.writeDouble(this.decalX);
      buf.writeDouble(this.decalY);
      buf.writeDouble(this.decalZ);
      buf.writeBoolean(this.flag);
   }

   public static class Handler implements IMessageHandler<DecalMessage, IMessage> {
      public IMessage onMessage(final DecalMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityDecal decal = null;
               switch(message.decalIndex) {
               case 0:
                  if (Left2MineConfig.bulletImpact == 2) {
                     decal = new EntityBulletHole(Minecraft.getMinecraft().world);
                  }
                  break;
               case 1:
                  if (message.flag) {
                     if (Left2MineConfig.bloodAmount == 3) {
                        decal = new EntityBlood(Minecraft.getMinecraft().world);
                     }
                  } else if (Left2MineConfig.bloodAmount <= 3 && Left2MineConfig.bloodAmount >= 1) {
                     decal = new EntityBlood(Minecraft.getMinecraft().world);
                  }
                  break;
               case 2:
                  decal = new EntityScorch(Minecraft.getMinecraft().world);
               }

               if (decal != null) {
                  ((EntityDecal)decal).setSide(EntityDecal.EnumDecalSide.values()[message.decalSide]);
                  ((EntityDecal)decal).setPosition(message.decalX, message.decalY, message.decalZ);
                  Minecraft.getMinecraft().world.spawnEntity((Entity)decal);
               }

            }
         });
         return null;
      }
   }
}
