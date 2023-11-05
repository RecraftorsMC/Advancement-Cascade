package com.github.nerjalnosk.advancement_cascade.mixin;

import com.github.nerjalnosk.advancement_cascade.AdvancementCascade;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;updateDisplay(Lnet/minecraft/advancement/Advancement;)V", shift = At.Shift.AFTER))
    private void onAdvancementGrantInjector(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        AdvancementCascade.advGetCriterion().trigger(this.owner);
    }

    @Inject(method = "revokeCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;updateDisplay(Lnet/minecraft/advancement/Advancement;)V", shift = At.Shift.AFTER))
    private void onAdvancementRevokeInjector(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        AdvancementCascade.advLossCriterion().trigger(this.owner);
    }
}
