package mod.imphack.command.commands;

import mod.imphack.Main;
import mod.imphack.command.Command;
import mod.imphack.module.Module;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

public class Bind extends Command {

	 public Bind() {
	        super("bind", new String[] { "<module>", "<bind>" });
	    }
	    
	    @Override
	    public void execute(final String[] commands) {
	        if (commands.length == 1) {
	            Command.sendMessage("Please specify a module.");
	            return;
	        }
	        final String rkey = commands[1];
	        final String moduleName = commands[0];
	        final Module module = Main.moduleManager.getModule(moduleName);
	        if (module == null) {
	            Command.sendMessage("Unknown module '" + module + "'!");
	            return;
	        }
	        if (rkey == null) {
	            Command.sendMessage(module.getName() + " is bound to " + ChatFormatting.GRAY + module.getBind());
	            return;
	        }
	        int key = Keyboard.getKeyIndex(rkey.toUpperCase());
	        if (rkey.equalsIgnoreCase("none")) {
	            key = -1;
	        }
	        if (key == 0) {
	            Command.sendMessage("Unknown key '" + rkey + "'!");
	            return;
	        }
	        module.setBind(key);
	        Command.sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rkey.toUpperCase());
	    }
	}