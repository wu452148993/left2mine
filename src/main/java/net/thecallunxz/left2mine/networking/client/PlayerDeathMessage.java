package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.gui.GuiElements;

public class PlayerDeathMessage implements IMessage {
   private String playerName;
   private int id;

   public PlayerDeathMessage() {
   }

   public PlayerDeathMessage(String name, int id) {
      this.playerName = name;
      this.id = id;
   }

   public void fromBytes(ByteBuf buf) {
      this.playerName = ByteBufUtils.readUTF8String(buf);
      this.id = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      if (this.playerName != null) {
         ByteBufUtils.writeUTF8String(buf, this.playerName);
      } else {
         ByteBufUtils.writeUTF8String(buf, "");
      }

      buf.writeInt(this.id);
   }

   public static class Handler implements IMessageHandler<PlayerDeathMessage, IMessage> {
      public IMessage onMessage(final PlayerDeathMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               switch(message.id) {
               case 0:
                  if (!Main.deadPlayerList.contains(message.playerName)) {
                     Main.deadPlayerList.add(message.playerName);
                  }
                  break;
               case 1:
                  Main.deadPlayerList.clear();
                  GuiElements.tips.clear();
                  GuiElements.arrows.clear();
               }

            }
         });
         return null;
      }
   }
}
