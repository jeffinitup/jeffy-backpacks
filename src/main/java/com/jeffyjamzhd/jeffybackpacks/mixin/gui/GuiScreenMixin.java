package com.jeffyjamzhd.jeffybackpacks.mixin.gui;

import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.api.impl.IGuiScreen;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin implements IGuiScreen {
    @Shadow protected Minecraft mc;
    @Unique
    long jbp$lastScroll = 0L;

    @Inject(method = "handleMouseInput", at = @At("TAIL"))
    private void mouseScroll(CallbackInfo ci, @Local(ordinal = 0) int x, @Local(ordinal = 1) int y) {
        // Get scroll
        int dWheel = Mouse.getEventDWheel();

        long sysTime = Minecraft.getSystemTime();
        if (dWheel != 0 && sysTime > jbp$lastScroll + 25L) {
            jbp$handleMouseScroll(x, y, dWheel);
            // Small input debounce
            jbp$lastScroll = sysTime;
        }
    }

    @Override
    public boolean jbp$handleMouseScroll(int x, int y, int scroll) {
        if (((GuiScreen) (Object) this) instanceof GuiContainer container) {
            // Do event handling for slot
            Slot slot = container.getSlotAtPosition(x, y);
            EntityPlayer player = mc.thePlayer;
            if (slot != null && slot.getHasStack()) {
                // Handle item in slot first
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof IItemExtendedInteraction ext) {
                    ext.itemScrolled(stack, player, player.getEntityWorld(),
                            scroll > 0 ? 1 : -1, (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)));
                }
            }

            // Check for item in cursor?
            ItemStack stack = mc.thePlayer.inventory.getItemStack();
            if (stack != null && stack.getItem() instanceof  IItemExtendedInteraction ext) {
                ext.itemScrolled(stack, player, player.getEntityWorld(),
                        scroll > 0 ? 1 : -1, (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)));
            }
        }
        return false;
    }
}
