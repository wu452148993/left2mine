package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;

public class CommandPanicStop extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "panicstop";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.panicstop.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (data.isInGame()) {
         data.setInPanicEvent(false);
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.panicstop.success", new Object[0]));
      } else {
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.stop.notingame", new Object[0]));
      }
   }
}
