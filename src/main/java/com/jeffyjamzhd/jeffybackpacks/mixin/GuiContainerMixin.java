package com.jeffyjamzhd.jeffybackpacks.mixin;

import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.registry.JBPackets;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
@Environment(EnvType.CLIENT)
public abstract class GuiContainerMixin {
    @Shadow public Container inventorySlots;
    @Shadow public abstract Slot getTheSlot();
    @Shadow protected abstract Slot getSlotAtPosition(int par1, int par2);
    @Shadow private long field_94070_G;
    @Shadow protected boolean field_94076_q;

    @Inject(method = "mouseMovedOrUp", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/GuiContainer;handleMouseClick(Lnet/minecraft/src/Slot;III)V",
            ordinal = 9), cancellable = true)
    private void jbp$addExtendedFunctionality(
            int par1, int par2, int par3,
            CallbackInfo ci) {
//        PlayerControllerMP player = Minecraft.getMinecraft().playerController;
//        ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
//        Slot slot = getSlotAtPosition(par1, par2);
//
//        // Check if right-clicking over stack
//        if (par3 == 1 && slot.getHasStack()) {
//            // Does stack have extended interactions?
//            if (slot.getStack().getItem() instanceof IItemExtendedInteraction) {
                // Send our own packet
//                ci.cancel();
//                JBPackets.sendExtendedInteraction(
//                        this.inventorySlots.windowId, slot.getSlotIndex(),
//                        stack, player.getNetClientHandler());
//                handleStack();
//            }
//        }
    }

    @Unique
    private void handleStack() {
        if (Minecraft.getMinecraft()
                .thePlayer.inventory.getItemStack() == null) {
            this.field_94070_G = 0L;
        }
        this.field_94076_q = false;
    }
}
