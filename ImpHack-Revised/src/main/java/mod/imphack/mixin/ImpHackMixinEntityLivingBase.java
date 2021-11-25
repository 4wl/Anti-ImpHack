package mod.imphack.mixin;

import mod.imphack.event.ImpHackEventBus;
import mod.imphack.event.events.ImpHackEventTotemPop;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class ImpHackMixinEntityLivingBase {
	@Inject(method = "checkTotemDeathProtection", at = @At(value = "RETURN", ordinal = 1))
	public void onTotemPop(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			ImpHackEventBus.EVENT_BUS.post(new ImpHackEventTotemPop());
		}
	}
}
