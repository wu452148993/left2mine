package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.SurvivalUpdateMessage;

public class CommandPanicStart extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "panicstart";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.panicstart.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (!data.isInGame()) {
         throw new CommandException("commands.left2mine.stop.notingame", new Object[0]);
      } else {
         if (data.isSurvivalInGame() && !data.hasSurvivalStarted()) {
            data.setInPanicEvent(true);
            data.setReadyToPanic(true);
            data.setSurvivalStarted(true);
            data.setGameStartTime(server.getEntityWorld().getTotalWorldTime());
            data.setLastBossTime(server.getEntityWorld().getTotalWorldTime() - (long)server.getEntityWorld().rand.nextInt(200));
            Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(4, server.getEntityWorld().getTotalWorldTime(), 0L));
            Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(2, server.getEntityWorld().getTotalWorldTime(), data.getBestSurvivalTime()));
         } else if (!data.isSurvivalInGame()) {
            boolean var5;
            int numberOfWaves;
            if (args.length == 1) {
               var5 = false;

               try {
                  numberOfWaves = Integer.parseInt(args[0]);
               } catch (NumberFormatException var10) {
                  throw new CommandException("commands.left2mine.panicstart.argfail", new Object[0]);
               }

               Main.proxy.director.setPanicWavesLeft(numberOfWaves);
               data.setInPanicEvent(true);
               data.setReadyToPanic(true);
            } else if (args.length == 2) {
               var5 = false;

               try {
                  numberOfWaves = Integer.parseInt(args[0]);
               } catch (NumberFormatException var9) {
                  throw new CommandException("commands.left2mine.panicstart.argfail", new Object[0]);
               }

               Main.proxy.director.setPanicWavesLeft(numberOfWaves);
               boolean var6 = false;

               int delay;
               try {
                  delay = Integer.parseInt(args[1]);
               } catch (NumberFormatException var8) {
                  throw new CommandException("commands.left2mine.panicstart.argfail", new Object[0]);
               }

               server.getEntityWorld().playSound((EntityPlayer)null, sender.getPosition(), InitSounds.horde_sound, SoundCategory.HOSTILE, 25.0F, 1.0F);
               Main.proxy.director.setPanicScreamTime(server.getEntityWorld().getTotalWorldTime() + 400L);
               Main.proxy.director.setPanicDelay(server.getEntityWorld().getTotalWorldTime() + (long)delay);
            } else {
               data.setInPanicEvent(true);
               data.setReadyToPanic(true);
            }
         }

         sender.sendMessage(new TextComponentTranslation("commands.left2mine.panicstart.success", new Object[0]));
      }
   }
}
