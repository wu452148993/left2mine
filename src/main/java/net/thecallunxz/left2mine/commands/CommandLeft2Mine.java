package net.thecallunxz.left2mine.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.thecallunxz.left2mine.init.InitCommands;

public class CommandLeft2Mine extends CommandBase {
   private final CommandLeft2Mine.SubCommandHandler subCommandHandler;

   public CommandLeft2Mine(MinecraftServer server) {
      this.subCommandHandler = new CommandLeft2Mine.SubCommandHandler(server);
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   private static String joinArgs(String[] args) {
      return String.join(" ", (CharSequence[])args);
   }

   private static String[] dropFirstString(String[] input) {
      String[] output = new String[input.length - 1];
      System.arraycopy(input, 1, output, 0, input.length - 1);
      return output;
   }

   public String getName() {
      return "left2mine";
   }

   public String getUsage(ICommandSender sender) {
      return "commands.left2mine.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         sender.sendMessage(new TextComponentTranslation("commands.left2mine.error", new Object[0]));
      } else {
         this.subCommandHandler.executeCommand(sender, joinArgs(args));
      }

   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
      return this.subCommandHandler.getTabCompletions(sender, joinArgs(args), pos);
   }

   public boolean isUsernameIndex(String[] args, int index) {
      ICommand subCommand = this.subCommandHandler.getCommand(args[0]);
      return index > 0 && subCommand != null && subCommand.isUsernameIndex(dropFirstString(args), index - 1);
   }

   private static class SubCommandHandler extends CommandHandler implements ISubCommandManager {
      private final Map<String, ICommand> orderedCommandMap;
      private final Set<ICommand> orderedCommandSet;
      private final MinecraftServer server;

      private SubCommandHandler(MinecraftServer server) {
         this.orderedCommandMap = new LinkedHashMap();
         this.orderedCommandSet = new LinkedHashSet();
         this.server = server;
         InitCommands.registerSubCommands(this);
      }

      protected MinecraftServer getServer() {
         return this.server;
      }

      @Nullable
      public ICommand getCommand(String commandName) {
         return (ICommand)this.getCommands().get(commandName);
      }

      public ICommand registerCommand(ICommand command) {
         this.orderedCommandMap.put(command.getName(), command);
         this.orderedCommandSet.add(command);
         Iterator var2 = command.getAliases().iterator();

         while(true) {
            String alias;
            ICommand iCommand;
            do {
               if (!var2.hasNext()) {
                  return super.registerCommand(command);
               }

               alias = (String)var2.next();
               iCommand = (ICommand)this.orderedCommandMap.get(alias);
            } while(iCommand != null && iCommand.getName().equals(alias));

            this.orderedCommandMap.put(alias, command);
         }
      }

      public List<String> getTabCompletions(ICommandSender sender, String input, @Nullable BlockPos pos) {
         String[] args = input.split(" ", -1);
         String commandName = args[0];
         if (args.length == 1) {
            List<String> list = new ArrayList();
            list.addAll((Collection)this.orderedCommandMap.entrySet().stream().filter((entry) -> {
               return CommandBase.doesStringStartWith(commandName, (String)entry.getKey());
            }).filter((entry) -> {
               return ((ICommand)entry.getValue()).checkPermission(this.getServer(), sender);
            }).map(Entry::getKey).collect(Collectors.toList()));
            return list;
         } else {
            if (args.length > 1) {
               ICommand iCommand = (ICommand)this.orderedCommandMap.get(commandName);
               if (iCommand != null && iCommand.checkPermission(this.getServer(), sender)) {
                  return iCommand.getTabCompletions(this.getServer(), sender, CommandLeft2Mine.dropFirstString(args), pos);
               }
            }

            return Collections.emptyList();
         }
      }

      public List<ICommand> getPossibleCommands(ICommandSender sender) {
         List<ICommand> list = new ArrayList();
         list.addAll((Collection)this.orderedCommandSet.stream().filter((iCommand) -> {
            return iCommand.checkPermission(this.getServer(), sender);
         }).collect(Collectors.toList()));
         return list;
      }

      public ICommand registerSubCommand(ICommand subCommand) {
         return this.registerCommand(subCommand);
      }

      // $FF: synthetic method
      SubCommandHandler(MinecraftServer x0, Object x1) {
         this(x0);
      }
   }
}
