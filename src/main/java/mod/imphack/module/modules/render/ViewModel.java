package mod.imphack.module.modules.render;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import mod.imphack.event.events.ImpHackTransformSideFirstPersonEvent;
import mod.imphack.module.Category;
import mod.imphack.module.Module;
import mod.imphack.setting.settings.BooleanSetting;
import mod.imphack.setting.settings.FloatSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ViewModel extends Module {
	public BooleanSetting cancelEating = new BooleanSetting("noEat", this, false);
	public FloatSetting LeftX = new FloatSetting("LeftX", this, 0f);
	public FloatSetting LeftY = new FloatSetting("LeftY", this, 0f);
	public FloatSetting LeftZ = new FloatSetting("LeftZ", this, 0f);
	public FloatSetting RightX = new FloatSetting("RightX", this, 0f);
	public FloatSetting RightY = new FloatSetting("RightY", this, 0f);
	public FloatSetting RightZ = new FloatSetting("RightZ", this, 0f);
	
	public ViewModel() {
		super("ViewModel", "allows u to change how ur model look in 1st person.", Category.RENDER);
		this.addSetting(LeftX, LeftY, LeftZ, RightX, RightY, RightZ);
	}

	@EventHandler
	private final Listener<ImpHackTransformSideFirstPersonEvent> listener = new Listener<>(event -> {
		if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
			GlStateManager.translate(RightX.getValue(), RightY.getValue(), RightZ.getValue());
		} else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
			GlStateManager.translate(LeftX.getValue(), LeftY.getValue(), LeftZ.getValue());
		}
	});
}