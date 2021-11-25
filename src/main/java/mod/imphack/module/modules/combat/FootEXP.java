package mod.imphack.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import mod.imphack.Main;
import mod.imphack.event.ImpHackEventBus;
import mod.imphack.event.events.ImpHackEventPacket;
import mod.imphack.module.Category;
import mod.imphack.module.Module;
import mod.imphack.module.modules.render.Esp;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

public class FootEXP extends Module {

	public FootEXP() {
		super("FootEXP", "FootEXP", Category.COMBAT);
		toggled = true;

	}
	public void onEnable() {
		if(((SpeedEXP)Main.moduleManager.getModule("SpeedEXP")).footEXP.isEnabled()) {
			
		
		ImpHackEventBus.EVENT_BUS.subscribe(this);
		}
	}
	
	public void onDisable() {
		ImpHackEventBus.EVENT_BUS.unsubscribe(this);
	}
	
	@EventHandler
	public Listener<ImpHackEventPacket.SendPacket> listener = new Listener<>(event -> {
		if(event.get_packet() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle) {
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90.0f, mc.player.onGround));
		}
	});

}
