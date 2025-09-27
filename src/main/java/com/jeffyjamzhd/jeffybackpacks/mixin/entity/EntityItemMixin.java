package com.jeffyjamzhd.jeffybackpacks.mixin.entity;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
abstract class EntityItemMixin extends Entity {
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "onCollideWithPlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/InventoryPlayer;addItemStackToInventory(Lnet/minecraft/src/ItemStack;)Z"))
    private boolean markInventoryOp(InventoryPlayer instance, ItemStack stack) {
        return instance.jbp$addStackFromWorld(stack);
    }

    @Inject(method = "attackEntityFrom", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/src/EntityItem;setDead()V", ordinal = 0))
    private void addDestroyHook(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        // Get item and run hook
        ItemStack stack = this.getEntityItem();
        if (stack != null)
            stack.getItem().jl$onItemDestroyed(stack, worldObj, posX, posY, posZ);
    }
}
