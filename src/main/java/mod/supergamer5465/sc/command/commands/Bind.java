package mod.supergamer5465.sc.command.commands;

import org.lwjgl.input.Keyboard;

import mod.supergamer5465.sc.Client;
import mod.supergamer5465.sc.Main;
import mod.supergamer5465.sc.command.Command;
import mod.supergamer5465.sc.module.Module;

public class Bind extends Command {

	@Override
	public String getAlias() {
		return "bind";
	}

	@Override
	public String getDescription() {
		return "Changes keybinds";
	}

	@Override
	public String getSyntax() {
		return ".bind set [Module] [Key] | .bind clear [Module] | .bind get [Module]";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args[0].equalsIgnoreCase("set")) {
			args[2] = args[2].toUpperCase();
			int key = Keyboard.getKeyIndex(args[2]);

			for (Module m : Main.moduleManager.getModuleList()) {
				if (args[1].equalsIgnoreCase(m.getName())) {
					m.setKey(Keyboard.getKeyIndex(Keyboard.getKeyName(key)));
					Client.addChatMessage(args[1] + " has been bound to " + Keyboard.getKeyName(key));
				}
			}
		}
		if (args[0].equalsIgnoreCase("clear")) {
			for (Module m : Main.moduleManager.getModuleList()) {
				m.setKey(Keyboard.CHAR_NONE);
				Client.addChatMessage(args[1] + " keybind has been cleared");
			}
		}
		if (args[0].equalsIgnoreCase("get")) {
			for (Module m : Main.moduleManager.getModuleList()) {
				if (m.getName().equalsIgnoreCase(args[1])) {
					Client.addChatMessage(
							m.getName() + " is bound to key: \"" + Keyboard.getKeyName(m.getKey()) + "\"");
					return;
				}
			}
			Client.addChatMessage("No such module exists");
		}
		if (args.length == 0) {
			Client.addChatMessage("No arguments");
			Client.addChatMessage(this.getSyntax());
		}
		if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("clear")
				&& !args[0].equalsIgnoreCase("get")) {
			Client.addChatMessage("Invalid arguments");
			Client.addChatMessage(this.getSyntax());
		}
	}

}
