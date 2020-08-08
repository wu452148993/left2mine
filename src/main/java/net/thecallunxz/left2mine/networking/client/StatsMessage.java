package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;

public class StatsMessage implements IMessage {
   private int ID;
   private int amount;
   private EntityPlayer player;

   public StatsMessage() {
   }

   public StatsMessage(StatsEnum stat, int amount, EntityPlayer player) {
      this.ID = stat.getId();
      this.amount = amount;
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

            this.ID = buf.readInt();
            this.amount = buf.readInt();
         }
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.player.getEntityId());
      buf.writeInt(this.ID);
      buf.writeInt(this.amount);
   }

   public static class Handler implements IMessageHandler<StatsMessage, IMessage> {
      public IMessage onMessage(final StatsMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.player != null) {
                  IStats stats = (IStats)message.player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  StatsEnum stat = StatsEnum.valueOf(message.ID);
                  stats.setStatFromEnum(stat, message.amount);
               }

            }
         });
         return null;
      }
   }
}
