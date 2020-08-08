package net.thecallunxz.left2mine.commands;

import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MusicMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.networking.client.SurvivalUpdateMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class CommandStartGame extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "start";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.start.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (data.isInGame()) {
         throw new CommandException("commands.left2mine.start.ingame", new Object[0]);
      } else if (!data.isSpawnSet(server.getEntityWorld()) && !data.isSurvivalSpawnSet(server.getEntityWorld())) {
         throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
      } else {
         if (args.length == 0) {
            if (server.getDifficulty().getDifficultyId() == 0) {
               throw new CommandException("commands.left2mine.start.peaceful", new Object[0]);
            }

            if (!data.isSpawnSet(server.getEntityWorld())) {
               throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
            }

            data.setDifficulty(server.getDifficulty().getDifficultyId());
            data.setSurvivalInGame(false);
            sender.sendMessage(new TextComponentTranslation("commands.left2mine.start.success", new Object[0]));
         } else {
            if (args.length != 1) {
               throw new CommandException("commands.left2mine.start.badarg", new Object[0]);
            }

            String str = args[0];
            if (!str.equals("1") && !str.equalsIgnoreCase("easy")) {
               if (!str.equals("2") && !str.equalsIgnoreCase("normal")) {
                  if (!str.equals("3") && !str.equalsIgnoreCase("hard")) {
                     if (!str.equals("4") && !str.equalsIgnoreCase("expert")) {
                        if (!str.equals("5") && !str.equalsIgnoreCase("survival")) {
                           if (!str.equals("6") && !str.equalsIgnoreCase("survival expert")) {
                              throw new CommandException("commands.left2mine.start.badarg", new Object[0]);
                           }

                           if (!data.isSurvivalSpawnSet(server.getEntityWorld())) {
                              throw new CommandException("commands.left2mine.start.nosurvivalspawn", new Object[0]);
                           }

                           data.setDifficulty(4);
                           data.setSurvivalInGame(true);
                           server.setDifficultyForAllWorlds(EnumDifficulty.HARD);
                        } else {
                           if (!data.isSurvivalSpawnSet(server.getEntityWorld())) {
                              throw new CommandException("commands.left2mine.start.nosurvivalspawn", new Object[0]);
                           }

                           data.setDifficulty(2);
                           data.setSurvivalInGame(true);
                           server.setDifficultyForAllWorlds(EnumDifficulty.NORMAL);
                        }
                     } else {
                        if (!data.isSpawnSet(server.getEntityWorld())) {
                           throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
                        }

                        data.setDifficulty(4);
                        data.setSurvivalInGame(false);
                        server.setDifficultyForAllWorlds(EnumDifficulty.HARD);
                     }
                  } else {
                     if (!data.isSpawnSet(server.getEntityWorld())) {
                        throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
                     }

                     data.setDifficulty(3);
                     data.setSurvivalInGame(false);
                     server.setDifficultyForAllWorlds(EnumDifficulty.HARD);
                  }
               } else {
                  if (!data.isSpawnSet(server.getEntityWorld())) {
                     throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
                  }

                  data.setDifficulty(2);
                  data.setSurvivalInGame(false);
                  server.setDifficultyForAllWorlds(EnumDifficulty.NORMAL);
               }
            } else {
               if (!data.isSpawnSet(server.getEntityWorld())) {
                  throw new CommandException("commands.left2mine.start.nospawn", new Object[0]);
               }

               data.setDifficulty(1);
               data.setSurvivalInGame(false);
               server.setDifficultyForAllWorlds(EnumDifficulty.EASY);
            }

            sender.sendMessage(new TextComponentTranslation("commands.left2mine.start.success", new Object[0]));
         }

         if (data.isSurvivalInGame() && !data.isSurvivalSpawnSet(server.getEntityWorld())) {
            throw new CommandException("commands.left2mine.start.nosurvivalspawn", new Object[0]);
         } else {
            double xCoord = (double)data.getSpawnPos().getX() + 0.5D;
            double yCoord = (double)data.getSpawnPos().getY();
            double zCoord = (double)data.getSpawnPos().getZ() + 0.5D;
            data.setLastSpawnPos(data.getSpawnPos());
            data.setRespawnPos(data.getSpawnPos());
            if (data.isSurvivalInGame()) {
               Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(0, server.getEntityWorld().getTotalWorldTime(), data.getBestSurvivalTime()));
               xCoord = (double)data.getSurvivalSpawnPos().getX() + 0.5D;
               yCoord = (double)data.getSurvivalSpawnPos().getY();
               zCoord = (double)data.getSurvivalSpawnPos().getZ() + 0.5D;
               data.setLastSpawnPos(data.getSurvivalSpawnPos());
               data.setRespawnPos(data.getSurvivalSpawnPos());
            }

            Iterator var11 = server.getPlayerList().getPlayers().iterator();

            while(var11.hasNext()) {
               EntityPlayerMP player = (EntityPlayerMP)var11.next();
               if (!player.isCreative()) {
                  IStats stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  stats.clearStats();
                  Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.CLEARALL, 0, player));
               }
            }

            if (!data.isSurvivalInGame()) {
               Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(0));
            }

            sender.getEntityWorld().getGameRules().setOrCreateGameRule("naturalRegeneration", "false");
            sender.getEntityWorld().getGameRules().setOrCreateGameRule("keepInventory", "false");
            data.resetInfectedCount();
            data.setTimesRestarted(0);
            data.setLastPanicTime(server.getEntityWorld().getTotalWorldTime());
            data.setReadyToPanic(false);
            data.setLastBossTime(server.getEntityWorld().getTotalWorldTime() - (long)server.getEntityWorld().rand.nextInt(1000));
            data.setReadyToBoss(false);
            data.setLastSpecialTime(server.getEntityWorld().getTotalWorldTime());
            data.setReadyToSpecial(false);
            data.setInPanicEvent(false);
            data.setGameStartTime(server.getEntityWorld().getTotalWorldTime());
            data.setInGame(true);
            data.setDirectorEnabled(true);
            Left2MineUtilities.gameOver(server.getEntityWorld(), false);
         }
      }
   }
}
