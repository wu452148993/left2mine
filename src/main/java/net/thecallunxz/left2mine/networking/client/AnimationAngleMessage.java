package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;

public class AnimationAngleMessage implements IMessage {
   private float animationAngle;
   private EntityPlayer player;

   public AnimationAngleMessage() {
   }

   public AnimationAngleMessage(float animationAngle, EntityPlayer player) {
      this.animationAngle = animationAngle;
      this.player = player;
   }

   public void fromBytes(ByteBuf buf) {
      int playerid = buf.readInt();
      if (Minecraft.getMinecraft() != null) {
         if (Minecraft.getMinecraft().world != null) {
            if (Minecraft.getMinecraft().world.getEntityByID(playerid) != null) {
               if (Minecraft.getMinecraft().world.getEntityByID(playerid) instanceof EntityPlayer) {
                  this.player = (EntityPlayer)Minecraft.getMinecraft().world.getEntityByID(playerid);
               } else {
                  this.player = null;
               }
            } else {
               this.player = null;
            }

            this.animationAngle = buf.readFloat();
         }
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.player.getEntityId());
      buf.writeFloat(this.animationAngle);
   }

   public static class Handler implements IMessageHandler<AnimationAngleMessage, IMessage> {
      public IMessage onMessage(final AnimationAngleMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.player != null) {
                  IEquip equip = (IEquip)message.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  equip.setAnimationAngle(message.animationAngle);
               }

            }
         });
         return null;
      }
   }
}
