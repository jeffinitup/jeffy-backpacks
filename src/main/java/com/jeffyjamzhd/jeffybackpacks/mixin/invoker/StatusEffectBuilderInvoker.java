package com.jeffyjamzhd.jeffybackpacks.mixin.invoker;

import btw.util.status.StatusCategory;
import btw.util.status.StatusEffectBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StatusEffectBuilder.class)
public interface StatusEffectBuilderInvoker {
    @Invoker("<init>")
    static StatusEffectBuilder create(int level, StatusCategory category) {
        throw new AssertionError();
    }
}
