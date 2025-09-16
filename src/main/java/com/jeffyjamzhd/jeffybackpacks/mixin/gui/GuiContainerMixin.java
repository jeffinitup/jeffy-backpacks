package com.jeffyjamzhd.jeffybackpacks.mixin.gui;

import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.llamalad7.mixinextras.sugar.Local;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
@Environment(EnvType.CLIENT)
public abstract class GuiContainerMixin extends GuiScreen {
    @Shadow protected static RenderItem itemRenderer;

    @Shadow protected boolean field_94076_q;
    @Shadow private long field_94070_G;
    @Shadow private Slot theSlot;

    @Shadow protected abstract void handleMouseClick(Slot par1Slot, int par2, int par3, int par4);
    @Shadow protected abstract void drawSlotInventory(Slot par1Slot);

    @Inject(method = "mouseMovedOrUp", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/GuiContainer;handleMouseClick(Lnet/minecraft/src/Slot;III)V",
            ordinal = 9), cancellable = true)
    private void jbp$addExtendedFunctionality(
            int par1, int par2, int par3,
            CallbackInfo ci,
            @Local(ordinal = 0) Slot slot) {
        // Check if right-clicking over stack
        if (par3 == 1 && slot != null && slot.getHasStack()) {
            // Does stack have extended interactions?
            if (slot.getStack().getItem() instanceof IItemExtendedInteraction) {
                // Send our own packet
                this.handleMouseClick(slot, slot.getSlotIndex(), par3, (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) ? 1 : 0);
                jbp$handleStack();
                ci.cancel();
            }
        }
    }

    @Inject(method = "drawScreen", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/InventoryPlayer;getItemStack()Lnet/minecraft/src/ItemStack;",
            ordinal = 1
    ), cancellable = true)
    private void jbp$drawTooltipWithItem(CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) int x, @Local(ordinal = 1, argsOnly = true)  int y) {
        if (this.theSlot != null && this.theSlot.getHasStack()) {
            ItemStack stackOver = this.theSlot.getStack();
            if (stackOver.getItem() instanceof ItemWithInventory inv && inv.hasProperCompoundTag(stackOver)) {
                this.jbp$renderTooltipWithStackContents(stackOver, x, y);
                ci.cancel();
            }
        }
    }

    @Unique
    private void jbp$handleStack() {
        if (Minecraft.getMinecraft()
                .thePlayer.inventory.getItemStack() == null) {
            this.field_94070_G = 0L;
        }
        this.field_94076_q = false;
    }

    @Unique
    private void jbp$renderTooltipWithStackContents(ItemStack stack, int x, int y) {
        // Declare variables
        int invSize = ((ItemWithInventory) stack.getItem()).inventorySize;
        BackpackInventory inv = new BackpackInventory(stack, invSize);
        int invSelected = inv.currentSlotID;
        Pair<Integer, Integer> grid = ((ItemWithInventory) stack.getItem()).getInvGridArrangement();

        int posX = x + 12;
        int posY = y - 12;
        int width, height;

        // Prepare for render
        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glDisable(2929);

        // Precalculate width
        width = Math.max(fontRenderer.getStringWidth(stack.getDisplayName()), grid.getLeft() * 18 + 2);
        height = Math.max(12, 10 + grid.getRight() * 20);

        // Handle screen margins
        if (posX + width > this.width) {
            posX -= 28 + width;
        }
        if (posY + height + 6 > this.height) {
            posY = this.height - height - 6;
        }
        this.zLevel = 300F;
        itemRenderer.zLevel = 300F;
        int color = -267386864;

        // Draw rect
        this.drawGradientRect(posX - 3, posY - 4, posX + width + 3, posY - 3, color, color);
        this.drawGradientRect(posX - 3, posY + height + 3, posX + width + 3, posY + height + 4, color, color);
        this.drawGradientRect(posX - 3, posY - 3, posX + width + 3, posY + height + 3, color, color);
        this.drawGradientRect(posX - 4, posY - 3, posX - 3, posY + height + 3, color, color);
        this.drawGradientRect(posX + width + 3, posY - 3, posX + width + 4, posY + height + 3, color, color);
        int var10 = 0x505000FF;
        int var11 = (var10 & 0xFEFEFE) >> 1 | var10 & 0xFF000000;
        this.drawGradientRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + height + 3 - 1, var10, var11);
        this.drawGradientRect(posX + width + 2, posY - 3 + 1, posX + width + 3, posY + height + 3 - 1, var10, var11);
        this.drawGradientRect(posX - 3, posY - 3, posX + width + 3, posY - 3 + 1, var10, var10);
        this.drawGradientRect(posX - 3, posY + height + 2, posX + width + 3, posY + height + 3, var11, var11);

        // Draw name
        String itemName = stack.getDisplayName();
        this.fontRenderer.drawStringWithShadow(itemName, posX, posY, -1);

        this.zLevel = 0.0f;
        itemRenderer.zLevel = 0.0f;
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(32826);

        // Prep render of backpack slots
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef(0F, 0F, 500F);

        for (int gridY = 0; gridY < invSize / grid.getRight(); gridY++) {
            for (int gridX = 0; gridX < grid.getLeft(); gridX++) {
                int invPos = ((gridY) * grid.getLeft()) + gridX;
                int stackX = posX + 2 + (gridX * 18);
                int stackY = posY + 12 + (gridY * 20);

                if (invPos >= invSize)
                    break;

                ItemStack stackAt = inv.getStackInSlot(invPos);

                if (invPos == invSelected && !(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))) {
                    drawRect(stackX, stackY, stackX + 16, stackY + 16, 0x55FFFFFF);
                } else if (stackAt == null) {
                    drawRect(stackX + 2, stackY + 2, stackX + 14, stackY + 14, 0x33FFFFFF);
                    continue;
                }
                drawSlotInventory(new Slot(inv, invPos, stackX, stackY));

            }
        }

        GL11.glEnable(2896);
        GL11.glEnable(2929);
        RenderHelper.enableStandardItemLighting();
    }


}
