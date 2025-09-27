package com.jeffyjamzhd.jeffybackpacks.mixin.btw.status;

import btw.util.CustomDamageSource;
import btw.util.status.StatusEffect;
import btw.util.status.StatusEffectBuilder;
import com.jeffyjamzhd.jeffybackpacks.api.impl.IStatusEffectBuilder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StatusEffectBuilder.class, remap = false)
public class StatusEffectBuilderMixin implements IStatusEffectBuilder {
    @Unique
    private float jbp$hungerMultiplier = 1F;
    @Unique
    private float jbp$fallDamageMultiplier = 1F;
    @Unique
    private DamageSource jbp$DOTsource = new DamageSource("generic");
    @Unique
    private int jbp$DOTdamageToApply = 0;
    @Unique
    private int jbp$DOTticksBetween = 0;

    @Override
    @Unique
    public StatusEffectBuilder jbp$setHungerMultiplier(float mult) {
        this.jbp$hungerMultiplier = mult;
        return (StatusEffectBuilder) (Object) this;
    }

    @Override
    @Unique
    public StatusEffectBuilder jbp$setFallDamageMultiplier(float mult) {
        this.jbp$fallDamageMultiplier = mult;
        return (StatusEffectBuilder) (Object) this;
    }

    @Override
    public StatusEffectBuilder jbp$setDamageOverTime(DamageSource damageSource, int damageToApply, int ticksBetween) {
        jbp$DOTsource = damageSource;
        jbp$DOTdamageToApply = damageToApply;
        jbp$DOTticksBetween = ticksBetween;
        return (StatusEffectBuilder) (Object) this;
    }

    @Inject(method = "build", at = @At(
            value = "RETURN", target = "Lbtw/util/status/StatusEffect;<init>()V"))
    public void addNewFields(CallbackInfoReturnable<StatusEffect> cir,
                             @Local StatusEffect effect) {
        effect.jbp$setHungerMultiplier(jbp$hungerMultiplier);
        effect.jbp$setFallDamageMultiplier(jbp$fallDamageMultiplier);
        effect.jbp$setDamageOverTime(jbp$DOTsource, jbp$DOTdamageToApply, jbp$DOTticksBetween);
    }
}
