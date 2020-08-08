package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;

public class CommandClearSurvival extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "clearsurvival";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.clearsurvival.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      data.setBestSurvivalTime(0L);
      notifyCommandListener(sender, this, "commands.left2mine.clearsurvival.success", new Object[0]);
   }
}
