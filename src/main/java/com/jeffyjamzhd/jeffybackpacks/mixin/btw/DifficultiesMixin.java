package com.jeffyjamzhd.jeffybackpacks.mixin.btw;

import btw.world.util.difficulty.Difficulties;
import com.jeffyjamzhd.jeffybackpacks.registry.JBDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Difficulties.class, remap = false)
public class DifficultiesMixin {
    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void defaultsHook(CallbackInfo ci) {
        JBDifficulty.preRegister();
    }
}
