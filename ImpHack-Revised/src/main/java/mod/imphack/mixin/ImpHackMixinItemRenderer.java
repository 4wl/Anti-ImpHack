package mod.imphack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;


import mod.imphack.event.ImpHackEventBus;
import mod.imphack.event.events.ImpHackTransformSideFirstPersonEvent;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;

@Mixin(ItemRenderer.class)
public class ImpHackMixinItemRenderer {

	@Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
	public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
		ImpHackTransformSideFirstPersonEvent event = new ImpHackTransformSideFirstPersonEvent(hand);
		ImpHackEventBus.EVENT_BUS.post(event);
	}

	@Inject(method = "transformFirstPerson", at = @At("HEAD"))
	public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo callbackInfo) {
		ImpHackTransformSideFirstPersonEvent event = new ImpHackTransformSideFirstPersonEvent(hand);
		ImpHackEventBus.EVENT_BUS.post(event);
	}

}