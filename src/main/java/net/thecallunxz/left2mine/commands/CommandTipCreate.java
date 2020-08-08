package net.thecallunxz.left2mine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.TipMessage;

public class CommandTipCreate extends CommandBase {
   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "tipcreate";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.tipcreate.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(server.getEntityWorld());
      if (args.length < 5) {
         throw new CommandException("commands.left2mine.tipcreate.argfail", new Object[0]);
      } else {
         float xCoord = 0.0F;
         float yCoord = 0.0F;
         float zCoord = 0.0F;
         boolean var8 = false;

         int timeAlive;
         try {
            xCoord = Float.parseFloat(args[0]);
            yCoord = Float.parseFloat(args[1]);
            zCoord = Float.parseFloat(args[2]);
            timeAlive = Integer.parseInt(args[3]);
         } catch (NumberFormatException var11) {
            throw new CommandException("commands.left2mine.tipcreate.argfail", new Object[0]);
         }

         String textStr = "";

         for(int i = 4; i < args.length; ++i) {
            if (i == 4) {
               textStr = textStr + args[i];
            } else {
               textStr = textStr + " " + args[i];
            }
         }

         Left2MinePacket.INSTANCE.sendToAll(new TipMessage(xCoord, yCoord, zCoord, timeAlive, textStr));
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.tipcreate.success", new Object[0]));
      }
   }
}
