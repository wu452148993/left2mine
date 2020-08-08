package net.thecallunxz.left2mine.commands;

import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;

public class CommandDirector extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "director";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.director.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (!data.isInGame()) {
         throw new CommandException("commands.left2mine.director.notingame", new Object[0]);
      } else if (args.length == 0) {
         throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
      } else {
         if (args[0].equals("spawn")) {
            if (args.length <= 1) {
               throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
            }

            if (args[1].equals("horde")) {
               if (args.length > 2) {
                  throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
               }

               if (Main.proxy.director.getPanicWarnTime() != 0L) {
                  throw new CommandException("commands.left2mine.director.hordefail", new Object[0]);
               }

               Main.proxy.director.setPanicWarnTime(server.getEntityWorld().getTotalWorldTime() + 40L);
               data.setLastPanicTime(server.getEntityWorld().getTotalWorldTime());
               Iterator var5 = server.getEntityWorld().playerEntities.iterator();

               while(var5.hasNext()) {
                  EntityPlayer player = (EntityPlayer)var5.next();
                  Left2MinePacket.INSTANCE.sendTo(new MovingSoundMessage(player, 14), (EntityPlayerMP)player);
               }

               notifyCommandListener(sender, this, "commands.left2mine.director.horde", new Object[0]);
            } else if (args[1].equals("hunter")) {
               data.addLurkingSpecial("hunter");
               notifyCommandListener(sender, this, "commands.left2mine.director.hunter", new Object[0]);
            } else if (args[1].equals("smoker")) {
               data.addLurkingSpecial("smoker");
               notifyCommandListener(sender, this, "commands.left2mine.director.smoker", new Object[0]);
            } else if (args[1].equals("boomer")) {
               data.addLurkingSpecial("boomer");
               notifyCommandListener(sender, this, "commands.left2mine.director.boomer", new Object[0]);
            } else if (args[1].equals("tankangry")) {
               data.addLurkingSpecial("tank");
               notifyCommandListener(sender, this, "commands.left2mine.director.tankangry", new Object[0]);
            } else if (args[1].equals("tank")) {
               Main.proxy.director.setForceTank(true);
               data.setReadyToBoss(true);
               notifyCommandListener(sender, this, "commands.left2mine.director.tank", new Object[0]);
            } else if (args[1].equals("witch")) {
               Main.proxy.director.setForceWitch(true);
               data.setReadyToBoss(true);
               notifyCommandListener(sender, this, "commands.left2mine.director.witch", new Object[0]);
            } else if (args[1].equals("special")) {
               data.addLurkingSpecial("random");
               notifyCommandListener(sender, this, "commands.left2mine.director.special", new Object[0]);
            } else {
               if (!args[1].equals("boss")) {
                  throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
               }

               data.setReadyToBoss(true);
               notifyCommandListener(sender, this, "commands.left2mine.director.boss", new Object[0]);
            }
         } else if (args[0].equals("disable")) {
            if (args.length > 1) {
               throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
            }

            notifyCommandListener(sender, this, "commands.left2mine.director.directordisabled", new Object[0]);
            data.setPauseTime(server.getEntityWorld().getTotalWorldTime());
            data.setDirectorEnabled(false);
         } else {
            if (!args[0].equals("enable")) {
               throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
            }

            if (args.length > 1) {
               throw new CommandException("commands.left2mine.director.argfail", new Object[0]);
            }

            if (data.getPauseTime() >= 0L) {
               data.setLastPanicTime(data.getLastPanicTime() + (server.getEntityWorld().getTotalWorldTime() - data.getPauseTime()));
               data.setLastSpecialTime(data.getLastSpecialTime() + (server.getEntityWorld().getTotalWorldTime() - data.getPauseTime()));
               data.setLastBossTime(data.getLastBossTime() + (server.getEntityWorld().getTotalWorldTime() - data.getPauseTime()));
            }

            data.setDirectorEnabled(true);
            notifyCommandListener(sender, this, "commands.left2mine.director.directorenabled", new Object[0]);
         }

      }
   }
}
