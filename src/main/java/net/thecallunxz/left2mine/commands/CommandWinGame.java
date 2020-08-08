package net.thecallunxz.left2mine.commands;

import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MusicMessage;

public class CommandWinGame extends CommandBase {
   public static long winTime = 0L;

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "win";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.win.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (data.isInGame() && !data.isSurvivalInGame()) {
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.win.success", new Object[0]));
         Iterator var5 = server.getEntityWorld().playerEntities.iterator();

         while(var5.hasNext()) {
            EntityPlayer player = (EntityPlayer)var5.next();
            if (!player.isCreative()) {
               player.setGameType(GameType.ADVENTURE);
               player.setPositionAndUpdate((double)server.getEntityWorld().getSpawnPoint().getX(), (double)server.getEntityWorld().getSpawnPoint().getY(), (double)server.getEntityWorld().getSpawnPoint().getZ());
               server.getEntityWorld().updateEntity(player);
            }
         }

         Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(3));
         winTime = server.getEntityWorld().getTotalWorldTime() + 5L;
      } else {
         throw new CommandException("commands.left2mine.win.notingame", new Object[0]);
      }
   }
}
