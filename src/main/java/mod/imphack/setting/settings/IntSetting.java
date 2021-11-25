package mod.imphack.setting.settings;

import java.util.function.Predicate;

import mod.imphack.Main;
import mod.imphack.module.Module;
import mod.imphack.module.modules.render.Nametags;
import mod.imphack.setting.Setting;

public class IntSetting extends Setting {
	public int value;

	public IntSetting(String name, Module parent, int value) {
		this.name = name;
		this.parent = parent;
		if (!Main.configLoaded)
			this.value = value;
	}

	public IntSetting(String string, int i, Nametags nametags, Predicate<Integer> shown) {
		// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;

		if (Main.config != null) {
			Main.config.Save();
		}
	}
}