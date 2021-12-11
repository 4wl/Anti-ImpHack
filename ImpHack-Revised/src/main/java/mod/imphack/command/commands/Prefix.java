package mod.imphack.command.commands;


import com.mojang.realmsclient.gui.ChatFormatting;

import mod.imphack.Main;
import mod.imphack.command.Command;

public class Prefix extends Command {

	 public Prefix() {
	        super("prefix", new String[] { "<char>" });
	    }
	    
	    @Override
	    public void execute(final String[] commands) {
	        if (commands.length == 1) {
	            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Main.cmdManager.getPrefix());
	            return;
	        }
	        Main.cmdManager.setPrefix(commands[0]);
	        Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
	    }
}
