package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.TipMessage;

public class CommandTipRemove extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "tipremove";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.tipremove.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (args.length != 3) {
         throw new CommandException("commands.left2mine.tipremove.argfail", new Object[0]);
      } else {
         float xCoord = 0.0F;
         float yCoord = 0.0F;
         float zCoord = 0.0F;

         try {
            xCoord = Float.parseFloat(args[0]);
            yCoord = Float.parseFloat(args[1]);
            zCoord = Float.parseFloat(args[2]);
         } catch (NumberFormatException var9) {
            throw new CommandException("commands.left2mine.tipremove.argfail", new Object[0]);
         }

         Left2MinePacket.INSTANCE.sendToAll(new TipMessage(xCoord, yCoord, zCoord));
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.tipremove.success", new Object[0]));
      }
   }
}
