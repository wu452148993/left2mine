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

public class PukedMessage implements IMessage {
   private boolean puked;
   private EntityPlayer player;

   public PukedMessage() {
   }

   public PukedMessage(boolean puked, EntityPlayer player) {
      this.puked = puked;
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

            this.puked = buf.readBoolean();
         }
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.player.getEntityId());
      buf.writeBoolean(this.puked);
   }

   public static class Handler implements IMessageHandler<PukedMessage, IMessage> {
      public IMessage onMessage(final PukedMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.player != null) {
                  IEquip equip = (IEquip)message.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  equip.setPuked(message.puked);
               }

            }
         });
         return null;
      }
   }
}
