package com.jeffyjamzhd.jeffybackpacks.mixin.emi;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import emi.dev.emi.emi.api.widget.ButtonWidget;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ButtonWidget.class, remap = false)
public class ButtonWidgetMixin {
    @ModifyReturnValue(method = "getTooltip", at = @At("RETURN"))
    private List<TooltipComponent> getTooltipFix(List<TooltipComponent> original) {
        if (original == null) {
            return List.of();
        }
        return original;
    }
}
