package mod.imphack.mixin;

import mod.imphack.event.ImpHackEventBus;
import mod.imphack.event.events.ImpHackEventPush;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class ImpHackMixinWorld {
	@Redirect(method = {
			"handleMaterialAcceleration" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
	public boolean isPushedbyWaterHook(Entity entity) {
		ImpHackEventPush event = new ImpHackEventPush(2, entity);
		ImpHackEventBus.EVENT_BUS.post(event);
		return entity.isPushedByWater() && !event.isCancelled();
	}
	
}
	    
