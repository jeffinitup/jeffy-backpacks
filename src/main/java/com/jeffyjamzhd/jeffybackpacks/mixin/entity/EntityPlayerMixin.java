package com.jeffyjamzhd.jeffybackpacks.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {
    @ModifyArg(method = "updateItemUse", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;spawnParticle(Ljava/lang/String;DDDDDD)V",
            ordinal = 0), index = 0)
    private String setSpawnEatParticleToOverride(
            String par1Str, @Local(argsOnly = true) ItemStack stack) {
        // Get override and actual ID
        int overrideID = stack.getItem().jbp$getIDForEatingParticle(stack);
        int actualID = stack.itemID;

        // Use original if override was not set
        if (overrideID == actualID)
            return par1Str;
        // Otherwise use override
        return "iconcrack_" + overrideID;
    }
}
