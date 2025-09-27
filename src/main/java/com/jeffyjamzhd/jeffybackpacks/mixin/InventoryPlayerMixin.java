package com.jeffyjamzhd.jeffybackpacks.mixin;

import com.jeffyjamzhd.jeffybackpacks.api.impl.IInventoryPlayer;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public abstract class InventoryPlayerMixin implements IInventoryPlayer {
    @Shadow public abstract int getFirstEmptyStack();
    @Shadow public abstract boolean addItemStackToInventory(ItemStack par1ItemStack);
    @Shadow public abstract ItemStack armorItemInSlot(int par1);
    @Shadow protected abstract void triggerItemAchievement(ItemStack stack);
    @Shadow public EntityPlayer player;

    @Unique boolean jbp$stackFromWorld = false;

    @Inject(method = "addItemStackToInventory", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;isItemDamaged()Z"),
            cancellable = true)
    private void addStackToFilteredBackpack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack backpack = armorItemInSlot(2);
        if (validForBackpackInsertion() && jbp$stackFromWorld) {
            // Check for filter
            ItemWithInventory invItem = (ItemWithInventory) backpack.getItem();
            if (!invItem.hasFilterTag(backpack))
                return;

            // Parse filter
            boolean matches = invItem.stackMatchesFilter(backpack, stack);
            if (matches) {
                putStackInBackpack(invItem, backpack, stack);
                cir.setReturnValue(true);
            }
        }
    }

    @ModifyReturnValue(method = "addItemStackToInventory", at = @At(
            value = "RETURN",
            ordinal = 4))
    private boolean addSingleStackToBackpack(boolean original, @Local(argsOnly = true) ItemStack stack) {
        ItemStack backpack = armorItemInSlot(2);
        if (!original && validForBackpackInsertion() && jbp$stackFromWorld)
            return putStackInBackpack((ItemWithInventory) backpack.getItem(), backpack, stack);
        return original;
    }

    @ModifyReturnValue(method = "addItemStackToInventory", at = @At(
            value = "RETURN",
            ordinal = 6))
    private boolean addStackToBackpack(boolean original, @Local(argsOnly = true) ItemStack stack) {
        ItemStack backpack = armorItemInSlot(2);
        if (!original && validForBackpackInsertion() && jbp$stackFromWorld)
            return putStackInBackpack((ItemWithInventory) backpack.getItem(), backpack, stack);
        return original;
    }

    @Inject(method = "onInventoryChanged", at = @At("TAIL"))
    private void updatePlayerBackpackItemCount(CallbackInfo ci) {
        player.jbp$updateBackpackItemCount((InventoryPlayer) (Object) this);
    }

    @Unique
    private boolean validForBackpackInsertion() {
        ItemStack chestplate = armorItemInSlot(2);
        return chestplate != null && chestplate.getItem() instanceof ItemWithInventory;
    }

    @Unique
    private boolean putStackInBackpack(ItemWithInventory inv, ItemStack backpack, ItemStack stack) {
        int currentStackSize = stack.stackSize;
        stack.stackSize = inv.putStackInInventory(backpack, stack, player, player.getEntityWorld());

        if (stack.stackSize != currentStackSize) {
            this.triggerItemAchievement(stack);
            return true;
        }
        return false;
    }

    @Override
    public boolean jbp$addStackFromWorld(ItemStack stack) {
        jbp$stackFromWorld = true;
        boolean result = this.addItemStackToInventory(stack);
        jbp$stackFromWorld = false;
        return result;
    }
}
