package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;

public class SurvivalUpdateMessage implements IMessage {
   private int updateID;
   private long startTime;
   private long bestTime;

   public SurvivalUpdateMessage() {
   }

   public SurvivalUpdateMessage(int updateID, long startTime, long bestTime) {
      this.updateID = updateID;
      this.startTime = startTime;
      this.bestTime = bestTime;
   }

   public SurvivalUpdateMessage(int updateID) {
      this.updateID = updateID;
      this.startTime = 0L;
      this.bestTime = 0L;
   }

   public void fromBytes(ByteBuf buf) {
      this.updateID = buf.readInt();
      this.startTime = buf.readLong();
      this.bestTime = buf.readLong();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.updateID);
      buf.writeLong(this.startTime);
      buf.writeLong(this.bestTime);
   }

   public static class Handler implements IMessageHandler<SurvivalUpdateMessage, IMessage> {
      public IMessage onMessage(final SurvivalUpdateMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               switch(message.updateID) {
               case 0:
                  Main.bestSurvivalTime = message.bestTime;
                  Main.gameStartTime = message.startTime;
                  Main.inSurvivalGame = true;
                  break;
               case 1:
                  Main.bestSurvivalTime = message.bestTime;
                  Main.gameStartTime = message.startTime;
                  Main.inSurvivalGame = false;
                  break;
               case 2:
                  Main.bestSurvivalTime = message.bestTime;
                  Main.gameStartTime = message.startTime;
                  Main.survivalStarted = true;
                  break;
               case 3:
                  Main.bestSurvivalTime = message.bestTime;
                  Main.gameStartTime = message.startTime;
                  Main.survivalStarted = false;
                  break;
               case 4:
                  Main.lastSurvivalTime = message.bestTime;
               }

            }
         });
         return null;
      }
   }
}
