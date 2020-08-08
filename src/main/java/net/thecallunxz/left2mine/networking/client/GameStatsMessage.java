package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.gui.GuiEndCredits;

public class GameStatsMessage implements IMessage {
   private long gameStartTime;
   private int difficulty;
   private int timesRestarted;

   public GameStatsMessage() {
   }

   public GameStatsMessage(long gameStartTime, int difficulty, int timesRestarted) {
      this.gameStartTime = gameStartTime;
      this.difficulty = difficulty;
      this.timesRestarted = timesRestarted;
   }

   public void fromBytes(ByteBuf buf) {
      this.gameStartTime = buf.readLong();
      this.difficulty = buf.readInt();
      this.timesRestarted = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.gameStartTime);
      buf.writeInt(this.difficulty);
      buf.writeInt(this.timesRestarted);
   }

   public static class Handler implements IMessageHandler<GameStatsMessage, IMessage> {
      public IMessage onMessage(final GameStatsMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               Main.gameStartTime = message.gameStartTime;
               Main.difficulty = message.difficulty;
               Main.timesRestarted = message.timesRestarted;
               Minecraft.getMinecraft().displayGuiScreen(new GuiEndCredits());
            }
         });
         return null;
      }
   }
}
