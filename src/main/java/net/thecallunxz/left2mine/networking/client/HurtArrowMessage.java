package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.gui.GuiElements;
import net.thecallunxz.left2mine.gui.GuiHurtArrow;

public class HurtArrowMessage implements IMessage {
   private double arrowX;
   private double arrowY;
   private double arrowZ;

   public HurtArrowMessage() {
   }

   public HurtArrowMessage(double arrowX, double arrowY, double arrowZ) {
      this.arrowX = arrowX;
      this.arrowY = arrowY;
      this.arrowZ = arrowZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.arrowX = buf.readDouble();
      this.arrowY = buf.readDouble();
      this.arrowZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.arrowX);
      buf.writeDouble(this.arrowY);
      buf.writeDouble(this.arrowZ);
   }

   public static class Handler implements IMessageHandler<HurtArrowMessage, IMessage> {
      public IMessage onMessage(final HurtArrowMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               GuiHurtArrow addArrow = new GuiHurtArrow(message.arrowX, message.arrowY, message.arrowZ, 100);
               GuiElements.arrows.add(addArrow);
            }
         });
         return null;
      }
   }
}
