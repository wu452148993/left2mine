package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeSlotMessage implements IMessage {
   private int slotID;

   public ChangeSlotMessage() {
   }

   public ChangeSlotMessage(int slotID) {
      this.slotID = slotID;
   }

   public void fromBytes(ByteBuf buf) {
      this.slotID = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.slotID);
   }

   public static class Handler implements IMessageHandler<ChangeSlotMessage, IMessage> {
      public IMessage onMessage(final ChangeSlotMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayer player = Minecraft.getMinecraft().player;
               player.inventory.currentItem = message.slotID;
            }
         });
         return null;
      }
   }
}
