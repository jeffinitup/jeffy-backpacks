package com.jeffyjamzhd.jeffybackpacks.mixin.entity;

import btw.util.status.StatusEffect;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    @ModifyArg(method = "entityLivingBaseFall", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityLivingBase;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),
            index = 1)
    private float multiplyFallDamage(float damage) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        if (entity instanceof EntityPlayer player) {
            float multiplier = 1F;
            for (StatusEffect effect : player.getAllActiveStatusEffects())
                multiplier = Math.max(effect.jbp$getFallDamageMultiplier(), multiplier);

            return damage * multiplier;
        }
        return damage;
    }
}
