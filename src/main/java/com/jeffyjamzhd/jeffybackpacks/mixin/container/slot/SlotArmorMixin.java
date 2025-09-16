package com.jeffyjamzhd.jeffybackpacks.mixin.container.slot;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlotArmor.class)
public class SlotArmorMixin {
    @ModifyReturnValue(method = "isItemValid", at = @At("RETURN"))
    private boolean verifyItemValidity(boolean original,
                                       @Local(ordinal = 0, argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor armor) {
            return armor.jbp$canBeWorn() && original;
        }
        return original;
    }
}
