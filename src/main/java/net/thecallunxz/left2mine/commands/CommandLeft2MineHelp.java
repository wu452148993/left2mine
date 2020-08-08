package net.thecallunxz.left2mine.commands;

import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandLeft2MineHelp extends CommandHelp {
   private final ISubCommandManager subCommandManager;

   public CommandLeft2MineHelp(ISubCommandManager subCommandManager) {
      this.subCommandManager = subCommandManager;
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.help.usage";
   }

   protected Map<String, ICommand> getCommandMap(MinecraftServer server) {
      return this.subCommandManager.getCommands();
   }

   protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server) {
      return this.subCommandManager.getPossibleCommands(sender);
   }
}
