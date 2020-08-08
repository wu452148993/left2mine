package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;

public class EquippedMessage implements IMessage {
   private ArrayList<ItemStack> items = new ArrayList();
   private EntityPlayer player;

   public EquippedMessage() {
   }

   public EquippedMessage(ArrayList<ItemStack> items, EntityPlayer player) {
      this.items = items;
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

            int size = buf.readInt();

            for(int i = 0; i < size; ++i) {
               int id = buf.readInt();
               Item item = Item.getItemById(id);
               this.items.add(item.getDefaultInstance());
            }

         }
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.player.getEntityId());
      buf.writeInt(this.items.size());

      for(int i = 0; i < this.items.size(); ++i) {
         int id = Item.getIdFromItem(((ItemStack)this.items.get(i)).getItem());
         buf.writeInt(id);
      }

   }

   public static class Handler implements IMessageHandler<EquippedMessage, IMessage> {
      public IMessage onMessage(final EquippedMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               if (message.player != null) {
                  IEquip equip = (IEquip)message.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  equip.setEquipped(message.items);
               }

            }
         });
         return null;
      }
   }
}
