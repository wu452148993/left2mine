package net.thecallunxz.left2mine.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;

public interface ISubCommandManager extends ICommandManager {
   ICommand registerSubCommand(ICommand var1);
}
