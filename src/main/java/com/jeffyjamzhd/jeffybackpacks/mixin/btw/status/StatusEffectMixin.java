package com.jeffyjamzhd.jeffybackpacks.mixin.btw.status;

import btw.util.status.StatusEffect;
import com.jeffyjamzhd.jeffybackpacks.api.impl.IStatusEffect;
import net.minecraft.src.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StatusEffect.class)
public class StatusEffectMixin implements IStatusEffect {
    @Unique
    private float jbp$hungerMultiplier = 1F;
    @Unique
    private float jbp$fallDamageMultiplier = 1F;
    @Unique
    private int jbp$DOTdamageToApply = 0;
    @Unique
    private int jbp$DOTticksBetween = 0;
    @Unique
    private DamageSource jbp$DOTsource = new DamageSource("generic");

    //***     Impl Setters     ***//

    @Override @Unique
    public void jbp$setHungerMultiplier(float multi) {
        jbp$hungerMultiplier = multi;
    }

    @Override @Unique
    public void jbp$setFallDamageMultiplier(float mult) {
        jbp$fallDamageMultiplier = mult;
    }

    @Override
    public void jbp$setDamageOverTime(DamageSource source, int damageToApply, int ticksBetween) {
        jbp$DOTsource = source;
        jbp$DOTdamageToApply = damageToApply;
        jbp$DOTticksBetween = ticksBetween;
    }

    //***     Impl Getters     ***//

    @Override @Unique
    public float jbp$getHungerMultiplier() {
        return jbp$hungerMultiplier;
    }

    @Override
    @Unique
    public float jbp$getFallDamageMultiplier() {
        return jbp$fallDamageMultiplier;
    }

    @Override
    public int jbp$getDamageOverTimeTicks() {
        return jbp$DOTticksBetween;
    }

    @Override
    public int jbp$getDamageOverTimeDamage() {
        return jbp$DOTdamageToApply;
    }

    @Override
    public DamageSource jbp$getDamageOverTimeSource() {
        return jbp$DOTsource;
    }
}
