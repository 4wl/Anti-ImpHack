 
package mod.imphack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import mod.imphack.Client;
import mod.imphack.Main;
import mod.imphack.command.Command;
import mod.imphack.module.Module;

public class Help extends Command {

	public Help() {
        super("help");
    }
    
    @Override
    public void execute(final String[] commands) {
        Command.sendMessage("Commands: ");
        for (final Command command : Main.cmdManager.getCommands()) {
            Command.sendMessage(ChatFormatting.GRAY + Main.cmdManager.getPrefix() + command.getName());
        }
    }

}
