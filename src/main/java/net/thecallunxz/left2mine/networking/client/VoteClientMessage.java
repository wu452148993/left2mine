package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.util.VotingUtil;

public class VoteClientMessage implements IMessage {
   private int playersVoted;
   private int maxPlayers;

   public VoteClientMessage() {
   }

   public VoteClientMessage(int playersVoted, int maxPlayers) {
      this.playersVoted = playersVoted;
      this.maxPlayers = maxPlayers;
   }

   public void fromBytes(ByteBuf buf) {
      this.playersVoted = buf.readInt();
      this.maxPlayers = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.playersVoted);
      buf.writeInt(this.maxPlayers);
   }

   public static class Handler implements IMessageHandler<VoteClientMessage, IMessage> {
      public IMessage onMessage(final VoteClientMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               VotingUtil.voting = message.playersVoted;
               VotingUtil.maxVoters = message.maxPlayers;
            }
         });
         return null;
      }
   }
}
