package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.gui.GuiElements;
import net.thecallunxz.left2mine.gui.GuiTipHandler;

public class TipMessage implements IMessage {
   private float tipX;
   private float tipY;
   private float tipZ;
   private boolean create;
   private int timeAlive;
   private String text;

   public TipMessage() {
   }

   public TipMessage(float tipX, float tipY, float tipZ, int timeAlive, String text) {
      this.tipX = tipX;
      this.tipY = tipY;
      this.tipZ = tipZ;
      this.timeAlive = timeAlive;
      this.text = text;
      this.create = true;
   }

   public TipMessage(float xCoord, float yCoord, float zCoord) {
      this.tipX = xCoord;
      this.tipY = yCoord;
      this.tipZ = zCoord;
      this.timeAlive = 0;
      this.text = "";
      this.create = false;
   }

   public void fromBytes(ByteBuf buf) {
      this.tipX = buf.readFloat();
      this.tipY = buf.readFloat();
      this.tipZ = buf.readFloat();
      this.timeAlive = buf.readInt();
      this.create = buf.readBoolean();
      this.text = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.tipX);
      buf.writeFloat(this.tipY);
      buf.writeFloat(this.tipZ);
      buf.writeInt(this.timeAlive);
      buf.writeBoolean(this.create);
      ByteBufUtils.writeUTF8String(buf, this.text);
   }

   public static class Handler implements IMessageHandler<TipMessage, IMessage> {
      public IMessage onMessage(final TipMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.create) {
                  GuiTipHandler addTip = new GuiTipHandler(message.tipX, message.tipY, message.tipZ, message.timeAlive, message.text);
                  GuiElements.tips.add(addTip);
               } else {
                  for(int i = 0; i < GuiElements.tips.size(); ++i) {
                     GuiTipHandler tip = (GuiTipHandler)GuiElements.tips.get(i);
                     if (tip.getX() == message.tipX && tip.getY() == message.tipY && tip.getZ() == message.tipZ) {
                        GuiElements.tips.remove(i);
                        break;
                     }
                  }
               }

            }
         });
         return null;
      }
   }
}
