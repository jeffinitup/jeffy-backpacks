package com.jeffyjamzhd.jeffybackpacks.mixin.entity;

import btw.util.status.StatusCategory;
import btw.util.status.StatusEffect;
import com.jeffyjamzhd.jeffybackpacks.api.impl.IEntityPlayer;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements IEntityPlayer {
    @Shadow protected abstract void damageEntity(DamageSource par1DamageSource, float par2);
    @Shadow public abstract boolean attackEntityFrom(DamageSource par1DamageSource, float par2);
    @Shadow public abstract ArrayList<StatusEffect> getAllActiveStatusEffects();
    @Shadow public abstract World getEntityWorld();
    @Shadow public InventoryPlayer inventory;

    /**
     * Amount of items currently stored within backpacks
     * the player is currently holding
     */
    @Unique private int jbp$itemsInsideBackpacks = 0;
    /**
     * Ticks left before the next damage is applied
     * <p>{@code key} - Status effect</p>
     * <p>{@code value} - Ticks left before next damage</p>
     */
    @Unique private HashMap<StatusEffect, Integer> jbp$ticksUntilNextDOT = new HashMap<>();

    public EntityPlayerMixin(World world) {
        super(world);
    }

    @ModifyArg(method = "updateItemUse", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;spawnParticle(Ljava/lang/String;DDDDDD)V",
            ordinal = 0), index = 0)
    private String setSpawnEatParticleToOverride(
            String par1Str, @Local(argsOnly = true) ItemStack stack) {
        // Get override and actual ID
        int overrideID = stack.getItem().jl$getIDForEatingParticle(stack);
        int actualID = stack.itemID;

        // Use original if override was not set
        if (overrideID == actualID)
            return par1Str;
        // Otherwise use override
        return "iconcrack_" + overrideID;
    }

    @ModifyArg(method = "addExhaustion", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/FoodStats;addExhaustion(F)V"),
            index = 0)
    private float hungerMultiplier(float par1) {
        float multiplier = 1F;
        for (StatusEffect effect : this.getAllActiveStatusEffects())
            multiplier = Math.max(effect.jbp$getHungerMultiplier(), multiplier);

        return par1 * multiplier;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void runAddonUpdates(CallbackInfo ci) {
        jbp$updateBackpackItemCount(this.inventory);
        if (!getEntityWorld().isRemote)
            jbp$updateDamageOverTime();
    }

    @Unique
    public void jbp$updateDamageOverTime() {
        ArrayList<StatusEffect> statusEffectList = getAllActiveStatusEffects();

        // Clear out old effects
        for (StatusEffect status : jbp$ticksUntilNextDOT.keySet()) {
            if (!statusEffectList.contains(status)) {
                jbp$ticksUntilNextDOT.remove(status);
            }
        }

        // Iterate through effects
        for (StatusEffect status : statusEffectList) {
            // Check if already exists
            if (jbp$ticksUntilNextDOT.containsKey(status)) {
                // Get information
                int value = jbp$ticksUntilNextDOT.get(status);
                int damageValue = status.jbp$getDamageOverTimeDamage();
                DamageSource source = status.jbp$getDamageOverTimeSource();

                // Decrement timer
                value--;
                jbp$ticksUntilNextDOT.put(status, value);

                // Reset ticks if needed
                if (value <= 0) {
                    // Get ticks
                    int reset = status.jbp$getDamageOverTimeTicks();

                    // Damage player and reset timer
                    this.attackEntityFrom(source, damageValue);
                    this.getEntityWorld()
                            .playSoundAtEntity(this, "random.classic_hurt", 0.8F, 0.6F + this.rand.nextFloat() * 0.1F);
                    jbp$ticksUntilNextDOT.put(status, reset);
                }

            } else if (status.jbp$getDamageOverTimeTicks() > 0) {
                // If it doesn't exist, but should...
                // Damage player and set timer
                DamageSource source = status.jbp$getDamageOverTimeSource();
                int damage = status.jbp$getDamageOverTimeDamage();
                int ticks = status.jbp$getDamageOverTimeTicks();

                this.attackEntityFrom(source, damage);
                this.getEntityWorld()
                        .playSoundAtEntity(this, "random.classic_hurt", 0.8F, 0.6F + this.rand.nextFloat() * 0.1F);
                jbp$ticksUntilNextDOT.put(status, ticks);
            }
        }
    }

    @Unique
    @Override
    public void jbp$updateBackpackItemCount(InventoryPlayer inventory) {
        int total = 0;

        // Check stack at cursor
        ItemStack cursorStack = inventory.getItemStack();
        if (cursorStack != null && cursorStack.getItem() instanceof ItemWithInventory inv) {
            total += inv.getItemCountInStack(cursorStack);
        }

        // Iterate through main inventory
        for (ItemStack stack : inventory.mainInventory) {
            if (stack == null)
                continue;

            // Add to total if item is valid
            if (stack.getItem() instanceof ItemWithInventory inv) {
                total += inv.getItemCountInStack(stack);
            }
        }

        // Iterate through armor
        for (ItemStack stack : inventory.armorInventory) {
            if (stack == null)
                continue;

            // Add to total if item is valid
            if (stack.getItem() instanceof ItemWithInventory inv) {
                // Dampen value slightly since the backpack is worn
                int count = inv.getItemCountInStack(stack);
                count = Math.max(0, count - 9);
                total += count;
            }
        }

        jbp$itemsInsideBackpacks = total;
    }

    @Override
    public int jbp$getBackpackItemCount() {
        return jbp$itemsInsideBackpacks;
    }
}
