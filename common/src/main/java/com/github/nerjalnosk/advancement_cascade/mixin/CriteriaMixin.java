package com.github.nerjalnosk.advancement_cascade.mixin;

import com.github.nerjalnosk.advancement_cascade.AdvancementCascade;
import com.github.nerjalnosk.advancement_cascade.PlayerAdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Criteria.class)
public abstract class CriteriaMixin {
    @Shadow
    private static <T extends Criterion<?>> T register(T object) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void staticInitInjector(CallbackInfo ci) {
        AdvancementCascade.criterion1(register(new PlayerAdvancementCriterion(AdvancementCascade.id("advancement_gain"))));
        AdvancementCascade.criterion2(register(new PlayerAdvancementCriterion(AdvancementCascade.id("advancement_loss"))));
    }
}
