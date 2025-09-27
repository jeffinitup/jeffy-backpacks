package com.jeffyjamzhd.jeffybackpacks.api.impl;

import btw.util.status.StatusEffectBuilder;
import net.minecraft.src.DamageSource;

public interface IStatusEffectBuilder {
    /**
     * Sets hunger multiplier
     */
    default StatusEffectBuilder jbp$setHungerMultiplier(float mult) {
        return null;
    }

    /**
     * Sets fall damage multiplier
     */
    default StatusEffectBuilder jbp$setFallDamageMultiplier(float mult) {
        return null;
    }

    /**
     * Sets damage over time
     */
    default StatusEffectBuilder jbp$setDamageOverTime(DamageSource damageSource, int damageToApply,
                                                      int ticksBetween) {
        return null;
    }
}
