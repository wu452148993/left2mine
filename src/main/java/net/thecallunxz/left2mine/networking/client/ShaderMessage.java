package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ShaderMessage implements IMessage {
   private int shaderID;

   public ShaderMessage() {
   }

   public ShaderMessage(int shaderID) {
      this.shaderID = shaderID;
   }

   public void fromBytes(ByteBuf buf) {
      this.shaderID = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.shaderID);
   }

   public static class Handler implements IMessageHandler<ShaderMessage, IMessage> {
      public IMessage onMessage(final ShaderMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               switch(message.shaderID) {
               case 0:
                  if (Minecraft.getMinecraft().entityRenderer != null) {
                     Minecraft.getMinecraft().entityRenderer.loadEntityShader((Entity)null);
                  }
                  break;
               case 1:
                  if (Minecraft.getMinecraft().entityRenderer != null) {
                     Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("left2mine:shaders/neardeath.json"));
                     Minecraft.getMinecraft().ingameGUI.setOverlayMessage((new TextComponentTranslation("message.player.neardeath", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)), false);
                  }
                  break;
               case 2:
                  if (Minecraft.getMinecraft().entityRenderer != null) {
                     Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("left2mine:shaders/boomerbile.json"));
                  }
                  break;
               case 3:
                  if (Minecraft.getMinecraft().entityRenderer != null) {
                     Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("left2mine:shaders/boomerbilend.json"));
                  }
               }

            }
         });
         return null;
      }
   }
}
