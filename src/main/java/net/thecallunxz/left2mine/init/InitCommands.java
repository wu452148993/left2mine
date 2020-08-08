package net.thecallunxz.left2mine.init;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.thecallunxz.left2mine.commands.CommandClearSurvival;
import net.thecallunxz.left2mine.commands.CommandDirector;
import net.thecallunxz.left2mine.commands.CommandLeft2Mine;
import net.thecallunxz.left2mine.commands.CommandLeft2MineHelp;
import net.thecallunxz.left2mine.commands.CommandPanicStart;
import net.thecallunxz.left2mine.commands.CommandPanicStop;
import net.thecallunxz.left2mine.commands.CommandStartGame;
import net.thecallunxz.left2mine.commands.CommandStopGame;
import net.thecallunxz.left2mine.commands.CommandTipCreate;
import net.thecallunxz.left2mine.commands.CommandTipRemove;
import net.thecallunxz.left2mine.commands.CommandWinGame;
import net.thecallunxz.left2mine.commands.ISubCommandManager;

public class InitCommands {
   public static void registerCommands(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandLeft2Mine(event.getServer()));
   }

   public static void registerSubCommands(ISubCommandManager subCommandManager) {
      subCommandManager.registerSubCommand(new CommandLeft2MineHelp(subCommandManager));
      subCommandManager.registerSubCommand(new CommandStartGame());
      subCommandManager.registerSubCommand(new CommandStopGame());
      subCommandManager.registerSubCommand(new CommandWinGame());
      subCommandManager.registerSubCommand(new CommandPanicStart());
      subCommandManager.registerSubCommand(new CommandPanicStop());
      subCommandManager.registerSubCommand(new CommandClearSurvival());
      subCommandManager.registerSubCommand(new CommandTipCreate());
      subCommandManager.registerSubCommand(new CommandTipRemove());
      subCommandManager.registerSubCommand(new CommandDirector());
   }
}
