package com.jeffyjamzhd.jeffybackpacks.mixin.emi;

import emi.dev.emi.emi.api.recipe.EmiInfoRecipe;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import net.minecraft.src.RenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiInfoRecipe.class, remap = false)
public class EmiInfoRecipeMixin {
    @Inject(method = "lambda$addWidgets$6", at = @At(value = "HEAD"))
    private static void enableGLFlag(EmiInfoRecipe.PageManager manager, int lineCount, int y, DrawContext raw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderHelper.disableStandardItemLighting();
    }
}
