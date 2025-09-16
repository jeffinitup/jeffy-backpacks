package com.jeffyjamzhd.jeffybackpacks.mixin.render;

import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Inject(method = "setArmorModel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderPlayer;setRenderPassModel(Lnet/minecraft/src/ModelBase;)V"))
    private void cancelBackpackRender4Now(
            AbstractClientPlayer par1AbstractClientPlayer,
            int par2, float par3, CallbackInfoReturnable<Integer> cir,
            @Local(ordinal = 0) ModelBiped model, @Local(ordinal = 0) ItemArmor item) {
        // Do a temporary render cancel
        // todo: satchel and backpack need a renderer when equipped
        if (item instanceof ItemWithInventory) {
            model.bipedBody.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedRightArm.showModel = false;
        }

    }
}
