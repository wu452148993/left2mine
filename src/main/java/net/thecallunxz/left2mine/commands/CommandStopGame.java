package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class CommandStopGame extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "stop";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.stop.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (!data.isInGame()) {
         throw new CommandException("commands.left2mine.stop.notingame", new Object[0]);
      } else {
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.stop.success", new Object[0]));
         Left2MineUtilities.endGame(server.getEntityWorld(), false);
      }
   }
}
