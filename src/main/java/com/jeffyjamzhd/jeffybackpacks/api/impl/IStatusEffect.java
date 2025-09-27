package com.jeffyjamzhd.jeffybackpacks.api.impl;

import net.minecraft.src.DamageSource;

public interface IStatusEffect {
    default void jbp$setFallDamageMultiplier(float mult) {}
    default void jbp$setHungerMultiplier(float multi) {}
    default void jbp$setDamageOverTime(DamageSource source, int damageToApply, int ticksBetween) {}

    default float jbp$getFallDamageMultiplier() {
        return 0F;
    }
    default float jbp$getHungerMultiplier() {
        return 0F;
    }
    default int jbp$getDamageOverTimeTicks() {
        return 0;
    }
    default int jbp$getDamageOverTimeDamage() {
        return 0;
    }
    default DamageSource jbp$getDamageOverTimeSource() {
        return null;
    }
}
