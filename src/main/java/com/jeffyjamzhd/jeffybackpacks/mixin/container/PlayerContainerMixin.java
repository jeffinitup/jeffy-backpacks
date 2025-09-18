package com.jeffyjamzhd.jeffybackpacks.mixin.container;

import btw.inventory.container.PlayerContainer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerContainer.class)
public class PlayerContainerMixin {
    @ModifyExpressionValue(method = "transferStackInSlot", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;",
            ordinal = 0))
    private Item checkArmor(Item original) {
        if (original instanceof ItemArmor armor && !armor.jbp$canBeWorn()) {
            return null;
        }
        return original;
    }
}
