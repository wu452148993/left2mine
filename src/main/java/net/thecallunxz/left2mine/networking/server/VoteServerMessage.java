package net.thecallunxz.left2mine.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MusicMessage;
import net.thecallunxz.left2mine.networking.client.VoteClientMessage;
import net.thecallunxz.left2mine.util.VotingUtil;

public class VoteServerMessage implements IMessage {
   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<VoteServerMessage, IMessage> {
      public IMessage onMessage(VoteServerMessage message, final MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayerMP player = ctx.getServerHandler().player;
               if (!VotingUtil.votedPlayerList.contains(player)) {
                  VotingUtil.votedPlayerList.add(player);
                  VotingUtil.voting = VotingUtil.votedPlayerList.size();
                  Left2MinePacket.INSTANCE.sendToAll(new VoteClientMessage(VotingUtil.voting, VotingUtil.maxVoters));
                  if (VotingUtil.voting >= VotingUtil.maxVoters) {
                     Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(8));
                  }
               }

            }
         });
         return null;
      }
   }
}
